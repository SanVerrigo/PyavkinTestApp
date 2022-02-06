package com.pyavkin.pyavkintestapp

class TabInfo(
    val section: Section,
    val loadedGifs: MutableList<GifEntityUI> = mutableListOf(),
    var currentLoadedGifInd: Int = 0,
    var pageToLoad: Int = 0,
) {

    enum class Section(val sectionName: String) {
        RANDOM("random"), LATEST("latest"), TOP("top"), HOT("hot")
    }
}
