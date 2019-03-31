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

package com.cjcalmeida.hexagonal.architecture.configuration;

import com.cjcalmeida.hexagonal.architecture.Application;
import com.cjcalmeida.hexagonal.architecture.domain.port.IGameUseCase;
import com.cjcalmeida.hexagonal.architecture.adapters.primary.WsAdapter;
import com.cjcalmeida.hexagonal.architecture.adapters.primary.ws.GamePort;
import com.revinate.ws.spring.SDDocumentCollector;
import com.revinate.ws.spring.SpringService;
import com.sun.xml.ws.transport.http.servlet.SpringBinding;
import com.sun.xml.ws.transport.http.servlet.WSSpringServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.xml.namespace.QName;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

/**
 * Configurations to WS adapter
 * Only in 'ws' profile
 */
@Profile("ws")
@Configuration
@AutoConfigureAfter(AppConfiguration.class)
public class WSAppConfiguration {

    private static final Collection<Object> GAME_SERVICE_METADATA;
    private static final Object GAME_SERVICE_PRIMARY_WSDL;

    static {
        ClassLoader cl = Application.class.getClassLoader();
        Map<URL, Object> docs = SDDocumentCollector.collectDocs("wsdl", cl);
        GAME_SERVICE_METADATA = docs.values();
        GAME_SERVICE_PRIMARY_WSDL = docs.get(cl.getResource("wsdl/game.wsdl"));
    }

    /**
     * Bean that handle all requests for {@link GamePort}
     * @param port
     * @return
     */
    @Bean
    @Autowired
    public GamePort wsGameAdapter(IGameUseCase port){
        return new WsAdapter(port);
    }

    /**
     * Defines Servlet to handle /ws/* endpoint
     * @return
     */
    @Bean
    public ServletRegistrationBean wsSpringServlet(){
        return new ServletRegistrationBean(new WSSpringServlet(), "/ws/*");
    }

    /**
     * Create a {@link SpringService} to expose game.wsdl service
     * @param port
     * @return
     * @throws Exception
     */
    @Bean
    @Autowired
    public SpringService gameService(GamePort port) throws Exception{
        SpringService service = new SpringService();
        service.setBean(port);
        service.setServiceName(new QName("http://github.com/cjcalmeida/hexagonal-architecture/games/", "GamePortService"));
        service.setPortName(new QName("http://github.com/cjcalmeida/hexagonal-architecture/games/", "GamePortSoap"));
        service.setPrimaryWsdl(GAME_SERVICE_PRIMARY_WSDL);
        service.setMetadata(GAME_SERVICE_METADATA);
        return service;
    }

    /**
     * Defines a bean to Binding /ws/Game endpoint
     * @param gameService
     * @return
     * @throws Exception
     */
    @Bean
    public SpringBinding gameBinding(SpringService gameService) throws Exception{
        SpringBinding binding = new SpringBinding();
        binding.setUrl("/ws/Game");
        binding.setService(gameService.getObject());
        return binding;
    }
}
