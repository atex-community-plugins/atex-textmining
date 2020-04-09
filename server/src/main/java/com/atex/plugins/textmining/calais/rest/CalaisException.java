package com.atex.plugins.textmining.calais.rest;

import java.io.IOException;

public class CalaisException extends IOException {

	private static final long serialVersionUID = 5217378786346376504L;

	private final String method;

	  private final String calaisRequestID;

	  private final String creationDate;

	  private final String calaisVersion;

	  private final String message;

	  public CalaisException(String method, String calaisRequestID,
	                         String creationDate, String calaisVersion,
	                         String message) {
	    this.method = method;
	    this.calaisRequestID = calaisRequestID;
	    this.creationDate = creationDate;
	    this.calaisVersion = calaisVersion;
	    this.message = message;
	  }

	  public String getMethod() {
	    return method;
	  }

	  public String getCalaisRequestID() {
	    return calaisRequestID;
	  }

	  public String getCreationDate() {
	    return creationDate;
	  }

	  public String getCalaisVersion() {
	    return calaisVersion;
	  }

	  @Override
	public String getMessage() {
	    return message;
	  }

	  public String getCalaisMessage() {
	    return String.format("Method=%s\ncalaisRequestID=%s\n"
	                         + "CreationDate=%s\nCalaisVersion=%s\n"
	                         + "Exception:\n\t%s", method, calaisRequestID,
	                         creationDate, calaisVersion, message);
	  }
	}
