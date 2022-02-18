package com.doubean.ford.data.repository;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.doubean.ford.api.DoubanService;
import com.doubean.ford.data.Group;
import com.doubean.ford.data.GroupSearchResponse;
import com.doubean.ford.data.GroupSearchResult;
import com.doubean.ford.data.GroupTopic;
import com.doubean.ford.data.GroupTopicComment;
import com.doubean.ford.data.GroupTopicComments;
import com.doubean.ford.data.GroupTopicCommentsResponse;
import com.doubean.ford.data.GroupTopicPopularComments;
import com.doubean.ford.data.GroupTopicsResponse;
import com.doubean.ford.data.SearchResultItem;
import com.doubean.ford.data.db.AppDatabase;
import com.doubean.ford.data.db.GroupDao;
import com.doubean.ford.util.AppExecutors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
                        LiveData<Group> groupLiveData = getGroup(groupIds.get(i[0]), false);
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

    public LiveData<Group> getGroup(String groupId, boolean forceFetch) {
        return new NetworkBoundResource<Group, Group>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull Group group) {
                groupDao.insertGroup(group);
            }

            @Override
            protected boolean shouldFetch(@Nullable Group data) {
                return data == null || forceFetch;
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
                        groupDao.insertGroupSearchResult(groupSearchResult);
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
        return new NetworkBoundResource<List<GroupTopic>, GroupTopicsResponse>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull GroupTopicsResponse item) {
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
            protected LiveData<GroupTopicsResponse> createCall() {
                if (TextUtils.isEmpty(tagId))
                    return doubanService.getGroupTopicsOfTag(groupId);
                return doubanService.getGroupTopicsOfTag(groupId, tagId);
            }
        }.asLiveData();
    }

    public LiveData<GroupTopic> getGroupTopic(String topicId) {
        return new NetworkBoundResource<GroupTopic, GroupTopic>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull GroupTopic item) {
                item.groupId = item.group.id;
                if (!item.topicTags.isEmpty())
                    item.tagId = item.topicTags.get(0).id;
                groupDao.addGroupTopic(item);
                //groupDao.insertGroup(item.group); item.group is incomplete: no tab info
            }

            @Override
            protected boolean shouldFetch(@Nullable GroupTopic data) {
                //return data == null;
                return true;
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

    public LiveData<GroupTopicComments> getGroupTopicComments(String topicId) {
        return new NetworkBoundResource<GroupTopicComments, GroupTopicCommentsResponse>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull GroupTopicCommentsResponse item) {
                for (GroupTopicComment comment : item.getComments()) {
                    comment.topicId = topicId;
                }
                for (GroupTopicComment comment : item.getPopularComments()) {
                    comment.topicId = topicId;
                }
                List<String> commentIds = item.getPopularCommentIds();
                GroupTopicPopularComments popularCommentsResult = new GroupTopicPopularComments(
                        topicId, commentIds);
                appDatabase.runInTransaction(() -> {
                    groupDao.insertGroupTopicComments(item.getComments());
                    groupDao.insertGroupTopicComments(item.getPopularComments());
                    groupDao.insertGroupTopicPopularComments(popularCommentsResult);
                });
            }

            @Override
            protected boolean shouldFetch(@Nullable GroupTopicComments data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<GroupTopicComments> loadFromDb() {
                MediatorLiveData<GroupTopicComments> result = new MediatorLiveData<>();

                LiveData<List<GroupTopicComment>> groupTopicPopularComments = Transformations.switchMap(groupDao.getGroupTopicPopularComments(topicId), new Function<GroupTopicPopularComments, LiveData<List<GroupTopicComment>>>() {
                    @Override
                    public LiveData<List<GroupTopicComment>> apply(GroupTopicPopularComments input) {
                        if (input == null)
                            return new LiveData<List<GroupTopicComment>>(null) {
                            };
                        else
                            return groupDao.loadOrderedComments(input.commentIds, new Comparator<GroupTopicComment>() {
                                @Override
                                public int compare(GroupTopicComment o1, GroupTopicComment o2) {
                                    return o1.voteCount - o2.voteCount;
                                }
                            });
                    }
                });

                LiveData<List<GroupTopicComment>> allComments = groupDao.getTopicComments(topicId);

                GroupTopicComments groupTopicComments = new GroupTopicComments();
                result.addSource(groupTopicPopularComments, new Observer<List<GroupTopicComment>>() {
                    @Override
                    public void onChanged(List<GroupTopicComment> comments) {
                        if (comments != null) {
                            groupTopicComments.setPopularComments(comments);
                            result.setValue(groupTopicComments);
                        }
                    }
                });
                result.addSource(allComments, new Observer<List<GroupTopicComment>>() {
                    @Override
                    public void onChanged(List<GroupTopicComment> comments) {
                        if (comments != null) {
                            groupTopicComments.setAllComments(comments);
                            result.setValue(groupTopicComments);
                        }
                    }
                });
                return result;
            }

            @NonNull
            @Override
            protected LiveData<GroupTopicCommentsResponse> createCall() {
                return doubanService.getGroupTopicComments(topicId);
            }
        }.asLiveData();
    }
}
