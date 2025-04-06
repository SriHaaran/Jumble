package asia.fourtitude.interviewq.jumble.service.impl;

import asia.fourtitude.interviewq.jumble.core.GameState;
import asia.fourtitude.interviewq.jumble.core.JumbleEngine;
import asia.fourtitude.interviewq.jumble.exception.GameNotFoundException;
import asia.fourtitude.interviewq.jumble.model.GameGuessInput;
import asia.fourtitude.interviewq.jumble.model.GameGuessModel;
import asia.fourtitude.interviewq.jumble.model.GameGuessOutput;
import asia.fourtitude.interviewq.jumble.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameServiceImpl implements GameService{

    private static final Logger LOG = LoggerFactory.getLogger(GameServiceImpl.class);

    @Autowired
    private JumbleEngine jumbleEngine;
    private final Map<String, GameGuessModel> gameBoards = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        System.out.println("GameServiceImpl Initialized");
    }

    @Override
    public GameGuessOutput createNewGame() {
        GameState gameState = this.jumbleEngine.createGameState(6, 3);

        GameGuessModel gameGuessModel = GameGuessModel.builder()
                .id(UUID.randomUUID().toString())
                .gameState(gameState)
                .createdAt(getTodayDate())
                .modifiedAt(getTodayDate())
                .build();

        gameBoards.put(gameGuessModel.getId(), gameGuessModel);

        return GameGuessOutput.builder()
                .id(gameGuessModel.getId())
                .result("Created new game.")
                .remainingWords(gameState.getSubWords().size())
                .originalWord(gameState.getOriginal())
                .scrambleWord(gameState.getScramble())
                .totalWords(gameState.getSubWords().size())
                .guessedWords(new ArrayList<>())
                .build();
    }

    @Override
    public GameGuessOutput processGuess(GameGuessInput input) {
        GameGuessOutput output = new GameGuessOutput();

        if (input.getId() == null || input.getId().isBlank()) {
            output.setResult("Invalid Game ID.");
            throw new GameNotFoundException("Invalid Game ID.", output);
        }

        GameGuessModel model = gameBoards.get(input.getId());
        if (!gameBoards.containsKey(input.getId())) {
            output.setResult("Game board/state not found.");
            throw new GameNotFoundException("Game board/state not found.", output);
        }

        GameState gameState = model.getGameState();
        String guessWord = sanitizeInput(input.getWord());

        gameState.setScramble(jumbleEngine.scramble(gameState.getScramble()));

        output.setId(model.getId());
        output.setOriginalWord(gameState.getOriginal());
        output.setScrambleWord(gameState.getScramble());
        output.setGuessWord(guessWord);
        output.setTotalWords(gameState.getSubWords().size());
        output.setRemainingWords(gameState.getSubWords().size() - gameState.getGuessedWords().size());

        if (gameState.updateGuessWord(guessWord)) {
            output.setResult("Guessed correctly.");
            output.setRemainingWords(output.getRemainingWords() - 1);
            output.setGuessedWords(gameState.getGuessedWords());
        } else {
            output.setResult("Guessed incorrectly.");
        }

        if (output.getRemainingWords() == 0) {
            output.setResult("All words guessed.");
        }

        model.setGameState(gameState);
        model.setModifiedAt(getTodayDate());
        gameBoards.put(model.getId(), model);

        return output;
    }

    private String sanitizeInput(String word) {
        return word == null ? null : word.trim().toLowerCase();
    }

    private Date getTodayDate() {
        return Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

}
