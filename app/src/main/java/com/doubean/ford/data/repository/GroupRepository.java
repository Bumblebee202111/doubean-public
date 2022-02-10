package com.doubean.ford.data.repository;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.doubean.ford.api.DoubanService;
import com.doubean.ford.data.FavGroup;
import com.doubean.ford.data.Group;
import com.doubean.ford.data.GroupSearchResponse;
import com.doubean.ford.data.GroupSearchResult;
import com.doubean.ford.data.GroupTopic;
import com.doubean.ford.data.GroupTopicTag;
import com.doubean.ford.data.SearchResultItem;
import com.doubean.ford.data.TopicsResponse;
import com.doubean.ford.data.db.AppDatabase;
import com.doubean.ford.data.db.GroupDao;
import com.doubean.ford.util.AppExecutors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupRepository {
    private static GroupRepository instance;
    private final AppExecutors appExecutors;
    private final GroupDao groupDao;
    private final DoubanService doubanService;
    private final AppDatabase appDatabase;

    private GroupRepository(AppExecutors appExecutors, AppDatabase appDatabase, DoubanService doubanService) {
        this.appExecutors = appExecutors;
        this.groupDao = appDatabase.getGroupDao();
        this.doubanService = doubanService;
        this.appDatabase = appDatabase;

    }

    public static GroupRepository getInstance(AppExecutors appExecutors, AppDatabase appDatabase, DoubanService doubanService) {
        if (instance == null) {
            synchronized (GroupRepository.class) {
                if (instance == null) {
                    instance = new GroupRepository(appExecutors, appDatabase, doubanService);
                }
            }
        }
        return instance;
    }

    public void addFavGroup(@NonNull FavGroup favGroup) {
        appExecutors.diskIO().execute(() ->
                groupDao.insertFavoriteGroup(favGroup));
    }

    public LiveData<List<FavGroup>> getFavGroups() {
        return groupDao.getAllFavGroupIds();
    }

    public LiveData<List<Group>> getGroups(@NonNull List<String> groupIds) {
        MediatorLiveData<List<Group>> groupsLiveData = new MediatorLiveData<>();
        List<Group> groups = new ArrayList<>(Arrays.asList(new Group[groupIds.size()]));
        if (groupIds.size() > 0) {
            MutableLiveData<Boolean> fetching = new MutableLiveData<>(false);
            final int[] i = {0};
            groupsLiveData.addSource(fetching, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    if (!aBoolean) {
                        LiveData<Group> groupLiveData = getGroup(groupIds.get(i[0]));
                        fetching.setValue(true);
                        groupsLiveData.addSource(groupLiveData, new Observer<Group>() {
                            @Override
                            public void onChanged(Group group) {
                                if (group != null) {
                                    groups.set(i[0]++, group);
                                    groupsLiveData.removeSource(groupLiveData);
                                    if (i[0] == groups.size()) {
                                        groupsLiveData.setValue(groups);
                                        groupsLiveData.removeSource(fetching);
                                    } else
                                        fetching.setValue(false);
                                }

                            }
                        });
                    }

                }
            });
        }

        return groupsLiveData;
    }

    public LiveData<Group> getGroup(String groupId) {
        return new NetworkBoundResource<Group, Group>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull Group group) {
                group.groupTabs.add(0, GroupTopicTag.DEFAULT);
                groupDao.insertGroup(group);
            }

            @Override
            protected boolean shouldFetch(@Nullable Group data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<Group> loadFromDb() {
                return groupDao.getGroup(groupId);
            }

            @NonNull
            @Override
            protected LiveData<Group> createCall() {
                return doubanService.getGroup(groupId, 1);
            }
        }.asLiveData();
    }

    public LiveData<List<Group>> search(String query) {
        return new NetworkBoundResource<List<Group>, GroupSearchResponse>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull GroupSearchResponse item) {
                List<String> groupIds = item.getGroupIds();
                GroupSearchResult groupSearchResult = new GroupSearchResult(
                        query, groupIds);
                List<Group> groups = new ArrayList<>();
                for (SearchResultItem searchResultItem : item.getItems()) {
                    groups.add(searchResultItem.getGroup());
                }

                appDatabase.runInTransaction(new Runnable() {
                    @Override
                    public void run() {

                        groupDao.insertGroups(groups);
                        groupDao.insert(groupSearchResult);
                    }
                });

            }

            @Override
            protected boolean shouldFetch(@Nullable List<Group> data) {
                //return data == null;
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Group>> loadFromDb() {
                return Transformations.switchMap(groupDao.search(query), searchData -> {
                    if (searchData == null) {
                        return new LiveData<List<Group>>(null) {
                        };
                    } else {
                        return groupDao.loadOrdered(searchData.groupIds, (g1, g2) -> g2.memberCount - g1.memberCount);
                    }
                });
            }

            @NonNull
            @Override
            protected LiveData<GroupSearchResponse> createCall() {
                return doubanService.searchGroups(query);
            }


        }.asLiveData();
    }

    public LiveData<List<GroupTopic>> getGroupTopics(String groupId, String tagId) {
        return new NetworkBoundResource<List<GroupTopic>, TopicsResponse>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull TopicsResponse item) {
                List<GroupTopic> topics = item.getTopics();
                for (GroupTopic topic : topics) {
                    topic.groupId = groupId;
                    if (!topic.topicTags.isEmpty()) {
                        topic.tagId = topic.topicTags.get(0).id;
                    }

                    groupDao.addGroupTopic(topic);
                }

            }

            @Override
            protected boolean shouldFetch(@Nullable List<GroupTopic> data) {
                //return data==null||data.isEmpty();
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<GroupTopic>> loadFromDb() {
                if (TextUtils.isEmpty(tagId))
                    return groupDao.getGroupTopics(groupId);
                return groupDao.getGroupTopics(groupId, tagId);
            }

            @NonNull
            @Override
            protected LiveData<TopicsResponse> createCall() {
                if (TextUtils.isEmpty(tagId))
                    return doubanService.getGroupTopics(groupId);
                return doubanService.getGroupTopics(groupId, tagId);
            }
        }.asLiveData();
    }

    public LiveData<GroupTopic> getGroupTopic(String topicId) {
        return new NetworkBoundResource<GroupTopic, GroupTopic>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull GroupTopic item) {
                groupDao.addGroupTopic(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable GroupTopic data) {
                return data == null;
            }

            @NonNull
            @Override
            protected LiveData<GroupTopic> loadFromDb() {
                return groupDao.getGroupTopic(topicId);
            }

            @NonNull
            @Override
            protected LiveData<GroupTopic> createCall() {
                return doubanService.getGroupTopic(topicId);
            }
        }.asLiveData();
    }

    public LiveData<Boolean> isFavorite(String groupId) {
        return groupDao.getGroupFavorite(groupId);
    }

    public void createFavoriteGroup(String groupId) {
        FavGroup favGroup = new FavGroup(groupId);
        appExecutors.diskIO().execute(() -> groupDao.insertFavoriteGroup(favGroup));
    }

    public void removeFavoriteGroup(String groupId) {
        FavGroup favGroup = new FavGroup(groupId);
        appExecutors.diskIO().execute(() -> groupDao.deleteFavoriteGroup(favGroup));
    }

}
