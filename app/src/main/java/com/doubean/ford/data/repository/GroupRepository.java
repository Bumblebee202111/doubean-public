package com.doubean.ford.data.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.doubean.ford.api.ApiResponse;
import com.doubean.ford.api.DoubanService;
import com.doubean.ford.api.GroupSearchResponse;
import com.doubean.ford.api.PostCommentsResponse;
import com.doubean.ford.api.PostsResponse;
import com.doubean.ford.api.SortByRequestParam;
import com.doubean.ford.data.db.AppDatabase;
import com.doubean.ford.data.db.GroupDao;
import com.doubean.ford.data.vo.GroupDetail;
import com.doubean.ford.data.vo.GroupItem;
import com.doubean.ford.data.vo.GroupPostsResult;
import com.doubean.ford.data.vo.GroupSearchResult;
import com.doubean.ford.data.vo.GroupTagPostsResult;
import com.doubean.ford.data.vo.Post;
import com.doubean.ford.data.vo.PostComment;
import com.doubean.ford.data.vo.PostComments;
import com.doubean.ford.data.vo.PostCommentsResult;
import com.doubean.ford.data.vo.PostItem;
import com.doubean.ford.data.vo.PostTopComments;
import com.doubean.ford.data.vo.Resource;
import com.doubean.ford.data.vo.SearchResultItem;
import com.doubean.ford.data.vo.SortBy;
import com.doubean.ford.data.vo.Status;
import com.doubean.ford.util.AppExecutors;
import com.doubean.ford.util.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;

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
                    LiveData<Resource<GroupDetail>> groupLiveData = getGroup(groupIds.get(i[0]), false);
                    fetching.setValue(true);
                    groupsLiveData.addSource(groupLiveData, group -> {
                        if (group.status == Status.SUCCESS) {
                            groups.set(i[0]++, group.data.toGroupItem());
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

    public LiveData<Resource<GroupDetail>> getGroup(String groupId, boolean forceFetch) {
        return new NetworkBoundResource<GroupDetail, GroupDetail>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull GroupDetail group) {
                groupDao.upsertDetail(group);
            }

            @Override
            protected boolean shouldFetch(@Nullable GroupDetail data) {
                return data == null || forceFetch;
            }

            @NonNull
            @Override
            protected LiveData<GroupDetail> loadFromDb() {
                return groupDao.loadDetail(groupId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GroupDetail>> createCall() {
                return doubanService.getGroup(groupId, 1);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Boolean>> searchNextPage(String query) {
        FetchNextPageTask<SearchResultItem, GroupSearchResult, GroupSearchResponse> fetchNextSearchPageTask = new FetchNextPageTask<SearchResultItem, GroupSearchResult, GroupSearchResponse>(
                doubanService, appDatabase) {

            @Override
            protected GroupSearchResult loadFromDb() {
                return appDatabase.getGroupDao().findSearchResult(query);
            }

            @Override
            protected Call<GroupSearchResponse> createCall(Integer nextPageStart) {
                return doubanService.searchGroups(query, Constants.RESULT_GROUPS_COUNT, nextPageStart);
            }

            @Override
            protected void saveMergedResult(@NonNull GroupSearchResult item, List<SearchResultItem> items) {
                appDatabase.getGroupDao().insertGroupSearchResult(item);
                List<GroupItem> groups = new ArrayList<>();
                for (SearchResultItem i : items) {
                    groups.add(i.getGroup());
                }
                appDatabase.getGroupDao().upsertGroups(groups);
            }

            @Override
            protected GroupSearchResult merge(List<String> ids, GroupSearchResult current, int total, Integer nextPageStart) {
                return new GroupSearchResult(query, ids,
                        total, nextPageStart);
            }

        };
        appExecutors.networkIO().execute(fetchNextSearchPageTask);
        return fetchNextSearchPageTask.getLiveData();
    }

    public LiveData<Resource<List<GroupItem>>> search(String query) {
        return new NetworkBoundResource<List<GroupItem>, GroupSearchResponse>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull GroupSearchResponse item) {
                List<String> groupIds = item.getGroupIds();
                GroupSearchResult groupSearchResult = new GroupSearchResult(
                        query, groupIds, item.getTotal(), item.getNextPageStart());
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
                        return groupDao.loadOrdered(searchData.ids);
                    }
                });
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GroupSearchResponse>> createCall() {
                return doubanService.searchGroups(query, Constants.RESULT_GROUPS_COUNT);
            }

        }.asLiveData();
    }

    public LiveData<Resource<List<PostItem>>> getGroupPosts(String groupId, SortBy sortBy) {
        return new NetworkBoundResource<List<PostItem>, PostsResponse>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull PostsResponse item) {
                if (sortBy == SortBy.NEW) {
                    item.getItems().sort((o1, o2) -> -o1.created.compareTo(o2.created));
                }
                List<String> postIds = item.getPostIds();
                List<PostItem> posts = item.getItems();
                GroupPostsResult groupPostsResult = new GroupPostsResult(groupId, sortBy, postIds, item.getTotal(), item.getNextPageStart());
                for (PostItem post : posts) {
                    post.groupId = groupId;
                    if (post.postTags != null && !post.postTags.isEmpty()) {
                        post.tagId = post.postTags.get(0).id;
                    }
                }

                appDatabase.runInTransaction(() -> {
                    groupDao.upsertPosts(posts);
                    groupDao.insertGroupPostsResult(groupPostsResult);
                });
            }

            @Override
            protected boolean shouldFetch(@Nullable List<PostItem> data) {
                //return data==null||data.isEmpty();
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<PostItem>> loadFromDb() {
                return Transformations.switchMap(groupDao.getGroupPosts(groupId, sortBy), findData -> {
                    if (findData == null) {
                        return new LiveData<List<PostItem>>(null) {
                        };
                    } else {
                        return groupDao.loadPosts(findData.ids);
                    }
                });
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<PostsResponse>> createCall() {
                SortByRequestParam sortByRequestParam = getSortByRequestParam(sortBy);
                return doubanService.getGroupPosts(groupId, sortByRequestParam.toString(), Constants.RESULT_POSTS_COUNT);

            }
        }.asLiveData();
    }

    public LiveData<Resource<Boolean>> getNextPageGroupPosts(String groupId, SortBy sortBy) {
        FetchNextPageTask<PostItem, GroupPostsResult, PostsResponse> fetchNextPageTask = new FetchNextPageTask<PostItem, GroupPostsResult, PostsResponse>(
                doubanService, appDatabase) {

            @Override
            protected GroupPostsResult loadFromDb() {
                return appDatabase.getGroupDao().findGroupPosts(groupId, sortBy);
            }

            @Override
            protected Call<PostsResponse> createCall(Integer nextPageStart) {
                return doubanService.getGroupPosts(groupId, getSortByRequestParam(sortBy).toString(), Constants.RESULT_POSTS_COUNT, nextPageStart);
            }

            @Override
            protected void saveMergedResult(@NonNull GroupPostsResult item, List<PostItem> items) {
                appDatabase.getGroupDao().insertGroupPostsResult(item);
                appDatabase.getGroupDao().upsertPosts(items);
            }

            @Override
            protected GroupPostsResult merge(List<String> ids, GroupPostsResult current, int total, Integer nextPageStart) {
                return new GroupPostsResult(groupId, sortBy, ids, total, nextPageStart);
            }

        };
        appExecutors.networkIO().execute(fetchNextPageTask);
        return fetchNextPageTask.getLiveData();
    }

    public LiveData<Resource<List<PostItem>>> getGroupTagPosts(String groupId, String tagId, SortBy sortBy) {
        return new NetworkBoundResource<List<PostItem>, PostsResponse>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull PostsResponse item) {
                if (sortBy == SortBy.NEW) {
                    item.getItems().sort((o1, o2) -> -o1.created.compareTo(o2.created));
                }
                List<String> postIds = item.getPostIds();
                List<PostItem> posts = item.getItems();
                GroupTagPostsResult groupTagPostsResult = new GroupTagPostsResult(groupId, tagId, sortBy, postIds, item.getTotal(), item.getNextPageStart());
                for (PostItem post : posts) {
                    post.groupId = groupId;
                    if (post.postTags != null && !post.postTags.isEmpty()) {
                        post.tagId = post.postTags.get(0).id;
                    }
                }
                appDatabase.runInTransaction(() -> {
                    groupDao.upsertPosts(posts);
                    groupDao.insertGroupTagPostsResult(groupTagPostsResult);
                });
            }

            @Override
            protected boolean shouldFetch(@Nullable List<PostItem> data) {
                //return data==null||data.isEmpty();
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<PostItem>> loadFromDb() {
                return Transformations.switchMap(appDatabase.getGroupDao().getGroupTagPosts(groupId, tagId, sortBy), findData -> {
                    if (findData == null) {
                        return new LiveData<List<PostItem>>(null) {
                        };
                    } else {
                        return appDatabase.getGroupDao().loadPosts(findData.ids);
                    }
                });
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<PostsResponse>> createCall() {
                return doubanService.getGroupTagPosts(groupId, tagId, getSortByRequestParam(sortBy).toString(), Constants.RESULT_POSTS_COUNT);

            }
        }.asLiveData();
    }

    public LiveData<Resource<Boolean>> getNextPageGroupTagPosts(String groupId, String tagId, SortBy sortBy) {
        FetchNextPageTask<PostItem, GroupTagPostsResult, PostsResponse> fetchNextPageTask = new FetchNextPageTask<PostItem, GroupTagPostsResult, PostsResponse>(
                doubanService, appDatabase) {

            @Override
            protected GroupTagPostsResult loadFromDb() {
                return appDatabase.getGroupDao().findGroupTagPosts(groupId, tagId, sortBy);
            }

            @Override
            protected Call<PostsResponse> createCall(Integer nextPageStart) {
                return doubanService.getGroupTagPosts(groupId, tagId, getSortByRequestParam(sortBy).toString(), Constants.RESULT_POSTS_COUNT, nextPageStart);
            }

            @Override
            protected void saveMergedResult(@NonNull GroupTagPostsResult item, List<PostItem> items) {
                appDatabase.getGroupDao().insertGroupTagPostsResult(item);
                appDatabase.getGroupDao().upsertPosts(items);
            }

            @Override
            protected GroupTagPostsResult merge(List<String> ids, GroupTagPostsResult current, int total, Integer nextPageStart) {
                return new GroupTagPostsResult(groupId, tagId, sortBy, ids,
                        total, nextPageStart);
            }

        };
        appExecutors.networkIO().execute(fetchNextPageTask);
        return fetchNextPageTask.getLiveData();
    }

    public LiveData<Resource<Post>> getPost(String postId) {
        return new NetworkBoundResource<Post, Post>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull Post item) {
                item.groupId = item.group.id;
                if (!item.postTags.isEmpty())
                    item.tagId = item.postTags.get(0).id;

                appDatabase.runInTransaction(() -> {
                    groupDao.insertPost(item);
                    groupDao.upsertDetail(item.group);
                });

            }

            @Override
            protected boolean shouldFetch(@Nullable Post data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<Post> loadFromDb() {
                return groupDao.loadPost(postId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Post>> createCall() {
                return doubanService.getGroupPost(postId);
            }
        }.asLiveData();
    }

    public LiveData<Resource<PostComments>> getPostComments(String postId) {
        return new NetworkBoundResource<PostComments, PostCommentsResponse>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull PostCommentsResponse item) {
                for (PostComment comment : item.getItems()) {
                    comment.postId = postId;
                }
                for (PostComment comment : item.getTopComments()) {
                    comment.postId = postId;
                }
                List<String> topCommentIds = item.getTopCommentIds();
                PostTopComments topCommentsResult = new PostTopComments(
                        postId, topCommentIds);
                List<String> commentIds = item.getIds();
                PostCommentsResult commentsResult = new PostCommentsResult(postId, commentIds,
                        item.getTotal(), item.getNextPageStart());
                appDatabase.runInTransaction(() -> {
                    groupDao.insertPostComments(item.getItems());
                    groupDao.insertPostComments(item.getTopComments());
                    groupDao.insertPostCommentsResult(commentsResult);
                    groupDao.insertPostTopComments(topCommentsResult);
                });
            }

            @Override
            protected boolean shouldFetch(@Nullable PostComments data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<PostComments> loadFromDb() {

                LiveData<List<PostComment>> allComments = Transformations.switchMap(groupDao.getPostComments(postId), new Function<PostCommentsResult, LiveData<List<PostComment>>>() {
                    @Override
                    public LiveData<List<PostComment>> apply(PostCommentsResult data) {
                        if (data == null) {
                            return new LiveData<List<PostComment>>(null) {
                            };
                        } else {
                            return groupDao.loadOrderedComments(data.ids);
                        }
                    }
                });

                LiveData<List<PostComment>> topComments = Transformations.switchMap(groupDao.loadPostTopComments(postId), data -> {
                    if (data == null) {
                        return new LiveData<List<PostComment>>(null) {
                        };
                    } else {
                        return groupDao.loadOrderedComments(data.commentIds);
                    }

                });

                return Transformations.switchMap(topComments, input1 -> Transformations.map(allComments, input2 -> new PostComments(input1, input2)));
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<PostCommentsResponse>> createCall() {
                return doubanService.getPostComments(postId, Constants.RESULT_POSTS_COUNT);
            }
        }.asLiveData();

    }

    public LiveData<Resource<Boolean>> getNextPagePostComments(String postId) {
        FetchNextPageTask<PostComment, PostCommentsResult, PostCommentsResponse> fetchNextPageTask = new FetchNextPageTask<PostComment, PostCommentsResult, PostCommentsResponse>(
                doubanService, appDatabase) {

            @Override
            protected PostCommentsResult loadFromDb() {
                return appDatabase.getGroupDao().findPostComments(postId);
            }

            @Override
            protected Call<PostCommentsResponse> createCall(Integer nextPageStart) {
                return doubanService.getPostComments(postId, nextPageStart, Constants.RESULT_COMMENTS_COUNT);
            }

            @Override
            protected void saveMergedResult(@NonNull PostCommentsResult item, List<PostComment> items) {
                appDatabase.getGroupDao().insertPostCommentsResult(item);
                appDatabase.getGroupDao().insertPostComments(items);
            }

            @NonNull
            @Override
            protected PostCommentsResult merge(List<String> ids, PostCommentsResult current, int total, Integer nextPageStart) {
                return new PostCommentsResult(postId, ids,
                        total, nextPageStart);
            }

        };
        appExecutors.networkIO().execute(fetchNextPageTask);
        return fetchNextPageTask.getLiveData();
    }

    private SortByRequestParam getSortByRequestParam(SortBy sortBy) {
        switch (sortBy) {
            case LAST_UPDATED:
            case NEW:
                return SortByRequestParam.NEW;
            case TOP:
                return SortByRequestParam.TOP;
            default:
                return null;
        }
    }
}
