package com.doubean.ford.api;

import androidx.annotation.NonNull;

public enum SortByRequestParam {
    NEW("new"),
    TOP("hot");

    private final String s;

    SortByRequestParam(String s) {
        this.s = s;
    }

    @NonNull
    @Override
    public String toString() {
        return s;
    }
}
