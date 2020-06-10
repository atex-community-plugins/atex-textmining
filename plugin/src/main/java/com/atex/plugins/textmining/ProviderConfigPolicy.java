package com.atex.plugins.textmining;

import com.polopoly.cm.app.policy.SingleValuePolicy;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.ContentPolicy;
import com.polopoly.siteengine.field.properties.ComponentMapProvider;

import java.util.Collections;
import java.util.Map;

public class ProviderConfigPolicy extends ContentPolicy implements ProviderConfig {
    protected static final String TOPIC_FIELDS = "topicMapping";
    protected static final String ENTITY_FIELDS = "entityMapping";
    protected static final String API_KEY = "apiKey";


    public Map<String, String> getTopicMappings() throws CMException {
        ComponentMapProvider componentMapPolicy = (ComponentMapProvider) getChildPolicy(TOPIC_FIELDS);
        return Collections.unmodifiableMap(componentMapPolicy.getComponentMap());
    }

    public Map<String, String> getEntityMappings() throws CMException {
        ComponentMapProvider componentMapPolicy = (ComponentMapProvider) getChildPolicy(ENTITY_FIELDS);
        return Collections.unmodifiableMap(componentMapPolicy.getComponentMap());
    }

    public String getApiKey() throws CMException {
        return ((SingleValuePolicy) getChildPolicy(API_KEY)).getValue();
    }

}
