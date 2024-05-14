package com.example.newsapp.domain.use_cases

import com.example.newsapp.data.local.entity.NewsEntity
import com.example.newsapp.data.local.entity.UserEntity
import com.example.newsapp.domain.model.News
import com.example.newsapp.domain.model.User
import com.example.newsapp.domain.repository.NewsRepository
import com.example.newsapp.domain.repository.UserRepository
import com.example.newsapp.utils.ErrorCode.UNEXPECTED_ERROR
import com.example.newsapp.utils.Resource.Loading
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test

class GetNewsDetailsTest {


    @MockK
    private lateinit var mockNewsRepository: NewsRepository
    @MockK
    private lateinit var mockUserRepository: UserRepository
    @MockK
    private lateinit var mockNews: News
    @MockK
    private lateinit var mockNewsEntity: NewsEntity
    @MockK
    private lateinit var mockUser: User
    @MockK
    private lateinit var mockUserEntity: UserEntity

    private val newsId = "newsId"
    private lateinit var getNewsDetails: GetNewsDetails

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        getNewsDetails = GetNewsDetails(mockNewsRepository, mockUserRepository)
    }

    @Test
    fun `emit loading first`() = runBlocking {
        val result = getNewsDetails.invoke(newsId).first()

        assertEquals(result.javaClass, Loading<News>().javaClass)
    }


    @Test
    fun `when news id is empty then do not call repositories and return unexpected error code`() = runBlocking {
        val result = getNewsDetails.invoke("").last()

        assertSame(UNEXPECTED_ERROR, result.errorCode)
        coVerify(inverse = true) {
            mockNewsRepository.getNewsBySearchQuery(any(), any())
            mockUserRepository.getUserById(any(), any())
        }
    }

    @Test
    fun `when news id is not empty then return news entity`() = runBlocking {
        mockRepositoriesAndAnswers()

        val result = getNewsDetails.invoke(newsId).last()

        assertEquals(mockNews, result.data!!)
    }

    @Test
    fun `when no error then call to both repository forcing db`() = runBlocking {
        mockRepositoriesAndAnswers()

        getNewsDetails.invoke(newsId).last()

        coVerify {
            mockNewsRepository.getNewsById(newsId, true)
            mockUserRepository.getUserById("user_id", true)
        }
    }

    @Test
    fun `when no error then map news entity and get the username`() = runBlocking {
        mockRepositoriesAndAnswers()

        getNewsDetails.invoke(newsId).last()

        coVerify {
            mockNewsEntity.toNews("user_name")
        }
    }

    private fun mockRepositoriesAndAnswers() {
        every { mockNewsEntity.toNews(any()) } returns mockNews
        every { mockNewsEntity.userId } returns "user_id"
        coEvery { mockNewsRepository.getNewsById(any(), any()) } returns mockNewsEntity
        every { mockUserEntity.toUser() } returns mockUser
        every { mockUserEntity.id } returns "user_id"
        every { mockUserEntity.userName } returns "user_name"
        coEvery { mockUserRepository.getUserById(any(), any()) } returns mockUserEntity
    }
}
