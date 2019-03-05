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
import com.cjcalmeida.hexagonal.architecture.infraestructure.jpa.JpaGameEntity;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class JpaAdapter implements IGameOutboundPort {

    private EntityManager entityManager;

    public JpaAdapter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void add(GameEntity entity) {
        JpaGameEntity jpaEntity = new JpaGameEntity();
        jpaEntity.setCreationDate(entity.getCreationDate());
        jpaEntity.setDescription(entity.getDescription());
        jpaEntity.setReleaseDate(entity.getReleaseDate());
        jpaEntity.setTitle(entity.getTitle());

        entityManager.persist(jpaEntity);
    }

    @Override
    public boolean exists(String title) {
        return !entityManager.createNamedQuery("Game.getByTitle")
                    .setParameter("title", title).getResultList().isEmpty();
    }

    @Override
    public boolean exists(Long id) {
        return !entityManager.createNamedQuery("Game.getById")
                .setParameter("id", id).getResultList().isEmpty();
    }

    @Override
    public GameEntity get(Long id) {
        JpaGameEntity result = (JpaGameEntity) entityManager.createNamedQuery("Game.getById")
                .setParameter("id", id).getSingleResult();
        return GameEntity.builder()
                .id(result.getId())
                .creationDate(result.getCreationDate())
                .releaseDate(result.getReleaseDate())
                .title(result.getTitle())
                .description(result.getDescription())
                .build();
    }

    @Override
    public Collection<GameEntity> findByTitleLike(String title) {
        List<JpaGameEntity> results = entityManager.createNamedQuery("Game.findByTitleLike")
                .setParameter("title", "%"+title+"%")
                .getResultList();

        return results.parallelStream()
                .map(jpa -> GameEntity.builder()
                .id(jpa.getId())
                .title(jpa.getTitle())
                .description(jpa.getDescription())
                .creationDate(jpa.getCreationDate())
                .releaseDate(jpa.getReleaseDate())
                .build())
                .collect(Collectors.toList());
    }

    @Override
    public Collection<GameEntity> listAll() {
        List<JpaGameEntity> results = entityManager.createNamedQuery("Game.listAll").getResultList();
        return results.parallelStream()
                .map(jpa -> GameEntity.builder()
                        .id(jpa.getId())
                        .title(jpa.getTitle())
                        .description(jpa.getDescription())
                        .creationDate(jpa.getCreationDate())
                        .releaseDate(jpa.getReleaseDate())
                        .build())
                .collect(Collectors.toList());
    }

}
