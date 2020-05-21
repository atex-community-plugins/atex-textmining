package com.atex.plugins.textmining.calais.rest;

public interface CalaisResponse {

	  CalaisObject getMeta();

	  CalaisObject getInfo();

	  Iterable<CalaisObject> getTopics();

	  Iterable<CalaisObject> getEntities();

	  Iterable<CalaisObject> getRelations();

	  Iterable<CalaisObject> getSocialTags();

	  String getPayload();

}
