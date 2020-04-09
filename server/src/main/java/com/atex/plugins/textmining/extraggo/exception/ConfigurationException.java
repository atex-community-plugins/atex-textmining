package com.atex.plugins.textmining.extraggo.exception;


public class ConfigurationException extends Exception {
	public ConfigurationException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}

	private final String message;
}