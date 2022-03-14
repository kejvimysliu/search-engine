package org.lucene.project.service;

import org.lucene.project.utils.SuggestAlgorithm;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class NotSupportedServiceExecutor implements IServiceExecutor{

    @Override
    public void executeCommand(String command) {

        Set<String> words = new HashSet<>();
        words.add("query");
        words.add("index");

        String suggestedWord = "";
        try {
            String[] word = suggest(words, command, 5);
            for (String w : word)
                suggestedWord = w;
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Did you mean : " + suggestedWord + "?");
    }

    private String[] suggest(Collection<String> words, String wordForSuggestions, int count) throws IOException {
        SuggestAlgorithm suggestAlgorithm = new SuggestAlgorithm();
        String[] suggestions = suggestAlgorithm.suggest(words, wordForSuggestions, count);
        return suggestions;
    }

}
