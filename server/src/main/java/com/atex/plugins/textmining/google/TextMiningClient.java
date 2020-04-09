package com.atex.plugins.textmining.google;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Pattern;

import com.atex.plugins.textmining.TextMiningConfig;
import com.atex.plugins.textmining.TextMining;
import com.atex.plugins.textmining.google.rest.GoogleLanguageClient;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.language.v1.*;
import com.google.common.base.Strings;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.PolicyCMServer;
import org.apache.commons.lang.StringUtils;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import com.polopoly.metadata.Annotation;
import com.polopoly.metadata.Dimension;
import com.polopoly.metadata.Entity;
import com.polopoly.metadata.Hit;
import com.polopoly.textmining.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextMiningClient implements TextMining {
    private static final int MAX_CONTENT_SIZE = 100000 * 1000;

    private static Logger log = LoggerFactory.getLogger(TextMiningClient.class);
    @Context
    private ServletContext servletContext;
    private GoogleLanguageClient client;
    private TextMiningConfig config;

    private Map<String,String> topicMap;

    private Map<String,String> entityMap;

    private float relevanceGate = 0.04F;

    private static final String DEFAULT_VALUE = "Add key here....";

    public TextMiningClient(TextMiningConfig config) {
        this.config = config;
        client = new GoogleLanguageClient();
        initialiseMapping();
    }

    private void initialiseMapping() {
        topicMap = config.getTopicMap();
        entityMap = config.getEntityMap();
    }

    @Override
    public boolean isConfigured() {
        String key = this.config.getApiKey();
        return (!StringUtils.isBlank(key)) ? !key.equalsIgnoreCase(DEFAULT_VALUE) : false;
    }

    public ServiceAccountCredentials getCredentials() throws IOException {
        InputStream stream = new ByteArrayInputStream(config.getApiKey().getBytes(StandardCharsets.UTF_8));
        return ServiceAccountCredentials.fromStream(stream);
    };

    @Override
    public Annotation analyzeText(Document document, PolicyCMServer cmServer) throws IOException {

        initialiseMapping();
        String text = StringUtils.left (document.body,99999);
        if (Strings.isNullOrEmpty(text) || text.length() > MAX_CONTENT_SIZE) {
            throw new IllegalArgumentException("Invalid content, either empty or exceeds maximum allowed size: " + MAX_CONTENT_SIZE);
        }
        Annotation result = new Annotation();
        result.setTaxonomyId("googleNL");
        result.setTaxonomyName("Google Natural Language");

        LanguageServiceSettings settings = LanguageServiceSettings.newBuilder().setCredentialsProvider(() -> getCredentials()).build();

        try (LanguageServiceClient language = LanguageServiceClient.create(settings)) {
            com.google.cloud.language.v1.Document doc = com.google.cloud.language.v1.Document.newBuilder().setContent(text).setType(com.google.cloud.language.v1.Document.Type.PLAIN_TEXT).build();
            AnnotateTextResponse annotateTextResponse = language.annotateText(doc, AnnotateTextRequest.Features.newBuilder().setExtractEntities(true).setClassifyText(true).build());
            for (ClassificationCategory classificationCategory : annotateTextResponse.getCategoriesList()) {
                Dimension dim = getDimensionFromTopic(classificationCategory, cmServer);
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

    private Dimension getDimensionFromEntity(com.google.cloud.language.v1.Entity entity, PolicyCMServer cmServer) throws CMException {
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

    private Dimension getDimensionFromTopic(ClassificationCategory category, PolicyCMServer cmServer) throws CMException {
        String dimensionId = config.getDimensionId();
        String dimensionName = config.getDimensionName();
        String topicName = category.getName();
        String topicRoot = topicName.split(Pattern.quote("/"))[1];
        String topicId = getTopicIdfromName (topicRoot);
        if (topicId != null) {
            String id;
            if (dimensionId != null && dimensionId.length() > 0) {
                id = dimensionId;
            } else {
                id = "dimension." + dimensionName;
            }
            Dimension dim = getDimensionFromId(id, cmServer);
            dim.addEntities(new Entity(topicId, topicRoot));
            return dim;
        }
        return null;
    }

    private String getTopicIdfromName(String entityName) {
        if (topicMap != null) return topicMap.get(entityName);
        return null;
    }
}
