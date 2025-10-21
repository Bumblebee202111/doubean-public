package com.github.bumblebee202111.doubean.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.withTransaction
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.bumblebee202111.doubean.data.db.dao.GroupDao
import com.github.bumblebee202111.doubean.data.db.dao.GroupTopicDao
import com.github.bumblebee202111.doubean.data.db.dao.GroupTopicRemoteKeyDao
import com.github.bumblebee202111.doubean.data.db.dao.SearchHistoryDao
import com.github.bumblebee202111.doubean.data.db.dao.UserDao
import com.github.bumblebee202111.doubean.data.db.dao.UserGroupDao
import com.github.bumblebee202111.doubean.data.db.model.CachedGroupEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupGroupNotificationTargetEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupTabEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupTabNotificationTargetEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupTabTopicRemoteKey
import com.github.bumblebee202111.doubean.data.db.model.GroupTagTopicItemEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupTopicTagEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupUserTopicFeedItemEntity
import com.github.bumblebee202111.doubean.data.db.model.PinnedGroupTabEntity
import com.github.bumblebee202111.doubean.data.db.model.SearchHistoryEntity
import com.github.bumblebee202111.doubean.data.db.model.TopicEntity
import com.github.bumblebee202111.doubean.data.db.model.TopicNotificationEntity
import com.github.bumblebee202111.doubean.data.db.model.TopicTagCrossRef
import com.github.bumblebee202111.doubean.data.db.model.UserEntity
import com.github.bumblebee202111.doubean.data.db.model.UserJoinedGroupIdEntity
import com.github.bumblebee202111.doubean.util.DATABASE_NAME
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json


@Database(
    entities = [
        CachedGroupEntity::class,
        TopicEntity::class,
        GroupTabEntity::class,
        GroupTopicTagEntity::class,
        GroupTabTopicRemoteKey::class,
        GroupTagTopicItemEntity::class,
        GroupUserTopicFeedItemEntity::class,
        TopicTagCrossRef::class,
        UserJoinedGroupIdEntity::class,
        PinnedGroupTabEntity::class,
        GroupGroupNotificationTargetEntity::class,
        GroupTabNotificationTargetEntity::class,
        TopicNotificationEntity::class,
        UserEntity::class,
        SearchHistoryEntity::class
    ],
    version = 18,
    exportSchema = false
)
@TypeConverters(
    Converters::class
)
@RewriteQueriesToDropUnusedColumns
abstract class AppDatabase : RoomDatabase() {
    abstract fun groupDao(): GroupDao
    abstract fun userDao(): UserDao
    abstract fun userGroupDao(): UserGroupDao
    abstract fun groupTopicDao(): GroupTopicDao
    abstract fun groupTopicRemoteKeyDao(): GroupTopicRemoteKeyDao
    abstract fun searchHistoryDao(): SearchHistoryDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null


        @OptIn(DelicateCoroutinesApi::class)
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
                .fallbackToDestructiveMigration(false)
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