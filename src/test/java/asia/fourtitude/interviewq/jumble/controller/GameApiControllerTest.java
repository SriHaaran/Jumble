package asia.fourtitude.interviewq.jumble.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import asia.fourtitude.interviewq.jumble.model.GameBoard;
import asia.fourtitude.interviewq.jumble.model.GameGuessInput;
import asia.fourtitude.interviewq.jumble.model.GameGuessOutput;
import asia.fourtitude.interviewq.jumble.service.GameService;
import asia.fourtitude.interviewq.jumble.service.impl.GameServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import asia.fourtitude.interviewq.jumble.TestConfig;
import asia.fourtitude.interviewq.jumble.core.JumbleEngine;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@WebMvcTest(GameApiController.class)
@Import(TestConfig.class)
@ExtendWith(MockitoExtension.class)
class GameApiControllerTest {

    static final ObjectMapper OM = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @Autowired
    JumbleEngine jumbleEngine;

    @Autowired
    GameService gameService;

    /*
     * NOTE: Refer to "RootControllerTest.java", "GameWebControllerTest.java"
     * as reference. Search internet for resource/tutorial/help in implementing
     * the unit tests.
     *
     * Refer to "http://localhost:8080/swagger-ui/index.html" for REST API
     * documentation and perform testing.
     *
     * Refer to Postman collection ("interviewq-jumble.postman_collection.json")
     * for REST API documentation and perform testing.
     */

    @Test
    void whenCreateNewGame_thenSuccess() throws Exception {
        MvcResult result = this.mvc.perform(get("/api/game/new")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        GameGuessOutput response = OM.readValue(result.getResponse().getContentAsString(), GameGuessOutput.class);

        assertEquals(HttpStatus.OK.value(),  result.getResponse().getStatus());
        assertNotNull(response.getId());
        assertEquals("Created new game.", response.getResult());
        assertNotNull(response.getOriginalWord());
        assertNotNull(response.getScrambleWord());
        assertTrue(response.getGuessedWords().isEmpty());
        assertTrue(response.getTotalWords() > 0);
        assertTrue(response.getRemainingWords() > 0 && response.getRemainingWords() == response.getTotalWords());
    }

    @Test
    void givenMissingId_whenPlayGame_thenInvalidId() throws Exception {
        GameGuessInput request = GameGuessInput.builder().build();

        MvcResult result = this.mvc.perform(post("/api/game/guess")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OM.writeValueAsString(request)))
                .andExpect(status().is4xxClientError())
                .andReturn();

        GameGuessOutput response = OM.readValue(result.getResponse().getContentAsString(), GameGuessOutput.class);

        assertEquals(HttpStatus.NOT_FOUND.value(),  result.getResponse().getStatus());
        assertEquals("Invalid Game ID.", response.getResult());
    }

    @Test
    void givenMissingRecord_whenPlayGame_thenRecordNotFound() throws Exception {
        GameGuessInput request = GameGuessInput.builder().id(UUID.randomUUID().toString()).build();

        MvcResult result = this.mvc.perform(post("/api/game/guess")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OM.writeValueAsString(request)))
                .andExpect(status().is4xxClientError())
                .andReturn();

        GameGuessOutput response = OM.readValue(result.getResponse().getContentAsString(), GameGuessOutput.class);

