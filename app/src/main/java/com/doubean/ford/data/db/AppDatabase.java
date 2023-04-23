package com.doubean.ford.data.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.doubean.ford.data.vo.Group;
import com.doubean.ford.data.vo.GroupFollow;
import com.doubean.ford.data.vo.GroupPostsResult;
import com.doubean.ford.data.vo.GroupSearchResult;
import com.doubean.ford.data.vo.GroupTagPostsResult;
import com.doubean.ford.data.vo.Post;
import com.doubean.ford.data.vo.PostComment;
import com.doubean.ford.data.vo.PostCommentsResult;
import com.doubean.ford.data.vo.PostTopComments;
import com.doubean.ford.data.vo.RecommendedGroupResult;
import com.doubean.ford.data.vo.RecommendedGroupsResult;
import com.doubean.ford.util.AppExecutors;
import com.doubean.ford.util.Constants;

/**
 * The Room database for this app
 */
@Database(entities = {Group.class, Post.class, GroupFollow.class, GroupSearchResult.class, GroupPostsResult.class, GroupTagPostsResult.class, PostComment.class, PostCommentsResult.class, PostTopComments.class, RecommendedGroupsResult.class, RecommendedGroupResult.class},
        version = 1,
        exportSchema = false)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance;

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                instance = buildDatabase(context);
            }
        }
        return instance;
    }

    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new AppExecutors().diskIO().execute(AppDatabase::prepopulate);
        }
    };

    public abstract GroupDao getGroupDao();

    public abstract GroupFollowsAndSavesDao getGroupFollowsAndSavesDao();

    private static AppDatabase buildDatabase(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, Constants.DATABASE_NAME)
                .addCallback(sRoomDatabaseCallback)
                .build();
    }

    private static void prepopulate() {

    }
}
