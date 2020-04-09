package com.atex.plugins.textmining.extraggo.api;

import com.atex.plugins.textmining.TextMiningConfig;
import com.atex.plugins.textmining.TextMining;
import com.atex.plugins.textmining.extraggo.converter.Converter;
import com.atex.plugins.textmining.extraggo.converter.Dimension;
import com.atex.plugins.textmining.extraggo.parser.Document;
import com.atex.plugins.textmining.extraggo.rest.ExtraggoClient;
import com.atex.plugins.textmining.extraggo.rest.ExtraggoRestClient;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.metadata.Annotation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class TextMiningClient implements TextMining {

    private static final String DEFAULT_VALUE = "Add key here....";

    public TextMiningClient(TextMiningConfig config) {
    	this.config = config;
        client = new ExtraggoRestClient(this.config.getApiKey());
    }

    @Override
    public boolean isConfigured() {
        String key = this.config.getApiKey();
        return (!StringUtils.isBlank(key)) ? !key.equalsIgnoreCase(DEFAULT_VALUE) : false;
    }

    public Annotation analyzeText(com.polopoly.textmining.Document document, PolicyCMServer server) throws IOException {
        try {
            String text = StringUtils.left (document.body,3500);

            Document annotation = client.analyze(text);
            Converter converter = new Converter( server, this.config.getEntityMap(), this.config.getTopicMap(),
                    new Dimension(this.config.getDimensionName(), this.config.getDimensionId()));
            return converter.convert(annotation);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IOException(e);
        }
    }

    private ExtraggoClient client;
    private TextMiningConfig config;

    private static Logger log = LoggerFactory.getLogger(TextMiningClient.class);
}
