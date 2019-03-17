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
import com.cjcalmeida.hexagonal.architecture.infraestructure.adapter.secondary.jpa.GameEntity;
import com.cjcalmeida.hexagonal.architecture.infraestructure.adapter.secondary.jpa.IGameRepository;
import lombok.extern.slf4j.Slf4j;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
public class JpaAdapter implements IGameRepositoryPort {

    private IGameRepository repository;

    public JpaAdapter(IGameRepository repository) {
        this.repository = repository;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void add(Game entity) {
        log.debug("Adding new Game {}", entity.getTitle());
        GameEntity jpaEntity = new GameEntity();
        jpaEntity.setCreationDate(entity.getCreationDate());
        jpaEntity.setDescription(entity.getDescription());
        jpaEntity.setReleaseDate(entity.getReleaseDate());
        jpaEntity.setTitle(entity.getTitle());
        repository.save(jpaEntity);
    }

    @Override
    public boolean exists(String title) {
        log.debug("Checking if game exists with title {}", title);
        return repository.existsByTitle(title);
    }

    @Override
    public boolean exists(Long id) {
        log.debug("Checking if game exists with id {}", id);
        return repository.existsById(id);
    }

    @Override
    public Game get(Long id) {
        log.debug("Geting Game with id {}", id);
        return fromEntity(repository.getOne(id));
    }

    @Override
    public Collection<Game> findByTitleLike(String title) {
        log.debug("Searching Game with title %{}%", title);
        return repository.findByTitleContaining(title)
                .parallelStream()
                .map(this::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Game> listAll() {
        log.debug("Retrieve all games");
        return repository.findAll().parallelStream()
                .map(this::fromEntity)
                .collect(Collectors.toList());
    }

    private Game fromEntity(GameEntity entity){
        return Game.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .releaseDate(entity.getReleaseDate())
                .creationDate(entity.getCreationDate())
                .build();
    }

}
