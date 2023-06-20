package com.doubean.ford.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.withTransaction
import androidx.sqlite.db.SupportSQLiteDatabase
import com.doubean.ford.data.db.model.*
import com.doubean.ford.util.DATABASE_NAME
import kotlinx.coroutines.*

/**
 * The Room database for this app
 */
@Database(
    entities = [
        GroupEntity::class,
        PostEntity::class,
        GroupTabEntity::class,
        GroupPostTagEntity::class,
        GroupSearchResult::class,
        GroupPostsResult::class,
        GroupTagPostsResult::class,
        PostCommentEntity::class,
        PostCommentsResult::class,
        RecommendedGroupsResult::class,
        RecommendedGroupEntity::class,
        RecommendedGroupPost::class,
        PostTagCrossRef::class,
        UserEntity::class,
        FollowedGroupEntity::class,
        FollowedGroupTabEntity::class,
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    Converters::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun groupDao(): GroupDao
    abstract fun userDao(): UserDao
    abstract fun groupFollowsAndSavesDao(): UserGroupDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase? {
            if (instance == null) {
                synchronized(this) { instance = buildDatabase(context) }
            }
            return instance
        }

        // Adapted from trackr
        private val sRoomDatabaseCallback: Callback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                GlobalScope.launch(Dispatchers.IO) {
                    insertSeedData()
                }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .addCallback(sRoomDatabaseCallback)
                .build()
        }

        suspend fun insertSeedData() {
            instance?.withTransaction {

            }
        }
    }
}