package com.atex.plugins.textmining.extraggo.parser.keyword;

public class TopDomains {
    public TopDomains(String name, float score) {
        this.name = name;
        this.score = score;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private float score = -1;
    private String name;
}
