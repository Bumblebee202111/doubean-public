package com.doubean.ford.api;

public enum SortByRequestParamType {
    NEW("new"),
    TOP("hot");

    private String param;

    SortByRequestParamType(String s) {
        this.param = s;
    }

    public String getParam() {
        return param;
    }
}
