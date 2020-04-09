package com.atex.plugins.textmining.extraggo.converter;

import com.atex.plugins.textmining.extraggo.exception.ConfigurationException;
import com.atex.plugins.textmining.extraggo.parser.Document;
import com.atex.plugins.textmining.extraggo.parser.entity.Entity;
import com.atex.plugins.textmining.extraggo.parser.keyword.Keyword;
import com.atex.plugins.textmining.extraggo.parser.keyword.TopDomains;
import com.couchbase.client.deps.io.netty.util.internal.StringUtil;
import com.google.common.base.Strings;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.common.collections.CollectionUtil;
import com.polopoly.metadata.Annotation;
import com.polopoly.metadata.Dimension;
import com.polopoly.metadata.Hit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class Converter {
    public Converter(PolicyCMServer server, Map<String, String> entities, Map<String, String> topics,
                     com.atex.plugins.textmining.extraggo.converter.Dimension dimension) throws ConfigurationException {
        if (entities == null || entities.size() == 0) {
            throw new ConfigurationException("NO ENTITY MAP DEFINED");
        }

        if (topics == null || topics.size() == 0) {
            throw new ConfigurationException("NO TOPICS MAP DEFINED");
        }

        this.server = server;
        this.entities = entities;
        this.topics = topics;
        this.dimension = dimension;
    }

    public Annotation convert(Document extraggo) {
        Annotation annotation = new Annotation();
        annotation.setTaxonomyId("extraggo");
        annotation.setTaxonomyName("Babelscape Extraggo");

        for (Entity entity : extraggo.getEntities()) {
            Dimension dim = getDimensionFromEntity(entity);
            if (dim != null) {
                annotation.addHit(new Hit(dim));
            }
        }

        for (Keyword keyword : extraggo.getKeywords()) {
            Dimension dim = getDimensionFromTopic(keyword);
            if (dim != null) {
                annotation.addHit(new Hit(dim));
            }
        }

        return annotation;
    }

    private Dimension getDimensionFromTopic(Keyword keyword) {
        String dimensionId = (this.dimension != null) ? this.dimension.getId() : null;
        String dimensionName = (this.dimension != null) ? this.dimension.getName() : null;

        String topicId = null;
        String topicName = null;
        if(!CollectionUtil.isNullOrEmpty(keyword.getTopDomains())) {
           for(TopDomains domain:keyword.getTopDomains()) {
               topicName = domain.getName();
               topicId = getTopicIdfromName(topicName);

               if(!StringUtil.isNullOrEmpty(topicId)) {
                   break;
               }
           }
        } else {
            topicName = keyword.getName();
            topicId = getTopicIdfromName(topicName);
        }

        Dimension dimension = null;

        try {
            if (topicId != null) {
                String id;
                if (dimensionId != null && dimensionId.length() > 0) {
                    id = dimensionId;
                } else {
                    id = "dimension." + dimensionName;
                }

                dimension = getDimensionFromId(id, this.server);
                dimension.addEntities(new com.polopoly.metadata.Entity(topicId, topicName));
            }
        } catch (CMException e) {
            log.error(e.getMessage());
        }

        return dimension;
    }

    private Dimension getDimensionFromEntity(Entity entity) {
        Dimension dimension = null;

        try {
            String dimensionName = this.entities.containsKey(entity.getLabel()) ? this.entities.get(entity.getLabel()) : this.entities.get("*");

            if (!StringUtil.isNullOrEmpty(dimensionName)) {
                if (getScore(entity) > RELEVANCE && !Strings.isNullOrEmpty(entity.getName())) {
                    dimension = getDimensionFromId(dimensionName, this.server);
                    dimension.addEntities(new com.polopoly.metadata.Entity(entity.getName()));
                } else {
                    log.debug("ENTITY SCORE TOO LOW [ " + getScore(entity) + " ]");
                }
            } else {
                log.debug("DIMENSION ID IS NULL FOR LABEL [ " + entity.getLabel() + " ]");
            }

        } catch (CMException e) {
            log.error(e.getMessage());
        }

        return dimension;
    }

    private String getTopicIdfromName(String entityName) {
        String topic = null;

        if (topics != null) {
            topic = topics.get(entityName);
        }

        return topic;
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

    private float getScore(Entity entity) {
        float score = -1;

        if (entity != null && entity.getDisambiguation() != null) {
            score = entity.getDisambiguation().getScore();
        }

        return score;
    }

    private PolicyCMServer server;
    private Map<String, String> entities;
    private Map<String, String> topics;
    private com.atex.plugins.textmining.extraggo.converter.Dimension dimension;

    private static final float RELEVANCE = -2.0F;

    private static Logger log = LoggerFactory.getLogger(Converter.class);
}