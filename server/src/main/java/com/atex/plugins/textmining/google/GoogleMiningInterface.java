package com.atex.plugins.textmining.google;

import com.atex.plugins.textmining.TextMiningInterface;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.metadata.Annotation;
import com.polopoly.textmining.Document;

import java.io.IOException;

public interface GoogleMiningInterface extends TextMiningInterface {
    boolean isConfigured();

    Annotation analyzeText(Document document, PolicyCMServer cmServer) throws IOException;
}
