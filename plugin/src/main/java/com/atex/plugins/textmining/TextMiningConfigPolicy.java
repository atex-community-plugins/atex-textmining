package com.atex.plugins.textmining;

import java.util.Map;

import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.app.policy.SelectableSubFieldPolicy;
import com.polopoly.cm.app.policy.SingleValuePolicy;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.cm.policy.ContentPolicy;
import com.polopoly.cm.policy.Policy;
import com.polopoly.model.DescribesModelType;

import java.util.logging.Level;
import java.util.logging.Logger;

@DescribesModelType
public class TextMiningConfigPolicy extends ContentPolicy {

    private static final Logger LOG = Logger.getLogger(TextMiningConfigPolicy.class.getName());


    public static final String CONFIG_EXTERNAL_ID = "com.atex.plugins.textmining.TextMiningConfigHome";
    public static final String DEFAULT_CONFIG_EXTERNAL_ID = "com.atex.plugins.textmining.config.default";
    protected static final String PROVIDER = "provider";

    protected static final String DIMENSION_NAME = "dimensionName";
    protected static final String DIMENSION_ID = "dimensionId";

    private static final String DEFAULT_VALUE = "";
    private static final String DEFAULT_VALUE_POLICY_NAME = "useDefault";
    public static String useDefault = "false";

    @Override
    protected void initSelf() {
        super.initSelf();
    }

    @Override
    public void postCreateSelf() throws CMException {
        super.postCreateSelf();
        if (getChildPolicy(PROVIDER) == null) {
            setDefaultValues();
        }
    }

    @Override
    public void preCommitSelf() throws CMException {
        super.preCommitSelf();
        useDefault = getUseDefault();
        if ("true".equals(useDefault) || getChildPolicy(PROVIDER) == null) {
            setDefaultValues();
        }
    }

    /**
     * Set some sane defaults for the policy, e.g the provider policy.
     * @throws CMException If there is an error getting or setting any default content.
     */
    private void setDefaultValues() throws CMException {
        final ContentRead content = this.getCMServer().getContent(new ExternalContentId(DEFAULT_CONFIG_EXTERNAL_ID));
        String[] componentList = content.getComponentGroupNames();
        for (String componentName : componentList) {
            if (!"provider/calais/apiKey".equals(componentName) && !"provider/extraggo/apiKey".equals(componentName) && !"provider/google/apiKey".equals(componentName) && !"useDefault".equals(componentName) && !"provider".equals(componentName)) {
                String[] propertyNames = content.getComponentNames(componentName);
                for (String propertyName : propertyNames) {
                    String propertyValue = content.getComponent(componentName, propertyName);
                    this.setComponent(componentName, propertyName, propertyValue);
                }
            }
        }
    }

    public String getUseDefault() throws CMException {
        final SingleValuePolicy checkBoxPolicy = (SingleValuePolicy) getChildPolicy(DEFAULT_VALUE_POLICY_NAME);
        final String useDefaultData = checkBoxPolicy.getValue();
        return useDefaultData;
    }

    public void setUseDefault(final String value) throws CMException {
        final SingleValuePolicy checkBoxPolicy = (SingleValuePolicy) getChildPolicy(DEFAULT_VALUE_POLICY_NAME);
        checkBoxPolicy.setValue(value);
    }

    public ProviderConfig getProvider() {
        try {
            final SelectableSubFieldPolicy subFieldPolicy = (SelectableSubFieldPolicy) getChildPolicy(PROVIDER);
            if (null != subFieldPolicy) {
                final String selected = subFieldPolicy.getSelectedSubFieldName();
                    if (null != selected) {
                        final Policy selectedFieldPolicy = subFieldPolicy.getChildPolicy(selected);
                        if (selectedFieldPolicy instanceof ProviderConfig) {
                            return (ProviderConfig) selectedFieldPolicy;
                        } else {
                            throw new Exception("UNEXPECTED POLICY FOR subfield" + selected);
                        }
                    }
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Failed to get provider", e);
        }
        return null;
    }

    public String getProviderName() throws CMException {
        try {
            final SelectableSubFieldPolicy subFieldPolicy = (SelectableSubFieldPolicy) getChildPolicy(PROVIDER);
            if (null != subFieldPolicy) {
                final String selected = subFieldPolicy.getSelectedSubFieldName();
                return selected;
            }
        } catch (CMException e) {
            LOG.log(Level.WARNING, "Failed to get provider name", e);
        }
        return null;
    }


    public String getApiKey() throws CMException {
        String key = getProvider().getApiKey();
        if (DEFAULT_VALUE.equals(key)) {
            key = "";
        }
        return key;
    }

    public String getDimensionName() throws CMException {
        return ((SingleValuePolicy) getChildPolicy(DIMENSION_NAME)).getValue();
    }

    public String getDimensionId() throws CMException {
        SingleValuePolicy childPolicy = (SingleValuePolicy) getChildPolicy(DIMENSION_ID);
        if (childPolicy != null) {
            return childPolicy.getValue();
        } else {
            return "";
        }
    }

    public Map<String, String> getTopicMappings() throws CMException {
        return getProvider().getTopicMappings();
    }

    public Map<String, String> getEntityMappings() throws CMException {
        return getProvider().getEntityMappings();
    }

    public TextMiningConfigBean getConfig() throws CMException {

        TextMiningConfigBean bean = new TextMiningConfigBean();
        bean.setProviderName(getProviderName());
        bean.setDimensionName(getDimensionName());
        bean.setDimensionId(getDimensionId());
        bean.setApiKey(getApiKey());
        bean.setEntityMap(getEntityMappings());
        bean.setTopicMap(getTopicMappings());

        return bean;
    }
}
