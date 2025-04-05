package asia.fourtitude.interviewq.jumble.controller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import asia.fourtitude.interviewq.jumble.core.GameState;
import asia.fourtitude.interviewq.jumble.core.JumbleEngine;
import asia.fourtitude.interviewq.jumble.model.GameBoard;

import java.util.List;

@Controller
@RequestMapping(path = "/game")
@SessionAttributes("board")
public class GameWebController {

    private static final Logger LOG = LoggerFactory.getLogger(GameWebController.class);
    public static final int MIN_NO_OF_LETTERS = 3;
    public static final String VIEW_BOARD = "game/board";

    private final JumbleEngine jumbleEngine;

    @Autowired(required = true)
    public GameWebController(JumbleEngine jumbleEngine) {
        this.jumbleEngine = jumbleEngine;
    }

    @ModelAttribute("board")
    public GameBoard gameBoard() {
        /*
         * This method with "@ModelAttribute" annotation, is so that
         * Spring can create/initialize an attribute into session scope.
         */
        return new GameBoard();
    }

    private void scrambleWord(GameBoard board) {
        if (board.getState() != null) {
            String oldScramble = board.getState().getScramble();
            int num = 0;
            do {
                String scramble = this.jumbleEngine.scramble(board.getState().getOriginal());
                board.getState().setScramble(scramble);
                num += 1;
            } while (oldScramble.equals(board.getState().getScramble()) && num <= 10);
        }
    }

    @GetMapping(path = "/goodbye")
    public String goodbye(SessionStatus status) {
        status.setComplete();
        return VIEW_BOARD;
    }

    @GetMapping("/help")
    public String doGetHelp() {
        return "game/help";
    }

    @GetMapping("/new")
    public String doGetNew(@ModelAttribute(name = "board") GameBoard board) {
        GameState state = this.jumbleEngine.createGameState(6, 3);

        board.setState(state);
        board.setWord(StringUtils.EMPTY);

        return VIEW_BOARD;
    }

    @GetMapping("/play")
    public String doGetPlay(@ModelAttribute(name = "board") GameBoard board) {
        scrambleWord(board);

        return VIEW_BOARD;
    }

    @PostMapping("/play")
    public String doPostPlay(
            @ModelAttribute(name = "board") GameBoard board,
            BindingResult bindingResult, Model model) {

        if (isSessionExpired(board)) {
            LOG.warn("Session expired or board state missing.");
            return VIEW_BOARD;
        }

        scrambleWord(board);

        String word = sanitizeInput(board.getWord());
        LOG.debug("Player guessed word: {}", word);

        if (isInvalidLength(word, bindingResult)) return VIEW_BOARD;

        GameState state = board.getState();
        if (isRepeatedGuess(state, word, bindingResult)) return VIEW_BOARD;

        if (!state.updateGuessWord(word)) {
            bindingResult.rejectValue("word", "error.word", "Guessed incorrectly");
            return VIEW_BOARD;
        }

        if (isGameComplete(state)) {
            LOG.info("Player guessed all words.");
        }

        return VIEW_BOARD;
    }

    private static boolean isSessionExpired(GameBoard board) {
        return board == null || board.getState() == null;
    }

    private String sanitizeInput(String word) {
        return word == null ? "" : word.trim().toLowerCase();
    }

    private boolean isInvalidLength(String word, BindingResult bindingResult) {
        if (word.length() < MIN_NO_OF_LETTERS) {
            bindingResult.rejectValue("word", "error.word", "Must be at least 3 characters.");
            return true;
        }
        return false;
    }

    private boolean isRepeatedGuess(GameState state, String word, BindingResult bindingResult) {
        if (state.getGuessedWords().contains(word)) {
            bindingResult.rejectValue("word", "error.word", "You already guessed this word.");
            return true;
        }
        return false;
    }

    private boolean isGameComplete(GameState state) {
        return state.getSubWords().size() == state.getGuessedWords().size();
    }

}
