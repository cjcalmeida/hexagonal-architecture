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
 * <b>Outbound Port</b> <br>
 * Infraestruture operations for Game Domain.<br><br>
 * This interface defines all operations that a Repository Adapter needs to implests.
 */
public interface IGameOutboundPort {

    /**
     * Create a new Entity in repository
     * @param entity Entity to create
     */
    void add(final GameEntity entity);

    /**
     * Verify if Game with title already exists
     * @param title Game title (equals verify)
     * @return true - Game already exists with title, otherwise Game not exists
     */
    boolean exists(final String title);

    /**
     * Verify if ID of Game exists
     * @param id Game id
     * @return true - Game already exists with title, otherwise Game not exists
     */
    boolean exists(final Integer id);

    /**
     * Returns Game that has Id
     * @param id Id of Game
     * @return All Game data
     */
    GameEntity get(final Integer id);

    /**
     * Search by Game title (like comparation)
     * @param title Title of Game
     * @return Collection of Games with all or part title informed
     */
    Collection<GameEntity> findByTitleLike(final String title);

    /**
     * List all Games in System
     * @return Collection of Games saved in system
     */
    Collection<GameEntity> listAll();
}
