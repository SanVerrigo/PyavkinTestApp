package com.pyavkin.pyavkintestapp

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pyavkin.pyavkintestapp.api.GifRepo
import com.pyavkin.pyavkintestapp.api.GifRepoImpl
import com.pyavkin.pyavkintestapp.api.entities.GifEntity
import com.pyavkin.pyavkintestapp.utils.NetworkUtils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers


class MainViewModel(context: Context) : ViewModel() {

    private val disposables = CompositeDisposable()
    private val repo: GifRepo = GifRepoImpl(context)

    val currentTab = MutableLiveData<TabInfo>()
    val currentGifUri = MutableLiveData<Uri>()
    val currentGifDescription = MutableLiveData<String>()
    val gifState = MutableLiveData<LoadState>()

    val currentTabValue: TabInfo
        get() = currentTab.value!!

    val forwardActive = MutableLiveData(true)
    val backActive = MutableLiveData(false)

    private lateinit var loadingGifDisposable: Disposable

    val tabs = listOf(
        TabInfo(TabInfo.Section.RANDOM),
        TabInfo(TabInfo.Section.LATEST),
        TabInfo(TabInfo.Section.HOT),
        TabInfo(TabInfo.Section.TOP),
    )

    private val networkUtils = NetworkUtils(context)

    fun initiateGifLoading(tabInfo: TabInfo) {
        if (!networkUtils.isNetworkAvailable()) {
            gifState.postValue(LoadState.NetworkIsNotAvailable)
            return
        }
        if (tabInfo.currentLoadedGifInd < tabInfo.loadedGifs.size) {
            val gifInfo = tabInfo.loadedGifs[tabInfo.currentLoadedGifInd]
            currentGifDescription.postValue(gifInfo.description)
            refreshButtonsState()
            loadGif(gifInfo.url, gifInfo.id, tabInfo.section.sectionName, gifInfo.description)
        } else {
            gifState.postValue(LoadState.Loading)
            val gifsSingle: Single<List<GifEntity>> =
                if (tabInfo.section == TabInfo.Section.RANDOM) {
                    repo.getRandomGif().map { listOf(it) }
                } else {
                    repo.getGifs(tabInfo.section.sectionName, tabInfo.pageToLoad)
                }
            gifsSingle
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { gifs ->
                        tabInfo.loadedGifs.addAll(gifs.map(::convertGifEntity))
                        refreshButtonsState()
                        tabInfo.pageToLoad++
                        if (tabInfo.currentLoadedGifInd >= tabInfo.loadedGifs.size) {
                            gifState.postValue(LoadState.Ended)
                            disposeLoadingGif()
                        } else {
                            val gifInfo = tabInfo.loadedGifs[tabInfo.currentLoadedGifInd]
                            currentGifDescription.postValue(gifInfo.description)
                            loadGif(
                                gifInfo.url, gifInfo.id, tabInfo.section.name, gifInfo.description
                            )
                        }
                    },
                    { error ->
                        Log.e("Err", error.message!!)
                    }
                ).also { disposables.add(it) }
        }
    }

    private fun loadGif(url: String, id: Long, section: String, title: String) {
        gifState.postValue(LoadState.Loading)
        disposeLoadingGif()
        loadingGifDisposable = repo.downloadGif(url, id, section, title)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { gifFileUri ->
                    gifState.postValue(LoadState.Loaded)
                    currentGifUri.postValue(gifFileUri)
                    loadingGifDisposable.dispose()
                },
                { error ->
                    gifState.postValue(LoadState.Failed(error))
                    Log.e("Err", error.message!!)
                }
            ).also { disposables.add(it) }
    }

    fun setCurrentSection(section: TabInfo.Section) {
        val tabInfo = tabs.first { it.section == section }
        currentTab.postValue(tabInfo)
    }

    fun refreshButtonsState() {
        val forwardNewValue = currentTabValue.currentLoadedGifInd < currentTabValue.loadedGifs.size
        if (forwardActive.value!! != forwardNewValue) {
            forwardActive.postValue(forwardNewValue)
        }
        val backNewValue = currentTabValue.currentLoadedGifInd > 0
        if (backActive.value!! != backNewValue) {
            backActive.postValue(backNewValue)
        }
    }

    private fun convertGifEntity(gifEntity: GifEntity): GifEntityUI {
        return GifEntityUI(
            gifEntity.url,
            gifEntity.description,
            gifEntity.id
        )
    }

    private fun disposeLoadingGif() {
        if (::loadingGifDisposable.isInitialized && !loadingGifDisposable.isDisposed) {
            loadingGifDisposable.dispose()
        }
    }

    fun incrementGifIndex() {
        val tabInfo = currentTabValue
        if (tabInfo.currentLoadedGifInd < tabInfo.loadedGifs.size) {
            currentTab.postValue(
                tabInfo.apply { ++tabInfo.currentLoadedGifInd }
            )
        }
    }

    fun decrementGifIndex() {
        val tabInfo = currentTabValue
        if (tabInfo.currentLoadedGifInd > 0) {
            currentTab.postValue(
                tabInfo.apply { --tabInfo.currentLoadedGifInd }
            )
        }
    }

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }
}

