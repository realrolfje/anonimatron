package com.rolfje.anonimatron.synonyms;

/**
 * Represents a synonym for a {@link Number}.
 */
public class NumberSynonym implements Synonym {
	private String type;
	private Number from;
	private Number to;
	private boolean shortLived = false;

	public NumberSynonym() {
	}

	public NumberSynonym(String type, Number from, Number to, boolean shortLived) {
		this.type = type;
		this.from = from;
		this.to = to;
		this.shortLived = shortLived;
	}

	@Override
	public boolean isShortLived() {
		return shortLived;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public Number getFrom() {
		return from;
	}

	@Override
	public Number getTo() {
		return to;
	}


	public void setType(String type) {
		this.type = type;
	}

	public void setTo(Number to) {
		this.to = to;
	}

	public void setFrom(Number from) {
		this.from = from;
	}

	public void setShortLived(boolean shortLived) {
		this.shortLived = shortLived;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		return this.hashCode() == obj.hashCode();
	}

	@Override
	public int hashCode() {
		return from.hashCode() + to.hashCode() + type.hashCode();
	}
}
