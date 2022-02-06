package com.pyavkin.pyavkintestapp

sealed class LoadState {

    object Loaded : LoadState()

    object Loading : LoadState()

    object Ended : LoadState()

    object NetworkIsNotAvailable : LoadState()

    class Failed(val error: Throwable) : LoadState()
}
