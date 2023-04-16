package com.pa.submissionaplikasistoryapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pa.submissionaplikasistoryapp.data.remote.pref.UserTokenPref
import com.pa.submissionaplikasistoryapp.data.remote.response.ListStoryItem
import com.pa.submissionaplikasistoryapp.data.remote.response.ResponseGetStories
import com.pa.submissionaplikasistoryapp.data.remote.response.ResponseLogin
import com.pa.submissionaplikasistoryapp.data.remote.response.ResponseRegister
import com.pa.submissionaplikasistoryapp.data.remote.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterRepository private constructor(
    private val apiService: ApiService
) {
    fun registerUser(name: String, email: String, password: String): LiveData<ResponseRegister> {
        val data = MutableLiveData<ResponseRegister>()
        try {
            val client = apiService.userRegister(name, email, password)
            client.enqueue(object : Callback<ResponseRegister> {
                override fun onResponse(
                    call: Call<ResponseRegister>,
                    response: Response<ResponseRegister>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            data.value = response.body()
                        }

                    }
                }

                override fun onFailure(call: Call<ResponseRegister>, t: Throwable) {
                    Log.e(TAG, t.message.toString())
                }
            })

        } catch (e: Exception) {
            Log.e("register", e.message.toString())
        }
        return data
    }

        fun loginUser(email: String, password: String): LiveData<ResponseLogin> {
        val data = MutableLiveData<ResponseLogin>()
        try {
            val client = apiService.userLogin(email, password)
            client.enqueue(object: Callback<ResponseLogin> {
                override fun onResponse(
                    call: Call<ResponseLogin>,
                    response: Response<ResponseLogin>
                ) {
                    if (response.isSuccessful) {
                        data.value = response.body()
                        UserTokenPref.setToken(response.body()!!.loginResult.token)
                    }
                }

                override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                    Log.e(TAG, "onFailure: ${t.message}")
                }
            })

        } catch (e: Exception) {
            Log.e(TAG, "onFailure: ${e.message}")
        }
        return data
    }

    fun getAllStories(token: String) : LiveData<List<ListStoryItem>> {
        val client = apiService.getAllStories(token)
        val data = MutableLiveData<List<ListStoryItem>>()
        client.enqueue(object: Callback<List<ListStoryItem>> {
            override fun onFailure(call: Call<List<ListStoryItem>>, t: Throwable) {
                Log.e("OnFailure", t.message.toString())
            }

            override fun onResponse(
                call: Call<List<ListStoryItem>>,
                response: Response<List<ListStoryItem>>
            ) {
                if (response.isSuccessful) {
                    data.value = response.body()
                }
            }
        })
        return data
    }

    companion object {
        private val TAG = "RegisterRepository"

        @Volatile
        private var instance: RegisterRepository? = null
        fun getInstance(
            apiService: ApiService
        ): RegisterRepository =
            instance ?: synchronized(this) {
                instance ?: RegisterRepository(apiService)
            }.also { instance = it }
    }

}
