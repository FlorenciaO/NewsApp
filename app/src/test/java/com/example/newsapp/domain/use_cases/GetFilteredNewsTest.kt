package com.example.newsapp.domain.use_cases

import com.example.newsapp.data.local.entity.NewsEntity
import com.example.newsapp.data.local.entity.UserEntity
import com.example.newsapp.domain.model.News
import com.example.newsapp.domain.model.User
import com.example.newsapp.domain.repository.NewsRepository
import com.example.newsapp.domain.repository.UserRepository
import com.example.newsapp.utils.ErrorCode.CONNECTION_ERROR
import com.example.newsapp.utils.ErrorCode.NO_COINCIDENCE
import com.example.newsapp.utils.ErrorCode.UNEXPECTED_ERROR
import com.example.newsapp.utils.Resource.Loading
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import java.io.IOException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import retrofit2.HttpException

class GetFilteredNewsTest {

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

    private val query = "query"
    private lateinit var getFilteredNews: GetFilteredNews

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        getFilteredNews = GetFilteredNews(mockNewsRepository, mockUserRepository)
    }

    @Test
    fun `emit loading first`() = runBlocking {
        val result = getFilteredNews.invoke(query).first()

        assertEquals(result.javaClass, Loading<List<News>>().javaClass)
    }


    @Test
    fun `when filtered list is empty returns no coincidence error code`() = runBlocking {
        coEvery { mockNewsRepository.getNewsBySearchQuery(any(), any()) } returns emptyList()

        val result = getFilteredNews.invoke(query).last()

        assertSame(NO_COINCIDENCE, result.errorCode)
    }

    @Test
    fun `when filtered list is empty then do not call user repository`() = runBlocking {
        coEvery { mockNewsRepository.getNewsBySearchQuery(any(), any()) } returns emptyList()

        getFilteredNews.invoke(query).last()

        coVerify(inverse = true) {
            mockUserRepository.getAllUsers(any())
        }
    }

    @Test
    fun `when filtered list is not empty then return filtered list`() = runBlocking {
        mockRepositoriesAndAnswers()

        val result = getFilteredNews.invoke(query).last()

        assertNotNull(result.data)
        assertEquals(listOf(mockNews).size, result.data!!.size)
        assertEquals(listOf(mockNews)[0], result.data!![0])
    }

    @Test
    fun `when no error then call to both repository forcing db`() = runBlocking {
        mockRepositoriesAndAnswers()

        getFilteredNews.invoke(query).last()

        coVerify {
            mockNewsRepository.getNewsBySearchQuery(true, query)
            mockUserRepository.getAllUsers(true)
        }
    }

    @Test
    fun `when no error then map all news and get username for each news`() = runBlocking {
        mockRepositoriesAndAnswers()

        getFilteredNews.invoke(query).last()

        coVerify {
            mockNewsEntity.toNews("user_name")
        }
    }

    private fun mockRepositoriesAndAnswers() {
        every { mockNewsEntity.toNews(any()) } returns mockNews
        every { mockNewsEntity.userId } returns "user_id"
        coEvery { mockNewsRepository.getNewsBySearchQuery(any(), any()) } returns listOf(mockNewsEntity)
        every { mockUserEntity.toUser() } returns mockUser
        every { mockUserEntity.id } returns "user_id"
        every { mockUserEntity.userName } returns "user_name"
        coEvery { mockUserRepository.getAllUsers(any()) } returns listOf(mockUserEntity)
    }
}
