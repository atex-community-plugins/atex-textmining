package com.atex.plugins.textmining.extraggo.parser.common;

public class Disambiguation {
    public Disambiguation(float score, String senseID) {
        this.score = score;
        this.senseID = senseID;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getSenseID() {
        return senseID;
    }

    public void setSenseID(String senseID) {
        this.senseID = senseID;
    }

    private float score = -1;
    private String senseID;
}