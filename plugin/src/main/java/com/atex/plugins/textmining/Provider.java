package com.atex.plugins.textmining;

import java.util.Map;

public interface Provider {
    public Map<String, String> getTopicMappings();
    public Map<String, String> getEntityMappings();
    public String getApiKey();
}
