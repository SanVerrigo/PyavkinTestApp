package com.pyavkin.pyavkintestapp.api.entities

import com.google.gson.annotations.SerializedName

class GifEntity(
    @SerializedName("id")
    val id: Long,

    @SerializedName("description")
    val description: String,

    @SerializedName("gifURL")
    val url: String? = null,
)
