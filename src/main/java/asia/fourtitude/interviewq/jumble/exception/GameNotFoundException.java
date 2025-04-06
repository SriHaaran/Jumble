package asia.fourtitude.interviewq.jumble.exception;

import asia.fourtitude.interviewq.jumble.model.GameGuessOutput;

public class GameNotFoundException extends RuntimeException {
    private final GameGuessOutput output;

    public GameNotFoundException(String message, GameGuessOutput output) {
        super(message);
        this.output = output;
    }

    public GameGuessOutput getOutput() {
        return output;
    }
}