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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.bumblebee202111.doubean.databinding.FragmentGroupSearchBinding
import com.github.bumblebee202111.doubean.ui.common.repeatWithViewLifecycle
import com.github.bumblebee202111.doubean.util.SpanCountCalculator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GroupSearchFragment : Fragment() {
    private val groupSearchViewModel: GroupSearchViewModel by viewModels()
    lateinit var binding: FragmentGroupSearchBinding
    private lateinit var adapter: SearchResultGroupAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentGroupSearchBinding.inflate(inflater, container, false)
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
        groupSearchViewModel.setQuery(query)
    }

    private fun initRecyclerView() {
        repeatWithViewLifecycle {
            launch {
                groupSearchViewModel.results
                    .collect { result ->
                        adapter.submitData(result)
                    }
            }
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