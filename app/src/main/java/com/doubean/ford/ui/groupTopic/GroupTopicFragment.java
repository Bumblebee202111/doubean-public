package com.doubean.ford.ui.groupTopic;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.doubean.ford.adapters.GroupTopicCommentAdapter;
import com.doubean.ford.data.GroupTopicComments;
import com.doubean.ford.databinding.FragmentGroupTopicBinding;
import com.doubean.ford.util.InjectorUtils;

import java.io.InputStream;


public class GroupTopicFragment extends Fragment {

    private GroupTopicViewModel groupTopicViewModel;
    private FragmentGroupTopicBinding binding;

    public static GroupTopicFragment newInstance() {
        return new GroupTopicFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentGroupTopicBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        GroupTopicFragmentArgs args = GroupTopicFragmentArgs.fromBundle(getArguments());
        GroupTopicViewModelFactory factory = InjectorUtils.provideGroupTopicDetailViewModelFactory(requireContext(), args.getTopicId());
        groupTopicViewModel = new ViewModelProvider(this, factory).get(GroupTopicViewModel.class);
        binding.setViewModel(groupTopicViewModel);

        WebView content = binding.content;
        content.setVerticalScrollBarEnabled(false);
        content.setHorizontalScrollBarEnabled(false);
        content.setPadding(0, 0, 0, 0);
        WebSettings webSettings = content.getSettings();
        //webSettings.setTextZoom(175);
        webSettings.setNeedInitialFocus(false);
        webSettings.setJavaScriptEnabled(true);
        //webSettings.setUseWideViewPort(true);
        //webSettings.setLoadWithOverviewMode(true);
        //webSettings.setMinimumFontSize(16);

        binding.content.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                injectCSS();
                WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

                DisplayMetrics metrics = new DisplayMetrics();
                manager.getDefaultDisplay().getMetrics(metrics);

                metrics.widthPixels /= metrics.density;

                //view.loadUrl("javascript:var scale = " + metrics.widthPixels + " / document.body.scrollWidth; document.body.style.zoom = scale;");
                super.onPageFinished(view, url);
            }
        });
        groupTopicViewModel.getTopic().observe(getViewLifecycleOwner(), groupTopic -> {
            if (groupTopic != null) {
                if (!TextUtils.isEmpty(groupTopic.content)) {
                    String encodedContent = Base64.encodeToString(groupTopic.content.getBytes(),
                            Base64.NO_PADDING);
                    binding.content.loadData(encodedContent, "text/html", "base64");


                }

                //binding.webView.loadUrl(groupTopic.url);//TODO: remove this line
            }

        });
        GroupTopicCommentAdapter popularCommentAdapter = new GroupTopicCommentAdapter();
        GroupTopicCommentAdapter allCommentAdapter = new GroupTopicCommentAdapter();
        groupTopicViewModel.getTopicComments().observe(getViewLifecycleOwner(), new Observer<GroupTopicComments>() {
            @Override
            public void onChanged(GroupTopicComments groupTopicComments) {
                if (groupTopicComments != null && groupTopicComments.getAllComments() != null && !groupTopicComments.getAllComments().isEmpty()) {
                    //for(int i=0;i<5;i++)
                    //    groupTopicComments.getAllComments().addAll(groupTopicComments.getAllComments());
                    popularCommentAdapter.submitList(groupTopicComments.getPopularComments());
                    allCommentAdapter.submitList(groupTopicComments.getAllComments());
                }

            }
        });
        binding.popularComments.addItemDecoration(new DividerItemDecoration(binding.popularComments.getContext(), DividerItemDecoration.VERTICAL));
        binding.allComments.addItemDecoration(new DividerItemDecoration(binding.allComments.getContext(), DividerItemDecoration.VERTICAL));
        binding.popularComments.setAdapter(popularCommentAdapter);
        binding.allComments.setAdapter(allCommentAdapter);
        return binding.getRoot();
    }

    /**
     * https://stackoverflow.com/a/30018910
     */
    private void injectCSS() {
        try {
            InputStream inputStream = requireContext().getAssets().open("style.css");
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