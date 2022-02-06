package com.pyavkin.pyavkintestapp

sealed class LoadState {

    class Loaded(val urlIsDefined: Boolean) : LoadState()

    object Loading : LoadState()

    object Ended : LoadState()

    object NetworkIsNotAvailable : LoadState()

    class Failed(val error: Throwable) : LoadState()
}
