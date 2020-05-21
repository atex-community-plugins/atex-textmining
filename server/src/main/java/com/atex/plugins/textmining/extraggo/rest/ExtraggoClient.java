package com.atex.plugins.textmining.extraggo.rest;

import com.atex.plugins.textmining.extraggo.parser.Document;

import java.io.IOException;

public interface ExtraggoClient {
	Document analyze(String content) throws IOException;
}