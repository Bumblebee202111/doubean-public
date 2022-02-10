package com.doubean.ford.ui.groupSearch;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.doubean.ford.adapters.GroupAdapter;
import com.doubean.ford.data.Group;
import com.doubean.ford.databinding.FragmentGroupSearchBinding;
import com.doubean.ford.util.InjectorUtils;

import java.util.List;

public class GroupSearchFragment extends Fragment {


    GroupSearchViewModel groupSearchViewModel;
    FragmentGroupSearchBinding binding;
    GroupAdapter adapter;

    public GroupSearchFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentGroupSearchBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        GroupSearchViewModelFactory factory = InjectorUtils.provideGroupSearchViewModelFactory(requireContext());
        groupSearchViewModel = new ViewModelProvider(this, factory).get(GroupSearchViewModel.class);

        initRecyclerView();

        adapter = new GroupAdapter();
        binding.groupList.setAdapter(adapter);
        initSearchInputListener();
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

        groupSearchViewModel.getResults().observe(getViewLifecycleOwner(), new Observer<List<Group>>() {
            @Override
            public void onChanged(List<Group> result) {
                binding.setGroups(result);
                binding.setResultCount(result == null ? 0 : result.size());
                adapter.submitList(result);
            }
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