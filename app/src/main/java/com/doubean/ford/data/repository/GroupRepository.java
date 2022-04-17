package com.doubean.ford.data.repository;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.doubean.ford.api.DoubanService;
import com.doubean.ford.api.GroupPostCommentsResponse;
import com.doubean.ford.api.GroupPostsResponse;
import com.doubean.ford.api.GroupSearchResponse;
import com.doubean.ford.data.db.AppDatabase;
import com.doubean.ford.data.db.GroupDao;
import com.doubean.ford.data.vo.GroupDetail;
import com.doubean.ford.data.vo.GroupItem;
import com.doubean.ford.data.vo.GroupPost;
import com.doubean.ford.data.vo.GroupPostComment;
import com.doubean.ford.data.vo.GroupPostComments;
import com.doubean.ford.data.vo.GroupPostItem;
import com.doubean.ford.data.vo.GroupPostPopularComments;
import com.doubean.ford.data.vo.GroupSearchResult;
import com.doubean.ford.data.vo.SearchResultItem;
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


    public LiveData<List<GroupItem>> getGroups(@NonNull List<String> groupIds) {
        MediatorLiveData<List<GroupItem>> groupsLiveData = new MediatorLiveData<>();
        List<GroupItem> groups = new ArrayList<>(Arrays.asList(new GroupItem[groupIds.size()]));
        if (groupIds.size() > 0) {
            MutableLiveData<Boolean> fetching = new MutableLiveData<>(false);
            final int[] i = {0};
            groupsLiveData.addSource(fetching, aBoolean -> {
                if (!aBoolean) {
                    LiveData<GroupDetail> groupLiveData = getGroup(groupIds.get(i[0]), false);
                    fetching.setValue(true);
                    groupsLiveData.addSource(groupLiveData, group -> {
                        if (group != null) {
                            groups.set(i[0]++, group.toGroupItem());
                            groupsLiveData.removeSource(groupLiveData);
                            if (i[0] == groups.size()) {
                                groupsLiveData.setValue(groups);
                                groupsLiveData.removeSource(fetching);
                            } else
                                fetching.setValue(false);
                        }

                    });
                }

            });
        }

        return groupsLiveData;
    }

    public LiveData<GroupDetail> getGroup(String groupId, boolean forceFetch) {
        return new NetworkBoundResource<GroupDetail, GroupDetail>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull GroupDetail group) {
                groupDao.upsertGroup(group);
            }

            @Override
            protected boolean shouldFetch(@Nullable GroupDetail data) {
                return data == null || forceFetch;
            }

            @NonNull
            @Override
            protected LiveData<GroupDetail> loadFromDb() {
                return groupDao.getGroup(groupId);
            }

            @NonNull
            @Override
            protected LiveData<GroupDetail> createCall() {
                return doubanService.getGroup(groupId, 1);
            }
        }.asLiveData();
    }

    public LiveData<List<GroupItem>> search(String query) {
        return new NetworkBoundResource<List<GroupItem>, GroupSearchResponse>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull GroupSearchResponse item) {
                List<String> groupIds = item.getGroupIds();
                GroupSearchResult groupSearchResult = new GroupSearchResult(
                        query, groupIds);
                List<GroupItem> groups = new ArrayList<>();
                for (SearchResultItem searchResultItem : item.getItems()) {
                    groups.add(searchResultItem.getGroup());
                }

                appDatabase.runInTransaction(() -> {
                    groupDao.upsertGroups(groups);
                    groupDao.insertGroupSearchResult(groupSearchResult);
                });

            }

            @Override
            protected boolean shouldFetch(@Nullable List<GroupItem> data) {
                //return data == null;
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<GroupItem>> loadFromDb() {
                return Transformations.switchMap(groupDao.search(query), searchData -> {
                    if (searchData == null) {
                        return new LiveData<List<GroupItem>>(null) {
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

    public LiveData<List<GroupPostItem>> getGroupPosts(String groupId, String tagId) {
        return new NetworkBoundResource<List<GroupPostItem>, GroupPostsResponse>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull GroupPostsResponse item) {
                List<GroupPostItem> posts = item.getPosts();
                for (GroupPostItem post : posts) {
                    post.groupId = groupId;
                    if (post.postTags != null && !post.postTags.isEmpty()) {
                        post.tagId = post.postTags.get(0).id;
                    }
                    groupDao.upsertPostItem(post);
                }

            }

            @Override
            protected boolean shouldFetch(@Nullable List<GroupPostItem> data) {
                //return data==null||data.isEmpty();
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<GroupPostItem>> loadFromDb() {
                if (TextUtils.isEmpty(tagId)) {
                    return groupDao.getGroupPosts(groupId);
                }
                return groupDao.getGroupPosts(groupId, tagId);
            }

            @NonNull
            @Override
            protected LiveData<GroupPostsResponse> createCall() {
                if (TextUtils.isEmpty(tagId)) {
                    return doubanService.getGroupPosts(groupId);
                }
                return doubanService.getGroupPostsOfTag(groupId, tagId);
            }
        }.asLiveData();
    }

    public LiveData<GroupPost> getGroupPost(String postId) {
        return new NetworkBoundResource<GroupPost, GroupPost>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull GroupPost item) {
                item.groupId = item.group.id;
                if (!item.postTags.isEmpty())
                    item.tagId = item.postTags.get(0).id;

                appDatabase.runInTransaction(() -> {
                    groupDao.insertPost(item);
                    groupDao.upsertGroup(item.group);
                });

            }

            @Override
            protected boolean shouldFetch(@Nullable GroupPost data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<GroupPost> loadFromDb() {
                return groupDao.getGroupPost(postId);
            }

            @NonNull
            @Override
            protected LiveData<GroupPost> createCall() {
                return doubanService.getGroupPost(postId);
            }
        }.asLiveData();
    }

    public LiveData<GroupPostComments> getGroupPostComments(String postId) {
        return new NetworkBoundResource<GroupPostComments, GroupPostCommentsResponse>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull GroupPostCommentsResponse item) {
                for (GroupPostComment comment : item.getComments()) {
                    comment.postId = postId;
                }
                for (GroupPostComment comment : item.getPopularComments()) {
                    comment.postId = postId;
                }
                List<String> commentIds = item.getPopularCommentIds();
                GroupPostPopularComments popularCommentsResult = new GroupPostPopularComments(
                        postId, commentIds);
                appDatabase.runInTransaction(() -> {
                    groupDao.insertGroupPostComments(item.getComments());
                    groupDao.insertGroupPostComments(item.getPopularComments());
                    groupDao.insertGroupPostPopularComments(popularCommentsResult);
                });
            }

            @Override
            protected boolean shouldFetch(@Nullable GroupPostComments data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<GroupPostComments> loadFromDb() {
                MediatorLiveData<GroupPostComments> result = new MediatorLiveData<>();

                GroupPostComments groupPostCommePnts = new GroupPostComments();
                result.addSource(groupDao.getPostComments(postId), comments -> {
                    if (comments != null && !comments.isEmpty()) {
                        groupPostCommePnts.setAllComments(comments);
                        //result.setValue(groupPostCommePnts);
                        LiveData<List<GroupPostComment>> groupPostPopularComments = Transformations.switchMap(groupDao.getGroupPostPopularComments(postId), data -> {
                            if (data == null)
                                return new LiveData<List<GroupPostComment>>(null) {
                                };
                            else
                                return groupDao.loadOrderedComments(data.commentIds, new Comparator<GroupPostComment>() {
                                    @Override
                                    public int compare(GroupPostComment o1, GroupPostComment o2) {
                                        return o2.voteCount - o1.voteCount;
                                    }
                                });
                        });
                        result.addSource(groupPostPopularComments, data -> {
                            groupPostCommePnts.setPopularComments(data);
                            result.setValue(groupPostCommePnts);
                        });
                    } else {
                        result.setValue(groupPostCommePnts);
                    }
                });


                return result;
            }

            @NonNull
            @Override
            protected LiveData<GroupPostCommentsResponse> createCall() {
                return doubanService.getGroupPostComments(postId);
            }
        }.asLiveData();
    }
}
