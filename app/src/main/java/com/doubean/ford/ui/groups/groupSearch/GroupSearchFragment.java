package com.doubean.ford.ui.groups.groupSearch;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.doubean.ford.adapters.GroupAdapter;
import com.doubean.ford.databinding.FragmentGroupSearchBinding;
import com.doubean.ford.util.InjectorUtils;
import com.doubean.ford.util.SpanCountCalculator;
import com.google.android.material.snackbar.Snackbar;

public class GroupSearchFragment extends Fragment {

    GroupSearchViewModel groupSearchViewModel;
    FragmentGroupSearchBinding binding;
    GroupAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentGroupSearchBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        GroupSearchViewModelFactory factory = InjectorUtils.provideGroupSearchViewModelFactory(requireContext());
        groupSearchViewModel = new ViewModelProvider(this, factory).get(GroupSearchViewModel.class);

        initRecyclerView();

        adapter = new GroupAdapter();
        binding.groupList.setAdapter(adapter);
        int spanCount = SpanCountCalculator.getSpanCount(getContext(), 500);
        binding.groupList.setLayoutManager(new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL));
        binding.groupList.addItemDecoration(new DividerItemDecoration(binding.groupList.getContext(), DividerItemDecoration.VERTICAL));
        binding.groupList.setItemAnimator(null);

        initSearchInputListener();

        binding.setCallback(() -> groupSearchViewModel.refresh());

        binding.swiperefresh.setOnRefreshListener(groupSearchViewModel::refreshResults);
        return binding.getRoot();
    }

    private void initSearchInputListener() {
        binding.input.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearch(v);
                return true;
            }
            return false;
        });
        binding.input.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN)
                    && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                doSearch(v);
                return true;
            }
            return false;
        });
    }

    private void doSearch(View v) {
        String query = binding.input.getText().toString();
        dismissKeyboard(v.getWindowToken());
        binding.setQuery(query);
        groupSearchViewModel.setQuery(query);
    }

    private void initRecyclerView() {

        binding.groupList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    groupSearchViewModel.loadNextPage();
                }
            }
        });
        groupSearchViewModel.getResults().observe(getViewLifecycleOwner(), result -> {
            binding.setSearchResource(result);
            binding.setResultCount(result == null || result.data == null ? 0 : result.data.size());
            adapter.submitList(result == null ? null : result.data);
            if (result != null)
                binding.swiperefresh.setRefreshing(false);
        });
        groupSearchViewModel.getLoadMoreStatus().observe(getViewLifecycleOwner(), loadingMore -> {
            if (loadingMore == null) {
                binding.setLoadingMore(false);
            } else {
                binding.setLoadingMore(loadingMore.isRunning());
                String error = loadingMore.getErrorMessageIfNotHandled();
                if (error != null) {
                    Snackbar.make(binding.loadMoreBar, error, Snackbar.LENGTH_LONG).show();
                }
            }
            binding.executePendingBindings();
        });

    }

    private void dismissKeyboard(IBinder windowToken) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(windowToken, 0);
        }
    }
}