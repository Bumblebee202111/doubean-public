package com.doubean.ford.data.vo;

public enum GroupRecommendationType {
    DAILY("日榜");

    private final String requestParam;

    GroupRecommendationType(String requestParam) {
        this.requestParam = requestParam;
    }

    public String getRequestParam() {
        return requestParam;
    }
}
