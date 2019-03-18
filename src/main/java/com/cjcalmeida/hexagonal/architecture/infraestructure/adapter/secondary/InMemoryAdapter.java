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

package com.cjcalmeida.hexagonal.architecture.infraestructure.adapter.secondary;

import com.cjcalmeida.hexagonal.architecture.domain.model.Game;
import com.cjcalmeida.hexagonal.architecture.domain.port.IGameRepositoryPort;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <strong>Secondary Adapter</strong><br>
 * Adapter to handle Repository Port as InMemory Implementation
 */
@Slf4j
public class InMemoryAdapter implements IGameRepositoryPort {

    private Map<Long, Game> data;
    private Long avaliableId;

    public InMemoryAdapter() {
        log.debug("Initializing InMemoryAdapter");
        this.data = new HashMap<>();
        this.avaliableId = 1L;
    }

    @Override
    public synchronized void add(Game entity) {
        log.debug("Adding new Game {}", entity.getTitle());
        data.put(this.avaliableId, Game.builder()
                .id(this.avaliableId)
                .title(entity.getTitle())
                .description(entity.getDescription())
                .creationDate(entity.getCreationDate())
                .releaseDate(entity.getReleaseDate())
                .build()
        );
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
    public Game get(Long id) {
        log.debug("Geting Game with id {}", id);
        return data.get(id);
    }

    @Override
    public Collection<Game> findByTitleLike(String title) {
        log.debug("Searching Game with title %{}%", title);
        return data.values().parallelStream()
                .filter(item -> item.getTitle().contains(title)).collect(Collectors.toList());
    }

    @Override
    public Collection<Game> listAll() {
        log.debug("Retrieve all games");
        return data.values();
    }
}
