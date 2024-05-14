package com.example.newsapp.domain.use_cases

import com.example.newsapp.data.local.entity.UserEntity
import com.example.newsapp.domain.model.User
import com.example.newsapp.domain.model.User.Address
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

class GetUserAddressTest {

    @MockK
    private lateinit var mockUserRepository: UserRepository
    @MockK
    private lateinit var mockUserAddress: Address
    @MockK
    private lateinit var mockUser: User
    @MockK
    private lateinit var mockUserEntity: UserEntity

    private val userId = "userId"

    private lateinit var getUserAddress: GetUserAddress

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        getUserAddress = GetUserAddress(mockUserRepository)
    }


    @Test
    fun `emit loading first`() = runBlocking {
        val result = getUserAddress.invoke(userId).first()

        assertEquals(result.javaClass, Loading<Address>().javaClass)
    }


    @Test
    fun `when user id is empty then do not call repository and return unexpected error code`() = runBlocking {
        val result = getUserAddress.invoke("").last()

        assertSame(UNEXPECTED_ERROR, result.errorCode)
        coVerify(inverse = true) {
            mockUserRepository.getUserById(any(), any())
        }
    }

    @Test
    fun `when no exception then return valid address`() = runBlocking {
        mockRepositoriesAndAnswers()

        val result = getUserAddress.invoke(userId).last()

        assertEquals(mockUserAddress, result.data!!)
    }

    @Test
    fun `when no exception then call to repository forcing db`() = runBlocking {
        mockRepositoriesAndAnswers()

        getUserAddress.invoke(userId).last()

        coVerify {
            mockUserRepository.getUserById(userId, true)
        }
    }

    private fun mockRepositoriesAndAnswers() {
        every { mockUserEntity.address } returns mockUserAddress
        coEvery { mockUserRepository.getUserById(any(), any()) } returns mockUserEntity
    }
}
