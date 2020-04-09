package com.atex.plugins.textmining;

import java.util.Map;


import com.polopoly.cm.app.policy.SelectableSubFieldPolicy;
import com.polopoly.cm.app.policy.SingleValuePolicy;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.ContentPolicy;
import com.polopoly.cm.policy.Policy;
import com.polopoly.model.DescribesModelType;

@DescribesModelType
public class TextMiningConfigPolicy extends ContentPolicy{
    public static final String CONFIG_EXTERNAL_ID = "com.atex.plugins.textmining.TextMiningConfigHome";

    protected static final String PROVIDER = "provider";

    protected static final String DIMENSION_NAME = "dimensionName";
    protected static final String DIMENSION_ID = "dimensionId";

    @Override
    protected void initSelf() {
        super.initSelf();
    }

    public Provider getProvider(){
        try {
            final SelectableSubFieldPolicy subFieldPolicy = (SelectableSubFieldPolicy) getChildPolicy(PROVIDER);
            if (null != subFieldPolicy) {
                final String selected = subFieldPolicy.getSelectedSubFieldName();
                    if (null != selected) {
                        final Policy selectedFieldPolicy = subFieldPolicy.getChildPolicy(selected);
                        if (selectedFieldPolicy instanceof Provider) {
                            return (Provider) selectedFieldPolicy;
                        }
                    }
            }
        } catch (CMException e) {
//            LOGGER.log(Level.SEVERE, "cannot get " + name + " from " + getContentId().getContentIdString() + ": " + e.getMessage(), e);
        }
        return null;
    }


    public String getApiKey() throws CMException {
        return getProvider().getApiKey();
    }

    public String getDimensionName() throws CMException {
        return ((SingleValuePolicy) getChildPolicy(DIMENSION_NAME)).getValue();
    }

    public String getDimensionId() throws CMException {
        SingleValuePolicy childPolicy = (SingleValuePolicy) getChildPolicy(DIMENSION_ID);
        if (childPolicy != null)
            return childPolicy.getValue();
        else
            return "";
    }

    public Map<String, String> getTopicMappings() throws CMException {
        return getProvider().getTopicMappings();
    }

    public Map<String, String> getEntityMappings() throws CMException {
        return getProvider().getEntityMappings();
    }

    public TextMiningConfig getConfig() throws CMException {

        TextMiningConfig bean = new TextMiningConfigBean();
        bean.setApiKey(getApiKey());
        bean.setDimensionName(getDimensionName());
        bean.setDimensionId(getDimensionId());
        bean.setEntityMap(getEntityMappings());
        bean.setTopicMap(getTopicMappings());

        return bean;
    }
}
