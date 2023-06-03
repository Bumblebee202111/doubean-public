package com.doubean.ford.ui.groups.groupsHome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.doubean.ford.R
import com.doubean.ford.adapters.RecommendedGroupAdapter
import com.doubean.ford.databinding.FragmentGroupsBinding
import com.doubean.ford.util.InjectorUtils

class GroupsHomeFragment : Fragment() {
    private val groupsHomeViewModel: GroupsHomeViewModel by viewModels {
        InjectorUtils.provideGroupsViewModelFactory(requireContext())
    }
    private lateinit var binding: FragmentGroupsBinding
    private lateinit var followAdapter: GroupFollowAdapter
    private lateinit var recommendedGroupAdapter: RecommendedGroupAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentGroupsBinding.inflate(inflater, container, false).apply {
            viewModel = groupsHomeViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.inflateMenu(R.menu.groups_menu)
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_search -> {
                    val action =
                        GroupsHomeFragmentDirections.actionGroupsToGroupSearch()
                    findNavController().navigate(action)
                    true
                }
                R.id.action_settings -> {
                    findNavController().navigate(R.id.navigation_settings)
                    true
                }
                else -> false
            }
        }

        followAdapter = GroupFollowAdapter()
        binding.followList.adapter = followAdapter

        recommendedGroupAdapter = RecommendedGroupAdapter(requireContext())
        binding.groupsOfTheDayList.adapter = recommendedGroupAdapter

        groupsHomeViewModel.follows
            .observe(viewLifecycleOwner) { followAdapter.submitList(it ?: emptyList()) }
        groupsHomeViewModel.groupsOfTheDay.observe(viewLifecycleOwner) { groups ->
            recommendedGroupAdapter.submitList(groups.data ?: emptyList())
        }
    }

}