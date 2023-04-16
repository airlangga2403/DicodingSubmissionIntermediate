package com.pa.submissionaplikasistoryapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pa.submissionaplikasistoryapp.data.remote.pref.UserTokenPref
import com.pa.submissionaplikasistoryapp.data.remote.response.*
import com.pa.submissionaplikasistoryapp.data.remote.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
                        if (responseBody != null) {
                            data.value = responseBody!!
                        } else {
                            data.value =
                                ResponseRegister(error = true, message = "Invalid credentials")
                        }
                    } else {
                        data.value = ResponseRegister(error = true, message = "Invalid credentials")
                    }
                }

                override fun onFailure(call: Call<ResponseRegister>, t: Throwable) {
                    Log.e(TAG, t.message.toString())
                    data.value = ResponseRegister(error = true, message = "Invalid credentials")
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            data.value = ResponseRegister(error = true, message = "Invalid credentials")
        }
        return data
    }


    fun loginUser(email: String, password: String): LiveData<ResponseLogin> {
        val data = MutableLiveData<ResponseLogin>()
        try {
            val client = apiService.userLogin(email, password)
            client.enqueue(object : Callback<ResponseLogin> {
                override fun onResponse(
                    call: Call<ResponseLogin>,
                    response: Response<ResponseLogin>
                ) {
                    if (response.isSuccessful) {
                        val loginResult = response.body()?.loginResult
                        if (loginResult != null) {
                            data.value = response.body()
                            val token = loginResult.token
                            token?.let {
                                UserTokenPref.setToken(token)
                            }
                        } else {
                            data.value = ResponseLogin(
                                LoginResult("", "", ""),
                                error = true,
                                message = "Invalid credentials"
                            )

                        }
                    } else {
                        data.value = ResponseLogin(
                            LoginResult("", "", ""),
                            error = true,
                            message = "Invalid credentials"
                        )

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

    fun getStories(page: Int, size: Int): LiveData<ResponseGetStories> {
        val client = apiService.getStories(page, size)
        val data = MutableLiveData<ResponseGetStories>()
        client.enqueue(object : Callback<ResponseGetStories> {
            override fun onResponse(
                call: Call<ResponseGetStories>,
                response: Response<ResponseGetStories>
            ) {
                if (response.isSuccessful) {
                    data.value = response.body()
                }

            }

            override fun onFailure(call: Call<ResponseGetStories>, t: Throwable) {
                Log.e(TAG, t.message.toString())
            }
        })
        return data
    }

    fun getDetailStory(id: String): LiveData<ResponseGetDetailStories> {
        val data = MutableLiveData<ResponseGetDetailStories>()
        val client = apiService.getStoryDetail(id)
        client.enqueue(object : Callback<ResponseGetDetailStories> {
            override fun onResponse(
                call: Call<ResponseGetDetailStories>,
                response: Response<ResponseGetDetailStories>
            ) {
                if (response.isSuccessful) {
                    data.value = response.body()
                }
            }

            override fun onFailure(call: Call<ResponseGetDetailStories>, t: Throwable) {
                Log.e(TAG, "OnFailure : ${t.message.toString()}")
            }
        })
        return data

    }

    fun uploadStory(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Double,
        lon: Double,
        multiPort: String
    ): LiveData<ResponseUploadStory> {
        val data = MutableLiveData<ResponseUploadStory>()
        val client = apiService.uploadStory(file, description, lat, lon, multiPort)
        client.enqueue(object: Callback<ResponseUploadStory> {
            override fun onResponse(
                call: Call<ResponseUploadStory>,
                response: Response<ResponseUploadStory>
            ) {
               if (response.isSuccessful) {
                   val responseBody = response.body()
                   if (responseBody != null ) {
                       data.value = response.body()
                   } else {
                       data.value = response.body()
                   }

               }
            }

            override fun onFailure(call: Call<ResponseUploadStory>, t: Throwable) {
               Log.e(TAG, "OnFailure : ${t.message.toString()}")
            }
        })
        return data
    }

    fun logoutUser() {
        UserTokenPref.setToken("")
        UserTokenPref.setLoggedIn(false)
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
