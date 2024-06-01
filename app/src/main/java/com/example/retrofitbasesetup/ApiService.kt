package com.example.retrofitbasesetup

import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("api/v1/employees")
    suspend fun getData():Response<DataResponse>

}