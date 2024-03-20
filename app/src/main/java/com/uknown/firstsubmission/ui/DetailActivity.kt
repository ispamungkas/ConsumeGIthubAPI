package com.uknown.firstsubmission.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.uknown.firstsubmission.R
import com.uknown.firstsubmission.databinding.ActivityDetailBinding
import com.uknown.firstsubmission.local.User
import com.uknown.firstsubmission.ui.adapter.SectionAdapter
import com.uknown.firstsubmission.ui.viewmodel.DetailViewModel
import com.uknown.firstsubmission.ui.viewmodel.MainViewModel
import com.uknown.firstsubmission.utils.Injection
import com.uknown.firstsubmission.utils.Resources
import com.uknown.firstsubmission.utils.ViewModelFactory

class DetailActivity : AppCompatActivity() {

    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding!!
    private val detailViewModel: DetailViewModel by viewModels {
        ViewModelFactory(Injection.detailRepository(applicationContext) as Any)
    }

    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory(Injection.mainRepository(applicationContext) as Any)
    }

    private var user: User? = null


    companion object {
        @StringRes
        private val TAB = intArrayOf(
            R.string.followers,
            R.string.following
        )
        const val SAMPLE = "USER"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val data: User? = intent.getParcelableExtra(SAMPLE)
        if (data != null) {
            user = data
        }
        getDetailUser()

        val sectionPagerAdapter = data?.username?.let { SectionAdapter(this, it) }
        binding.viewPager.adapter = sectionPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = resources.getString(TAB[position])
        }.attach()

        supportActionBar?.elevation = 0f

        user?.username?.let {
            detailViewModel.getDetailUser(it)
            attachToView()
        }

        user?.username?.let { name ->
            mainViewModel.getDataUser(name).observe(this) { d ->
                if (d != null) {
                    binding.fab.setImageResource(R.drawable.selected_favorite)
                    binding.fab.setOnClickListener {
                        user?.let { it1 -> mainViewModel.deleteDataDao(it1) }
                        Toast.makeText(this, "Has deleted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    binding.fab.setImageResource(R.drawable.unselecteed_favorite)
                    binding.fab.setOnClickListener {
                        user?.let { it1 -> mainViewModel.insertDataDao(it1) }
                        Toast.makeText(this, "Save Successfull", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun getDetailUser() {
        user?.username.let {
            if (it != null) {
                detailViewModel.getDetailUser(it)
            }
        }
    }

    private fun attachToView() {
        detailViewModel.detailUser.observe(this) {
            when (it) {
                is Resources.Loading -> isLoading(true)
                is Resources.Failed -> {
                    isLoading(false)
                    Toast.makeText(this@DetailActivity, it.message, Toast.LENGTH_SHORT).show()
                }

                is Resources.Success -> {
                    isLoading(false)
                    val data = it.data
                    binding.name.text = data?.name
                    binding.username.text = data?.login
                    binding.followersCount.text =
                        getString(R.string.countFollowers, data?.followers)
                    binding.followingCount.text =
                        getString(R.string.countFollowing, data?.following)
                    Glide.with(this)
                        .load(data?.avatarUrl)
                        .into(binding.image)
                    binding.btnShare.setOnClickListener {
                        data?.avatarUrl?.let { it1 -> share(it1) }
                    }

                }
            }
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

    private fun share(uri: String) {
        val dataIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, uri)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(dataIntent, "Share")
        startActivity(shareIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}