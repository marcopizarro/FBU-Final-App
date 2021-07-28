package com.marcopizarro.podcast;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class KeywordExtractor {

    public static final String TAG = "KeywordExtractor";
    private static final List<String> stopWords = Arrays.asList("a", "about", "above", "after", "again", "against", "ain", "all", "am", "an", "and", "any", "are", "aren", "aren't", "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by", "can", "couldn", "couldn't", "d", "did", "didn", "didn't", "do", "does", "doesn", "doesn't", "doing", "don", "don't", "down", "during", "each", "few", "for", "from", "further", "had", "hadn", "hadn't", "has", "hasn", "hasn't", "have", "haven", "haven't", "having", "he", "her", "here", "hers", "herself", "him", "himself", "his", "how", "i", "if", "in", "into", "is", "isn", "isn't", "it", "it's", "its", "itself", "just", "ll", "m", "ma", "me", "mightn", "mightn't", "more", "most", "mustn", "mustn't", "my", "myself", "needn", "needn't", "no", "nor", "not", "now", "o", "of", "off", "on", "once", "only", "or", "other", "our", "ours", "ourselves", "out", "over", "own", "re", "s", "same", "shan", "shan't", "she", "she's", "should", "should've", "shouldn", "shouldn't", "so", "some", "such", "t", "than", "that", "that'll", "the", "their", "theirs", "them", "themselves", "then", "there", "these", "they", "this", "those", "through", "to", "too", "under", "until", "up", "ve", "very", "was", "wasn", "wasn't", "we", "were", "weren", "weren't", "what", "when", "where", "which", "while", "who", "whom", "why", "will", "with", "won", "won't", "wouldn", "wouldn't", "y", "you", "you'd", "you'll", "you're", "you've", "your", "yours", "yourself", "yourselves", "could", "he'd", "he'll", "he's", "here's", "how's", "i'd", "i'll", "i'm", "i've", "let's", "ought", "she'd", "she'll", "that's", "there's", "they'd", "they'll", "they're", "they've", "we'd", "we'll", "we're", "we've", "what's", "when's", "where's", "who's", "why's", "would");

    public KeywordExtractor() {
    }

    public static String getFrequecy(String str) {
        List<String> tokens = tokenize(str);
        List<String> cleanTokens = clean(tokens);
        Map<String, Integer> freq = analyze(cleanTokens);
        return getHighestFreq(freq);
    }

    private static List<String> tokenize(String str) {
        return Arrays.asList(str.split(" "));
    }

    private static List<String> clean(List<String> tokens) {
        List<String> cleanTokens = new ArrayList<String>();
        for (String s : tokens) {
            s = s.toLowerCase();
            if (!stopWords.contains(s)) {
                cleanTokens.add(s);
            }
        }
        return cleanTokens;
    }

    private static Map<String, Integer> analyze(List<String> cleanTokens) {
        Map freq = new LinkedHashMap<String, Integer>();
        for (String s : cleanTokens) {
            if (freq.containsKey(s)) {
                freq.put(s, (Integer) freq.get(s) + 1);
            } else {
                freq.put(s, 1);
            }
        }
        return freq;
    }

    private static String getHighestFreq(Map<String, Integer> freq){
        Map.Entry<String,Integer> maxEntry = null;

        for(Map.Entry<String,Integer> entry : freq.entrySet()) {
            if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
                maxEntry = entry;
            }
        }
        return maxEntry.getKey();
    }
}
