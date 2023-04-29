package com.doubean.ford.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.doubean.ford.data.vo.*
import com.doubean.ford.util.AppExecutors
import com.doubean.ford.util.Constants

/**
 * The Room database for this app
 */
@Database(
    entities = [Group::class, Post::class, GroupFollow::class, GroupSearchResult::class, GroupPostsResult::class, GroupTagPostsResult::class, PostComment::class, PostCommentsResult::class, PostTopComments::class, RecommendedGroupsResult::class, RecommendedGroupResult::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    Converters::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun groupDao(): GroupDao
    abstract fun groupFollowsAndSavesDao(): GroupFollowsAndSavesDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        @JvmStatic
        fun getInstance(context: Context): AppDatabase? {
            if (instance == null) {
                synchronized(AppDatabase::class.java) { instance = buildDatabase(context) }
            }
            return instance
        }

        private val sRoomDatabaseCallback: Callback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                AppExecutors().diskIO().execute { prepopulate() }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return databaseBuilder(context, AppDatabase::class.java, Constants.DATABASE_NAME)
                .addCallback(sRoomDatabaseCallback)
                .build()
        }

        private fun prepopulate() {}
    }
}