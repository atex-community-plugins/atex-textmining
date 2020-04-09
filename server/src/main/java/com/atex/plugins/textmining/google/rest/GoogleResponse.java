package com.atex.plugins.textmining.google.rest;

import com.google.cloud.language.v1.ClassificationCategory;
import com.google.cloud.language.v1.Entity;
import java.util.List;
import java.util.Map;

public class GoogleResponse {
    private List<Entity> entities;
    private List<ClassificationCategory> classificationCategories;
    private Map<String, String> metadata;
    public List<Entity> getEntities(){ return entities; }
    public List<ClassificationCategory> getClassificationCategories(){ return classificationCategories; }
    public Map<String, String> getMetadata(){return metadata;}
    public void setEntities(List<Entity> entities){ this.entities = entities; }
    public void setClassificationCategories(List<ClassificationCategory> classificationCategories){ this.classificationCategories = classificationCategories; }
    public void setMetadata(Map<String, String> metadata){this.metadata = metadata;}
}
