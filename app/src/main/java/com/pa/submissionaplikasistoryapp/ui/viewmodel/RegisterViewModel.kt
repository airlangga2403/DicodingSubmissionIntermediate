package com.pa.submissionaplikasistoryapp.ui.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.pa.submissionaplikasistoryapp.data.remote.response.ListStoryItem
import com.pa.submissionaplikasistoryapp.data.remote.response.ResponseGetDetailStories
import com.pa.submissionaplikasistoryapp.data.remote.response.ResponseRegister
import com.pa.submissionaplikasistoryapp.data.repository.RegisterRepository
import com.pa.submissionaplikasistoryapp.database.StoryLocation
import okhttp3.MultipartBody
import okhttp3.RequestBody

class RegisterViewModel(private val registerRepository: RegisterRepository) : ViewModel() {

    fun registerUser(name: String, email: String, password: String): LiveData<ResponseRegister> {
        return registerRepository.registerUser(name, email, password)
    }

    fun loginUser(email: String, password: String) = registerRepository.loginUser(email, password)


    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        return registerRepository.getStories().cachedIn(viewModelScope)
    }

    fun getDetailStory(id: String): LiveData<ResponseGetDetailStories> {
        return registerRepository.getDetailStory(id)
    }

    fun getLocationUser(): LiveData<List<StoryLocation>> = registerRepository.getLocationUser()

    fun logoutUser() = registerRepository.logoutUser()

    fun uploadStory(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Double,
        lon: Double,
        multiPort: String,
    ) = registerRepository.uploadStory(file, description, lat, lon, multiPort)

    fun uploadStoryWithoutLocation(
        file: MultipartBody.Part,
        description: RequestBody,
        multiPort: String,
    ) = registerRepository.uploadStoryWithoutLocation(file, description, multiPort)
}
