package com.uknown.firstsubmission.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.uknown.firstsubmission.databinding.FragmentFollowersFragmnetBinding
import com.uknown.firstsubmission.local.User
import com.uknown.firstsubmission.network.response.ItemsItem
import com.uknown.firstsubmission.ui.DetailActivity
import com.uknown.firstsubmission.ui.adapter.UserListAdapter
import com.uknown.firstsubmission.ui.viewmodel.DetailViewModel
import com.uknown.firstsubmission.utils.Injection
import com.uknown.firstsubmission.utils.Resources
import com.uknown.firstsubmission.utils.ViewModelFactory

class FollowFragment : Fragment() {

    companion object {
        const val SECTION = "FollowFragment"
        const val SECTION2 = "FollowFragmentss"
    }

    private var _binding: FragmentFollowersFragmnetBinding? = null
    private val binding get() = _binding!!
    private val detailViewModel: DetailViewModel by viewModels {
        ViewModelFactory(Injection.detailRepository(requireContext()) as Any)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFollowersFragmnetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val index = arguments?.getInt(SECTION)
        val username = arguments?.getString(SECTION2)

        username?.let { detailViewModel.getFollowers(it) }

        if (index == 1) {
            username?.let {
                detailViewModel.getFollowers(it).observe(viewLifecycleOwner) {
                    when (it) {
                        is Resources.Loading -> isLoading(true)
                        is Resources.Failed -> {
                            isLoading(false)
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }

                        is Resources.Success -> {
                            isLoading(false)
                            it.data?.let { it1 -> attachRecycler(it1) }
                        }
                    }
                }
            }
        } else {
            username?.let {
                detailViewModel.getFollowing(it).observe(viewLifecycleOwner) {
                    when (it) {
                        is Resources.Loading -> isLoading(true)
                        is Resources.Failed -> {
                            isLoading(false)
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }

                        is Resources.Success -> {
                            isLoading(false)
                            it.data?.let { it1 -> attachRecycler(it1) }
                        }
                    }
                }
            }
        }

        binding.rvSearch.setHasFixedSize(true)
        binding.rvSearch.layoutManager = LinearLayoutManager(requireContext())


    }

    private fun attachRecycler(list: List<ItemsItem>) {
        val adapter = UserListAdapter {
            val go = Intent(requireContext(), DetailActivity::class.java)
            val user = it.login?.let { it1 ->
                User(
                    it1,
                    it.avatarUrl,
                )
            }
            go.putExtra(DetailActivity.SAMPLE, user)
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