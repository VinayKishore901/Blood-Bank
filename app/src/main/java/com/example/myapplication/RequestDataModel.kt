package com.example.myapplication

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class RequestDataModel {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("url")
    @Expose
    var url: String? = null

    @SerializedName("number")
    @Expose
    var number: String? = null

}

