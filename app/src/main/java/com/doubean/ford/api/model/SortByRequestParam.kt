package com.doubean.ford.api.model

enum class SortByRequestParam(private val s: String) {
    NEW("new"), TOP("hot");

    override fun toString(): String {
        return s
    }
}