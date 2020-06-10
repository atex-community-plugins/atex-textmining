package com.atex.plugins.textmining;

import com.polopoly.cm.client.CMException;

import java.util.Map;

public interface ProviderConfig {
    public Map<String, String> getTopicMappings() throws CMException;
    public Map<String, String> getEntityMappings() throws CMException;
    public String getApiKey() throws CMException;
}
