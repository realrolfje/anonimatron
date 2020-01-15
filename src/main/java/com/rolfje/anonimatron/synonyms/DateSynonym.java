package com.rolfje.anonimatron.synonyms;


import java.sql.Date;

/**
 * Represents a synonym for a {@link java.sql.Date}.
 * 
 */
public class DateSynonym implements Synonym {
	private String type;
	private Date from;
	private Date to;
	private boolean shortlived = false;

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
		this.to = (Date) to;
	}

	public void setFrom(Object from) {
		this.from = (Date) from;
	}

	public void setShortlived(boolean shortlived) {
		this.shortlived = shortlived;
	}

	@Override
	public boolean isShortLived() {
		return shortlived;
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && this.hashCode() == obj.hashCode();
	}

	@Override
	public int hashCode() {
		return from.hashCode() + to.hashCode() + type.hashCode();
	}
}
