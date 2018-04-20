package com.rolfje.anonimatron.synonyms;

import com.rolfje.anonimatron.anonymizer.Hasher;

public class HashedFromSynonym implements Synonym {

	private String from;
	private Object to;
	private String type;

	public HashedFromSynonym() {
	}

	public HashedFromSynonym(Hasher hasher, Synonym synonym) {
		from = hasher.base64Hash(synonym.getFrom());
		to = synonym.getTo();
		type = synonym.getType();
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public Object getFrom() {
		return from;
	}

	@Override
	public Object getTo() {
		return to;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public void setTo(Object to) {
		this.to = to;
	}

	public void setType(String type) {
		this.type = type;
	}
}
