package com.doubean.ford.ui.groups.postDetail;

import static com.doubean.ford.data.vo.PostCommentSortBy.ALL;
import static com.doubean.ford.data.vo.PostCommentSortBy.TOP;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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
import com.doubean.ford.data.vo.PostCommentSortBy;
import com.doubean.ford.data.vo.Resource;
import com.doubean.ford.databinding.FragmentPostDetailBinding;
import com.doubean.ford.ui.common.DoubeanWebView;
import com.doubean.ford.ui.common.DoubeanWebViewClient;
import com.doubean.ford.util.Constants;
import com.doubean.ford.util.InjectorUtils;
import com.doubean.ford.util.OpenInUtil;
import com.doubean.ford.util.ShareUtil;
import com.google.android.material.snackbar.Snackbar;


public class PostDetailFragment extends Fragment {

    final Boolean[] isToolbarShown = {false};
    private PostDetailViewModel postDetailViewModel;
    private FragmentPostDetailBinding binding;
    private String shareText = "";

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
                        binding.toolbar.setBackgroundColor(groupColor);
                        binding.appbar.setBackgroundColor(groupColor);
                        PostCommentAdapter commentAdapter = new PostCommentAdapter(post.data);
                        binding.comments.setAdapter(commentAdapter);
                        binding.comments.addItemDecoration(new DividerItemDecoration(binding.comments.getContext(), DividerItemDecoration.VERTICAL));
                        subscribeUi(commentAdapter, (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                            if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                                postDetailViewModel.loadNextPage();
                            }
                        });
                    }
                }
            }

        });


        binding.toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        binding.toolbar.setOnMenuItemClickListener(item -> {
            Resource<Post> postResource = postDetailViewModel.getPost().getValue();
            if (postResource != null && postResource.data != null) {
                Post post = postResource.data;
                switch (item.getItemId()) {
                    case R.id.action_view_in_web:
                        navigateToWebView(requireView(), post);
                        return true;
                    case R.id.action_share:
                        shareText = post.group.name;
                        if (post.tagId != null) {
                            shareText += "|" + post.getTagName();
                        }
                        shareText += '@' + post.author.name + "： " +
                                post.title + "\r\n" +
                                post.url + "\r\n" +
                                post.content;
                        ShareUtil.Share(getContext(), shareText);
                        return true;
                    case R.id.action_view_in_douban:
                        if (post.uri != null) {
                            String urlString = post.uri;
                            OpenInUtil.openInDouban(getContext(), urlString);
                        }
                        return true;
                    case R.id.action_view_in_browser:
                        if (post.url != null) {
                            String urlString = post.url;
                            OpenInUtil.openInBrowser(getContext(), urlString);
                        }
                        return true;
                    default:
                        return false;
                }
            }
            return false;
        });
        setHasOptionsMenu(true);


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


    private void subscribeUi(PostCommentAdapter commentAdapter, NestedScrollView.OnScrollChangeListener postDetailScrollViewListener) {
        postDetailViewModel.getPostComments().observe(getViewLifecycleOwner(), result -> {
            binding.setFindResource(result);
            binding.setResultCount(result == null || result.data == null || result.data.getAllComments() == null ? 0 : result.data.getAllComments().size());
            if (result != null && result.data != null) {
                Spinner spinner = binding.sortCommentsBySpinner;
                ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.sort_comments_by_array, android.R.layout.simple_spinner_item);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(arrayAdapter);
                if (result.data.getTopComments() != null && result.data.getTopComments().isEmpty())
                    spinner.setSelection(1);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        switch (getSortByAt(position)) {
                            case TOP:
                                commentAdapter.submitList(result.data.getTopComments());
                                binding.postDetailScrollview.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) null);
                                break;
                            case ALL:
                                commentAdapter.submitList(result.data.getAllComments());
                                binding.postDetailScrollview.setOnScrollChangeListener(postDetailScrollViewListener);
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

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
        PostDetailFragmentDirections.ActionPostDetailToGroupDetail direction = PostDetailFragmentDirections.actionPostDetailToGroupDetail(post.groupId).setDefaultTabId(post.tagId);
        Navigation.findNavController(v).navigate(direction);
    }

    private void navigateToGroup(View v, Post post) {
        PostDetailFragmentDirections.ActionPostDetailToGroupDetail direction = PostDetailFragmentDirections.actionPostDetailToGroupDetail(post.groupId);
        Navigation.findNavController(v).navigate(direction);
    }

    private PostCommentSortBy getSortByAt(int position) {
        switch (position) {
            case 0:
                return TOP;
            case 1:
                return ALL;
        }
        return null;
    }
}