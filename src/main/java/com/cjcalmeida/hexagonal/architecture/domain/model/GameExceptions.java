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

package com.cjcalmeida.hexagonal.architecture.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <b>Domain</b><br>
 * All Exceptions that can ocorrs in Domain side.
 * These exceptions has a message key to i18n.
 */
public class GameExceptions {

    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GameException extends Exception {
        private String messageKey;

        private GameException(String messageKey, String message) {
            super(message);
            this.messageKey = messageKey;
        }
    }

    /**
     * Occurs when tries create a new Game that already exists <br>
     * <b>message-key:</b> game.already.exists
     */
    public final static class GameAlreadyExistsException extends GameException {
        public GameAlreadyExistsException() {
            super("game.already.exists");
        }
    }

    /**
     * Occurs when Game cant be created. <br>
     * Message attribute has the main reason for exception <br>
     * <b>message-key:</b> game.not.created
     */
    @Getter
    public final static class GameNotCreatedException extends GameException {
        public GameNotCreatedException(String message) {
            super("game.not.created", message);
        }
    }

    /**
     * Occurs when Game cant be found <br>
     * <b>message-key:</b> game.not.found
     */
    @Getter
    public final static class GameNotFoundException extends GameException {
        public GameNotFoundException() {
            super("game.not.found");
        }
    }
}
