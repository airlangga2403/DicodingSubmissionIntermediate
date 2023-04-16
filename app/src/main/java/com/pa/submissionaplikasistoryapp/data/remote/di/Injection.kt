package com.pa.submissionaplikasistoryapp.data.remote.di

import android.content.Context
import com.pa.submissionaplikasistoryapp.data.remote.retrofit.ApiConfig
import com.pa.submissionaplikasistoryapp.data.repository.RegisterRepository

object Injection {
    fun provideRepository(context: Context): RegisterRepository {
        val apiService = ApiConfig.getApiService()
        return RegisterRepository.getInstance(apiService)
    }
}