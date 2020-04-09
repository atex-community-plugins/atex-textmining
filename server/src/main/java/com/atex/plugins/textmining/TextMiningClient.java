package com.atex.plugins.textmining;

import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.metadata.Annotation;
import com.polopoly.textmining.Document;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

public class TextMiningClient implements TextMining {


    @Override
    public boolean isConfigured() {
        return false;
    }

    @Override
    public Annotation analyzeText(Document document, PolicyCMServer cmServer) throws IOException {
        return null;
    }
}
