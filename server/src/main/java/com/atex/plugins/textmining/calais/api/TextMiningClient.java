package com.atex.plugins.textmining.calais.api;

import java.io.IOException;
import java.util.Map;

import com.atex.plugins.textmining.TextMiningConfig;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.PolicyCMServer;
import org.apache.commons.lang.StringUtils;

import com.atex.plugins.textmining.calais.rest.CalaisClient;
import com.atex.plugins.textmining.calais.rest.CalaisObject;
import com.atex.plugins.textmining.calais.rest.CalaisResponse;
import com.atex.plugins.textmining.calais.rest.CalaisRestClient;
import com.polopoly.metadata.Annotation;
import com.polopoly.metadata.Dimension;
import com.polopoly.metadata.Entity;
import com.polopoly.metadata.Hit;
import com.polopoly.textmining.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextMiningClient {

    private static Logger log = LoggerFactory.getLogger(TextMiningClient.class);

    private CalaisClient client;

    private TextMiningConfig config;

    private Map<String,String> topicMap;

    private Map<String,String> entityMap;

    private float relevanceGate = 0.04F;

    private static final String DEFAULT_VALUE = "Add key here....";


    public TextMiningClient(TextMiningConfig config) {
    	this.config = config;
        client = new CalaisRestClient(config.getApiKey());
        initialiseMapping();
    }

    private void initialiseMapping() {
    	topicMap = config.getTopicMap();
        entityMap = config.getEntityMap();
    }

    public boolean isConfigured() {
        String key = this.config.getApiKey();
        return (!StringUtils.isBlank(key)) ? !key.equalsIgnoreCase(DEFAULT_VALUE) : false;
    }

    public Annotation analyzeText(Document document, PolicyCMServer cmServer) throws IOException {

        initialiseMapping();
        String text = StringUtils.left (document.body,99999);
        CalaisResponse response = client.analyze(text);
        Annotation result = new Annotation();

        result.setTaxonomyId("opencalais");
        result.setTaxonomyName("Open Calais");

        try {
            for (CalaisObject entity : response.getEntities()) {

                Dimension dim = getDimensionFromEntity(entity, cmServer);
                if (dim != null) {
                    result.addHit(new Hit(dim));
                }
            }

            for (CalaisObject topic : response.getTopics()) {
                Dimension dim = getDimensionFromTopic(topic, cmServer);
                if (dim != null) {
                    result.addHit(new Hit(dim));
                }
            }
        } catch (CMException e) {
            log.warn("Unable to get hits from open calais",e);
        }
        return result;

}

    private Dimension getDimensionFromEntity(CalaisObject entity, PolicyCMServer cmServer) throws CMException {

        if (entityMap != null){
            String dimensionName = entityMap.get(entity.getField("_type"));
            if (dimensionName == null) {
                dimensionName = entityMap.get("*");
            }
            if (dimensionName.length() > 0) {
                String entityName = entity.getField("name");

                float relevance = Float.valueOf(entity.getField("relevance"));

                if (relevance > relevanceGate && StringUtils.isNotBlank(entityName)) {

                    Dimension dim = getDimensionFromId(dimensionName, cmServer);
                    dim.addEntities(new Entity(entityName));
                    return dim;
                }
            }
        }
        return null;
    }

    private Dimension getDimensionFromId(String dimensionId, PolicyCMServer cmServer) throws CMException {
        String policyName = cmServer.getPolicy(new ExternalContentId(dimensionId)).getPolicyName();
        if (policyName == null) {
            policyName = dimensionId;
            final String prefix = "dimension.";
            if (policyName.startsWith(prefix))
                policyName = policyName.substring(prefix.length());
        }
        return new Dimension(dimensionId, policyName, false);
    }

    private Dimension getDimensionFromTopic(CalaisObject entity, PolicyCMServer cmServer) throws CMException {
        String dimensionId = config.getDimensionId();
        String dimensionName = config.getDimensionName();
        String topicName = entity.getField("name");
        String topicId = getTopicIdfromName (topicName);

        if (topicId != null) {
            String id;
            if (dimensionId != null && dimensionId.length() > 0) {
                id = dimensionId;
            } else {
                id = "dimension." + dimensionName;
            }
            Dimension dim = getDimensionFromId(id, cmServer);
            dim.addEntities(new Entity(topicId, topicName));
            return dim;
        }
        return null;
    }

    private String getTopicIdfromName(String entityName) {
        if (topicMap != null) return topicMap.get(entityName);
        return null;
    }
}
