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
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

@RunWith(MockitoJUnitRunner.class)
public class GameBusinessTest {

    @Mock
    private IGameRepositoryPort infrastructure;

    @InjectMocks
    private GameBusiness business;

    @Test
    public void create_with_no_error() throws Exception {
        Game entity = Game.builder()
                .description("Real Football Game")
                .releaseDate(new Date())
                .title("Real Football")
                .build();
        business.create(entity);
    }

    @Test(expected = GameExceptions.GameAlreadyExistsException.class)
    public void game_already_exists_on_creation() throws Exception {
        Mockito.when(infrastructure.exists(ArgumentMatchers.anyString()))
                .thenReturn(true);

        Game entity = Game.builder()
                .description("Real Racing Game")
                .releaseDate(new Date())
                .title("Real Racing")
                .build();

        business.create(entity);
    }

    @Test(expected = GameExceptions.GameNotCreatedException.class)
    public void game_not_created_with_runtime_exception() throws Exception {
        Mockito.doThrow(IllegalArgumentException.class)
            .when(infrastructure).add(ArgumentMatchers.any(Game.class));

        Game entity = Game.builder()
                .description("Real Racing Game")
                .releaseDate(new Date())
                .title("Real Racing")
                .build();

        business.create(entity);
    }

    @Test
    public void game_exists_on_get() throws Exception{
        Mockito.when(infrastructure.exists(ArgumentMatchers.anyLong())).thenReturn(true);
        Mockito.when(infrastructure.get(ArgumentMatchers.anyLong())).thenReturn(Game.builder()
                .id(1L)
                .creationDate(new Date())
                .description("Game very cool")
                .title("The Game")
                .releaseDate(new Date())
                .build());

        Assert.assertNotNull("Expected non null object", business.get(1L));
    }

    @Test(expected = GameExceptions.GameNotFoundException.class)
    public void game_not_exists_on_get() throws Exception{
        Mockito.when(infrastructure.exists(ArgumentMatchers.anyLong())).thenReturn(false);
        business.get(1L);
    }

    @Test(expected = GameExceptions.GameNotFoundException.class)
    public void empty_list_on_list_all() throws Exception{
        Mockito.when(infrastructure.listAll()).thenReturn(Collections.emptyList());
        business.listAll();
    }

    @Test
    public void games_foud_on_list_all() throws Exception {
        Mockito.when(infrastructure.listAll()).thenReturn(
                Arrays.asList(Game.builder()
                        .id(1L)
                        .releaseDate(new Date())
                        .title("Game Title")
                        .description("Description of the game")
                        .creationDate(new Date())
                        .build()
        ));

        Assert.assertFalse("List of games is empty", business.listAll().isEmpty());
    }

    @Test(expected = GameExceptions.GameNotFoundException.class)
    public void title_is_null_on_find() throws Exception {
        business.find(null);
    }

    @Test(expected = GameExceptions.GameNotFoundException.class)
    public void title_is_empty_on_find() throws Exception {
        business.find("");
    }

    @Test(expected = GameExceptions.GameNotFoundException.class)
    public void no_game_found_on_find() throws Exception {
        Mockito.when(infrastructure.findByTitleLike(ArgumentMatchers.anyString())).thenReturn(Collections.emptyList());
        business.find("The Game");
    }

    @Test
    public void game_found_on_find() throws Exception {
        Mockito.when(infrastructure.findByTitleLike(ArgumentMatchers.anyString())).thenReturn(
                Arrays.asList(Game.builder()
                        .id(1L)
                        .releaseDate(new Date())
                        .title("Game Title")
                        .description("Description of the game")
                        .creationDate(new Date())
                        .build()
                ));
        business.find("The Game");
    }
}