        assertEquals(HttpStatus.NOT_FOUND.value(),  result.getResponse().getStatus());
        assertEquals("Game board/state not found.", response.getResult());
    }

    @Test
    void givenCreateNewGame_whenSubmitNullWord_thenGuessedIncorrectly() throws Exception {
        // Step 1: Create a new game
        MvcResult newGameResult = this.mvc.perform(get("/api/game/new")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        GameGuessOutput newGame = OM.readValue(newGameResult.getResponse().getContentAsString(), GameGuessOutput.class);

        // Step 2: Submit null word
        GameGuessInput input = GameGuessInput.builder().id(newGame.getId()).word(null).build();

        MvcResult guessResult = this.mvc.perform(post("/api/game/guess")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OM.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andReturn();

        GameGuessOutput response = OM.readValue(guessResult.getResponse().getContentAsString(), GameGuessOutput.class);

        assertEquals("Guessed incorrectly.", response.getResult());
        assertEquals(newGame.getId(), response.getId());
        assertEquals(newGame.getOriginalWord(), response.getOriginalWord());
        assertNotNull(response.getScrambleWord());
        assertNull(response.getGuessWord());
        assertEquals(newGame.getTotalWords(), response.getTotalWords());
        assertEquals(newGame.getRemainingWords(), response.getRemainingWords());
        assertTrue(response.getGuessedWords().isEmpty());
    }

    @Test
    void givenCreateNewGame_whenSubmitWrongWord_thenGuessedIncorrectly() throws Exception {
        // Step 1: Create a new game
        MvcResult newGameResult = this.mvc.perform(get("/api/game/new")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        GameGuessOutput newGame = OM.readValue(newGameResult.getResponse().getContentAsString(), GameGuessOutput.class);

        // Step 2: Submit wrong word
        String wrongWord = "wrongguess";
        GameGuessInput input = GameGuessInput.builder().id(newGame.getId()).word(wrongWord).build();

        MvcResult guessResult = this.mvc.perform(post("/api/game/guess")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OM.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andReturn();

        GameGuessOutput response = OM.readValue(guessResult.getResponse().getContentAsString(), GameGuessOutput.class);

        assertEquals("Guessed incorrectly.", response.getResult());
        assertEquals(newGame.getId(), response.getId());
        assertEquals(newGame.getOriginalWord(), response.getOriginalWord());
        assertNotNull(response.getScrambleWord());
        assertEquals(wrongWord, response.getGuessWord());
        assertEquals(newGame.getTotalWords(), response.getTotalWords());
        assertEquals(newGame.getRemainingWords(), response.getRemainingWords());
        assertTrue(response.getGuessedWords().isEmpty());
    }

    @Test
    void givenCreateNewGame_whenSubmitFirstCorrectWord_thenGuessedCorrectly() throws Exception {
        // Step 1: Create a new game
        MvcResult newGameResult = this.mvc.perform(get("/api/game/new")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        GameGuessOutput newGame = OM.readValue(newGameResult.getResponse().getContentAsString(), GameGuessOutput.class);
        Collection<String> answers = jumbleEngine.generateSubWords(newGame.getOriginalWord(), 3);
        String correctWord = answers.stream().findFirst().orElse(null);

        // Step 2: Submit correct word
        GameGuessInput input = GameGuessInput.builder().id(newGame.getId()).word(correctWord).build();

        MvcResult guessResult = this.mvc.perform(post("/api/game/guess")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OM.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andReturn();

        GameGuessOutput response = OM.readValue(guessResult.getResponse().getContentAsString(), GameGuessOutput.class);

        assertEquals("Guessed correctly.", response.getResult());
        assertEquals(newGame.getId(), response.getId());
        assertEquals(newGame.getOriginalWord(), response.getOriginalWord());
        assertNotNull(response.getScrambleWord());
        assertEquals(correctWord, response.getGuessWord());
        assertEquals(newGame.getTotalWords(), response.getTotalWords());
        assertEquals(newGame.getRemainingWords() - 1, response.getRemainingWords());
        assertTrue(response.getGuessedWords().contains(correctWord));
    }

    @Test
    void givenCreateNewGame_whenSubmitAllCorrectWord_thenAllGuessed() throws Exception {
        // Step 1: Create a new game
        MvcResult newGameResult = this.mvc.perform(get("/api/game/new")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        GameGuessOutput newGame = OM.readValue(newGameResult.getResponse().getContentAsString(), GameGuessOutput.class);
        ArrayList<String> answers = new ArrayList<>(jumbleEngine.generateSubWords(newGame.getOriginalWord(), 3));

        for (int i = 0; i < answers.size() - 1; i++) {
            GameGuessInput request = GameGuessInput.builder()
                    .id(newGame.getId())
                    .word(answers.get(i))
                    .build();

            this.mvc.perform(post("/api/game/guess")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(OM.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }

        // Step 3: Submit last word to complete
        String finalWord = answers.get(answers.size() - 1);
        GameGuessInput finalInput = GameGuessInput.builder().id(newGame.getId()).word(finalWord).build();

        MvcResult finalResult = this.mvc.perform(post("/api/game/guess")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OM.writeValueAsString(finalInput)))
                .andExpect(status().isOk())
                .andReturn();

        GameGuessOutput response = OM.readValue(finalResult.getResponse().getContentAsString(), GameGuessOutput.class);

        assertEquals("All words guessed.", response.getResult());
        assertEquals(newGame.getId(), response.getId());
        assertEquals(newGame.getOriginalWord(), response.getOriginalWord());
        assertNotNull(response.getScrambleWord());
        assertEquals(finalWord, response.getGuessWord());
        assertEquals(newGame.getTotalWords(), response.getTotalWords());
        assertEquals(0, response.getRemainingWords());
        assertTrue(response.getGuessedWords().contains(finalWord));
    }

}
