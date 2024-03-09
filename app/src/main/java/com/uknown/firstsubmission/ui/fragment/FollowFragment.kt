package com.uknown.firstsubmission.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.uknown.firstsubmission.databinding.FragmentFollowersFragmnetBinding
import com.uknown.firstsubmission.network.response.ItemsItem
import com.uknown.firstsubmission.ui.DetailActivity
import com.uknown.firstsubmission.ui.adapter.UserListAdapter
import com.uknown.firstsubmission.ui.viewmodel.DetailViewModel

class FollowFragment : Fragment() {

    companion object {
        const val SECTION = "FollowFragment"
        const val SECTION2 ="FollowFragmentss"
    }

    private var _binding: FragmentFollowersFragmnetBinding? = null
    private val binding get () = _binding!!
    private lateinit var detailViewModel: DetailViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentFollowersFragmnetBinding.inflate(inflater, container, false)
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return activity?.let {
                    DetailViewModel(
                        it.application
                    )
                } as T
            }
        }
        detailViewModel = ViewModelProvider(this, factory).get(DetailViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val index = arguments?.getInt(SECTION)
        val username = arguments?.getString(SECTION2)

        username?.let { detailViewModel.getFollowers(it) }

        if (index == 1) {
            username?.let {
                detailViewModel.getFollowers(it)
                detailViewModel.listFollows.observe(viewLifecycleOwner) {
                    attachRecycler(it)
                }
            }
        } else {
            username?.let {
                detailViewModel.getFollowing(it)
                detailViewModel.listFollow.observe(viewLifecycleOwner) {
                    attachRecycler(it)
                }
            }
        }

        binding.rvSearch.setHasFixedSize(true)
        binding.rvSearch.layoutManager = LinearLayoutManager(requireContext())
        detailViewModel.isLoading.observe(viewLifecycleOwner){
            isLoading(it)
        }

    }

    private fun attachRecycler(list: List<ItemsItem>) {
        val adapter = UserListAdapter {
            val go = Intent(requireContext(), DetailActivity::class.java)
            go.putExtra(DetailActivity.USER, it.login)
            startActivity(go)
            activity?.finish()
        }
        adapter.submitList(list)
        binding.rvSearch.adapter = adapter
    }

    private fun isLoading(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

}