package com.pa.submissionaplikasistoryapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import com.pa.submissionaplikasistoryapp.data.paging.StoryRemoteMediator
import com.pa.submissionaplikasistoryapp.data.remote.pref.UserTokenPref
import com.pa.submissionaplikasistoryapp.data.remote.response.*
import com.pa.submissionaplikasistoryapp.data.remote.retrofit.ApiService
import com.pa.submissionaplikasistoryapp.database.StoryDatabase
import com.pa.submissionaplikasistoryapp.database.StoryLocation
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterRepository private constructor(
    private val database: StoryDatabase,
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
                            data.value = responseBody
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
        val client = apiService.userLogin(email, password)
        client.enqueue(object : Callback<ResponseLogin> {
            override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                if (response.isSuccessful) {
                    val loginResult = response.body()?.loginResult
                    if (loginResult != null) {
                        data.value = response.body()
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
                data.value = ResponseLogin(
                    LoginResult("", "", ""),
                    error = true,
                    message = "Failed to connect to the server"
                )
            }
        })
        return data
    }


    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = 5, enablePlaceholders = false),
            remoteMediator = StoryRemoteMediator(database, apiService)
        ) {
            database.storyDao().getAllStory()
        }.liveData
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
        client.enqueue(object : Callback<ResponseUploadStory> {
            override fun onResponse(
                call: Call<ResponseUploadStory>,
                response: Response<ResponseUploadStory>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
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

    fun uploadStoryWithoutLocation(
        file: MultipartBody.Part,
        description: RequestBody,
        multiPort: String
    ): LiveData<ResponseUploadStory> {
        val data = MutableLiveData<ResponseUploadStory>()
        val client = apiService.uploadStoryWithoutLocation(file, description, multiPort)
        client.enqueue(object : Callback<ResponseUploadStory> {
            override fun onResponse(
                call: Call<ResponseUploadStory>,
                response: Response<ResponseUploadStory>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
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
        UserTokenPref.setToken("token")
        UserTokenPref.setLoggedIn(false)
    }

    fun getLocationUser(): LiveData<List<StoryLocation>> = database.storyDao().getUserLocation()


    companion object {
        private const val TAG = "RegisterRepository"

        @Volatile
        private var instance: RegisterRepository? = null
        fun getInstance(
            database: StoryDatabase,
            apiService: ApiService
        ): RegisterRepository =
            instance ?: synchronized(this) {
                instance ?: RegisterRepository(database, apiService)
            }.also { instance = it }
    }

}
