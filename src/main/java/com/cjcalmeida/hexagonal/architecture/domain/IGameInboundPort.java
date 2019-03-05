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

import java.util.Collection;

/**
 * <b>Inbound Port</b><br>
 * Business operations to Game Domain
 */
public interface IGameInboundPort {

    /**
     * Create a new Game
     * @param game New Game to create
     * @return true to Game created
     * @throws GameExceptions.GameAlreadyExistsException Game already exists
     * @throws GameExceptions.GameNotCreatedException Game cant be created
     */
    boolean create(GameEntity game) throws GameExceptions.GameAlreadyExistsException, GameExceptions.GameNotCreatedException;

    /**
     * Obtain the Game by Id
     * @param id Id of the Game
     * @return Entity with all data
     * @throws GameExceptions.GameNotFoundException No games found with id informed
     */
    GameEntity get(final Long id) throws GameExceptions.GameNotFoundException;

    /**
     * List all Games
     * @return List of all Games
     */
    Collection<GameEntity> listAll() throws GameExceptions.GameNotFoundException;

    /**
     * Search all games with title like
     * @param title All or part of Game title
     * @return List of all Games with title match
     * @throws GameExceptions.GameNotFoundException No games found with title searched
     */
    Collection<GameEntity> find(String title) throws GameExceptions.GameNotFoundException;

}
