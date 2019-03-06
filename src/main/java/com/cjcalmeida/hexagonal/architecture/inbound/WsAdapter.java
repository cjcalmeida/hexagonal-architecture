/*
 * Copyright (c) 2019 Cristiano Almeida
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.cjcalmeida.hexagonal.architecture.inbound;

import com.cjcalmeida.hexagonal.architecture.domain.GameEntity;
import com.cjcalmeida.hexagonal.architecture.domain.GameExceptions;
import com.cjcalmeida.hexagonal.architecture.domain.IGameInboundPort;
import com.cjcalmeida.hexagonal.architecture.inbound.ws.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jws.WebService;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.stream.Collectors;

@Slf4j
@WebService(endpointInterface = "com.cjcalmeida.hexagonal.architecture.inbound.ws.GamePort")
public class WsAdapter implements GamePort {

    private IGameInboundPort business;
    private static final String GAME_CREATED_RESPONSE = "CREATED";
    private final ObjectFactory wsFactory = new ObjectFactory();

    @Autowired
    public WsAdapter(IGameInboundPort business) {
        log.info("Initializing WS Adapter");
        this.business = business;
    }

    @Override
    public String createGame(CreateGameRequest createGameRequest) throws GameFault_Exception {
        BasicGameInfo wsGame = createGameRequest.getGame();
        try {
            business.create(GameEntity.builder()
                    .title(wsGame.getTitle())
                    .description(wsGame.getDescription())
                    .releaseDate(toDate(wsGame.getReleaseDate()))
                    .build());

        }catch (GameExceptions.GameAlreadyExistsException e){
            throw throwGameFault(e.getMessageKey(), null);
        }catch (GameExceptions.GameNotCreatedException e){
            throw  throwGameFault(e.getMessageKey(), e.getMessage());
        }
        return GAME_CREATED_RESPONSE;
    }

    @Override
    public GetGameResponse getGame(GetGameRequest getGameRequest) throws GameFault_Exception {
        long id = getGameRequest.getId();
        try{
            GameEntity entity = business.get(id);
            FullGameInfo gameWs = wsFactory.createFullGameInfo();
            gameWs.setId(entity.getId());
            gameWs.setTitle(entity.getTitle());
            gameWs.setDescription(entity.getDescription());
            gameWs.setReleaseDate(toDate(entity.getReleaseDate()));
            gameWs.setCreationDate(toDate(entity.getCreationDate()));
            GetGameResponse response = wsFactory.createGetGameResponse();
            response.setGame(gameWs);
            return response;
        }catch (GameExceptions.GameNotFoundException e){
            throw throwGameFault(e.getMessageKey(), null);
        }
    }

    @Override
    public ListGameResponse listGame(Object listGameRequest) throws GameFault_Exception {
        ListGameResponse response = wsFactory.createListGameResponse();
        try {
            response.getGame().addAll(
                    business.listAll().parallelStream()
                            .map(this::fromEntity)
                            .collect(Collectors.toList()));
            return response;
        }catch (GameExceptions.GameNotFoundException e) {
            throw throwGameFault(e.getMessageKey(), null);
        }
    }

    @Override
    public SearchGameResponse searchGame(SearchGameRequest searchGameRequest) throws GameFault_Exception {
        String query = searchGameRequest.getSearch();
        SearchGameResponse response = wsFactory.createSearchGameResponse();
        try {
            response.getGame().addAll(
                    business.find(query).parallelStream()
                            .map(this::fromEntity)
                            .collect(Collectors.toList())
            );
        }catch (GameExceptions.GameNotFoundException e) {
            throw  throwGameFault(e.getMessageKey(), null);
        }
        return response;
    }

    private FullGameInfo fromEntity(GameEntity entity){
        FullGameInfo info = wsFactory.createFullGameInfo();
        info.setId(entity.getId());
        info.setTitle(entity.getTitle());
        info.setDescription(entity.getDescription());
        info.setReleaseDate(toDate(entity.getReleaseDate()));
        info.setCreationDate(toDate(entity.getCreationDate()));
        return info;
    }

    private Date toDate(XMLGregorianCalendar date){
        return date.toGregorianCalendar().getTime();
    }

    private XMLGregorianCalendar toDate(Date date) {
        try {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
        }catch (DatatypeConfigurationException e){
            log.error("Error on parsing Date to XMLGregorianCalendar returning null date");
            log.error("", e);
            return null;
        }
    }

    private GameFault_Exception throwGameFault(String messageKey, String cause){
        GameFault gameFault = wsFactory.createGameFault();
        gameFault.setMessageKey(messageKey);
        gameFault.setReason(cause);
        Fault fault = wsFactory.createFault();
        fault.setFault(gameFault);

        return new GameFault_Exception(null, fault);
    }
}

