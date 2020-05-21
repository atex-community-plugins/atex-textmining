package com.atex.plugins.textmining.google.rest;

import java.io.IOException;

public interface GoogleClient {

    GoogleResponse annotate(String content) throws IOException;

}
