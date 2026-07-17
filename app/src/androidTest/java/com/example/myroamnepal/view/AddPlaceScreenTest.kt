package com.example.myroamnepal.view

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.myroamnepal.model.PlaceModel
import com.example.myroamnepal.model.UserModel
import com.example.myroamnepal.repo.PlaceRepo
import com.example.myroamnepal.repo.UserRepo
import com.example.myroamnepal.view.ui.theme.MyRoamNepalTheme
import com.example.myroamnepal.viewModel.PlaceViewModel
import com.example.myroamnepal.viewModel.UserViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Synchronous fake [PlaceRepo] so the Compose test doesn't touch real Firebase/Cloudinary.
 */
private class AddPlaceFakeRepo : PlaceRepo {
    var uploadImageResult: Pair<Boolean, String?> = Pair(true, "https://example.com/image.jpg")
    var addPlaceResult: Pair<Boolean, String> = Pair(true, "Place added successfully")

    var uploadImageCallCount = 0
    var addPlaceCallCount = 0

    override fun uploadImage(imageUri: Uri, callback: (Boolean, String?) -> Unit) {
        uploadImageCallCount++
        callback(uploadImageResult.first, uploadImageResult.second)
    }

    override fun addPlace(place: PlaceModel, callback: (Boolean, String) -> Unit) {
        addPlaceCallCount++
        callback(addPlaceResult.first, addPlaceResult.second)
    }

    override fun getAllPlaces(callback: (Boolean, List<PlaceModel>, String) -> Unit) =
        callback(true, emptyList(), "Success")

    override fun getPlacesByUser(userId: String, callback: (Boolean, List<PlaceModel>, String) -> Unit) =
        callback(true, emptyList(), "Success")

    override fun getPlaceById(id: String, callback: (Boolean, PlaceModel?, String) -> Unit) =
        callback(true, null, "Success")

    override fun updatePlace(place: PlaceModel, callback: (Boolean, String) -> Unit) =
        callback(true, "Place updated successfully")

    override fun deletePlace(id: String, callback: (Boolean, String) -> Unit) =
        callback(true, "Place deleted successfully")
}

/**
 * Synchronous fake [UserRepo] that reports a fixed "current user" (or none).
 */
private class AddPlaceFakeUserRepo(private val currentUser: UserModel?) : UserRepo {
    override fun login(email: String, password: String, callback: (Boolean, String, UserModel?) -> Unit) {}
    override fun sendPasswordResetEmail(email: String, callback: (Boolean, String) -> Unit) {}
    override fun logOut(callback: (Boolean, String) -> Unit) {}
    override fun getCurrentUser(callback: (Boolean, UserModel?) -> Unit) = callback(currentUser != null, currentUser)
    override fun changePassword(oldPassword: String, newPassword: String, callback: (Boolean, String) -> Unit) {}
    override fun updateUser(userModel: UserModel, callback: (Boolean, String) -> Unit) {}
    override fun addUser(id: String, model: UserModel, callback: (Boolean, String) -> Unit) {}
    override fun rollbackCurrentUserRegistration() {}
    override fun register(email: String, password: String, callback: (Boolean, String, String) -> Unit) {}
    override fun getAllUsers(callback: (Boolean, String, List<UserModel>) -> Unit) {}
    override fun deleteUser(uid: String, callback: (Boolean, String) -> Unit) {}
    override fun toggleFavorite(userId: String, placeId: String, callback: (Boolean, String) -> Unit) {}
}

class AddPlaceScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun setAddPlaceScreen(
        placeRepo: AddPlaceFakeRepo = AddPlaceFakeRepo(),
        currentUser: UserModel? = UserModel(uid = "uid1", fullName = "Alice"),
        onBack: () -> Unit = {}
    ): PlaceViewModel {
        val placeViewModel = PlaceViewModel(placeRepo)
        val userViewModel = UserViewModel(AddPlaceFakeUserRepo(currentUser))
        composeTestRule.setContent {
            MyRoamNepalTheme {
                AddPlaceScreen(
                    placeViewModel = placeViewModel,
                    userViewModel = userViewModel,
                    onBack = onBack
                )
            }
        }
        return placeViewModel
    }

    @Test
    fun addPlaceScreen_displaysAllKeyElements() {
        setAddPlaceScreen()

        composeTestRule.onNodeWithTag("name_field").assertExists()
        composeTestRule.onNodeWithTag("description_field").assertExists()
        composeTestRule.onNodeWithTag("tips_field").assertExists()
        composeTestRule.onNodeWithTag("best_season_field").assertExists()
        composeTestRule.onNodeWithTag("location_field").assertExists()
        composeTestRule.onNodeWithTag("post_gem_button").assertExists()
    }

    @Test
    fun postGemButton_withAllFieldsEmpty_doesNotCallRepoOrGoBack() {
        val repo = AddPlaceFakeRepo()
        var backCalled = false

        setAddPlaceScreen(placeRepo = repo, onBack = { backCalled = true })

        composeTestRule.onNodeWithTag("post_gem_button").performClick()
        composeTestRule.waitForIdle()

        assertEquals(0, repo.uploadImageCallCount)
        assertEquals(0, repo.addPlaceCallCount)
        assertFalse(backCalled)
    }

    @Test
    fun postGemButton_withRequiredFieldsFilledButNoImage_doesNotCallRepo() {
        val repo = AddPlaceFakeRepo()
        var backCalled = false

        setAddPlaceScreen(placeRepo = repo, onBack = { backCalled = true })

        composeTestRule.onNodeWithTag("name_field").performTextInput("Hidden Waterfall")
        composeTestRule.onNodeWithTag("description_field").performTextInput("A secret gem")
        composeTestRule.onNodeWithTag("location_field").performTextInput("Pokhara, Kaski")
        composeTestRule.onNodeWithTag("post_gem_button").performClick()
        composeTestRule.waitForIdle()

        assertEquals(0, repo.uploadImageCallCount)
        assertEquals(0, repo.addPlaceCallCount)
        assertFalse(backCalled)
    }

    @Test
    fun postGemButton_withNoUserLoggedIn_doesNotCallRepo() {
        val repo = AddPlaceFakeRepo()

        setAddPlaceScreen(placeRepo = repo, currentUser = null)

        composeTestRule.onNodeWithTag("name_field").performTextInput("Hidden Waterfall")
        composeTestRule.onNodeWithTag("description_field").performTextInput("A secret gem")
        composeTestRule.onNodeWithTag("location_field").performTextInput("Pokhara, Kaski")
        composeTestRule.onNodeWithTag("post_gem_button").performClick()
        composeTestRule.waitForIdle()

        assertEquals(0, repo.uploadImageCallCount)
        assertEquals(0, repo.addPlaceCallCount)
    }

    @Test
    fun backButton_click_invokesOnBack() {
        var backCalled = false

        setAddPlaceScreen(onBack = { backCalled = true })

        composeTestRule.onNodeWithTag("back_button").performClick()

        assertTrue(backCalled)
    }
}
