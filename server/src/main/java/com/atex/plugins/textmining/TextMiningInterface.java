package com.atex.plugins.textmining;

import java.io.IOException;

import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.metadata.Annotation;
import com.polopoly.textmining.Document;

public interface TextMiningInterface {
    boolean isConfigured();
    Annotation analyzeText(Document document, PolicyCMServer cmServer) throws IOException;
}
