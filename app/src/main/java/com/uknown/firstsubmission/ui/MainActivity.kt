package com.uknown.firstsubmission.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.uknown.firstsubmission.databinding.ActivityMainBinding
import com.uknown.firstsubmission.local.User
import com.uknown.firstsubmission.network.response.UserResponse
import com.uknown.firstsubmission.ui.adapter.UserListAdapter
import com.uknown.firstsubmission.ui.viewmodel.MainViewModel
import com.uknown.firstsubmission.utils.Injection
import com.uknown.firstsubmission.utils.Resources
import com.uknown.firstsubmission.utils.ViewModelFactory

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory(Injection.mainRepository(applicationContext) as Any)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setTheme()

        binding.rvSearch.layoutManager = LinearLayoutManager(this@MainActivity)
        binding.rvSearch.setHasFixedSize(true)

        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView.editText
                .setOnEditorActionListener { textView, i, keyEvent ->
                    searchBar.setText(searchView.text.toString())
                    searchBar.text.let {
                        mainViewModel.getAllData(it.toString())
                    }
                    searchView.hide()
                    false
                }
        }

        mainViewModel.apply {
            data.observe(this@MainActivity) {
                when (it) {
                    is Resources.Loading -> isLoading(true)
                    is Resources.Failed -> {
                        isLoading(false)
                        Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_SHORT).show()
                    }

                    is Resources.Success -> {
                        isLoading(false)
                        attachIntoRecycleView(it.data)
                    }
                }
            }
        }

        binding.apply {
            btnFavorite.setOnClickListener {
                val intent = Intent(this@MainActivity, FavoriteActivity::class.java)
                startActivity(intent)
            }
            darkAction.setOnCheckedChangeListener { compoundButton, check ->
                mainViewModel.setThemeSetting(check)
            }
        }
    }

    private fun attachIntoRecycleView(data: UserResponse) {
        val adapter = UserListAdapter {
            val go = Intent(this@MainActivity, DetailActivity::class.java)
            val usr = it.login?.let { it1 ->
                User(
                    it1,
                    it.avatarUrl,
                )
            }
            go.putExtra(DetailActivity.SAMPLE, usr)
            startActivity(go)
        }
        adapter.submitList(data.items)
        binding.rvSearch.adapter = adapter
    }

    private fun isLoading(value: Boolean) {
        binding.progressBar.visibility = if (value) View.VISIBLE else View.INVISIBLE
    }

    private fun setTheme() {
        mainViewModel.getThemeSetting().observe(this@MainActivity) {
            if (it) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.darkAction.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding.darkAction.isChecked = false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}