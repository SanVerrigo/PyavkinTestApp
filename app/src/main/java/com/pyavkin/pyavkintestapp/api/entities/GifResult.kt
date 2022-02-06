package com.pyavkin.pyavkintestapp.api.entities

import com.google.gson.annotations.SerializedName

class GifResult(
    @SerializedName("result")
    val result: List<GifEntity>,
)
