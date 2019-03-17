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

import com.cjcalmeida.hexagonal.architecture.domain.model.Game;
import com.cjcalmeida.hexagonal.architecture.infraestructure.adapter.secondary.InMemoryAdapter;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.util.Date;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InMemoryAdapterTest {

    private static InMemoryAdapter inMemory = new InMemoryAdapter();

    @Test
    public void add() {
        inMemory.add(Game.builder()
                .title("Title")
                .description("Game Description")
                .releaseDate(new Date())
                .creationDate(new Date())
                .build());

        Assert.assertTrue("Game not saved", inMemory.exists(1L));
    }

    @Test
    public void exists_by_title() {
        Assert.assertTrue(inMemory.exists("Title"));
    }

    @Test
    public void exists_by_title_not_exists() {
        Assert.assertFalse(inMemory.exists("itl"));
    }

    @Test
    public void exists_by_id() {
        Assert.assertTrue(inMemory.exists(1L));
    }

    @Test
    public void exists_by_id_not_exists() {
        Assert.assertFalse(inMemory.exists(2L));
    }

    @Test
    public void get() {
        Game game = inMemory.get(1L);
        Assert.assertNotNull(game);
        Assert.assertNotNull(game.getId());
    }

    @Test
    public void findByTitleLike() {
        Assert.assertFalse(inMemory.findByTitleLike("itl").isEmpty());
    }

    @Test
    public void findByTitleLike_not_exists() {
        Assert.assertTrue(inMemory.findByTitleLike("Game").isEmpty());
    }

    @Test
    public void listAll() {
        Assert.assertFalse(inMemory.listAll().isEmpty());
    }
}