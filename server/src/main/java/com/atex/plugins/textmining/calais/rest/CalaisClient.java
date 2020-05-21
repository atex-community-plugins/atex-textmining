package com.atex.plugins.textmining.calais.rest;

import java.io.IOException;

public interface CalaisClient {

	  CalaisResponse analyze(String content) throws IOException;

}