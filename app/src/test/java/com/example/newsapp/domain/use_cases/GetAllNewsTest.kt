package com.example.newsapp.domain.use_cases

import com.example.newsapp.data.local.entity.NewsEntity
import com.example.newsapp.data.local.entity.UserEntity
import com.example.newsapp.domain.model.News
import com.example.newsapp.domain.model.User
import com.example.newsapp.domain.repository.NewsRepository
import com.example.newsapp.domain.repository.UserRepository
import com.example.newsapp.utils.ErrorCode
import com.example.newsapp.utils.Resource
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import java.io.IOException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException

class GetAllNewsTest {

    @MockK
    private lateinit var mockNewsRepository: NewsRepository
    @MockK
    private lateinit var mockUserRepository: UserRepository
    @MockK
    private lateinit var httpException: HttpException
    @MockK
    private lateinit var ioException: IOException
    @MockK
    private lateinit var mockNews: News
    @MockK
    private lateinit var mockNewsEntity: NewsEntity
    @MockK
    private lateinit var mockUser: User
    @MockK
    private lateinit var mockUserEntity: UserEntity

    private lateinit var getAllNews: GetAllNews

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        getAllNews = GetAllNews(mockNewsRepository, mockUserRepository)
    }

    @Test
    fun `emit loading first`() = runBlocking {
        val result = getAllNews.invoke().first()

        assertEquals(result.javaClass, Resource.Loading<List<News>>().javaClass)
    }


    @Test
    fun `when hhtp exception returns unexpected error code`() = runBlocking {
        coEvery { mockNewsRepository.getAllNews(any()) } throws httpException

        val result = getAllNews.invoke().last()

        assertSame(ErrorCode.UNEXPECTED_ERROR, result.errorCode)
    }

    @Test
    fun `when io exception returns unexpected error code`() = runBlocking {
        coEvery { mockNewsRepository.getAllNews(any()) } throws ioException

        val result = getAllNews.invoke().last()

        assertSame(result.errorCode, ErrorCode.CONNECTION_ERROR)
    }

    @Test
    fun `when no exception then return valid list`() = runBlocking {
        mockRepositoriesAndAnswers()

        val result = getAllNews.invoke().last()

        assertNotNull(result.data)
        assertEquals(listOf(mockNews).size, result.data!!.size)
        assertEquals(listOf(mockNews)[0], result.data!![0])
    }

    @Test
    fun `when no exception then call to both repository not forcing db`() = runBlocking {
        mockRepositoriesAndAnswers()

        getAllNews.invoke().last()

        coVerify {
            mockNewsRepository.getAllNews(false)
            mockUserRepository.getAllUsers(false)
        }
    }

    @Test
    fun `when no exception then map all news and get username for each news`() = runBlocking {
        mockRepositoriesAndAnswers()

        getAllNews.invoke().last()

        coVerify {
            mockNewsEntity.toNews("user_name")
        }
    }

    private fun mockRepositoriesAndAnswers() {
        every { mockNewsEntity.toNews(any()) } returns mockNews
        every { mockNewsEntity.userId } returns "user_id"
        coEvery { mockNewsRepository.getAllNews(any()) } returns listOf(mockNewsEntity)
        every { mockUserEntity.toUser() } returns mockUser
        every { mockUserEntity.id } returns "user_id"
        every { mockUserEntity.userName } returns "user_name"
        coEvery { mockUserRepository.getAllUsers(any()) } returns listOf(mockUserEntity)
    }
}
