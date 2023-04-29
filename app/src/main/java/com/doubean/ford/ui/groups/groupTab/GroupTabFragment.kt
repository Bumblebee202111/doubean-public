package com.doubean.ford.ui.groups.groupTab

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.widget.PopupMenu
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.doubean.ford.R
import com.doubean.ford.adapters.PostAdapter
import com.doubean.ford.data.vo.GroupDetail
import com.doubean.ford.data.vo.PostItem
import com.doubean.ford.data.vo.PostSortBy
import com.doubean.ford.data.vo.Resource
import com.doubean.ford.databinding.FragmentGroupTabBinding
import com.doubean.ford.ui.common.LoadMoreState
import com.doubean.ford.ui.common.RetryCallback
import com.doubean.ford.util.InjectorUtils
import com.doubean.ford.util.ShareUtil
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar

class GroupTabFragment : Fragment() {
    lateinit var binding: FragmentGroupTabBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupTabBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        val args: Bundle = requireArguments()
        val groupId: String = args.getString(ARG_GROUP_ID)!!
        val tagId: String? = args.getString(ARG_TAG_ID)
        val group: GroupDetail = args.getSerializable(ARG_GROUP) as GroupDetail
        val factory: GroupTabViewModelFactory = InjectorUtils.provideGroupTabViewModelFactory(
            requireContext(), groupId, tagId
        )
        val groupTabViewModel: GroupTabViewModel =
            ViewModelProvider(this, factory)[GroupTabViewModel::class.java]
        val followUnfollow: MaterialButton = binding.followUnfollow
        binding.postList.addItemDecoration(
            DividerItemDecoration(
                binding.postList.context,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.scrollView.setOnScrollChangeListener { v: NestedScrollView, _, scrollY, _, _ ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                groupTabViewModel.loadNextPage()
            }
        }
        val adapter = PostAdapter(group)
        binding.postList.adapter = adapter
        groupTabViewModel.posts.observe(viewLifecycleOwner) { result: Resource<List<PostItem?>?>? ->
            binding.findResource = result
            binding.resultCount =
                if (result?.data == null) 0 else result.data.size
            adapter.submitList(result?.data)
            if (result != null) binding.swiperefresh.isRefreshing = false
        }
        val groupColor: Int = group.color
        followUnfollow.iconTint = ColorStateList.valueOf(groupColor)
        followUnfollow.setTextColor(groupColor)
        binding.more.setOnClickListener { v ->
            val popup = PopupMenu(
                requireContext(), v
            )
            popup.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.action_share -> group.tabs?.firstOrNull() { it ->
                        val shareText: String =
                            group.name + "|" + it.name + ' ' + group.url + "\r\n"
                        context?.let { ShareUtil.Share(it, shareText) }
                        true
                    }
                }
                false
            }
            popup.inflate(R.menu.menu_group_tab)
            popup.show()
        }
        groupTabViewModel.loadMoreStatus.observe(
            viewLifecycleOwner
        ) { loadingMore: LoadMoreState? ->
            if (loadingMore == null) {
                binding.loadingMore = false
            } else {
                binding.loadingMore = loadingMore.isRunning
                val error: String? = loadingMore.errorMessageIfNotHandled
                if (error != null) {
                    Snackbar.make(binding.loadMoreBar, error, Snackbar.LENGTH_LONG).show()
                }
            }
            binding.executePendingBindings()
        }
        if (tagId == null) { //All
            binding.followUnfollow.visibility = View.GONE
            binding.more.visibility = View.GONE
        } else { //Non-all tab
            followUnfollow.setOnClickListener {
                val followed = groupTabViewModel.followed.value
                if (followed != null) {
                    if (followed) {
                        groupTabViewModel.removeFollow()
                        Snackbar.make(binding.root, R.string.unfollowed_tab, Snackbar.LENGTH_LONG)
                            .show()
                    } else {
                        groupTabViewModel.addFollow()
                        Snackbar.make(binding.root, R.string.followed_tab, Snackbar.LENGTH_LONG)
                            .show()
                    }
                }
            }
            groupTabViewModel.followed.observe(viewLifecycleOwner) { followed: Boolean? ->
                if (followed!!) {
                    followUnfollow.setIconResource(R.drawable.ic_remove)
                    followUnfollow.setText(R.string.unfollow)
                } else {
                    followUnfollow.setIconResource(R.drawable.ic_add)
                    followUnfollow.setText(R.string.follow)
                }
            }
        }
        val spinner: Spinner = binding.sortPostsBySpinner
        val arrayAdapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sort_posts_by_array, R.layout.support_simple_spinner_dropdown_item
        )
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinner.adapter = arrayAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                // An Kotlin bug: https://stackoverflow.com/questions/48343622/how-to-fix-parameter-specified-as-non-null-is-null-on-rotating-screen-in-a-fragm
                view: View?,
                position: Int,
                id: Long
            ) {
                groupTabViewModel.setSortBy(getSortByAt(position))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        binding.callback = object : RetryCallback {
            override fun retry() {
                groupTabViewModel.refreshPosts()
            }
        }
        binding.swiperefresh.setOnRefreshListener { groupTabViewModel.refreshPosts() }
        return binding.root
    }

    private fun getSortByAt(position: Int): PostSortBy? {
        when (position) {
            0 -> return PostSortBy.LAST_UPDATED
            1 -> return PostSortBy.NEW
            2 -> return PostSortBy.TOP
        }
        return null
    }

    companion object {
        const val ARG_GROUP_ID = "group_id"
        const val ARG_TAG_ID = "tag_id"
        private const val ARG_GROUP = "group"
        fun newInstance(
            groupId: String?,
            tagId: String?,
            groupDetail: GroupDetail?
        ): GroupTabFragment {
            val args = Bundle()
            args.putString(ARG_GROUP_ID, groupId)
            args.putString(ARG_TAG_ID, tagId)
            args.putSerializable(ARG_GROUP, groupDetail)
            val fragment = GroupTabFragment()
            fragment.arguments = args
            return fragment
        }
    }
}