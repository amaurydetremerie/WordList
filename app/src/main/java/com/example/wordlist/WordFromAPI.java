package com.example.wordlist;

public class WordFromAPI extends Word{

    private String word;
    private Integer score;

    WordFromAPI(int score, String word){
        super(word);
        this.score = score;
    }

    public String getWord() {
        return word;
    }
    public Integer getScore() {
        return score;
    }
}