package com.doubean.ford.ui.groups.groupSearch;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.doubean.ford.data.repository.GroupRepository;
import com.doubean.ford.data.vo.GroupItem;
import com.doubean.ford.data.vo.Resource;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class GroupSearchViewModel extends ViewModel {
    private final LiveData<Resource<List<GroupItem>>> results;
    private final MutableLiveData<String> query = new MutableLiveData<>();
    private final GroupRepository groupRepository;

    public GroupSearchViewModel(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
        results = Transformations.switchMap(query, search -> {
            if (search == null || search.trim().length() == 0) {
                return new MutableLiveData<>();
            } else {
                return groupRepository.search(search);
            }
        });
    }


    public LiveData<Resource<List<GroupItem>>> getResults() {
        return results;
    }

    public void setQuery(@NonNull String originalInput) {
        String input = originalInput.toLowerCase(Locale.getDefault()).trim();
        if (Objects.equals(input, query.getValue())) {
            return;
        }
        query.setValue(input);
    }
}
