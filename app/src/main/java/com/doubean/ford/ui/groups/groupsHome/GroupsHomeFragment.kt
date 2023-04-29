package com.doubean.ford.ui.groups.groupsHome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.doubean.ford.R
import com.doubean.ford.adapters.GroupFollowAdapter
import com.doubean.ford.adapters.RecommendedGroupAdapter
import com.doubean.ford.data.vo.RecommendedGroup
import com.doubean.ford.data.vo.Resource
import com.doubean.ford.databinding.FragmentGroupsBinding
import com.doubean.ford.util.InjectorUtils

class GroupsHomeFragment : Fragment() {
    private lateinit var groupsHomeViewModel: GroupsHomeViewModel
    private lateinit var factory: GroupsHomeViewModelFactory
    private lateinit var binding: FragmentGroupsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        factory = InjectorUtils.provideGroupsViewModelFactory(requireContext())
        groupsHomeViewModel = ViewModelProvider(this, factory)[GroupsHomeViewModel::class.java]
        binding = FragmentGroupsBinding.inflate(inflater, container, false)
        binding.viewModel = groupsHomeViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        val adapter = GroupFollowAdapter()
        binding.followList.adapter = adapter
        groupsHomeViewModel.followList
            .observe(viewLifecycleOwner) { list -> adapter.submitList(list ?: emptyList()) }
        val recommendedGroupAdapter = RecommendedGroupAdapter(requireContext())
        binding.groupsOfTheDayList.adapter = recommendedGroupAdapter
        groupsHomeViewModel.groupsOfTheDay.observe(viewLifecycleOwner) { groups: Resource<List<RecommendedGroup?>?>? ->
            recommendedGroupAdapter.submitList(if (groups?.data == null) emptyList() else groups.data)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.inflateMenu(R.menu.groups_menu)
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_search -> {
                    val action: NavDirections =
                        GroupsHomeFragmentDirections.actionGroupsToGroupSearch()
                    findNavController().navigate(action)
                    return@setOnMenuItemClickListener true
                }
                R.id.action_settings -> {
                    findNavController().navigate(R.id.navigation_settings)
                    return@setOnMenuItemClickListener true
                }
                else -> return@setOnMenuItemClickListener false
            }
        }
    }
}