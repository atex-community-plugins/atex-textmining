package com.atex.plugins.textmining.calais.rest;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.polopoly.common.io.StreamUtil;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public final class CalaisRestClient implements CalaisClient {

	private static final String CALAIS_URL = "https://api-eit.refinitiv.com/permid/calais";
	private static final String CONTENT_TYPE = "text/raw";
	private static final String OUTPUT_FORMAT = "application/json";
	private static final int MAX_CONTENT_SIZE = 100000 * 1000;


	public static String uniqueAccessKey;

	private HttpClient client;


	public CalaisRestClient(String uniqueAccessKey) {
		CalaisRestClient.uniqueAccessKey = uniqueAccessKey;
		MultiThreadedHttpConnectionManager httpConnectionManager;
		HttpConnectionManagerParams params;

		httpConnectionManager = new MultiThreadedHttpConnectionManager();
		params = new HttpConnectionManagerParams();
		params.setDefaultMaxConnectionsPerHost(25);
		params.setSoTimeout(60 * 1000);
		params.setConnectionTimeout(60 * 1000);
		httpConnectionManager.setParams(params);
		client = new HttpClient(httpConnectionManager);

	}


    private PostMethod createPostMethod() {
        PostMethod method = new PostMethod(CALAIS_URL);
        // Set mandatory parameters
        method.setRequestHeader("X-AG-Access-Token", uniqueAccessKey);
        // Set input content type
        method.setRequestHeader("Content-Type", CONTENT_TYPE);
        // Set response/output format
        method.setRequestHeader("outputformat", OUTPUT_FORMAT);
        return method;
    }


	private final ObjectMapper mapper = new ObjectMapper();


	@Override
	public CalaisResponse analyze(String content)
			throws IOException {
		if (Strings.isNullOrEmpty(content) || content.length() > MAX_CONTENT_SIZE) {
			throw new IllegalArgumentException("Invalid content, either empty or exceeds maximum allowed size: " + MAX_CONTENT_SIZE);
		}
		PostMethod postMethod = createPostMethod();
		RequestEntity requestEntity = new StringRequestEntity(content, null, null);
		postMethod.setRequestEntity(requestEntity);
		try {
            int returnCode = client.executeMethod(postMethod);
            if (returnCode == HttpStatus.SC_OK) {
            	String payload = StreamUtil.readFullyToString(postMethod.getResponseBodyAsStream(), "UTF-8");
        		try {
        			Map<String, Object> map = mapper.readValue(payload, Map.class);
        			return processResponse(map, payload);
        		} catch (JsonParseException e) {
        			throw parseError(payload);
        		}
            } else {
            	CalaisException exception = new CalaisException("POST", "" + returnCode, new Date().toString(), "1.0", "Server return code: " + returnCode + ", Body: " + postMethod.getResponseBodyAsString());
            	throw exception;
            }
        } finally {
        	postMethod.releaseConnection();
        }
	}

	public static CalaisResponse processResponse(Map<String, Object> map,
			final String payload) {
		Map<String, Object> doc = (Map<String, Object>) map.remove("doc");
		final CalaisObject info = extractObject(doc, "info");
		final CalaisObject meta = extractObject(doc, "meta");
		Multimap<String, CalaisObject> hierarchy = createHierarchy(map);
		final Iterable<CalaisObject> topics = Iterables
				.unmodifiableIterable(hierarchy.get("topics"));
		final Iterable<CalaisObject> entities = Iterables
				.unmodifiableIterable(hierarchy.get("entities"));
		final Iterable<CalaisObject> relations = Iterables
				.unmodifiableIterable(hierarchy.get("relations"));
		final Iterable<CalaisObject> socialTags = Iterables
				.unmodifiableIterable(hierarchy.get("socialTag"));
		return new CalaisResponse() {
			@Override
			public CalaisObject getInfo() {
				return info;
			}

			@Override
			public CalaisObject getMeta() {
				return meta;
			}

			@Override
			public Iterable<CalaisObject> getTopics() {
				return topics;
			}

			@Override
			public Iterable<CalaisObject> getEntities() {
				return entities;
			}

			@Override
			public Iterable<CalaisObject> getRelations() {
				return relations;
			}

			@Override
			public Iterable<CalaisObject> getSocialTags() {
				return socialTags;
			}

			@Override
			public String getPayload() {
				return payload;
			}
		};
	}

	private static CalaisObject extractObject(Map<String, Object> map,
			String key) {
		return new MapBasedCalaisObject((Map<String, Object>) map.remove(key));
	}

	private final static class MapBasedCalaisObject implements CalaisObject {

		private final Map<String, Object> map;

		private MapBasedCalaisObject(Map<String, Object> map) {
			this.map = ImmutableMap.copyOf(map);
		}

		@Override
		public String getField(String field) {
			Object o = map.get(field);
			return o == null ? null : o.toString();
		}

		@Override
		public String toString() {
			return map.toString();
		}

	}

	private static Multimap<String, CalaisObject> createHierarchy(
			Map<String, Object> root) {
		Multimap<String, CalaisObject> result = ArrayListMultimap.create();
		for (Map.Entry<String, Object> me : root.entrySet()) {
			Map<String, Object> map = (Map<String, Object>) me.getValue();
			map.put("_uri", me.getKey());
			String group = (String) map.get("_typeGroup");
			result.put(group, new MapBasedCalaisObject(map));
		}
		return result;
	}

	private CalaisException parseError(String error) {
		try {
			DocumentBuilderFactory domFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			StringReader reader = new StringReader(error);
			Document doc = builder.parse(new InputSource(reader));
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			String method = (String) xpath.evaluate("/Error/@Method", doc,
					XPathConstants.STRING);
			String calaisRequestID = (String) xpath.evaluate(
					"/Error/@calaisRequestID", doc, XPathConstants.STRING);
			String creationDate = (String) xpath.evaluate(
					"/Error/@CreationDate", doc, XPathConstants.STRING);
			String calaisVersion = (String) xpath.evaluate(
					"/Error/@CalaisVersion", doc, XPathConstants.STRING);
			String exception = (String) xpath.evaluate(
					"/Error/Exception/text()", doc, XPathConstants.STRING);
			return new CalaisException(method, calaisRequestID, creationDate,
					calaisVersion, exception);
		} catch (Exception e) {
			throw new RuntimeException("Unable to parse exception", e);
		}
	}

}
