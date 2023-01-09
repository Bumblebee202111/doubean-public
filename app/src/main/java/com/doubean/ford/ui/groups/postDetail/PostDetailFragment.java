package com.doubean.ford.ui.groups.postDetail;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import com.doubean.ford.MobileNavigationDirections;
import com.doubean.ford.R;
import com.doubean.ford.adapters.PostCommentAdapter;
import com.doubean.ford.data.vo.Post;
import com.doubean.ford.data.vo.Resource;
import com.doubean.ford.data.vo.Status;
import com.doubean.ford.databinding.FragmentPostDetailBinding;
import com.doubean.ford.ui.common.DoubeanWebView;
import com.doubean.ford.ui.common.DoubeanWebViewClient;
import com.doubean.ford.util.Constants;
import com.doubean.ford.util.InjectorUtils;
import com.google.android.material.snackbar.Snackbar;


public class PostDetailFragment extends Fragment {

    final Boolean[] isToolbarShown = {false};
    private PostDetailViewModel postDetailViewModel;
    private FragmentPostDetailBinding binding;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentPostDetailBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        PostDetailFragmentArgs args = PostDetailFragmentArgs.fromBundle(getArguments());
        PostDetailViewModelFactory factory = InjectorUtils.providePostDetailViewModelFactory(requireContext(), args.getPostId());

        postDetailViewModel = new ViewModelProvider(this, factory).get(PostDetailViewModel.class);
        binding.setViewModel(postDetailViewModel);

        DoubeanWebView content = binding.content;

        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                case Configuration.UI_MODE_NIGHT_YES:
                    WebSettingsCompat.setForceDark(content.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
                    break;
                case Configuration.UI_MODE_NIGHT_NO:
                case Configuration.UI_MODE_NIGHT_UNDEFINED:
                    WebSettingsCompat.setForceDark(content.getSettings(), WebSettingsCompat.FORCE_DARK_OFF);
                    break;
            }
        }

        content.setPadding(0, 0, 0, 0);
        WebSettings webSettings = content.getSettings();
        webSettings.setNeedInitialFocus(false);
        //webSettings.setUseWideViewPort(true);
        //webSettings.setLoadWithOverviewMode(true);
        DoubeanWebViewClient webViewClient = new DoubeanWebViewClient(Constants.POST_CONTENT_CSS_FILENAME);
        content.setWebViewClient(webViewClient);

        getActivity().getWindow().setStatusBarColor(getContext().getColor(R.color.doubean_green));

        postDetailViewModel.getPost().observe(getViewLifecycleOwner(), post -> {
            if (post != null && post.data != null) {
                if (!TextUtils.isEmpty(post.data.content)) {
                    String encodedContent = Base64.encodeToString(post.data.content.getBytes(),
                            Base64.NO_PADDING);
                    binding.content.loadData(encodedContent, "text/html", "base64");
                }
                if (post.data.postTags != null) {
                    binding.postTag.setOnClickListener(v -> navigateToGroupTab(v, post.data));
                }

                binding.groupName.setOnClickListener(v -> navigateToGroup(v, post.data));

                binding.groupAvatar.setOnClickListener(v -> navigateToGroup(v, post.data));

                if (post.data.group != null) {
                    int groupColor = post.data.group.getColor();

                    if (groupColor != 0) {
                        getActivity().getWindow().setStatusBarColor(groupColor);
                        binding.toolbar.setBackgroundColor(groupColor);
                        PostCommentAdapter topCommentAdapter = new PostCommentAdapter(post.data.author.id, groupColor);
                        PostCommentAdapter allCommentAdapter = new PostCommentAdapter(post.data.author.id, groupColor);
                        binding.topComments.setAdapter(topCommentAdapter);
                        binding.allComments.setAdapter(allCommentAdapter);
                        subscribeUi(topCommentAdapter, allCommentAdapter);

                        binding.topComments.addItemDecoration(new DividerItemDecoration(binding.topComments.getContext(), DividerItemDecoration.VERTICAL));
                        binding.allComments.addItemDecoration(new DividerItemDecoration(binding.allComments.getContext(), DividerItemDecoration.VERTICAL));
                    }

                }


            }

        });


        binding.toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        binding.toolbar.setOnMenuItemClickListener(item -> {
            //noinspection SwitchStatementWithTooFewBranches
            switch (item.getItemId()) {
                case R.id.action_view_in_web:
                    Resource<Post> post = postDetailViewModel.getPost().getValue();
                    if (post != null && post.data != null) {
                        navigateToWebView(requireView(), post.data);
                    }
                    return true;
                default:
                    return false;
            }

        });
        setHasOptionsMenu(true);

        binding.postDetailScrollview.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                postDetailViewModel.loadNextPage();
            }
        });
        postDetailViewModel.getLoadMoreStatus().observe(getViewLifecycleOwner(), loadingMore -> {
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
        binding.setCallback(postDetailViewModel::refreshPostComments);
        binding.swiperefresh.setOnRefreshListener(postDetailViewModel::refreshPostComments);
        return binding.getRoot();
    }

    public void onResume() {
        super.onResume();
        postDetailViewModel.getPost().observe(getViewLifecycleOwner(), post -> {
            if (post != null) {
                if (post.status == Status.SUCCESS) {
                    if (post.data.group != null && post.data.group.colorString != null) {
                        getActivity().getWindow().setStatusBarColor(post.data.group.getColor());
                    } else {
                        getActivity().getWindow().setStatusBarColor(getContext().getColor(R.color.doubean_green));
                    }
                } else if (post.status == Status.ERROR) {
                    getActivity().getWindow().setStatusBarColor(getContext().getColor(R.color.doubean_green));
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    private void subscribeUi(PostCommentAdapter topCommentAdapter, PostCommentAdapter allCommentAdapter) {
        postDetailViewModel.getPostComments().observe(getViewLifecycleOwner(), result -> {
            binding.setFindResource(result);
            binding.setResultCount(result == null || result.data == null || result.data.getAllComments() == null ? 0 : result.data.getAllComments().size());
            if (result != null && result.data != null) {
                topCommentAdapter.submitList(result == null || result.data == null ? null : result.data.getTopComments());
                allCommentAdapter.submitList(result == null || result.data == null ? null : result.data.getAllComments());
            }

            if (result != null)
                binding.swiperefresh.setRefreshing(false);
        });
    }


    private void navigateToWebView(View v, Post post) {
        MobileNavigationDirections.ActionGlobalNavigationWebView direction = MobileNavigationDirections.actionGlobalNavigationWebView(post.url);
        Navigation.findNavController(requireView()).navigate(direction);
    }

    private void navigateToGroupTab(View v, Post post) {
        PostDetailFragmentDirections.ActionNavigationPostDetailToNavigationGroupDetail direction = PostDetailFragmentDirections.actionNavigationPostDetailToNavigationGroupDetail(post.groupId).setDefaultTabId(post.tagId);
        Navigation.findNavController(v).navigate(direction);
    }

    private void navigateToGroup(View v, Post post) {
        PostDetailFragmentDirections.ActionNavigationPostDetailToNavigationGroupDetail direction = PostDetailFragmentDirections.actionNavigationPostDetailToNavigationGroupDetail(post.groupId);
        Navigation.findNavController(v).navigate(direction);
    }
}