package com.atex.plugins.textmining.extraggo.parser.keyword;

import com.atex.plugins.textmining.extraggo.parser.common.Disambiguation;
import com.atex.plugins.textmining.extraggo.parser.common.Position;

import java.util.List;

public class Keyword {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public void setPositions(List<Position> positions) {
        this.positions = positions;
    }

    public Disambiguation getDisambiguation() {
        return disambiguation;
    }

    public void setDisambiguation(Disambiguation disambiguation) {
        this.disambiguation = disambiguation;
    }

    public List<TopDomains> getTopDomains() {
        return topDomains;
    }

    public void setTopDomains(List<TopDomains> topDomains) {
        this.topDomains = topDomains;
    }

    public List<TokenLevelDisambiguations> getTokenLevelDisambiguations() {
        return tokenLevelDisambiguations;
    }

    public void setTokenLevelDisambiguations(List<TokenLevelDisambiguations> tokenLevelDisambiguations) {
        this.tokenLevelDisambiguations = tokenLevelDisambiguations;
    }

    private int id;
    private String name;
    private float score = -1;
    private List<Position> positions;
    private List<TopDomains> topDomains;
    private Disambiguation disambiguation;
    private List<TokenLevelDisambiguations> tokenLevelDisambiguations;
}
