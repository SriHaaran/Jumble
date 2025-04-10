package asia.fourtitude.interviewq.jumble.model;

import javax.validation.constraints.NotBlank;
import java.util.Collection;

public class PrefixForm {

    @NotBlank(message = "must not be blank")
    private String prefix;

    private Collection<String> words;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Collection<String> getWords() {
        return words;
    }

    public void setWords(Collection<String> words) {
        this.words = words;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (prefix != null) {
            sb.append(sb.length() == 0 ? "" : ", ").append("prefix=[").append(prefix).append(']');
        }
        if (words != null) {
            sb.append(sb.length() == 0 ? "" : ", ").append("words=[").append(words).append(']');
        }
        return sb.toString();
    }

}
