package com.atex.plugins.textmining;

import java.util.Map;

public interface TextMiningConfig {
    String getProviderName();

    void setProviderName(String providerName);

    String getApiKey();

    void setApiKey(String apiKey);

    String getDimensionName();

    void setDimensionName(String dimensionName);

    Map<String, String> getTopicMap();

    void setTopicMap(Map<String, String> topicMap);

    Map<String, String> getEntityMap();

    void setEntityMap(Map<String, String> entityMap);

    String getDimensionId();

    void setDimensionId(String dimensionId);
}
