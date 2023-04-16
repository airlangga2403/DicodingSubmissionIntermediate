package com.pa.submissionaplikasistoryapp.ui.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.pa.submissionaplikasistoryapp.data.remote.response.ResponseGetStories
import com.pa.submissionaplikasistoryapp.data.remote.response.ResponseRegister
import com.pa.submissionaplikasistoryapp.data.repository.RegisterRepository

class RegisterViewModel(private val registerRepository: RegisterRepository) : ViewModel() {
    fun registerUser(name: String, email: String, password: String): LiveData<ResponseRegister> {
        return registerRepository.registerUser(name, email, password)
    }
    fun loginUser(email: String, password: String) = registerRepository.loginUser(email,password)


    fun getAllStories(page: Int, size: Int): LiveData<ResponseGetStories> {
        return registerRepository.getStories(page, size)
    }
}
