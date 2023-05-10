package com.pa.submissionaplikasistoryapp.data.remote.di

import android.content.Context
import com.pa.submissionaplikasistoryapp.data.remote.retrofit.ApiConfig
import com.pa.submissionaplikasistoryapp.data.repository.RegisterRepository
import com.pa.submissionaplikasistoryapp.database.StoryDatabase

object Injection {
    fun provideRepository(context: Context): RegisterRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return RegisterRepository.getInstance(database, apiService)
    }
}