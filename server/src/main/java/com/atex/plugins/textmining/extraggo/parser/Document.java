package com.atex.plugins.textmining.extraggo.parser;

import com.atex.plugins.textmining.extraggo.parser.entity.Entity;
import com.atex.plugins.textmining.extraggo.parser.keyword.Keyword;

import java.util.List;

public class Document {
    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    public List<Keyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<Keyword> keywords) {
        this.keywords = keywords;
    }

    private List<Entity> entities;
    private List<Keyword> keywords;
}
