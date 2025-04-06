package asia.fourtitude.interviewq.jumble.service;

import asia.fourtitude.interviewq.jumble.model.GameGuessInput;
import asia.fourtitude.interviewq.jumble.model.GameGuessOutput;

public interface GameService {
    GameGuessOutput createNewGame();
    GameGuessOutput processGuess(GameGuessInput input);
}
