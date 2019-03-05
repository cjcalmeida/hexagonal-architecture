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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class JdbcAdapter implements IGameOutboundPort {

    private NamedParameterJdbcTemplate jdbc;
    private GameMapper mapper;

    @Autowired
    public JdbcAdapter(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
        mapper = new GameMapper();
    }

    @Override
    public void add(GameEntity entity) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("title", entity.getTitle());
        params.addValue("description", entity.getDescription());
        params.addValue("releaseDate", entity.getReleaseDate());
        params.addValue("creationDate", entity.getCreationDate(), Types.TIMESTAMP);

        jdbc.update("INSERT INTO Game(title, description, release_date, creation_date) " +
                "VALUES(:title, :description, :releaseDate, :creationDate)",
                params);
    }

    @Override
    public boolean exists(String title) {
        Map<String, Object> params = new HashMap<>();
        params.put("title", title);
        return !jdbc.query("SELECT * FROM Game WHERE title = :title", params, mapper).isEmpty();
    }

    @Override
    public boolean exists(Long id) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        return !jdbc.query("SELECT * FROM Game WHERE id = :id", params, mapper).isEmpty();
    }

    @Override
    public GameEntity get(Long id) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        return jdbc.query("SELECT * FROM Game WHERE id = :id", params, mapper).get(0);
    }

    @Override
    public Collection<GameEntity> findByTitleLike(String title) {
        Map<String, Object> params = new HashMap<>();
        params.put("title", "%"+title+"%");
        return jdbc.query("SELECT * FROM Game WHERE title LIKE :title", params, mapper);
    }

    @Override
    public Collection<GameEntity> listAll() {
        return jdbc.query("SELECT * FROM Game", mapper);
    }

    class GameMapper implements RowMapper<GameEntity> {
        @Override
        public GameEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            return GameEntity.builder()
                    .id(rs.getLong("id"))
                    .title(rs.getString("title"))
                    .releaseDate(rs.getDate("release_date"))
                    .description(rs.getString("description"))
                    .creationDate(rs.getTimestamp("creation_date"))
                    .build();
        }
    }
}
