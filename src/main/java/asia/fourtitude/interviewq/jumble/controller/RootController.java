package asia.fourtitude.interviewq.jumble.controller;

import java.time.ZonedDateTime;
import java.util.Collection;

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

import asia.fourtitude.interviewq.jumble.core.JumbleEngine;
import asia.fourtitude.interviewq.jumble.model.ExistsForm;
import asia.fourtitude.interviewq.jumble.model.PrefixForm;
import asia.fourtitude.interviewq.jumble.model.ScrambleForm;
import asia.fourtitude.interviewq.jumble.model.SearchForm;
import asia.fourtitude.interviewq.jumble.model.SubWordsForm;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/")
public class RootController {

    private static final Logger LOG = LoggerFactory.getLogger(RootController.class);

    @Autowired
    private JumbleEngine jumbleEngine;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("timeNow", ZonedDateTime.now());
        return "index";
    }

    @GetMapping("scramble")
    public String doGetScramble(Model model) {
        model.addAttribute("form", new ScrambleForm());
        return "scramble";
    }

    @PostMapping("scramble")
    public String doPostScramble(
            @Valid @ModelAttribute(name = "form") ScrambleForm form,
            BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "scramble";
        }

        String scrambledWord = jumbleEngine.scramble(form.getWord());
        form.setScramble(scrambledWord);

        return "scramble";
    }

    @GetMapping("palindrome")
    public String doGetPalindrome(Model model) {
        model.addAttribute("words", this.jumbleEngine.retrievePalindromeWords());
        return "palindrome";
    }

    @GetMapping("exists")
    public String doGetExists(Model model) {
        model.addAttribute("form", new ExistsForm());
        return "exists";
    }

    @PostMapping("exists")
    public String doPostExists(
            @Valid @ModelAttribute(name = "form") ExistsForm form,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "exists";
        }

        Boolean isWordExist = jumbleEngine.exists(form.getWord());
        form.setExists(isWordExist);

        return "exists";
    }

    @GetMapping("prefix")
    public String doGetPrefix(Model model) {
        model.addAttribute("form", new PrefixForm());
        return "prefix";
    }

    @PostMapping("prefix")
    public String doPostPrefix(
            @Valid @ModelAttribute(name = "form") PrefixForm form,
            BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "prefix";
        }

        Collection<String> words = jumbleEngine.wordsMatchingPrefix(form.getPrefix().trim());
        form.setWords(words);

        return "prefix";
    }

    @GetMapping("search")
    public String doGetSearch(Model model) {
        model.addAttribute("form", new SearchForm());
        return "search";
    }

    @PostMapping("search")
    public String doPostSearch(
            @Valid @ModelAttribute(name = "form") SearchForm form,
            BindingResult bindingResult, Model model) {

        boolean allEmpty = !isCharNotNullOrBlank(form.getStartChar())
                && !isCharNotNullOrBlank(form.getEndChar())
                && (form.getLength() == null || form.getLength().toString().isBlank());

        if (allEmpty) {
            bindingResult.rejectValue("startChar", "error.startChar", "Invalid startChar");
            bindingResult.rejectValue("endChar", "error.endChar", "Invalid endChar");
            bindingResult.rejectValue("length", "error.length", "Invalid length");
        }

        if (bindingResult.hasErrors()) {
            return "search";
        }

        Character startChar = isCharNotNullOrBlank(form.getStartChar()) ? form.getStartChar().charAt(0) : null;
        Character endChar = isCharNotNullOrBlank(form.getEndChar()) ? form.getEndChar().charAt(0) : null;

        Collection<String> words = jumbleEngine.searchWords(startChar, endChar, form.getLength());
        form.setWords(words);

        return "search";
    }

    private static boolean isCharNotNullOrBlank(String character) {
        return character != null && !character.isBlank();
    }

    @GetMapping("subWords")
    public String goGetSubWords(Model model) {
        model.addAttribute("form", new SubWordsForm());
        return "subWords";
    }

    @PostMapping("subWords")
    public String doPostSubWords(
            @Valid @ModelAttribute(name = "form") SubWordsForm form,
            BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "subWords";
        }

        Collection<String> subWords = jumbleEngine.generateSubWords(form.getWord().trim(), form.getMinLength());
        form.setWords(subWords);

        return "subWords";
    }

}
