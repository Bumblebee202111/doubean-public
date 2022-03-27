package com.doubean.ford.ui.groups.postDetail;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.doubean.ford.MobileNavigationDirections;
import com.doubean.ford.R;
import com.doubean.ford.adapters.GroupPostCommentAdapter;
import com.doubean.ford.data.vo.GroupPost;
import com.doubean.ford.data.vo.GroupPostComments;
import com.doubean.ford.databinding.FragmentPostDetailBinding;
import com.doubean.ford.util.Constants;
import com.doubean.ford.util.InjectorUtils;

import java.io.InputStream;


public class PostDetailFragment extends Fragment {

    private PostDetailViewModel postDetailViewModel;
    private FragmentPostDetailBinding binding;
    final Boolean[] isToolbarShown = {false};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentPostDetailBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        PostDetailFragmentArgs args = PostDetailFragmentArgs.fromBundle(getArguments());
        PostDetailViewModelFactory factory = InjectorUtils.providePostDetailViewModelFactory(requireContext(), args.getPostId());
        postDetailViewModel = new ViewModelProvider(this, factory).get(PostDetailViewModel.class);
        binding.setViewModel(postDetailViewModel);

        WebView content = binding.content;
        content.setVerticalScrollBarEnabled(false);
        content.setHorizontalScrollBarEnabled(false);
        content.setPadding(0, 0, 0, 0);
        WebSettings webSettings = content.getSettings();
        webSettings.setNeedInitialFocus(false);
        webSettings.setJavaScriptEnabled(true);
        //webSettings.setUseWideViewPort(true);
        //webSettings.setLoadWithOverviewMode(true);
        binding.content.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                injectCSS();
                super.onPageFinished(view, url);
            }
        });

        postDetailViewModel.getPost().observe(getViewLifecycleOwner(), groupPost -> {
            if (groupPost != null) {
                if (!TextUtils.isEmpty(groupPost.content)) {
                    String encodedContent = Base64.encodeToString(groupPost.content.getBytes(),
                            Base64.NO_PADDING);
                    binding.content.loadData(encodedContent, "text/html", "base64");
                }
                //requireActivity().getWindow().setStatusBarColor(groupPost.group.getColor());
            }

        });
        GroupPostCommentAdapter popularCommentAdapter = new GroupPostCommentAdapter();
        GroupPostCommentAdapter allCommentAdapter = new GroupPostCommentAdapter();
        binding.popularComments.setAdapter(popularCommentAdapter);
        binding.allComments.setAdapter(allCommentAdapter);
        subscribeUi(popularCommentAdapter, allCommentAdapter);

        binding.popularComments.addItemDecoration(new DividerItemDecoration(binding.popularComments.getContext(), DividerItemDecoration.VERTICAL));
        binding.allComments.addItemDecoration(new DividerItemDecoration(binding.allComments.getContext(), DividerItemDecoration.VERTICAL));

        binding.toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        binding.toolbar.setOnMenuItemClickListener(item -> {
            //noinspection SwitchStatementWithTooFewBranches
            switch (item.getItemId()) {
                case R.id.action_view_in_web:
                    GroupPost post = postDetailViewModel.getPost().getValue();
                    if (post != null) {
                        MobileNavigationDirections.ActionGlobalNavigationWebView direction = MobileNavigationDirections.actionGlobalNavigationWebView(post.url);
                        Navigation.findNavController(requireView()).navigate(direction);
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
        getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getContext(), R.color.doubean_green));

    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    private void subscribeUi(GroupPostCommentAdapter popularCommentAdapter, GroupPostCommentAdapter allCommentAdapter) {
        postDetailViewModel.getPostComments().observe(getViewLifecycleOwner(), new Observer<GroupPostComments>() {
            @Override
            public void onChanged(GroupPostComments groupPostComments) {
                if (groupPostComments != null && groupPostComments.getAllComments() != null && !groupPostComments.getAllComments().isEmpty()) {
                    popularCommentAdapter.submitList(groupPostComments.getPopularComments());
                    allCommentAdapter.submitList(groupPostComments.getAllComments());
                }

            }
        });
    }

    /**
     * https://stackoverflow.com/a/30018910
     */
    private void injectCSS() {
        try {
            InputStream inputStream = requireContext().getAssets().open(Constants.POST_CONTENT_CSS_FILENAME);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            binding.content.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('style');" +
                    "style.type = 'text/css';" +
                    // Tell the browser to BASE64-decode the string into your script !!!
                    "style.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(style)" +
                    "})()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}