package com.atex.plugins.textmining.extraggo.parser.entity;

import com.atex.plugins.textmining.extraggo.parser.common.Disambiguation;
import com.atex.plugins.textmining.extraggo.parser.common.Position;

import java.util.List;

public class Entity {
    public Entity() {
    }

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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Disambiguation getDisambiguation() {
        return disambiguation;
    }

    public void setDisambiguation(Disambiguation disambiguation) {
        this.disambiguation = disambiguation;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public void setPositions(List<Position> positions) {
        this.positions = positions;
    }

    private int id;
    private String name;
    private String label;
    private Disambiguation disambiguation;
    private List<Position> positions;
}