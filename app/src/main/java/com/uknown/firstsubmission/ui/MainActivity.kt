package com.uknown.firstsubmission.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.uknown.firstsubmission.R
import com.uknown.firstsubmission.databinding.ActivityMainBinding
import com.uknown.firstsubmission.network.response.UserResponse
import com.uknown.firstsubmission.ui.adapter.UserListAdapter
import com.uknown.firstsubmission.ui.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainViewModel(
                    application
                ) as T
            }
        }
        mainViewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)

        binding.rvSearch.layoutManager = LinearLayoutManager(this@MainActivity)
        binding.rvSearch.setHasFixedSize(true)

        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView.editText
                .setOnEditorActionListener { textView, i, keyEvent ->
                    searchBar.setText(searchView.text.toString())
                    searchBar.text.let {
                        mainViewModel.getSpesificUserData(it.toString())
                    }
                    searchView.hide()
                    false
                }
        }

        mainViewModel.apply {
            userResponse.observe(this@MainActivity) {
                attachIntoRecycleView(it)
            }
            isLoading.observe(this@MainActivity) {
                isLoading(it)
            }
        }

    }

    private fun attachIntoRecycleView(data: UserResponse) {
        val adapter = UserListAdapter {
            val go = Intent(this@MainActivity, DetailActivity::class.java)
            go.putExtra(DetailActivity.USER, it.login)
            startActivity(go)
        }
        adapter.submitList(data.items)
        binding.rvSearch.adapter = adapter
    }

    private fun isLoading (value: Boolean) {
        binding.progressBar.visibility = if (value) View.VISIBLE else View.INVISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.setting -> Toast.makeText(this@MainActivity, "Action Setting Displayed", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}