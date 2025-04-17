package com.github.bumblebee202111.doubean.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.github.bumblebee202111.doubean.data.db.model.PopulatedTopicDetail
import com.github.bumblebee202111.doubean.data.db.model.PopulatedTopicItem
import com.github.bumblebee202111.doubean.data.db.model.PopulatedTopicItemWithGroup
import com.github.bumblebee202111.doubean.data.db.model.TopicDetailPartialEntity
import com.github.bumblebee202111.doubean.data.db.model.TopicEntity
import com.github.bumblebee202111.doubean.data.db.model.TopicItemPartialEntity
import com.github.bumblebee202111.doubean.data.db.model.TopicTagCrossRef
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
interface GroupTopicDao {
    @Transaction
    @Query("SELECT * FROM topics WHERE id IN (:topicIds)")
    fun loadTopics(topicIds: List<String>): Flow<List<PopulatedTopicItem>>
    fun loadOrderedTopics(topicIds: List<String>) = loadTopics(topicIds).map { topics ->
        topics.sortedWith(compareBy { o -> topicIds.indexOf(o.partialEntity.id) })
    }

    @Transaction
    @Query("SELECT * FROM topics WHERE id IN (:topicIds)")
    fun loadTopicsWithGroups(topicIds: List<String>): Flow<List<PopulatedTopicItemWithGroup>>

    fun loadOrderedTopicsWithGroups(topicIds: List<String>) =
        loadTopicsWithGroups(topicIds).map { topics ->
            topics.sortedWith(compareBy { o -> topicIds.indexOf(o.partialEntity.id) })
        }

    @Insert(entity = TopicEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopicDetail(topic: TopicDetailPartialEntity)

    @Upsert(entity = TopicEntity::class)
    suspend fun upsertTopics(topics: List<TopicItemPartialEntity>)

    @Transaction
    @Query("SELECT * FROM topics WHERE id=:topicId")
    fun loadTopic(topicId: String): Flow<PopulatedTopicDetail?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopicTagCrossRefs(topicTagCrossRefs: List<TopicTagCrossRef>)

    @Query("DELETE FROM topics_tags WHERE topic_id =:topicId")
    suspend fun deleteTopicTagCrossRefsByTopicId(topicId: String)

    @Query("DELETE FROM topics_tags WHERE topic_id IN (:topicIds)")
    suspend fun deleteTopicTagCrossRefsByTopicIds(topicIds: List<String>)
}