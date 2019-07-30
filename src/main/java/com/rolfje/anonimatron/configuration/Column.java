package com.rolfje.anonimatron.configuration;

import java.util.Map;

public class Column {
    private String name;
    private String type;
    private int size = -1;
    private boolean shortlived;
    private Map<String, String> parameters;

    public Column() {

    }

    public Column(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public Column(String name, String type, int size) {
        this(name, type);
        this.size = size;
    }

    public Column(String name, String type, int size, boolean shortlived) {
        this(name, type);
        this.size = size;
        this.shortlived = shortlived;
    }

    public Column(String name, String type, int size, boolean shortlived, Map<String, String> parameters) {
        this(name, type, size, shortlived);
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isShortLived() {
        return shortlived;
    }

    public void setShortlived(boolean shortlived) {
        this.shortlived = shortlived;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
}
