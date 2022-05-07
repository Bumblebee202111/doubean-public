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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import com.doubean.ford.MobileNavigationDirections;
import com.doubean.ford.R;
import com.doubean.ford.adapters.PostCommentAdapter;
import com.doubean.ford.data.vo.GroupPost;
import com.doubean.ford.data.vo.GroupPostComments;
import com.doubean.ford.databinding.FragmentPostDetailBinding;
import com.doubean.ford.ui.common.DoubeanWebView;
import com.doubean.ford.ui.common.DoubeanWebViewClient;
import com.doubean.ford.util.Constants;
import com.doubean.ford.util.InjectorUtils;


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

        postDetailViewModel.getPost().observe(getViewLifecycleOwner(), post -> {
            if (post != null) {
                if (!TextUtils.isEmpty(post.content)) {
                    String encodedContent = Base64.encodeToString(post.content.getBytes(),
                            Base64.NO_PADDING);
                    binding.content.loadData(encodedContent, "text/html", "base64");
                }
                if (post.postTags != null) {
                    binding.postTag.setOnClickListener(v -> navigateToGroupTab(v, post));
                }

                binding.groupName.setOnClickListener(v -> navigateToGroup(v, post));

                binding.groupName.setOnClickListener(v -> navigateToGroup(v, post));

                PostCommentAdapter topCommentAdapter = new PostCommentAdapter(post.author.id);
                PostCommentAdapter allCommentAdapter = new PostCommentAdapter(post.author.id);
                binding.topComments.setAdapter(topCommentAdapter);
                binding.allComments.setAdapter(allCommentAdapter);
                subscribeUi(topCommentAdapter, allCommentAdapter);

                binding.topComments.addItemDecoration(new DividerItemDecoration(binding.topComments.getContext(), DividerItemDecoration.VERTICAL));
                binding.allComments.addItemDecoration(new DividerItemDecoration(binding.allComments.getContext(), DividerItemDecoration.VERTICAL));

            }

        });


        binding.toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        binding.toolbar.setOnMenuItemClickListener(item -> {
            //noinspection SwitchStatementWithTooFewBranches
            switch (item.getItemId()) {
                case R.id.action_view_in_web:
                    GroupPost post = postDetailViewModel.getPost().getValue();
                    if (post != null) {
                        navigateToWebView(requireView(), post);
                    }
                    return true;
                default:
                    return false;
            }

        });
        setHasOptionsMenu(true);

        binding.postDetailScrollview.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        postDetailViewModel.getPost().observe(getViewLifecycleOwner(), post -> {
            if (post != null) {
                int color = post.group.getColor();
                requireActivity().getWindow().setStatusBarColor(color);
                binding.toolbar.setBackgroundColor(color);
            }

        });
    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    private void subscribeUi(PostCommentAdapter topCommentAdapter, PostCommentAdapter allCommentAdapter) {
        postDetailViewModel.getPostComments().observe(getViewLifecycleOwner(), new Observer<GroupPostComments>() {
            @Override
            public void onChanged(GroupPostComments groupPostComments) {
                if (groupPostComments != null && groupPostComments.getAllComments() != null && !groupPostComments.getAllComments().isEmpty()) {
                    topCommentAdapter.submitList(groupPostComments.getTopComments());
                    allCommentAdapter.submitList(groupPostComments.getAllComments());
                }

            }
        });
    }


    private void navigateToWebView(View v, GroupPost post) {
        MobileNavigationDirections.ActionGlobalNavigationWebView direction = MobileNavigationDirections.actionGlobalNavigationWebView(post.url);
        Navigation.findNavController(requireView()).navigate(direction);
    }

    private void navigateToGroupTab(View v, GroupPost post) {
        PostDetailFragmentDirections.ActionNavigationPostDetailToNavigationGroupDetail direction = PostDetailFragmentDirections.actionNavigationPostDetailToNavigationGroupDetail(post.groupId).setDefaultTabId(post.tagId);
        Navigation.findNavController(v).navigate(direction);
    }

    private void navigateToGroup(View v, GroupPost post) {
        PostDetailFragmentDirections.ActionNavigationPostDetailToNavigationGroupDetail direction = PostDetailFragmentDirections.actionNavigationPostDetailToNavigationGroupDetail(post.groupId);
        Navigation.findNavController(v).navigate(direction);
    }
}