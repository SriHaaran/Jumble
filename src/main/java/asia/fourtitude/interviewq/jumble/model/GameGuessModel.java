package asia.fourtitude.interviewq.jumble.model;

import java.util.Date;

import asia.fourtitude.interviewq.jumble.core.GameState;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
public class GameGuessModel {

    private String id;

    private Date createdAt;

    private Date modifiedAt;

    private GameState gameState;

    public void setId(String id) {
        this.id = id;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (id != null) {
            sb.append(sb.length() == 0 ? "" : ", ").append("id=[").append(id).append(']');
        }
        if (createdAt != null) {
            sb.append(sb.length() == 0 ? "" : ", ").append("createdAt=[").append(createdAt.toInstant()).append(']');
        }
        if (modifiedAt != null) {
            sb.append(sb.length() == 0 ? "" : ", ").append("modifiedAt=[").append(modifiedAt.toInstant()).append(']');
        }
        if (gameState != null) {
            sb.append(sb.length() == 0 ? "" : ", ").append("gameState=[").append(gameState).append(']');
        }
        return sb.toString();
    }

}
