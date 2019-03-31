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

package com.cjcalmeida.hexagonal.architecture.adapters.secondary;

import com.cjcalmeida.hexagonal.architecture.adapters.secondary.jpa.GameEntity;
import com.cjcalmeida.hexagonal.architecture.adapters.secondary.jpa.IGameRepository;
import com.cjcalmeida.hexagonal.architecture.domain.model.Game;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

@RunWith(MockitoJUnitRunner.class)
public class JpaAdapterTest {

    @Mock
    private IGameRepository jpa;
    @InjectMocks
    private JpaAdapter adapter;

    @Test
    public void add() {
        List<Object> db = new ArrayList<>();
        Mockito.when(jpa.save(ArgumentMatchers.any(GameEntity.class))).then(invocation -> {
            db.add(invocation.getArgument(0));
            return invocation.getArgument(0);
        });
        adapter.add(Game.builder()
            .title("Test")
            .description("Description")
            .releaseDate(new Date())
        .build());

        Assert.assertTrue(db.size() == 1);
        Assert.assertTrue("Test".equals(((GameEntity)db.get(0)).getTitle()));
    }

    @Test
    public void exists_by_title_false() {
        Mockito.when(jpa.existsByTitle(ArgumentMatchers.anyString())).thenReturn(false);
        Assert.assertFalse(adapter.exists("Test"));
    }

    @Test
    public void exists_by_title_true() {
        Mockito.when(jpa.existsByTitle(ArgumentMatchers.anyString())).thenReturn(true);
        Assert.assertTrue(adapter.exists("Test"));
    }

    @Test
    public void exists_by_id_true() {
        Mockito.when(jpa.existsById(ArgumentMatchers.anyLong())).thenReturn(true);
        Assert.assertTrue(adapter.exists(1L));
    }

    @Test
    public void exists_by_id_false() {
        Mockito.when(jpa.existsById(ArgumentMatchers.anyLong())).thenReturn(false);
        Assert.assertFalse(adapter.exists(1L));
    }

    @Test
    public void get() {
        GameEntity entity = new GameEntity();
        entity.setCreationDate(new Date());
        entity.setDescription("Description");
        entity.setReleaseDate(new Date());
        entity.setTitle("Test");
        entity.setId(1L);
        Mockito.when(jpa.getOne(ArgumentMatchers.anyLong())).thenReturn(entity);
        Game game = adapter.get(1L);

        Assert.assertNotNull(game);
        Assert.assertTrue("Test".equals(game.getTitle()));
    }

    @Test
    public void findByTitleLike_not_empty() {
        GameEntity entity = new GameEntity();
        entity.setId(1L);
        entity.setTitle("Test");
        entity.setReleaseDate(new Date());
        entity.setDescription("Description");
        entity.setCreationDate(new Date());
        Mockito.when(jpa.findByTitleContaining(ArgumentMatchers.anyString())).thenReturn(Arrays.asList(entity));

        Collection<Game> games = adapter.findByTitleLike("Test");
        Assert.assertFalse(games.isEmpty());
    }

    @Test
    public void findByTitleLike_empty() {
        Mockito.when(jpa.findByTitleContaining(ArgumentMatchers.anyString())).thenReturn(Collections.emptyList());

        Collection<Game> games = adapter.findByTitleLike("Test");
        Assert.assertTrue(games.isEmpty());
    }

    @Test
    public void listAll_not_empty() {
        GameEntity entity = new GameEntity();
        entity.setId(1L);
        entity.setTitle("Test");
        entity.setReleaseDate(new Date());
        entity.setDescription("Description");
        entity.setCreationDate(new Date());
        Mockito.when(jpa.findAll()).thenReturn(Arrays.asList(entity));

        Assert.assertFalse(adapter.listAll().isEmpty());
    }
}