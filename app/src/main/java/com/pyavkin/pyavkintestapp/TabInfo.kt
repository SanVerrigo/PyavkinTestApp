package com.pyavkin.pyavkintestapp

class TabInfo(
    val section: String,
    val loadedGifs: MutableList<GifEntityUI> = mutableListOf(),
    var currentLoadedGifInd: Int = 0,
    var pageToLoad: Int = 0,
)
