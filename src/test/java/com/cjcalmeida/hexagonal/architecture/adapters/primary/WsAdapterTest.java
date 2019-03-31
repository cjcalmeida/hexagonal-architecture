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

package com.cjcalmeida.hexagonal.architecture.adapters.primary;

import com.cjcalmeida.hexagonal.architecture.adapters.primary.ws.*;
import com.cjcalmeida.hexagonal.architecture.domain.model.Game;
import com.cjcalmeida.hexagonal.architecture.domain.model.GameExceptions;
import com.cjcalmeida.hexagonal.architecture.domain.port.IGameUseCase;
import com.cjcalmeida.hexagonal.architecture.infraestructure.adapter.primary.ws.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Arrays;
import java.util.Date;

@RunWith(MockitoJUnitRunner.class)
public class WsAdapterTest {

    @Mock
    private IGameUseCase business;

    private WsAdapter adapter;
    private ObjectFactory factory = new ObjectFactory();

    @Before
    public void setUp() {
        adapter = new WsAdapter(business);
    }

    @Test
    public void createGame_success() throws Exception{
        CreateGameRequest request = factory.createCreateGameRequest();
        BasicGameInfo payload = factory.createBasicGameInfo();
        payload.setTitle("Test");
        payload.setDescription("Description");
        payload.setReleaseDate(createDate());
        request.setGame(payload);

        String result = adapter.createGame(request);
        Assert.assertEquals("CREATED", result);
    }

    @Test(expected = GameFault_Exception.class)
    public void createGame_without_payload() throws Exception{
        CreateGameRequest request = factory.createCreateGameRequest();
        request.setGame(null);
        adapter.createGame(request);
    }

    @Test(expected = GameFault_Exception.class)
    public void createGame_business_error_game_already_exists() throws Exception{
        Mockito.when(business.create(ArgumentMatchers.any(Game.class))).thenThrow(GameExceptions.GameAlreadyExistsException.class);
        CreateGameRequest request = factory.createCreateGameRequest();
        BasicGameInfo game = factory.createBasicGameInfo();
        game.setReleaseDate(createDate());
        request.setGame(game);
        adapter.createGame(request);
    }

    @Test(expected = GameFault_Exception.class)
    public void createGame_business_error_game_not_created() throws Exception{
        Mockito.when(business.create(ArgumentMatchers.any(Game.class))).thenThrow(GameExceptions.GameNotCreatedException.class);
        CreateGameRequest request = factory.createCreateGameRequest();
        BasicGameInfo game = factory.createBasicGameInfo();
        game.setReleaseDate(createDate());
        request.setGame(game);
        adapter.createGame(request);
    }

    @Test
    public void getGame_found() throws Exception{
        Mockito.when(business.get(ArgumentMatchers.anyLong())).thenReturn(createEntity());
        GetGameRequest request = factory.createGetGameRequest();
        request.setId(1L);
        GetGameResponse result = adapter.getGame(request);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getGame());
        Assert.assertEquals(1L, result.getGame().getId());
        Assert.assertEquals("Test", result.getGame().getTitle());
    }

    @Test(expected = GameFault_Exception.class)
    public void getGame_not_found() throws Exception{
        Mockito.when(business.get(ArgumentMatchers.anyLong())).thenThrow(GameExceptions.GameNotFoundException.class);
        GetGameRequest request = factory.createGetGameRequest();
        request.setId(1L);
        adapter.getGame(request);
    }

    @Test(expected = GameFault_Exception.class)
    public void listGame_not_found() throws Exception{
        Mockito.when(business.listAll()).thenThrow(GameExceptions.GameNotFoundException.class);
        adapter.listGame(factory.createListGameRequest(""));
    }

    @Test
    public void listGame_found() throws Exception {
        Mockito.when(business.listAll()).thenReturn(Arrays.asList(createEntity()));
        ListGameResponse result = adapter.listGame(factory.createListGameRequest(""));
        Assert.assertNotNull(result);
        Assert.assertFalse(result.getGame().isEmpty());
        Assert.assertEquals(1, result.getGame().size());
    }

    @Test(expected = GameFault_Exception.class)
    public void searchGame_not_found() throws Exception{
        Mockito.when(business.find(ArgumentMatchers.anyString())).thenThrow(GameExceptions.GameNotFoundException.class);
        SearchGameRequest request = factory.createSearchGameRequest();
        request.setSearch("Test");
        adapter.searchGame(request);
    }

    @Test
    public void searchGame_found() throws Exception{
        Mockito.when(business.find(ArgumentMatchers.anyString())).thenReturn(Arrays.asList(createEntity()));
        SearchGameRequest request = factory.createSearchGameRequest();
        request.setSearch("Test");
        adapter.searchGame(request);
    }

    private XMLGregorianCalendar createDate() throws Exception {
        return DatatypeFactory.newInstance().newXMLGregorianCalendar();
    }

    private Game createEntity(){
        return Game.builder()
                .id(1L)
                .title("Test")
                .description("Description")
                .releaseDate(new Date())
                .creationDate(new Date())
                .build();
    }
}