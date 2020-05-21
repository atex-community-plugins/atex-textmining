package com.atex.plugins.textmining.extraggo.rest;

import com.atex.plugins.textmining.extraggo.exception.RemoteServerException;
import com.atex.plugins.textmining.extraggo.parser.Document;
import com.atex.plugins.textmining.extraggo.parser.Parser;
import com.google.common.base.Strings;
import com.polopoly.common.io.StreamUtil;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;

import java.io.IOException;
import java.util.Date;

public class ExtraggoRestClient implements ExtraggoClient {
	public ExtraggoRestClient(String key) {
		MultiThreadedHttpConnectionManager httpConnectionManager = new MultiThreadedHttpConnectionManager();
		HttpConnectionManagerParams params = new HttpConnectionManagerParams();
		params.setDefaultMaxConnectionsPerHost(25);
		params.setSoTimeout(60 * 1000);
		params.setConnectionTimeout(60 * 1000);
		httpConnectionManager.setParams(params);

		this.key = key;
		this.client = new HttpClient(httpConnectionManager);
	}

	public Document analyze(String content) throws IOException {
		if (Strings.isNullOrEmpty(content)) {
			throw new IllegalArgumentException("INVALID CONTENT: CONTENT IS EMPTY");
		}

		PostMethod postMethod = createPostMethod();
		postMethod.setParameter("text", content);
		postMethod.setParameter("key", this.key);

		try {
			int returnCode = client.executeMethod(postMethod);
			if (returnCode == HttpStatus.SC_OK) {
				String json = StreamUtil.readFullyToString(postMethod.getResponseBodyAsStream(), "UTF-8");
				return Parser.getInstance().parse(json);
			} else {
				throw new RemoteServerException("POST", String.valueOf(returnCode), new Date().toString(), "1.0.0-SNAPSHOT", "HTTP CODE: " +
						returnCode + ", BODY: " + postMethod.getResponseBodyAsString());
			}
		} finally {
			postMethod.releaseConnection();
		}
	}

	private PostMethod createPostMethod() {
		return new PostMethod(EXTRAGGO_ENDPOINT);
	}

	private String key;
	private HttpClient client;

	private static final String EXTRAGGO_ENDPOINT = "https://api.babelscape.com/extraggo";
}