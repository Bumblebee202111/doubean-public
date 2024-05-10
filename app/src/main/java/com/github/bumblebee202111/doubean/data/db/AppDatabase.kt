package com.github.bumblebee202111.doubean.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.withTransaction
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.bumblebee202111.doubean.data.db.dao.GroupDao
import com.github.bumblebee202111.doubean.data.db.dao.GroupSearchResultRemoteKeyDao
import com.github.bumblebee202111.doubean.data.db.dao.GroupTopicDao
import com.github.bumblebee202111.doubean.data.db.dao.UserDao
import com.github.bumblebee202111.doubean.data.db.dao.UserGroupDao
import com.github.bumblebee202111.doubean.data.db.model.FollowedGroupEntity
import com.github.bumblebee202111.doubean.data.db.model.FollowedGroupTabEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupPostTagEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupPostsResult
import com.github.bumblebee202111.doubean.data.db.model.GroupSearchResultItemEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupSearchResultRemoteKey
import com.github.bumblebee202111.doubean.data.db.model.GroupTabEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupTagPostsResult
import com.github.bumblebee202111.doubean.data.db.model.PostEntity
import com.github.bumblebee202111.doubean.data.db.model.PostTagCrossRef
import com.github.bumblebee202111.doubean.data.db.model.RecommendedGroupEntity
import com.github.bumblebee202111.doubean.data.db.model.RecommendedGroupPost
import com.github.bumblebee202111.doubean.data.db.model.RecommendedGroupsResult
import com.github.bumblebee202111.doubean.data.db.model.RecommendedPostNotificationEntity
import com.github.bumblebee202111.doubean.data.db.model.UserEntity
import com.github.bumblebee202111.doubean.util.DATABASE_NAME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

/**
 * The Room database for this app
 */
@Database(
    entities = [
        GroupEntity::class,
        PostEntity::class,
        GroupTabEntity::class,
        GroupPostTagEntity::class,
        GroupSearchResultItemEntity::class,
        GroupSearchResultRemoteKey::class,
        GroupPostsResult::class,
        GroupTagPostsResult::class,
        RecommendedGroupsResult::class,
        RecommendedGroupEntity::class,
        RecommendedGroupPost::class,
        PostTagCrossRef::class,
        FollowedGroupEntity::class,
        FollowedGroupTabEntity::class,
        RecommendedPostNotificationEntity::class,
        UserEntity::class,
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(
    Converters::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun groupDao(): GroupDao
    abstract fun userDao(): UserDao
    abstract fun groupFollowsAndSavesDao(): UserGroupDao

    abstract fun groupTopicDao(): GroupTopicDao

    abstract fun groupSearchResultRemoteKeyDap(): GroupSearchResultRemoteKeyDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        // Adapted from trackr
        private val sRoomDatabaseCallback: Callback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                GlobalScope.launch(Dispatchers.IO) {
                    insertSeedData()
                }
            }
        }

        fun buildDatabase(context: Context, json: Json): AppDatabase {
            return databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .addCallback(sRoomDatabaseCallback)
                .addTypeConverter(Converters(json))
                .build()
        }

        suspend fun insertSeedData() {
            instance?.withTransaction {

            }
        }
    }
}