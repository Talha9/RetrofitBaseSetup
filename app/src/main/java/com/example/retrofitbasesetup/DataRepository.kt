package com.example.retrofitbasesetup


class DataRepository : IDataRepository {
    private val apiService = RetrofitClient.createService(ApiService::class.java)
    override suspend fun getUsersData(): IResponse<DataResponse> {
        return handleRequest {
            apiService.getData()
        }
    }


}