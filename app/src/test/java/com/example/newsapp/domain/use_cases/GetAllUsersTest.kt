package com.example.newsapp.domain.use_cases

import com.example.newsapp.data.local.entity.UserEntity
import com.example.newsapp.domain.model.News
import com.example.newsapp.domain.model.User
import com.example.newsapp.domain.repository.UserRepository
import com.example.newsapp.utils.ErrorCode.CONNECTION_ERROR
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException

class GetAllUsersTest {
    @MockK
    private lateinit var mockUserRepository: UserRepository
    @MockK
    private lateinit var anyException: Exception
    @MockK
    private lateinit var mockUser: User
    @MockK
    private lateinit var mockUserEntity: UserEntity

    private lateinit var getAllUsers: GetAllUsers

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        getAllUsers = GetAllUsers(mockUserRepository)
    }


    @Test
    fun `emit loading first`() = runBlocking {
        val result = getAllUsers.invoke().first()

        assertEquals(result.javaClass, Loading<List<User>>().javaClass)
    }


    @Test
    fun `when exception returns unexpected error code`() = runBlocking {
        coEvery { mockUserRepository.getAllUsers(any()) } throws anyException

        val result = getAllUsers.invoke().last()

        assertSame(UNEXPECTED_ERROR, result.errorCode)
    }

    @Test
    fun `when no exception then return valid list`() = runBlocking {
        mockRepositoriesAndAnswers()

        val result = getAllUsers.invoke().last()

        assertNotNull(result.data)
        assertEquals(listOf(mockUser).size, result.data!!.size)
        assertEquals(listOf(mockUser)[0], result.data!![0])
    }

    @Test
    fun `when no exception then call to repository forcing db`() = runBlocking {
        mockRepositoriesAndAnswers()

        getAllUsers.invoke().last()

        coVerify {
            mockUserRepository.getAllUsers(true)
        }
    }

    @Test
    fun `when no exception then map all users`() = runBlocking {
        mockRepositoriesAndAnswers()

        getAllUsers.invoke().last()

        coVerify {
            mockUserEntity.toUser()
        }
    }

    private fun mockRepositoriesAndAnswers() {
        every { mockUserEntity.toUser() } returns mockUser
        coEvery { mockUserRepository.getAllUsers(any()) } returns listOf(mockUserEntity)
    }
}
