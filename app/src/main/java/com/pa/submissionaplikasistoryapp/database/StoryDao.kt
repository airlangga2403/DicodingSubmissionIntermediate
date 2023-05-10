package com.pa.submissionaplikasistoryapp.database

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pa.submissionaplikasistoryapp.data.remote.response.ListStoryItem


@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: ListStoryItem)

    @Query("SELECT * FROM story")
    fun getAllStory(): PagingSource<Int, ListStoryItem>

    @Query("DELETE FROM story")
    suspend fun deleteAll()

    @Query("SELECT name , description, lon, lat FROM story WHERE lon IS NOT NULL AND lat IS NOT NULL")
    fun getUserLocation(): LiveData<List<StoryLocation>>
}
