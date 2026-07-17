package com.example.myroamnepal.viewModel

import com.example.myroamnepal.model.UserModel
import com.example.myroamnepal.repo.UserRepo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

/**
 * Hand-written fake of [UserRepo] that invokes callbacks synchronously,
 * so tests can assert on [UserViewModel] state right after calling a method.
 */
private class FakeUserRepo : UserRepo {

    var loginResult: Triple<Boolean, String, UserModel?> =
        Triple(true, "Login Successful", UserModel(uid = "1", email = "test@test.com"))
    var lastLoginEmail: String? = null
    var lastLoginPassword: String? = null
    var loginCallCount = 0

    var resetResult: Pair<Boolean, String> = Pair(true, "Reset link sent to your email.")
    var lastResetEmail: String? = null
    var resetCallCount = 0

    override fun login(email: String, password: String, callback: (Boolean, String, UserModel?) -> Unit) {
        loginCallCount++
        lastLoginEmail = email
        lastLoginPassword = password
        callback(loginResult.first, loginResult.second, loginResult.third)
    }

    override fun sendPasswordResetEmail(email: String, callback: (Boolean, String) -> Unit) {
        resetCallCount++
        lastResetEmail = email
        callback(resetResult.first, resetResult.second)
    }

    override fun logOut(callback: (Boolean, String) -> Unit) {
        callback(true, "Logged out successfully.")
    }

    override fun getCurrentUser(callback: (Boolean, UserModel?) -> Unit) {
        callback(false, null)
    }

    override fun changePassword(oldPassword: String, newPassword: String, callback: (Boolean, String) -> Unit) {}
    override fun updateUser(userModel: UserModel, callback: (Boolean, String) -> Unit) {}
    override fun addUser(id: String, model: UserModel, callback: (Boolean, String) -> Unit) {}
    override fun rollbackCurrentUserRegistration() {}
    override fun register(email: String, password: String, callback: (Boolean, String, String) -> Unit) {}
    override fun getAllUsers(callback: (Boolean, String, List<UserModel>) -> Unit) {}
    override fun deleteUser(uid: String, callback: (Boolean, String) -> Unit) {}
    override fun toggleFavorite(userId: String, placeId: String, callback: (Boolean, String) -> Unit) {}
}

class UserViewModelLoginTest {

    private lateinit var repo: FakeUserRepo
    private lateinit var viewModel: UserViewModel

    @Before
    fun setup() {
        repo = FakeUserRepo()
        viewModel = UserViewModel(repo)
    }

    @Test
    fun `login with blank email shows validation message and does not call repo`() {
        viewModel.login("", "password123")

        assertEquals("Please fill all fields", viewModel.message.value)
        assertNull(viewModel.user.value)
        assertFalse(viewModel.loading.value)
        assertEquals(0, repo.loginCallCount)
    }

    @Test
    fun `login with blank password shows validation message and does not call repo`() {
        viewModel.login("test@test.com", "")

        assertEquals("Please fill all fields", viewModel.message.value)
        assertEquals(0, repo.loginCallCount)
    }

    @Test
    fun `login with both fields blank shows validation message`() {
        viewModel.login("", "")

        assertEquals("Please fill all fields", viewModel.message.value)
        assertEquals(0, repo.loginCallCount)
    }

    @Test
    fun `successful login updates user, message and clears loading`() {
        val expectedUser = UserModel(uid = "123", email = "test@test.com", role = "user")
        repo.loginResult = Triple(true, "Login Successful", expectedUser)

        viewModel.login("test@test.com", "password123")

        assertEquals(expectedUser, viewModel.user.value)
        assertEquals("Login Successful", viewModel.message.value)
        assertFalse(viewModel.loading.value)
    }

    @Test
    fun `failed login sets error message and leaves user null`() {
        repo.loginResult = Triple(false, "Login Failed", null)

        viewModel.login("test@test.com", "wrongpassword")

        assertNull(viewModel.user.value)
        assertEquals("Login Failed", viewModel.message.value)
        assertFalse(viewModel.loading.value)
    }

    @Test
    fun `login trims whitespace from email but not password`() {
        viewModel.login("  test@test.com  ", "password123")

        assertEquals("test@test.com", repo.lastLoginEmail)
        assertEquals("password123", repo.lastLoginPassword)
    }

    @Test
    fun `clearMessage resets message to null`() {
        viewModel.login("", "")
        assertEquals("Please fill all fields", viewModel.message.value)

        viewModel.clearMessage()

        assertNull(viewModel.message.value)
    }

    @Test
    fun `sendPasswordResetEmail with blank email shows validation message and does not call repo`() {
        viewModel.sendPasswordResetEmail("")

        assertEquals("Please enter email", viewModel.message.value)
        assertEquals(0, repo.resetCallCount)
    }

    @Test
    fun `sendPasswordResetEmail success updates message and clears loading`() {
        viewModel.sendPasswordResetEmail("test@test.com")

        assertEquals("test@test.com", repo.lastResetEmail)
        assertEquals("Reset link sent to your email.", viewModel.message.value)
        assertFalse(viewModel.loading.value)
    }

    @Test
    fun `sendPasswordResetEmail trims whitespace from email`() {
        viewModel.sendPasswordResetEmail("  test@test.com  ")

        assertEquals("test@test.com", repo.lastResetEmail)
    }
}
