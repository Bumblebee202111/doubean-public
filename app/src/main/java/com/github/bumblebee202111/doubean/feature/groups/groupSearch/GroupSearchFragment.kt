package com.github.bumblebee202111.doubean.feature.groups.groupSearch

import android.content.Context
import android.os.Bundle
import android.os.IBinder
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.bumblebee202111.doubean.databinding.FragmentGroupSearchBinding
import com.github.bumblebee202111.doubean.model.Result
import com.github.bumblebee202111.doubean.ui.common.RetryCallback
import com.github.bumblebee202111.doubean.ui.common.bindResult
import com.github.bumblebee202111.doubean.util.SpanCountCalculator
import com.github.bumblebee202111.doubean.util.showSnackbar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupSearchFragment : Fragment() {
    private val groupSearchViewModel: GroupSearchViewModel by viewModels()
    lateinit var binding: FragmentGroupSearchBinding
    private lateinit var adapter: SearchResultGroupAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentGroupSearchBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        adapter = SearchResultGroupAdapter()
        binding.groupList.adapter = adapter
        val spanCount = SpanCountCalculator.getSpanCount(requireContext(), 500)
        binding.groupList.layoutManager =
            StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
        binding.groupList.addItemDecoration(
            DividerItemDecoration(
                binding.groupList.context,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.groupList.itemAnimator = null
        initSearchInputListener()
        binding.callback = object : RetryCallback {
            override fun retry() {
                groupSearchViewModel.refresh()
            }
        }
        binding.swiperefresh.setOnRefreshListener { groupSearchViewModel.refreshResults() }
    }

    private fun initSearchInputListener() {
        binding.input.setOnEditorActionListener { v: View, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearch(v)
                true
            } else {
                false
            }
        }
        binding.input.setOnKeyListener { view: View, keyCode: Int, event: KeyEvent ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                doSearch(view)
                true
            } else {
                false
            }
        }
    }

    private fun doSearch(v: View) {
        val query = binding.input.text.toString()
        dismissKeyboard(v.windowToken)
        binding.query = query
        groupSearchViewModel.setQuery(query)
    }

    private fun initRecyclerView() {
        binding.groupList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    groupSearchViewModel.loadNextPage()
                }
            }
        })
        groupSearchViewModel.results
            .observe(viewLifecycleOwner) { result ->
                binding.loadingState.bindResult(result)
                adapter.submitList(result?.data)
                if (result != null) binding.swiperefresh.isRefreshing = false
                binding.noResultsText.isVisible =
                    (result is Result.Success) && result.data.isEmpty()
            }
        groupSearchViewModel.loadMoreStatus
            .observe(viewLifecycleOwner) { loadingMore ->
                if (loadingMore == null) {
                    binding.loadingMore = false
                } else {
                    binding.loadingMore = loadingMore.isRunning
                    val error = loadingMore.errorMessageIfNotHandled
                    if (error != null) {
                        binding.loadMoreBar.showSnackbar(error, Snackbar.LENGTH_LONG)
                    }
                }
                binding.executePendingBindings()
            }
    }

    private fun dismissKeyboard(windowToken: IBinder) {
        val activity = activity
        if (activity != null) {
            val imm = activity.getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            imm.hideSoftInputFromWindow(windowToken, 0)
        }
    }
}