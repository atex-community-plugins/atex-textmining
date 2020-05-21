package com.atex.plugins.textmining;

import java.util.Map;

public class TextMiningConfigBean implements TextMiningConfig {
	private String apiKey;
	private String provider;
	private String dimensionName;
	private String dimensionId;
	private Map<String, String> topicMap;
	private Map<String, String> entityMap;

	@Override
	public String getProviderName() {
		return provider;
	}

	@Override
	public void setProviderName(String provider) {
		this.provider = provider;
	}

	@Override
	public String getApiKey() {
		return apiKey;
	}

	@Override
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	@Override
	public String getDimensionName() {
		return dimensionName;
	}

	@Override
	public void setDimensionName(String dimensionName) {
		this.dimensionName = dimensionName;
	}

	@Override
	public Map<String, String> getTopicMap() {
		return topicMap;
	}

	@Override
	public void setTopicMap(Map<String, String> topicMap) {
		this.topicMap = topicMap;
	}

	@Override
	public Map<String, String> getEntityMap() {
		return entityMap;
	}

	@Override
	public void setEntityMap(Map<String, String> entityMap) {
		this.entityMap = entityMap;
	}

	@Override
	public String getDimensionId() {
		return dimensionId;
	}

	@Override
	public void setDimensionId(String dimensionId) {
		this.dimensionId = dimensionId;
	}
}