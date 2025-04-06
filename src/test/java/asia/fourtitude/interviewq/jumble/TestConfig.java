package asia.fourtitude.interviewq.jumble;

import asia.fourtitude.interviewq.jumble.service.GameService;
import asia.fourtitude.interviewq.jumble.service.impl.GameServiceImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import asia.fourtitude.interviewq.jumble.core.JumbleEngine;

@TestConfiguration
public class TestConfig {

    @Bean
    public JumbleEngine jumbleEngine() {
        return new JumbleEngine();
    }

    @Bean
    public GameService gameService() {
        return new GameServiceImpl();
    }

}
