package com.example.myroamnepal.view

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.myroamnepal.model.PlaceModel
import com.example.myroamnepal.repo.PlaceRepo
import com.example.myroamnepal.view.ui.theme.MyRoamNepalTheme
import com.example.myroamnepal.viewModel.PlaceViewModel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * Synchronous, in-memory fake [PlaceRepo] so the Compose test doesn't touch real Firebase.
 */
private class PostManagementFakeRepo : PlaceRepo {
    val allPlaces: MutableList<PlaceModel> = mutableListOf()

    var deletePlaceCallCount = 0
    var lastDeletedId: String? = null

    override fun uploadImage(imageUri: Uri, callback: (Boolean, String?) -> Unit) {}
    override fun addPlace(place: PlaceModel, callback: (Boolean, String) -> Unit) {}

    override fun getAllPlaces(callback: (Boolean, List<PlaceModel>, String) -> Unit) =
        callback(true, allPlaces.toList(), "Success")

    override fun getPlacesByUser(userId: String, callback: (Boolean, List<PlaceModel>, String) -> Unit) =
        callback(true, emptyList(), "Success")

    override fun getPlaceById(id: String, callback: (Boolean, PlaceModel?, String) -> Unit) =
        callback(true, allPlaces.find { it.id == id }, "Success")

    override fun updatePlace(place: PlaceModel, callback: (Boolean, String) -> Unit) =
        callback(true, "Place updated successfully")

    override fun deletePlace(id: String, callback: (Boolean, String) -> Unit) {
        deletePlaceCallCount++
        lastDeletedId = id
        allPlaces.removeAll { it.id == id }
        callback(true, "Place deleted successfully")
    }
}

class PostManagementSectionTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun setPostManagementSection(repo: PostManagementFakeRepo): PlaceViewModel {
        val viewModel = PlaceViewModel(repo)
        viewModel.getAllPlaces()
        composeTestRule.setContent {
            MyRoamNepalTheme {
                PostManagementSection(viewModel = viewModel)
            }
        }
        return viewModel
    }

    @Test
    fun postManagementSection_displaysExistingPlaces() {
        val repo = PostManagementFakeRepo().apply {
            allPlaces.add(PlaceModel(id = "p1", name = "Hidden Waterfall", location = "Pokhara"))
        }

        setPostManagementSection(repo)

        composeTestRule.onNodeWithText("Hidden Waterfall").assertExists()
    }

    @Test
    fun deletePlace_confirmingDialog_removesPlaceAndCallsRepo() {
        val repo = PostManagementFakeRepo().apply {
            allPlaces.add(PlaceModel(id = "p1", name = "Hidden Waterfall", location = "Pokhara"))
        }

        setPostManagementSection(repo)

        composeTestRule.onNodeWithTag("delete_button_p1").performClick()
        composeTestRule.onNodeWithTag("confirm_delete_button").performClick()
        composeTestRule.waitForIdle()

        assertEquals(1, repo.deletePlaceCallCount)
        assertEquals("p1", repo.lastDeletedId)
        composeTestRule.onNodeWithText("Hidden Waterfall").assertDoesNotExist()
    }

    @Test
    fun deletePlace_cancelingDialog_doesNotCallRepoAndKeepsPlace() {
        val repo = PostManagementFakeRepo().apply {
            allPlaces.add(PlaceModel(id = "p1", name = "Hidden Waterfall", location = "Pokhara"))
        }

        setPostManagementSection(repo)

        composeTestRule.onNodeWithTag("delete_button_p1").performClick()
        composeTestRule.onNodeWithTag("cancel_delete_button").performClick()
        composeTestRule.waitForIdle()

        assertEquals(0, repo.deletePlaceCallCount)
        composeTestRule.onNodeWithText("Hidden Waterfall").assertExists()
    }

    @Test
    fun deletePlace_withMultiplePlaces_onlyRemovesSelectedPlace() {
        val repo = PostManagementFakeRepo().apply {
            allPlaces.add(PlaceModel(id = "p1", name = "Hidden Waterfall", location = "Pokhara"))
            allPlaces.add(PlaceModel(id = "p2", name = "Secret Cave", location = "Chitwan"))
        }

        setPostManagementSection(repo)

        composeTestRule.onNodeWithTag("delete_button_p1").performClick()
        composeTestRule.onNodeWithTag("confirm_delete_button").performClick()
        composeTestRule.waitForIdle()

        assertEquals("p1", repo.lastDeletedId)
        composeTestRule.onNodeWithText("Hidden Waterfall").assertDoesNotExist()
        composeTestRule.onNodeWithText("Secret Cave").assertExists()
    }
}
