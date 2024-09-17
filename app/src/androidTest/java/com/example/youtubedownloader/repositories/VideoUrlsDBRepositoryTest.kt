package com.example.youtubedownloader.repositories

import com.example.youtubedownloader.database.VideoUrlDao
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.*
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.youtubedownloader.database.VideoUrls
import com.example.youtubedownloader.getOrAwaitValue
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class VideoUrlsDBRepositoryTest {

    @Inject
    lateinit var dao: VideoUrlDao

    private lateinit var repository:VideoUrlsDBRepository

    @get:Rule
    val hiltAndroidRule=HiltAndroidRule(this)

    @get:Rule
    val instantTaskRule=InstantTaskExecutorRule()

    @Before
    fun setUp() {
        hiltAndroidRule.inject()
        repository=VideoUrlsDBRepository(dao)
    }

    @Test
    fun getUrls() {

    }

    @Test
    fun insertUrl() =runBlocking{
        repository.insertUrl(VideoUrls(videoUrl = "ABC", title = "Title", date = "ABC",time="abc",id=0))
        assertEquals(1,repository.getUrls().getOrAwaitValue().size)
    }

    @Test
    fun deleteUrl() {
    }

    @Test
    fun getLatestUrl() {
    }

    @Test
    fun updateLatestUrl() {
    }
}