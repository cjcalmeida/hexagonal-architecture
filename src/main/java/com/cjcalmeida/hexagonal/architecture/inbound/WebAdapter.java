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

package com.cjcalmeida.hexagonal.architecture.inbound;

import com.cjcalmeida.hexagonal.architecture.domain.GameEntity;
import com.cjcalmeida.hexagonal.architecture.domain.GameExceptions;
import com.cjcalmeida.hexagonal.architecture.domain.IGameInboundPort;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Map;

@Profile("web")
@Controller
public class WebAdapter {

    private IGameInboundPort business;
    private final String TEMPLATE_NAME = "template";

    public WebAdapter(IGameInboundPort business) {
        this.business = business;
    }

    @GetMapping("/")
    public String redirect(){
        return "redirect:/web/game";
    }

    @GetMapping("/web/game")
    public String home(@RequestParam(name = "q", required = false) String query, Model model){
        model.addAttribute("fragment", "fragments/list");
        try {
            Collection<GameEntity> results;
            if (query != null) {
                results = business.find(query);
                model.addAttribute("query", query);
            } else {
                results = business.listAll();
            }
            model.addAttribute("games", results);
        }catch (GameExceptions.GameNotFoundException e){
            model.addAttribute("error", e.getMessageKey());
        }
        return TEMPLATE_NAME;
    }

    @GetMapping("/web/game/new")
    public String initNewGame(Model model) {
        model.addAttribute("fragment", "fragments/new");
        model.addAttribute("view", false);
        model.addAttribute("data", new GameView());
        return TEMPLATE_NAME;
    }

    @PostMapping("/web/game/new")
    public String create(@RequestParam Map<String, String> game, Model model) {
        if(game == null || game.isEmpty()){
            model.addAttribute("error", "game.data.required");
        }
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            business.create(GameEntity.builder()
                    .title(game.get("title"))
                    .description(game.get("description"))
                    .releaseDate(format.parse(game.get("release")))
            .build());
            return redirect();
        }catch (ParseException e){
            model.addAttribute("error","game.data.format.invalid");
        }catch (GameExceptions.GameAlreadyExistsException e){
            model.addAttribute("error", e.getMessageKey());
        }catch (GameExceptions.GameNotCreatedException e){
            model.addAttribute("error", e.getMessageKey());
            model.addAttribute("errorDetail", e.getMessage());
        }

        //Error
        GameView view = new GameView();
        view.setTitle(game.get("title"));
        view.setDescription(game.get("description"));
        view.setRelease(game.get("release"));
        model.addAttribute("data", view);

        model.addAttribute("view", false);
        model.addAttribute("fragment", "fragments/new");
        return TEMPLATE_NAME;
    }

    @GetMapping("/web/game/{id}")
    public String get(@PathVariable("id") Long id, Model model) {
        try {
            GameEntity entity = business.get(id);
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            GameView view = new GameView();
            view.setRelease(format.format(entity.getReleaseDate()));
            view.setTitle(entity.getTitle());
            view.setDescription(entity.getDescription());
            view.setId(entity.getId());
            format.applyPattern("dd/MM/yyyy HH:mm:ss");
            view.setCreation(format.format(entity.getCreationDate()));

            model.addAttribute("fragment", "fragments/new");
            model.addAttribute("view", true);
            model.addAttribute("data", view);
        }catch (GameExceptions.GameNotFoundException e){

            model.addAttribute("error", e.getMessageKey());
            model.addAttribute("fragment", "fragments/list");
        }
        return TEMPLATE_NAME;
    }

    @Data
    @NoArgsConstructor
    public static class GameView {
        private Long id;
        private String title;
        private String description;
        private String release;
        private String creation;

    }
}