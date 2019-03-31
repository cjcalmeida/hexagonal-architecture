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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

@RunWith(MockitoJUnitRunner.class)
public class RestAdapterTest {

    @Mock
    private IGameUseCase business;

    private RestAdapter adapter;

    @Before
    public void setUp() throws Exception {
        adapter = new RestAdapter(business);
    }

    @Test
    public void create() throws Exception{
        RestAdapter.GameRepresentation representation = new RestAdapter.FullGameRepresentation();
        representation.setTitle("Test");
        representation.setDescription("Description");
        representation.setReleaseDate(new Date());
        ResponseEntity response = adapter.create(representation);
        Assert.assertEquals(201, response.getStatusCodeValue());
    }

    @Test
    public void get() throws Exception {
        Mockito.when(business.get(ArgumentMatchers.anyLong())).thenReturn(
                Game.builder()
                .id(1L)
                .title("Test")
                .description("Description")
                .releaseDate(new Date())
                .creationDate(new Date())
                .build());
        ResponseEntity response = adapter.get(1L);
        Assert.assertEquals(HttpStatus.FOUND, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertTrue(response.getBody().getClass().isAssignableFrom(RestAdapter.FullGameRepresentation.class));
    }

    @Test
    public void find() throws Exception {
        Game game = Game.builder()
                .id(1L)
                .title("Test")
                .description("Description")
                .releaseDate(new Date())
                .creationDate(new Date())
                .build();
        Mockito.when(business.find(ArgumentMatchers.anyString()))
                .thenReturn(Arrays.asList(game));
        Mockito.when(business.listAll())
                .thenReturn(Arrays.asList(game));

        ResponseEntity response = adapter.find("Test");
        Assert.assertFalse(((Collection)response.getBody()).isEmpty());

        response = adapter.find(null);
        Assert.assertFalse(((Collection)response.getBody()).isEmpty());

        response = adapter.find("");
        Assert.assertFalse(((Collection)response.getBody()).isEmpty());

    }

    @Test
    public void handleDomainException_GameAlreadyExistsException() {
        ResponseEntity response = adapter.handleDomainException(new GameExceptions.GameAlreadyExistsException());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void handleDomainException_GameNotCreatedException() {
        ResponseEntity response = adapter.handleDomainException(new GameExceptions.GameNotCreatedException("Runtime"));
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void handleDomainException_GameException() {
        ResponseEntity response = adapter.handleDomainException(new GameExceptions.GameNotFoundException());
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}