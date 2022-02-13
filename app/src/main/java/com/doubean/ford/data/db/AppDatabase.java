package com.doubean.ford.data.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.doubean.ford.data.Group;
import com.doubean.ford.data.GroupFavorite;
import com.doubean.ford.data.GroupSearchResult;
import com.doubean.ford.data.GroupTopic;
import com.doubean.ford.util.AppExecutors;
import com.doubean.ford.util.Constants;

@Database(entities = {Group.class, GroupTopic.class, GroupFavorite.class, GroupSearchResult.class}, version = 1, exportSchema = false)
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

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            new AppExecutors().diskIO().execute(() -> {
                // Populate the database in the background.
                // If you want to start with more words, just add them.
                GroupDao dao = instance.getGroupDao();
                dao.insertFavoriteGroup(new GroupFavorite("644960", "53959"));
                dao.insertFavoriteGroup(new GroupFavorite("732299", null));
                dao.insertFavoriteGroup(new GroupFavorite("665372", null));
                //dao.addGroupTopic(new GroupTopic("12345","644960",null,"aaaaa", null,null,0,0,null,null));
                //dao.addFavGroup(new FavGroup("667320"));
            });
        }
    };

    public abstract GroupDao getGroupDao();

    public abstract GroupFavoritesDao getFavoriteGroupDao();

    private static AppDatabase buildDatabase(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, Constants.DATABASE_NAME)
                .addCallback(sRoomDatabaseCallback)
                .build();
    }
}
