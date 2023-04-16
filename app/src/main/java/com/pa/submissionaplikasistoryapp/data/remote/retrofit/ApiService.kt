package com.pa.submissionaplikasistoryapp.data.remote.retrofit


import com.pa.submissionaplikasistoryapp.data.remote.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun userRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<ResponseRegister>

    @FormUrlEncoded
    @POST("login")
    fun userLogin(
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<ResponseLogin>

    @GET("stories")
    fun getStories(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Call<ResponseGetStories>

    @GET("stories/{id}")
    fun getStoryDetail(
        @Path("id")
        id : String
    ): Call<ResponseGetDetailStories>

    @Multipart
    @POST("stories")
    fun uploadStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double?,
        @Part("lon") lon: Double?,
        @Header("Accept") type: String,
    ): Call<ResponseUploadStory>


}
