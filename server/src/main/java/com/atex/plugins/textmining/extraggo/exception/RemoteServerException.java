package com.atex.plugins.textmining.extraggo.exception;

import java.io.IOException;

public class RemoteServerException extends IOException {
	public RemoteServerException(String method, String id, String creationDate, String version, String message) {
		this.method = method;
		this.id = id;
		this.creationDate = creationDate;
		this.version = version;
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}

	public String getMethod() {
		return method;
	}

	public String getId() {
		return id;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public String getVersion() {
		return version;
	}

	public String getErrorMessage() {
		return String.format("Method=%s\nID=%s\n" + "CreationDate=%s\nVersion=%s\n" + "Exception:\n\t%s", method, id,
				creationDate, version, message);
	}

	private static final long serialVersionUID = 5217378786346376504L;

	private final String method;
	private final String id;
	private final String creationDate;
	private final String version;
	private final String message;
}