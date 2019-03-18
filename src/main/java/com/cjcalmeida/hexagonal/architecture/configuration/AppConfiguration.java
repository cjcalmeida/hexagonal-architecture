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

import com.cjcalmeida.hexagonal.architecture.domain.GameBusiness;
import com.cjcalmeida.hexagonal.architecture.domain.port.IGameRepositoryPort;
import com.cjcalmeida.hexagonal.architecture.domain.port.IGameUseCase;
import com.cjcalmeida.hexagonal.architecture.infraestructure.adapter.secondary.InMemoryAdapter;
import com.cjcalmeida.hexagonal.architecture.infraestructure.adapter.secondary.JdbcAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * All Global Configuration
 */
@Slf4j
@Configuration
public class AppConfiguration {

    /**
     * InMemory Repository Adapter Bean
     * Execute when no Bean of type {@link IGameRepositoryPort} is found
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(IGameRepositoryPort.class)
    public IGameRepositoryPort inmemoryBean(){
        log.info("Initializing InMemory Adapter");
        return new InMemoryAdapter();
    }

    /**
     * Defines bean of Application Business
     * @param infraestructure
     * @return
     */
    @Bean
    @Autowired
    public IGameUseCase businessBean(IGameRepositoryPort infraestructure){
        log.info("Initializing Business");
        return new GameBusiness(infraestructure);
    }

}
