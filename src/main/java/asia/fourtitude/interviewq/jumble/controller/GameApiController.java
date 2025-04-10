package asia.fourtitude.interviewq.jumble.controller;

import asia.fourtitude.interviewq.jumble.exception.GameNotFoundException;
import asia.fourtitude.interviewq.jumble.model.GameGuessInput;
import asia.fourtitude.interviewq.jumble.model.GameGuessOutput;
import asia.fourtitude.interviewq.jumble.service.GameService;
import asia.fourtitude.interviewq.jumble.service.impl.GameServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Game API", description = "Guessing words game REST API endpoint.")
@RequestMapping(path = "/api/game")
public class GameApiController {

    private static final Logger LOG = LoggerFactory.getLogger(GameApiController.class);

    @Autowired
    private GameService gameService;

    @Operation(
            summary = "Creates new game board/state",
            description = "Creates a new game board/state and registered into game engine referenced by `id`. All subsequent operation/play is tied to `id`.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = GameGuessOutput.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Success",
                                                    description = "Created a new game/board and registered into system.",
                                                    value = "{\n" +
                                                            "  \"result\": \"Created new game.\",\n" +
                                                            "  \"id\": \"65e0d7a4-59bf-4065-beb1-3c2220d87e1e\",\n" +
                                                            "  \"original_word\": \"titans\",\n" +
                                                            "  \"scramble_word\": \"nisatt\",\n" +
                                                            "  \"total_words\": 29,\n" +
                                                            "  \"remaining_words\": 29,\n" +
                                                            "  \"guessed_words\": []\n" +
                                                            "}")}))})
    @GetMapping(value = "/new", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GameGuessOutput> newGame() {
        return ResponseEntity.ok(gameService.createNewGame());
    }

    @Operation(
            summary = "Submits word to play the game",
            description = "Submits a guessed `word`, along with `id` to play the game.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = GameGuessOutput.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Guessed Correctly First Time",
                                                    description = "Guessed correctly the first time.",
                                                    value = "{\n" +
                                                            "  \"result\": \"Guessed correctly.\",\n" +
                                                            "  \"id\": \"88b4278c-5141-42af-86e6-2a1d4cfa5f3b\",\n" +
                                                            "  \"original_word\": \"ranker\",\n" +
                                                            "  \"scramble_word\": \"nekarr\",\n" +
                                                            "  \"guess_word\": \"rank\",\n" +
                                                            "  \"total_words\": 15,\n" +
                                                            "  \"remaining_words\": 14,\n" +
                                                            "  \"guessed_words\": [\n" +
                                                            "    \"rank\"\n" +
                                                            "  ]\n" +
                                                            "}"),
                                            @ExampleObject(
                                                    name = "Guessed Correctly Subsequent",
                                                    description = "Guessed correctly with subsequent word.",
                                                    value = "{\n" +
                                                            "  \"result\": \"Guessed correctly.\",\n" +
                                                            "  \"id\": \"e23a268c-e4af-4123-a610-755e34ac201c\",\n" +
                                                            "  \"original_word\": \"burger\",\n" +
                                                            "  \"scramble_word\": \"rerugb\",\n" +
                                                            "  \"guess_word\": \"rug\",\n" +
                                                            "  \"total_words\": 15,\n" +
                                                            "  \"remaining_words\": 7,\n" +
                                                            "  \"guessed_words\": [\n" +
                                                            "    \"bug\",\n" +
                                                            "    \"bur\",\n" +
                                                            "    \"err\",\n" +
                                                            "    \"rug\",\n" +
                                                            "    \"burr\",\n" +
                                                            "    \"grub\",\n" +
                                                            "    \"rube\",\n" +
                                                            "    \"urge\"\n" +
                                                            "  ]\n" +
                                                            "}"),
                                            @ExampleObject(
                                                    name = "Guessed Incorrectly",
                                                    description = "Guessed with incorrect word.",
                                                    value = "{\n" +
                                                            "  \"result\": \"Guessed incorrectly.\",\n" +
                                                            "  \"id\": \"88b4278c-5141-42af-86e6-2a1d4cfa5f3b\",\n" +
                                                            "  \"original_word\": \"ranker\",\n" +
                                                            "  \"scramble_word\": \"rnraek\",\n" +
                                                            "  \"guess_word\": \"answer\",\n" +
                                                            "  \"total_words\": 15,\n" +
                                                            "  \"remaining_words\": 15,\n" +
                                                            "  \"guessed_words\": []\n" +
                                                            "}"),
                                            @ExampleObject(
                                                    name = "All Guessed",
                                                    description = "All words guessed.",
                                                    value = "{\n" +
                                                            "  \"result\": \"All words guessed.\",\n" +
                                                            "  \"id\": \"353ee769-a472-4704-a5f2-d525f181a03e\",\n" +
                                                            "  \"original_word\": \"gloomy\",\n" +
                                                            "  \"scramble_word\": \"gomlyo\",\n" +
                                                            "  \"guess_word\": \"moo\",\n" +
                                                            "  \"total_words\": 9,\n" +
                                                            "  \"remaining_words\": 0,\n" +
                                                            "  \"guessed_words\": [\n" +
                                                            "    \"goo\",\n" +
                                                            "    \"gym\",\n" +
                                                            "    \"log\",\n" +
                                                            "    \"loo\",\n" +
                                                            "    \"moo\",\n" +
                                                            "    \"glom\",\n" +
                                                            "    \"logo\",\n" +
                                                            "    \"loom\",\n" +
                                                            "    \"gloom\"\n" +
                                                            "  ]\n" +
                                                            "}")})),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = GameGuessOutput.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Invalid ID",
                                                    description = "The input `ID` is invalid.",
                                                    value = "{\n" +
                                                            "  \"result\": \"Invalid Game ID.\"\n" +
                                                            "}"),
                                            @ExampleObject(
                                                    name = "Record not found",
                                                    description = "The `ID` is correct format, but game board/state is not found in system.",
                                                    value = "{\n" +
                                                            "  \"result\": \"Game board/state not found.\"\n" +
                                                            "}")}))})
    @PostMapping(value = "/guess", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GameGuessOutput> playGame(
            @Parameter(
                    description = "Submits the `word` to guess.",
                    required = true,
                    schema = @Schema(implementation = GameGuessInput.class),
                    example = "{\n" +
                            "  \"id\": \"4579256c-326f-4169-9b56-6d1d1a2c11f0\",\n" +
                            "  \"word\": \"answer\"\n" +
                            "}")
            @RequestBody GameGuessInput input) {

        try {
            return ResponseEntity.ok(gameService.processGuess(input));
        } catch (GameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getOutput());
        }
    }
}
