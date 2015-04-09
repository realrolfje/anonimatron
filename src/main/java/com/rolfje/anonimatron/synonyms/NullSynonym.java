package com.rolfje.anonimatron.synonyms;


/**
 * Represents a synonym for a null value of any type.
 * 
 */
public class NullSynonym implements Synonym {
	private String type;
	
	
	public NullSynonym(String type) {
		this.type=type;
	}
	
	public String getType() {
		return type;
	}

	public Object getFrom() {
		return null;
	}

	public Object getTo() {
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		return this.hashCode() == obj.hashCode();
	}

	@Override
	public int hashCode() {
		return type.hashCode();
	}
}
