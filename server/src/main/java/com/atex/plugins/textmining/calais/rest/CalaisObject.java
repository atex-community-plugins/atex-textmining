package com.atex.plugins.textmining.calais.rest;

public interface CalaisObject {

	  /**
	   * @param field the field name
	   * @return field value toString(), null if not exists.
	   */
	  String getField(String field);

}