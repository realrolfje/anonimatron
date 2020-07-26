package com.rolfje.anonimatron.synonyms;


/**
 * Represents a synonym for a {@link String}.
 */
public class StringSynonym implements Synonym {
    private String type;
    private String from;
    private String to;
    private boolean shortlived = false;

    public StringSynonym() {
    }

    public StringSynonym(String type, String from, String to, boolean shortlived) {
        this.type = type;
        this.from = from;
        this.to = to;
        this.shortlived = shortlived;
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

    public void setType(String type) {
        this.type = type;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
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
        return (obj != null) && (this.getClass() == obj.getClass()) && (this.hashCode() == obj.hashCode());
    }

    @Override
    public int hashCode() {
        return from.hashCode() + to.hashCode() + type.hashCode();
    }
}
