package com.rolfje.anonimatron.synonyms;


/**
 * Represents a synonym for a {@link String}.
 * 
 */
public class StringSynonym implements Synonym {
	private String type;
	private String from;
	private String to;

	public StringSynonym() {
	}

	public StringSynonym(String type, String from, String to) {
		this.type = type;
		this.from = from;
		this.to = to;
	}

	public String getType() {
		return type;
	}

	public Object getFrom() {
		return from;
	}

	public Object getTo() {
		return to;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setTo(Object to) {
		this.to = (String) to;
	}

	public void setFrom(Object from) {
		this.from = (String) from;
	}

	@Override
	public boolean equals(Object obj) {
		return this.hashCode() == obj.hashCode();
	}

	@Override
	public int hashCode() {
		return from.hashCode() + to.hashCode() + type.hashCode();
	}
}
