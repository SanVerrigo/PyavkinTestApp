package com.pyavkin.pyavkintestapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.tabs.TabLayout
import com.pyavkin.pyavkintestapp.databinding.ActivityMainBinding
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by lazy {
        MainViewModel(this)
    }

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val onTabSelectedListener = object : TabLayout.OnTabSelectedListener {

        override fun onTabSelected(tab: TabLayout.Tab) {
            val section = TabInfo.Section.valueOf(tab.tag as String)
            viewModel.setCurrentSection(section)
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) = Unit

        override fun onTabReselected(tab: TabLayout.Tab?) = Unit
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewModel.currentGifUri.observe(this, { gifUri ->
            Glide.with(this)
                .load(gifUri)
                .transform(CenterCrop())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.contentGif)
        })
        viewModel.gifState.observe(this, ::setState)
        viewModel.currentTab.observe(this, { tabInfo ->
            Glide.with(this)
                .load(R.color.placeholder_color)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.contentGif)
            viewModel.initiateGifLoading(tabInfo)
        })
        viewModel.currentGifDescription.observe(this, binding.labelGif::setText)
        binding.forwardBtn.setOnClickListener {
            viewModel.incrementGifIndex()
            viewModel.refreshButtonsState()
        }
        viewModel.forwardActive.observe(this, binding.forwardBtn::setEnabled)
        viewModel.backActive.observe(this, binding.backBtn::setEnabled)
        binding.backBtn.setOnClickListener {
            viewModel.decrementGifIndex()
            viewModel.refreshButtonsState()
        }
        binding.tryAgainBtn.setOnClickListener {
            viewModel.initiateGifLoading(viewModel.currentTabValue)
        }
        binding.shareBtn.setOnClickListener {
            viewModel.shareCurrentGif(this)
        }
        binding.tabsContainer.addOnTabSelectedListener(onTabSelectedListener)
        inhabitWithTabs()
    }

    private fun inhabitWithTabs() {
        viewModel.tabs
            .forEach { tabInfo ->
                setTabBySection(tabInfo.section)
            }
        binding.tabsContainer.selectTab(binding.tabsContainer.getTabAt(0))
    }

    private fun setTabBySection(section: TabInfo.Section) {
        val tab = binding.tabsContainer.newTab()
        tab.tag = section.toString()
        val tabName = when (section) {
            TabInfo.Section.RANDOM -> R.string.random_label
            TabInfo.Section.TOP -> R.string.top_label
            TabInfo.Section.HOT -> R.string.hot_label
            TabInfo.Section.LATEST -> R.string.latest_label
        }
        tab.setText(tabName)
        binding.tabsContainer.addTab(tab)
    }

    private fun setState(loadState: LoadState) {
        when (loadState) {
            is LoadState.Loaded -> {
                binding.loadingIndicator.isGone = true
                binding.tryAgainContainer.isGone = true
                gifContainerSetVisible(true)
                if (loadState.urlIsDefined) {
                    binding.urlNotDefinedWarning.isGone = true
                    binding.contentGif.isVisible = true
                    binding.shareBtn.isVisible = true
                } else {
                    binding.urlNotDefinedWarning.isVisible = true
                    binding.contentGif.isGone = true
                    binding.shareBtn.isGone = true
                }
            }
            LoadState.Loading -> {
                binding.loadingIndicator.isVisible = true
                binding.tryAgainContainer.isGone = true
                binding.urlNotDefinedWarning.isGone = true
                binding.shareBtn.isGone = true
                gifContainerSetVisible(true)
            }
            LoadState.Ended -> {
                binding.loadingIndicator.isGone = true
                binding.tryAgainContainer.isGone = true
                binding.urlNotDefinedWarning.isGone = true
                binding.shareBtn.isGone = true
                binding.labelGif.setText(R.string.last_loaded)
                gifContainerSetVisible(true)
            }
            LoadState.NetworkIsNotAvailable -> {
                binding.loadingIndicator.isGone = true
                binding.tryAgainContainer.isVisible = true
                binding.urlNotDefinedWarning.isGone = true
                binding.shareBtn.isGone = true
                gifContainerSetVisible(false)
            }
            is LoadState.Failed -> {
                when (loadState.error) {
                    is SocketTimeoutException -> {
                        viewModel.gifState.postValue(LoadState.NetworkIsNotAvailable)
                    }
                    else -> {
                        Toast.makeText(this, loadState.error.message, Toast.LENGTH_SHORT).show()
                        binding.loadingIndicator.isGone = true
                        binding.tryAgainContainer.isGone = true
                        binding.urlNotDefinedWarning.isGone = true
                        binding.shareBtn.isGone = true
                        gifContainerSetVisible(true)
                    }
                }

            }
        }
    }

    private fun gifContainerSetVisible(visible: Boolean) {
        binding.gifContainer.isVisible = visible
        binding.forwardBtn.isVisible = visible
        binding.backBtn.isVisible = visible
        binding.tabsContainer.isVisible = visible
    }
}
