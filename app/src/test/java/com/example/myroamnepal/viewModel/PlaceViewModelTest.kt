package com.example.myroamnepal.viewModel

import android.net.Uri
import com.example.myroamnepal.model.PlaceModel
import com.example.myroamnepal.repo.PlaceRepo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito


private class FakePlaceRepo : PlaceRepo {

    var uploadImageResult: Pair<Boolean, String?> = Pair(true, "https://example.com/image.jpg")
    var addPlaceResult: Pair<Boolean, String> = Pair(true, "Place added successfully")
    var deletePlaceResult: Pair<Boolean, String> = Pair(true, "Place deleted successfully")
    var updatePlaceResult: Pair<Boolean, String> = Pair(true, "Place updated successfully")

    val allPlaces: MutableList<PlaceModel> = mutableListOf()

    var uploadImageCallCount = 0
    var addPlaceCallCount = 0
    var lastAddedPlace: PlaceModel? = null
    var deletePlaceCallCount = 0
    var lastDeletedId: String? = null
    var getAllPlacesCallCount = 0
    var updatePlaceCallCount = 0

    override fun uploadImage(imageUri: Uri, callback: (Boolean, String?) -> Unit) {
        uploadImageCallCount++
        callback(uploadImageResult.first, uploadImageResult.second)
    }

    override fun addPlace(place: PlaceModel, callback: (Boolean, String) -> Unit) {
        addPlaceCallCount++
        lastAddedPlace = place
        if (addPlaceResult.first) allPlaces.add(place)
        callback(addPlaceResult.first, addPlaceResult.second)
    }

    override fun getAllPlaces(callback: (Boolean, List<PlaceModel>, String) -> Unit) {
        getAllPlacesCallCount++
        callback(true, allPlaces.toList(), "Success")
    }

    override fun getPlacesByUser(userId: String, callback: (Boolean, List<PlaceModel>, String) -> Unit) {
        callback(true, allPlaces.filter { it.uploadedBy == userId }, "Success")
    }

    override fun getPlaceById(id: String, callback: (Boolean, PlaceModel?, String) -> Unit) {
        callback(true, allPlaces.find { it.id == id }, "Success")
    }

    override fun updatePlace(place: PlaceModel, callback: (Boolean, String) -> Unit) {
        updatePlaceCallCount++
        if (updatePlaceResult.first) {
            val index = allPlaces.indexOfFirst { it.id == place.id }
            if (index != -1) allPlaces[index] = place
        }
        callback(updatePlaceResult.first, updatePlaceResult.second)
    }

    override fun deletePlace(id: String, callback: (Boolean, String) -> Unit) {
        deletePlaceCallCount++
        lastDeletedId = id
        if (deletePlaceResult.first) allPlaces.removeAll { it.id == id }
        callback(deletePlaceResult.first, deletePlaceResult.second)
    }
}

class PlaceViewModelTest {

    private lateinit var repo: FakePlaceRepo
    private lateinit var viewModel: PlaceViewModel
    private val fakeUri: Uri = Mockito.mock(Uri::class.java)

    @Before
    fun setup() {
        repo = FakePlaceRepo()
        viewModel = PlaceViewModel(repo)
    }

    @Test
    fun `addPlace with valid data uploads image then adds place`() {
        viewModel.addPlace(
            name = "Hidden Lake",
            description = "A nice place",
            tips = "Bring water",
            bestSeason = "Autumn",
            location = "Pokhara",
            imageUri = fakeUri,
            uploadedBy = "uid1",
            uploadedByName = "Alice"
        )

        assertEquals(1, repo.uploadImageCallCount)
        assertEquals(1, repo.addPlaceCallCount)
        assertEquals("Place added successfully", viewModel.message.value)
        assertFalse(viewModel.loading.value)
    }

    @Test
    fun `deletePlace success removes place and refreshes list`() {
        repo.allPlaces.add(PlaceModel(id = "p1", name = "Place 1"))
        repo.allPlaces.add(PlaceModel(id = "p2", name = "Place 2"))
        
        // Initial fetch
        viewModel.getAllPlaces()
        assertEquals(2, viewModel.places.value.size)

        viewModel.deletePlace("p1")

        assertEquals(1, repo.deletePlaceCallCount)
        assertEquals(2, repo.getAllPlacesCallCount) // Initial + refresh
        assertEquals(1, viewModel.places.value.size)
        assertEquals("p2", viewModel.places.value[0].id)
    }

    @Test
    fun `updatePlace success updates data and refreshes list`() {
        repo.allPlaces.add(PlaceModel(id = "p1", name = "Old Name"))
        viewModel.getAllPlaces()

        val updatedPlace = PlaceModel(id = "p1", name = "New Name")
        viewModel.updatePlace(updatedPlace)

        assertEquals(1, repo.updatePlaceCallCount)
        assertEquals(2, repo.getAllPlacesCallCount)
        assertEquals("New Name", viewModel.places.value[0].name)
    }

    @Test
    fun `clearMessage resets message state`() {
        repo.addPlaceResult = Pair(false, "Error")
        viewModel.addPlace("","","","", "", null, "", "")
        
        viewModel.clearMessage()
        assertNull(viewModel.message.value)
    }
}
