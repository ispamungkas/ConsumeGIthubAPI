package com.uknown.firstsubmission.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.uknown.firstsubmission.R
import com.uknown.firstsubmission.databinding.ActivityDetailBinding
import com.uknown.firstsubmission.ui.adapter.SectionAdapter
import com.uknown.firstsubmission.ui.viewmodel.DetailViewModel

class DetailActivity : AppCompatActivity() {

    private var _binding : ActivityDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var detailViewModel: DetailViewModel

    companion object {
        @StringRes
        private val TAB = intArrayOf(
            R.string.followers,
            R.string.following
        )
        const val USER = "Get user"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initiateViewModel()
        getDetailUser()
        attachToView()

        val username = intent.getStringExtra(USER)
        val sectionPagerAdapter = username?.let { SectionAdapter(this, it) }
        binding.viewPager.adapter = sectionPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = resources.getString(TAB[position])
        }.attach()

        supportActionBar?.elevation = 0f
    }

    private fun initiateViewModel() {
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DetailViewModel(
                    application
                ) as T
            }
        }
        detailViewModel = ViewModelProvider(this, factory).get(DetailViewModel::class.java)
    }

    private fun getDetailUser() {
        intent.getStringExtra(USER).let {
            if (it != null) {
                detailViewModel.getDetailUser(it)
            }
        }
    }

    private fun attachToView() {
        detailViewModel.userDetail.observe(this) {
            binding.name.text = it.name
            binding.username.text = it.login
            binding.followersCount.text = getString(R.string.countFollowers, it.followers)
            binding.followingCount.text = getString(R.string.countFollowing, it.following)
            Glide.with(this)
                .load(it.avatarUrl)
                .into(binding.image)
        }
        detailViewModel.isLoading.observe(this) {
            isLoading(it)
        }
    }

    private fun isLoading(state: Boolean) {
        if (state) {
            with(binding) {
                progressBar.visibility = View.VISIBLE
                name.visibility = View.INVISIBLE
                username.visibility = View.INVISIBLE
                image.visibility = View.INVISIBLE
            }
        } else {
            with(binding) {
                progressBar.visibility = View.GONE
                name.visibility = View.VISIBLE
                username.visibility = View.VISIBLE
                image.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}