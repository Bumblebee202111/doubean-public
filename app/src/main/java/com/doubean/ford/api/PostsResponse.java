package com.doubean.ford.api;

import androidx.annotation.NonNull;

import com.doubean.ford.data.vo.PostItem;

import java.util.ArrayList;
import java.util.List;

public class PostsResponse extends ListResponse<PostItem> {
    public PostsResponse(int start, int count, int total, List<PostItem> items) {
        super(start, count, total, items);
    }

    @NonNull
    public List<String> getPostIds() {
        List<String> postIds = new ArrayList<>();
        for (PostItem item : getItems()) {
            postIds.add(item.id);
        }
        return postIds;
    }
}
