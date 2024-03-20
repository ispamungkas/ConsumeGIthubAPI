package com.uknown.firstsubmission.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.uknown.firstsubmission.databinding.ActivityFavoriteBinding
import com.uknown.firstsubmission.local.User
import com.uknown.firstsubmission.network.response.ItemsItem
import com.uknown.firstsubmission.ui.adapter.UserListAdapter
import com.uknown.firstsubmission.ui.viewmodel.MainViewModel
import com.uknown.firstsubmission.utils.Injection
import com.uknown.firstsubmission.utils.ViewModelFactory

class FavoriteActivity : AppCompatActivity() {

    private var _binding: ActivityFavoriteBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory(Injection.mainRepository(applicationContext) as Any)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvFavorite.layoutManager = LinearLayoutManager(this)
        binding.rvFavorite.setHasFixedSize(true)

        mainViewModel.getAllDataDao().observe(this) {
            val data = ArrayList<ItemsItem>()
            for (item in it) {
                val temp = ItemsItem(
                    null,
                    null,
                    null,
                    item.username,
                    null,
                    null,
                    item.avatarUrl,
                    null,
                    null
                )
                data.add(temp)
            }
            attachIntoAdapter(data)
        }

    }

    private fun attachIntoAdapter(item: List<ItemsItem>) {
        val adapter = UserListAdapter {
            val intent = Intent(this@FavoriteActivity, DetailActivity::class.java)
            val user = it.login?.let { it1 ->
                User(
                    it1,
                    it.avatarUrl
                )
            }
            intent.putExtra(DetailActivity.SAMPLE, user)
            startActivity(intent)
        }
        adapter.submitList(item)
        binding.rvFavorite.adapter = adapter
    }
}