package com.doubean.ford.ui.groups.groupSearch;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.doubean.ford.data.repository.GroupRepository;
import com.doubean.ford.data.vo.GroupItem;
import com.doubean.ford.data.vo.Resource;
import com.doubean.ford.ui.common.LoadMoreState;
import com.doubean.ford.ui.common.NextPageHandler;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class GroupSearchViewModel extends ViewModel {
    private final LiveData<Resource<List<GroupItem>>> results;
    private final MutableLiveData<String> query = new MutableLiveData<>();
    private final GroupRepository groupRepository;
    private final NextPageHandler nextPageHandler;
    private final MutableLiveData<Boolean> reloadTrigger = new MutableLiveData<>();

    public GroupSearchViewModel(GroupRepository groupRepository) {
        nextPageHandler = new NextPageHandler() {
            @Override
            public LiveData<Resource<Boolean>> loadNextPageFromRepo(String... params) {
                return groupRepository.searchNextPage(params[0]);
            }
        };
        this.groupRepository = groupRepository;
        results = Transformations.switchMap(reloadTrigger, i -> Transformations.switchMap(query, search -> {
            if (search == null || search.trim().length() == 0) {
                return new MutableLiveData<>();
            } else {
                return groupRepository.search(search);
            }
        }));
        refreshResults();
    }

    public void refreshResults() {
        reloadTrigger.setValue(true);
    }

    public LiveData<Resource<List<GroupItem>>> getResults() {
        return results;
    }

    public void setQuery(@NonNull String originalInput) {
        String input = originalInput.toLowerCase(Locale.getDefault()).trim();
        if (Objects.equals(input, query.getValue())) {
            return;
        }
        nextPageHandler.reset();
        query.setValue(input);
    }

    public LiveData<LoadMoreState> getLoadMoreStatus() {
        return nextPageHandler.getLoadMoreState();
    }

    public void loadNextPage() {
        String value = query.getValue();
        if (value == null || value.trim().length() == 0) {
            return;
        }
        nextPageHandler.loadNextPage(value);
    }

    void refresh() {
        if (query.getValue() != null) {
            query.setValue(query.getValue());
        }
    }

}
