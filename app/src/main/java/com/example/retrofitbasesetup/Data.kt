package com.example.retrofitbasesetup

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


@Keep
class Data {
    @SerializedName("employee_age")
    @Expose
    val employee_age: Int? = null

    @SerializedName("employee_name")
    @Expose
    val employee_name: String? = null

    @SerializedName("employee_salary")
    @Expose
    val employee_salary: Int? = null

    @SerializedName("id")
    @Expose
    val id: Int? = null

    @SerializedName("profile_image")
    @Expose
    val profile_image: String? = null
}