package com.atex.plugins.textmining;

import java.io.IOException;
import java.util.Map;

import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.metadata.Annotation;
import com.polopoly.metadata.Dimension;
import com.polopoly.metadata.Entity;
import com.polopoly.textmining.Document;

public abstract class TextMiningClient implements TextMining {

    protected TextMiningConfig config;
    protected Map<String,String> topicMap;
    protected Map<String,String> entityMap;


    public TextMiningClient(TextMiningConfig config) {
        this.config = config;
        initialiseMapping();
    }

    public Dimension getDimensionFromId(String dimensionId, PolicyCMServer cmServer) throws CMException {
        String policyName = null;
        if (cmServer != null) {
            policyName = cmServer.getPolicy(new ExternalContentId(dimensionId)).getPolicyName();
        }
        if (policyName == null) {
            policyName = dimensionId;
            final String prefix = "dimension.";
            if (policyName.startsWith(prefix))
                policyName = policyName.substring(prefix.length());
        }
        return new Dimension(dimensionId, policyName, false);
    }

    protected Dimension getDimensionFromTopic(String categoryName, PolicyCMServer cmServer) throws CMException {
        String dimensionId = config.getDimensionId();
        String dimensionName = config.getDimensionName();
        String topicId = getTopicIdfromName (categoryName);
        if (topicId != null) {
            String id;
            if (dimensionId != null && dimensionId.length() > 0) {
                id = dimensionId;
            } else {
                id = "dimension." + dimensionName;
            }
            Dimension dim = getDimensionFromId(id, cmServer);
            dim.addEntities(new Entity(topicId, categoryName));
            return dim;
        }
        return null;
    }

    private String getTopicIdfromName(String entityName) {
        if (topicMap != null) return topicMap.get(entityName);
        return null;
    }

    protected void initialiseMapping() {
        topicMap = config.getTopicMap();
        entityMap = config.getEntityMap();
    }

    public abstract boolean isConfigured();

    public abstract Annotation analyzeText(Document document, PolicyCMServer server) throws IOException;

    @Override
    public abstract String getDescription();

}
