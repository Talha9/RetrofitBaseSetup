package com.example.retrofitbasesetup

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


@Keep
class DataResponse {

    @SerializedName("data")
    @Expose
    var data: List<Data>?=null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null


}