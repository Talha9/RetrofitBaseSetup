package com.example.retrofitbasesetup

interface IDataRepository:ApiRepo {

    suspend fun getUsersData():IResponse<DataResponse>
}