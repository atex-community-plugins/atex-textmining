package com.atex.plugins.textmining.extraggo.parser.keyword;

import com.atex.plugins.textmining.extraggo.parser.common.Position;

public class TokenLevelDisambiguations {
    public TokenLevelDisambiguations(String senseID, float score, Position position) {
        this.senseID = senseID;
        this.score = score;
        this.position = position;
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

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    private String senseID;
    private float score = -1;
    private Position position;
}
