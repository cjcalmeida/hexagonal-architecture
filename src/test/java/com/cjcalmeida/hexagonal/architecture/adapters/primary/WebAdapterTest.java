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

package com.cjcalmeida.hexagonal.architecture.adapters.primary;

import com.cjcalmeida.hexagonal.architecture.domain.model.Game;
import com.cjcalmeida.hexagonal.architecture.domain.model.GameExceptions;
import com.cjcalmeida.hexagonal.architecture.domain.port.IGameUseCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.ConcurrentModel;

import java.util.*;

@RunWith(MockitoJUnitRunner.class)
public class WebAdapterTest {

    @Mock
    private IGameUseCase business;
    @InjectMocks
    private WebAdapter web;

    @Test
    public void redirect() {
        Assert.assertEquals("redirect:/web/game", web.redirect());
    }

    @Test
    public void home_fragment_to_show() throws Exception {
        Mockito.when(business.listAll()).thenReturn(Collections.emptyList());
        ConcurrentModel model = new ConcurrentModel();
        web.home(null, model);
        Assert.assertEquals("Tela exibida deve ser: Lista", "fragments/list", model.get("fragment"));
        //Assert.assertEquals("Pesquisa realizada deve ser nulo", null, model.get("query"));
        //Assert.assertEquals("Total de itens deve ser 1", 1, ((List<Game>)model.get("games")).size());
    }

    @Test
    public void home_without_query_then_listAll() throws Exception {
        Mockito.when(business.listAll()).thenReturn(Arrays.asList(Game.builder().build()));
        ConcurrentModel model = new ConcurrentModel();
        web.home(null, model);
        Assert.assertFalse(model.containsAttribute("query"));
        Assert.assertEquals("Total de itens deve ser 1", 1, ((List<Game>)model.get("games")).size());
    }

    @Test
    public void home_with_query_then_findGame() throws Exception {
        Mockito.when(business.find(ArgumentMatchers.anyString())).thenReturn(Arrays.asList(Game.builder().build()));
        ConcurrentModel model = new ConcurrentModel();
        web.home("sample", model);
        Assert.assertEquals("Pesquisa realizada deve ser 'sample'", "sample", model.get("query"));
        Assert.assertEquals("Total de itens deve ser 1", 1, ((List<Game>)model.get("games")).size());
    }

    @Test
    public void home_game_not_found() throws Exception {
        Mockito.when(business.find(ArgumentMatchers.anyString())).thenThrow(GameExceptions.GameNotFoundException.class);
        ConcurrentModel model = new ConcurrentModel();
        web.home("sample", model);
        Assert.assertFalse(model.containsAttribute("games"));
    }

    @Test
    public void initNewGame() {
        ConcurrentModel model = new ConcurrentModel();
        web.initNewGame(model);
        Assert.assertFalse((Boolean) model.get("view"));
        Assert.assertTrue(model.containsAttribute("data"));
        Assert.assertEquals("fragments/new", model.get("fragment"));
    }

    @Test
    public void create_success() throws Exception {
        Mockito.when(business.create(ArgumentMatchers.any(Game.class))).thenReturn(true);

        ConcurrentModel model = new ConcurrentModel();
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("title", "Game");
        requestParams.put("description", "Description");
        requestParams.put("release", "01/12/2018");

        Assert.assertEquals("redirect:/web/game", web.create(requestParams, model));
    }

    @Test
    public void create_fail_game_empty() throws Exception {
        ConcurrentModel model = new ConcurrentModel();
        Map<String, String> requestParams = new HashMap<>();
        web.create(requestParams, model);
        Assert.assertTrue(model.containsAttribute("data"));
        Assert.assertFalse((Boolean) model.get("view"));
    }

    @Test
    public void create_error_date_parser() throws Exception {
        ConcurrentModel model = new ConcurrentModel();
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("title", "Game");
        requestParams.put("description", "Description");
        requestParams.put("release", "01-12-2018");

        web.create(requestParams, model);

        Assert.assertTrue(model.containsAttribute("data"));
        Assert.assertEquals("game.data.format.invalid", model.get("error"));
        Assert.assertFalse((Boolean) model.get("view"));
    }

    @Test
    public void create_domain_errors() throws Exception {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("title", "Game");
        requestParams.put("description", "Description");
        requestParams.put("release", "01/12/2018");

        Mockito.when(business.create(ArgumentMatchers.any(Game.class)))
                .thenThrow(GameExceptions.GameNotCreatedException.class);
        ConcurrentModel model = new ConcurrentModel();
        Assert.assertEquals("template", web.create(requestParams, model));

        Mockito.clearInvocations(business);
        Mockito.when(business.create(ArgumentMatchers.any(Game.class)))
                .thenThrow(GameExceptions.GameNotCreatedException.class);
        model = new ConcurrentModel();
        Assert.assertEquals("template", web.create(requestParams, model));
    }

    @Test
    public void get() throws Exception {
        Mockito.when(business.get(ArgumentMatchers.anyLong())).thenReturn(
                Game.builder()
                .id(1L)
                .creationDate(new Date())
                .releaseDate(new Date())
                .description("Description")
                .title("Title")
                .build()
        );
        ConcurrentModel model = new ConcurrentModel();
        web.get(1L, model);
        Assert.assertTrue((Boolean) model.get("view"));
        Assert.assertTrue(model.containsAttribute("data"));
    }

    @Test
    public void get_error_not_found() throws Exception {
        Mockito.when(business.get(ArgumentMatchers.anyLong())).thenThrow(GameExceptions.GameNotFoundException.class);
        ConcurrentModel model = new ConcurrentModel();
        web.get(1L, model);
        Assert.assertFalse(model.containsAttribute("data"));
    }
}