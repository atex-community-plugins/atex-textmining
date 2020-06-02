package com.atex.plugins.textmining.google;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import com.atex.plugins.textmining.TextMining;
import com.atex.plugins.textmining.TextMiningClient;
import com.atex.plugins.textmining.TextMiningConfig;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.language.v1.AnnotateTextRequest;
import com.google.cloud.language.v1.AnnotateTextResponse;
import com.google.cloud.language.v1.ClassificationCategory;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.LanguageServiceSettings;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.metadata.Annotation;
import com.polopoly.metadata.Dimension;
import com.polopoly.metadata.Entity;
import com.polopoly.metadata.Hit;
import com.polopoly.textmining.Document;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoogleMiningClient extends TextMiningClient implements TextMining {
    private static final int MAX_CONTENT_SIZE = 99999;

    private static Logger log = LoggerFactory.getLogger(GoogleMiningClient.class);
    @Context
    private ServletContext servletContext;
    private float relevanceGate = 0.04F;

    private static final String DEFAULT_VALUE = "Add key here....";
    private final String description = "Google Natural Language Text Mining";

    public GoogleMiningClient(TextMiningConfig config) {
        super(config);
    }

    @Override
    public boolean isConfigured() {
        String key = this.config.getApiKey();
        return StringUtils.isNotBlank(key);
    }

    public ServiceAccountCredentials getCredentials() throws IOException {
        InputStream stream = new ByteArrayInputStream(config.getApiKey().getBytes(StandardCharsets.UTF_8));
        return ServiceAccountCredentials.fromStream(stream);
    };

    @Override
    public Annotation analyzeText(Document document, PolicyCMServer cmServer) throws IOException {

        String text = StringUtils.left (document.body,MAX_CONTENT_SIZE);

        Annotation result = new Annotation();
        result.setTaxonomyId("googleNL");
        result.setTaxonomyName("Google Natural Language");

        LanguageServiceSettings settings = LanguageServiceSettings.newBuilder().setCredentialsProvider(() -> getCredentials()).build();

        try (LanguageServiceClient language = LanguageServiceClient.create(settings)) {
            com.google.cloud.language.v1.Document doc = com.google.cloud.language.v1.Document.newBuilder().setContent(text).setType(com.google.cloud.language.v1.Document.Type.PLAIN_TEXT).build();
            AnnotateTextResponse annotateTextResponse = language.annotateText(doc, AnnotateTextRequest.Features.newBuilder().setExtractEntities(true).setClassifyText(true).build());
            for (ClassificationCategory classificationCategory : annotateTextResponse.getCategoriesList()) {
                String topicName = classificationCategory.getName();
                String mainTopic = topicName.split(Pattern.quote("/"))[1];
                Dimension dim = getDimensionFromTopic(mainTopic, cmServer);
                if (dim != null) {
                    result.addHit(new Hit(dim));
                }

            }
            for (com.google.cloud.language.v1.Entity entity : annotateTextResponse.getEntitiesList()) {
                Dimension dim = getDimensionFromEntity(entity, cmServer);
                if (dim != null) {
                    result.addHit(new Hit(dim));
                }
            }
        } catch (IOException | CMException e) {
            log.warn("Unable to get hits from google",e);
        }
        return result;
    }

    protected Dimension getDimensionFromEntity(com.google.cloud.language.v1.Entity entity, PolicyCMServer cmServer) throws CMException {
        if (entityMap != null){
            String dimensionName = entityMap.get(entity.getType().toString());
            if (dimensionName == null) {
                dimensionName = entityMap.get("*");
            }
            if (dimensionName.length() > 0) {
                String entityName = entity.getName();
                float relevance = entity.getSalience();
                if (relevance > relevanceGate && StringUtils.isNotBlank(entityName)) {
                    Dimension dim = getDimensionFromId(dimensionName, cmServer);
                    dim.addEntities(new Entity(entityName));
                    return dim;
                }
            }
        }
        return null;
    }

    @Override
    public String getDescription() {
        return description;
    }

}
