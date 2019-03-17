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

package com.cjcalmeida.hexagonal.architecture.domain;

import com.cjcalmeida.hexagonal.architecture.domain.model.Game;
import com.cjcalmeida.hexagonal.architecture.domain.model.GameExceptions;
import com.cjcalmeida.hexagonal.architecture.domain.port.IGameRepositoryPort;
import com.cjcalmeida.hexagonal.architecture.domain.port.IGameUseCase;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Date;

/**
 * <b>Domain</b><br>s
 * Contains all business logic about Game Domain.
 * As this class is part of Domain Model, she is pure java class, with minimal frameworks dependencies
 */
@Slf4j
public final class GameBusiness implements IGameUseCase {

    private final IGameRepositoryPort repository;

    public GameBusiness(IGameRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public final boolean create(final Game game) throws GameExceptions.GameAlreadyExistsException, GameExceptions.GameNotCreatedException {
        log.info("Init Game creation");
        if(repository.exists(game.getTitle())){
            log.error("Game already exists");
            throw new GameExceptions.GameAlreadyExistsException();
        }
        try {
            log.debug("Creating new game called '{}'", game.getTitle());
            repository.add(Game.builder()
                    .title(game.getTitle())
                    .description(game.getDescription())
                    .releaseDate(game.getReleaseDate())
                    .creationDate(new Date())
                    .build()
            );
            log.info("Game created");
            return true;
        }catch (Exception e) {
            log.error("Exception on creating new game", e);
            throw new GameExceptions.GameNotCreatedException(e.getMessage());
        }
    }

    @Override
    public Game get(Long id) throws GameExceptions.GameNotFoundException {
        log.info("Init Game Get");
        if(!repository.exists(id)){
            log.error("Game with id {} not exists", id);
            throw new GameExceptions.GameNotFoundException();
        }
        log.info("Game with id {} found", id);
        return repository.get(id);
    }

    @Override
    public Collection<Game> listAll() throws GameExceptions.GameNotFoundException {
        log.info("Init Listing Games");
        Collection<Game> allGames = repository.listAll();
        log.info("Found {} games", allGames.size());

        if(allGames.isEmpty()){
            throw new GameExceptions.GameNotFoundException();
        }
        return allGames;
    }

    @Override
    public Collection<Game> find(final String title) throws GameExceptions.GameNotFoundException {
        log.info("Init Finding Game");
        if(title == null || title.isEmpty()){
            log.error("Title not informed");
            throw new GameExceptions.GameNotFoundException();
        }
        final Collection<Game> gamesFound = repository.findByTitleLike(title);
        if(gamesFound.isEmpty()){
            log.error("Games not found with title like '{}'", title);
            throw new GameExceptions.GameNotFoundException();
        }
        return gamesFound;
    }
}
