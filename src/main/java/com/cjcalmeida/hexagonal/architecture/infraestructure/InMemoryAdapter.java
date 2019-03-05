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

package com.cjcalmeida.hexagonal.architecture.infraestructure;

import com.cjcalmeida.hexagonal.architecture.domain.GameEntity;
import com.cjcalmeida.hexagonal.architecture.domain.IGameOutboundPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of {@link IGameOutboundPort} <br>
 * Implements in Memory Adapter strategy
 */
@Slf4j
public class InMemoryAdapter implements IGameOutboundPort {

    private Map<Long, GameEntity> data;
    private Long avaliableId;

    public InMemoryAdapter() {
        log.debug("Initializing InMemoryAdapter");
        this.data = new HashMap<>();
        this.avaliableId = 1L;
    }

    @Override
    public synchronized void add(GameEntity entity) {
        log.debug("Adding new Game {}", entity.getTitle());
        entity.setId(this.avaliableId);
        data.put(this.avaliableId, entity);
        this.avaliableId += 1;
    }

    @Override
    public boolean exists(String title) {
        log.debug("Checking if game exists with title {}", title);
        return data.values().parallelStream()
                .anyMatch(item -> item.getTitle().equalsIgnoreCase(title));
    }

    @Override
    public boolean exists(Long id) {
        log.debug("Checking if game exists with id {}", id);
        return data.containsKey(id);
    }

    @Override
    public GameEntity get(Long id) {
        log.debug("Geting Game with id {}", id);
        return data.get(id);
    }

    @Override
    public Collection<GameEntity> findByTitleLike(String title) {
        log.debug("Searching Game with title %{}%", title);
        return data.values().parallelStream()
                .filter(item -> item.getTitle().contains(title)).collect(Collectors.toList());
    }

    @Override
    public Collection<GameEntity> listAll() {
        log.debug("Retrieve all games");
        return data.values();
    }
}
