package com.doubean.ford.util;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LiveDataUtil {

    public static <T> LiveData<List<T>> zip(LiveData<T>... liveItems) {
        final List<T> zipped = new ArrayList<>(Arrays.asList((T[]) new Object[liveItems.length]));
        final MediatorLiveData<List<T>> mediator = new MediatorLiveData<>();
        for (int i = 0; i < liveItems.length; i++) {
            int finalI = i;
            mediator.addSource(liveItems[i], o -> {
                zipped.set(finalI, o);
                mediator.setValue(zipped);
            });
        }
        return mediator;
    }
}
