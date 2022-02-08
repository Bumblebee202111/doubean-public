package com.doubean.ford.ui.groupTopicDetail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.doubean.ford.data.GroupTopic;
import com.doubean.ford.databinding.FragmentGroupTopicDetailBinding;
import com.doubean.ford.util.InjectorUtils;


public class GroupTopicDetailFragment extends Fragment {

    private GroupTopicDetailViewModel groupTopicDetailViewModel;

    public static GroupTopicDetailFragment newInstance() {
        return new GroupTopicDetailFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        FragmentGroupTopicDetailBinding binding = FragmentGroupTopicDetailBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        GroupTopicDetailFragmentArgs args = GroupTopicDetailFragmentArgs.fromBundle(getArguments());
        GroupTopicDetailViewModelFactory factory = InjectorUtils.provideGroupTopicDetailViewModelFactory(requireContext(), args.getTopicId());
        groupTopicDetailViewModel = new ViewModelProvider(this, factory).get(GroupTopicDetailViewModel.class);

        //TODO: consider not loading HTTP, instead, using WebView for each topic/comment only
        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);

            }
        });
        binding.webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.99 Safari/537.36");
        groupTopicDetailViewModel.getGroupTopic().observe(getViewLifecycleOwner(), new Observer<GroupTopic>() {
            @Override
            public void onChanged(GroupTopic groupTopic) {
                if (groupTopic != null)
                    binding.webView.loadUrl(groupTopic.url);
            }
        });


        return binding.getRoot();
    }


}