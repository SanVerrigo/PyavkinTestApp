package com.pyavkin.pyavkintestapp.api.entities

import com.google.gson.annotations.SerializedName

class GifEntity(
    @SerializedName("gifURL")
    val url: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("id")
    val id: Long,
)
