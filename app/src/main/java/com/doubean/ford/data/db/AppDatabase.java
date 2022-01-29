package com.doubean.ford.data.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.doubean.ford.data.Group;
import com.doubean.ford.data.GroupTopic;
import com.doubean.ford.data.GroupTopicTag;
import com.doubean.ford.util.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

@Database(entities = {Group.class, GroupTopic.class, GroupTopicTag.class}, version = 1, exportSchema = false)
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

    private static AppDatabase buildDatabase(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, Constants.DATABASE_NAME)
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        try {
                            InputStream input = context.getApplicationContext().getAssets().open(Constants.GROUP_DATA_FILENAME);
                            JsonReader reader = new JsonReader(new InputStreamReader(input));
                            Type plantType = new TypeToken<List<Group>>() {
                            }.getType();
                            List<Group> groupList = new Gson().fromJson(reader, plantType);
                            input.close();
                            //instance.getGroupDao().insertAll(groupList);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //WorkManager.getInstance(context).enqueue(OneTimeWorkRequest.from(SeedDatabaseWorker.class));
                    }
                })
                .build();
    }

    public abstract GroupDao getGroupDao();

    public abstract GroupTopicDao getGroupTopicDao();
}
