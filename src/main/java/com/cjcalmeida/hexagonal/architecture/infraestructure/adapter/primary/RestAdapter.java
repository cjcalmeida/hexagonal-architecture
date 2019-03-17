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

package com.cjcalmeida.hexagonal.architecture.infraestructure.adapter.primary;

import com.cjcalmeida.hexagonal.architecture.domain.model.Game;
import com.cjcalmeida.hexagonal.architecture.domain.model.GameExceptions;
import com.cjcalmeida.hexagonal.architecture.domain.port.IGameUseCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Profile("api")
@RestController
@RequestMapping("/api/game")
@Slf4j
public class RestAdapter {

    private IGameUseCase service;

    @Autowired
    public RestAdapter(IGameUseCase service) {
        log.info("Initializing API Adapter");
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody GameRepresentation game)
            throws GameExceptions.GameAlreadyExistsException, GameExceptions.GameNotCreatedException{
        service.create(
                Game.builder()
                .title(game.getTitle())
                .description(game.getDescription())
                .releaseDate(game.getReleaseDate())
                .build()
        );
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") Long id) throws GameExceptions.GameNotFoundException {
        Game game = service.get(id);
        FullGameRepresentation representation = new FullGameRepresentation();
        representation.setId(game.getId());
        representation.setTitle(game.getTitle());
        representation.setDescription(game.getDescription());
        representation.setReleaseDate(game.getReleaseDate());
        representation.setCreationDate(game.getCreationDate());
        return new ResponseEntity<>(representation, HttpStatus.FOUND);
    }

    @GetMapping
    public ResponseEntity<?> find(@RequestParam(required = false, name = "q") String title)
            throws GameExceptions.GameNotFoundException{

        Collection<Game> results;
        if(title == null || title.isEmpty()) {
            results = service.listAll();
        }else{
            results = service.find(title);
        }

        Collection<FullGameRepresentation> representations = results.parallelStream()
                .map(entity -> {
                    FullGameRepresentation representation = new FullGameRepresentation();
                    representation.setId(entity.getId());
                    representation.setCreationDate(entity.getCreationDate());
                    representation.setReleaseDate(entity.getReleaseDate());
                    representation.setDescription(entity.getDescription());
                    representation.setTitle(entity.getTitle());
                    return representation;
                })
                .collect(Collectors.toList());

        return new ResponseEntity<>(representations, HttpStatus.OK);
    }

    /*
        Exception Handlers
     */

    @ExceptionHandler({GameExceptions.GameAlreadyExistsException.class})
    public ResponseEntity<GameExceptionRepresentation> handleDomainException(
            GameExceptions.GameAlreadyExistsException e){
        return new ResponseEntity<>(new GameExceptionRepresentation(e.getMessageKey(), e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({GameExceptions.GameNotCreatedException.class})
    public ResponseEntity<GameExceptionRepresentation> handleDomainException(
            GameExceptions.GameNotCreatedException e){
        return new ResponseEntity<>(new GameExceptionRepresentation(e.getMessageKey(), e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({GameExceptions.GameNotFoundException.class})
    public ResponseEntity<GameExceptionRepresentation> handleDomainException(GameExceptions.GameException e){
        return new ResponseEntity<>(new GameExceptionRepresentation(e.getMessageKey(), e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    /*
       Api Representations
     */

    @Data
    @NoArgsConstructor
    public static class GameRepresentation {
        private String title;
        private String description;
        private Date releaseDate;
    }

    @Data
    @NoArgsConstructor
    public static class FullGameRepresentation extends GameRepresentation{
        private Long id;
        private Date creationDate;
    }

    @Getter
    @AllArgsConstructor
    public static class GameExceptionRepresentation {
        private String key;
        private String cause;
    }
}
