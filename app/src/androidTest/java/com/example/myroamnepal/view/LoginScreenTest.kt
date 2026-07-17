package com.example.myroamnepal.view

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.myroamnepal.model.UserModel
import com.example.myroamnepal.repo.UserRepo
import com.example.myroamnepal.view.ui.theme.MyRoamNepalTheme
import com.example.myroamnepal.viewModel.UserViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Synchronous fake [UserRepo] so the Compose test doesn't touch real Firebase.
 */
private class FakeUserRepo(
    var loginResult: Triple<Boolean, String, UserModel?> =
        Triple(true, "Login Successful", UserModel(uid = "1", email = "test@test.com", role = "user"))
) : UserRepo {

    var lastResetEmail: String? = null
    var resetCallCount = 0

    override fun login(email: String, password: String, callback: (Boolean, String, UserModel?) -> Unit) {
        callback(loginResult.first, loginResult.second, loginResult.third)
    }

    override fun sendPasswordResetEmail(email: String, callback: (Boolean, String) -> Unit) {
        resetCallCount++
        lastResetEmail = email
        callback(true, "Reset link sent to your email.")
    }

    override fun logOut(callback: (Boolean, String) -> Unit) = callback(true, "Logged out successfully.")
    override fun getCurrentUser(callback: (Boolean, UserModel?) -> Unit) = callback(false, null)
    override fun changePassword(oldPassword: String, newPassword: String, callback: (Boolean, String) -> Unit) {}
    override fun updateUser(userModel: UserModel, callback: (Boolean, String) -> Unit) {}
    override fun addUser(id: String, model: UserModel, callback: (Boolean, String) -> Unit) {}
    override fun rollbackCurrentUserRegistration() {}
    override fun register(email: String, password: String, callback: (Boolean, String, String) -> Unit) {}
    override fun getAllUsers(callback: (Boolean, String, List<UserModel>) -> Unit) {}
    override fun deleteUser(uid: String, callback: (Boolean, String) -> Unit) {}
    override fun toggleFavorite(userId: String, placeId: String, callback: (Boolean, String) -> Unit) {}
}

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun setLoginScreen(
        repo: FakeUserRepo = FakeUserRepo(),
        onLoginSuccess: (UserModel) -> Unit = {},
        onSignUpClick: () -> Unit = {}
    ): UserViewModel {
        val viewModel = UserViewModel(repo)
        composeTestRule.setContent {
            MyRoamNepalTheme {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = onLoginSuccess,
                    onSignUpClick = onSignUpClick
                )
            }
        }
        return viewModel
    }

    private fun emailField(): SemanticsNodeInteraction = composeTestRule.onNodeWithTag("email_field")
    private fun passwordField(): SemanticsNodeInteraction = composeTestRule.onNodeWithTag("password_field")
    private fun loginButton(): SemanticsNodeInteraction = composeTestRule.onNodeWithTag("login_button")

    @Test
    fun loginScreen_displaysAllKeyElements() {
        setLoginScreen()

        emailField().assertExists()
        passwordField().assertExists()
        loginButton().assertExists()
        composeTestRule.onNodeWithTag("forgot_password_button").assertExists()
        composeTestRule.onNodeWithTag("signup_button").assertExists()
    }

    @Test
    fun login_withValidCredentials_invokesOnLoginSuccessWithUser() {
        var loggedInUser: UserModel? = null
        val expectedUser = UserModel(uid = "42", email = "test@test.com", role = "user")
        val repo = FakeUserRepo(loginResult = Triple(true, "Login Successful", expectedUser))

        setLoginScreen(repo = repo, onLoginSuccess = { loggedInUser = it })

        emailField().performTextInput("test@test.com")
        passwordField().performTextInput("password123")
        loginButton().performClick()

        composeTestRule.waitForIdle()
        assertEquals(expectedUser, loggedInUser)
    }

    @Test
    fun login_withInvalidCredentials_doesNotInvokeOnLoginSuccess() {
        var loggedInUser: UserModel? = null
        val repo = FakeUserRepo(loginResult = Triple(false, "Login Failed", null))

        setLoginScreen(repo = repo, onLoginSuccess = { loggedInUser = it })

        emailField().performTextInput("test@test.com")
        passwordField().performTextInput("wrongpassword")
        loginButton().performClick()

        composeTestRule.waitForIdle()
        assertNull(loggedInUser)
        // Login button should be re-enabled (not stuck showing the loading spinner).
        composeTestRule.onNodeWithText("Login").assertExists()
    }

    @Test
    fun login_withEmptyFields_doesNotInvokeOnLoginSuccess() {
        var loggedInUser: UserModel? = null

        setLoginScreen(onLoginSuccess = { loggedInUser = it })

        loginButton().performClick()

        composeTestRule.waitForIdle()
        assertNull(loggedInUser)
    }

    @Test
    fun signUpButton_click_invokesOnSignUpClick() {
        var signUpClicked = false

        setLoginScreen(onSignUpClick = { signUpClicked = true })

        composeTestRule.onNodeWithTag("signup_button").performClick()

        assertTrue(signUpClicked)
    }

    @Test
    fun forgotPassword_withEmailEntered_triggersPasswordResetOnRepo() {
        val repo = FakeUserRepo()

        setLoginScreen(repo = repo)

        emailField().performTextInput("reset@test.com")
        composeTestRule.onNodeWithTag("forgot_password_button").performClick()

        composeTestRule.waitForIdle()
        assertEquals(1, repo.resetCallCount)
        assertEquals("reset@test.com", repo.lastResetEmail)
    }

    @Test
    fun forgotPassword_withNoEmailEntered_doesNotCallRepo() {
        val repo = FakeUserRepo()

        setLoginScreen(repo = repo)

        composeTestRule.onNodeWithTag("forgot_password_button").performClick()

        composeTestRule.waitForIdle()
        assertEquals(0, repo.resetCallCount)
    }
}
