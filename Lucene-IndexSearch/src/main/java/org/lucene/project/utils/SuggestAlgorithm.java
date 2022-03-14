package org.lucene.project.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class SuggestAlgorithm {

    private int calculate(char[] source, char[] target) {
        int sourceLength = source.length;
        int targetLength = target.length;
        int[][] distanceMatrix = new int[sourceLength + 1][targetLength + 1];
        int i;
        for (i = 0; i < sourceLength + 1; i++)
            distanceMatrix[i][0] = i;
        for (i = 0; i < targetLength + 1; i++)
            distanceMatrix[0][i] = i;
        for (i = 1; i < sourceLength + 1; i++) {
            for (int j = 1; j < targetLength + 1; j++) {
                int cost = 0;
                if (source[i - 1] == target[j - 1]) {
                    cost = 0;
                } else {
                    cost = 2;
                }
                int insertionCost = distanceMatrix[i][j - 1] + 1;
                int deletionCost = distanceMatrix[i - 1][j] + 1;
                int substitutionCost = distanceMatrix[i - 1][j - 1] + cost;
                int distance = Math.min(insertionCost, Math.min(deletionCost, substitutionCost));
                if (i > 1 && j > 1 && source[i - 1] == target[j - 2] && source[i - 2] == target[j - 1])
                    distance = Math.min(distance, distanceMatrix[i - 2][j - 2] + cost);
                distanceMatrix[i][j] = distance;
            }
        }
        return distanceMatrix[sourceLength][targetLength];
    }

    public String[] suggest(Collection<String> dictionary, String word, int count) {
        Map<Integer, Set<String>> map = new TreeMap<>();
        for (String term : dictionary) {
            String target = term.trim();
            int eDistance = calculate(word.toCharArray(), target.toCharArray());
            Set<String> wordSet = map.get(Integer.valueOf(eDistance));
            if (wordSet == null) {
                wordSet = new HashSet<>();
                map.put(Integer.valueOf(eDistance), wordSet);
            }
            wordSet.add(target);
        }
        if (!map.isEmpty()) {
            Iterator<Integer> iterator = map.keySet().iterator();
            if (iterator.hasNext()) {
                Integer ed = iterator.next();
                Set<String> wordSet = map.get(ed);
                String[] finalWords = new String[wordSet.size()];
                wordSet.toArray(finalWords);
                if (finalWords.length > count) {
                    String[] sublist = Arrays.<String>copyOfRange(finalWords, 0, count);
                    return sublist;
                }
                return finalWords;
            }
        }
        return new String[] { null };
    }

}
