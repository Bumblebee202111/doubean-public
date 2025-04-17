package com.github.bumblebee202111.doubean.data.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import androidx.room.Upsert
import com.github.bumblebee202111.doubean.data.db.model.CachedGroupEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupTabEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupTagTopicItemEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupTopicTagEntity
import com.github.bumblebee202111.doubean.data.db.model.PopulatedTopicItem
import com.github.bumblebee202111.doubean.data.db.model.SimpleCachedGroupPartialEntity
import com.github.bumblebee202111.doubean.model.groups.TopicSortBy
import kotlinx.coroutines.flow.Flow


@Dao
@RewriteQueriesToDropUnusedColumns
interface GroupDao {
    @Query(value = "SELECT * FROM cached_groups WHERE id=:id")
    fun getCachedGroup(id: String): Flow<CachedGroupEntity?>

    @Query(value = "SELECT * FROM cached_groups WHERE id=:id")
    fun getSimpleCachedGroup(id: String): Flow<SimpleCachedGroupPartialEntity?>

    @Insert(entity = CachedGroupEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedGroup(cachedGroup: CachedGroupEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedGroups(cachedGroups: List<CachedGroupEntity>)

    @Upsert(entity = CachedGroupEntity::class)
    suspend fun upsertSimpleCachedGroup(simpleCachedGroup: SimpleCachedGroupPartialEntity)

    @Upsert(entity = CachedGroupEntity::class)
    suspend fun upsertSimpleCachedGroups(simpleCachedGroups: List<SimpleCachedGroupPartialEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupTabs(tabs: List<GroupTabEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopicTags(topicTags: List<GroupTopicTagEntity>)

    @Insert(GroupTagTopicItemEntity::class, onConflict = OnConflictStrategy.REPLACE)
    fun insertGroupTopicItems(topicItems: List<GroupTagTopicItemEntity>)

    @Transaction
    @Query("""SELECT id, title, author_id, create_time, update_time, like_count, reaction_count, reshares_count, save_count, comments_count, short_content, content, cover_url, url, uri, topics.group_id, images, ip_location FROM group_tag_topics LEFT JOIN topics ON topic_id = topics.id WHERE group_tag_topics.group_id = :groupId AND group_tag_topics.tag_id = :tagId AND sort_by = :sortBy ORDER BY `index` ASC""")
    fun groupTagTopicPagingSource(
        groupId: String,
        tagId: String,
        sortBy: TopicSortBy,
    ): PagingSource<Int, PopulatedTopicItem>

    @Query("DELETE FROM group_tag_topics WHERE group_id = :groupId AND tag_id = :tagId AND sort_by = :sortBy")
    fun deleteGroupTagTopicItems(groupId: String, tagId: String, sortBy: TopicSortBy)
}