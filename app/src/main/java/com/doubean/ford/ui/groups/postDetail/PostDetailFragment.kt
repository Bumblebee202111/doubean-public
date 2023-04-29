package com.doubean.ford.ui.groups.postDetail

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.doubean.ford.MobileNavigationDirections
import com.doubean.ford.R
import com.doubean.ford.adapters.PostCommentAdapter
import com.doubean.ford.data.vo.Post
import com.doubean.ford.data.vo.PostCommentSortBy
import com.doubean.ford.data.vo.PostComments
import com.doubean.ford.data.vo.Resource
import com.doubean.ford.databinding.FragmentPostDetailBinding
import com.doubean.ford.ui.common.DoubeanWebView
import com.doubean.ford.ui.common.DoubeanWebViewClient
import com.doubean.ford.ui.common.LoadMoreState
import com.doubean.ford.ui.common.RetryCallback
import com.doubean.ford.util.Constants
import com.doubean.ford.util.InjectorUtils
import com.doubean.ford.util.OpenInUtil
import com.doubean.ford.util.ShareUtil
import com.google.android.material.snackbar.Snackbar

class PostDetailFragment : Fragment() {
    private lateinit var postDetailViewModel: PostDetailViewModel
    private lateinit var binding: FragmentPostDetailBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        val args = PostDetailFragmentArgs.fromBundle(
            requireArguments()
        )
        val factory: PostDetailViewModelFactory =
            InjectorUtils.providePostDetailViewModelFactory(requireContext(), args.postId)
        postDetailViewModel = ViewModelProvider(this, factory)[PostDetailViewModel::class.java]
        binding.viewModel = postDetailViewModel
        val content: DoubeanWebView = binding.content
        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> WebSettingsCompat.setForceDark(
                    content.settings,
                    WebSettingsCompat.FORCE_DARK_ON
                )
                Configuration.UI_MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_UNDEFINED -> WebSettingsCompat.setForceDark(
                    content.settings,
                    WebSettingsCompat.FORCE_DARK_OFF
                )
            }
        }
        content.setPadding(0, 0, 0, 0)
        val webSettings: WebSettings = content.settings
        webSettings.setNeedInitialFocus(false)
        //webSettings.setUseWideViewPort(true);
        //webSettings.setLoadWithOverviewMode(true);
        val webViewClient = DoubeanWebViewClient(Constants.POST_CONTENT_CSS_FILENAME)
        content.webViewClient = webViewClient
        postDetailViewModel.post.observe(viewLifecycleOwner) { post: Resource<Post> ->
            if (post.data != null) {
                if (!post.data.content.isNullOrBlank()) {
                    val encodedContent = Base64.encodeToString(
                        post.data.content.toByteArray(),
                        Base64.NO_PADDING
                    )
                    binding.content.loadData(encodedContent, "text/html", "base64")
                }
                if (post.data.postTags != null) {
                    binding.postTag.setOnClickListener { navigateToGroupTab(post.data) }
                }
                binding.groupName.setOnClickListener { navigateToGroup(post.data) }
                binding.groupAvatar.setOnClickListener { navigateToGroup(post.data) }
                if (post.data.group != null) {
                    val groupColor: Int = post.data.group.color
                    if (groupColor != 0) {
                        binding.toolbar.setBackgroundColor(groupColor)
                        binding.appbar.setBackgroundColor(groupColor)
                        val commentAdapter = PostCommentAdapter(post.data)
                        binding.comments.adapter = commentAdapter
                        binding.comments.addItemDecoration(
                            DividerItemDecoration(
                                binding.comments.context,
                                DividerItemDecoration.VERTICAL
                            )
                        )
                        subscribeUi(
                            commentAdapter
                        ) { v: NestedScrollView, _, scrollY, _, _ ->
                            if (scrollY == v.getChildAt(0)
                                    .measuredHeight - v.measuredHeight
                            ) {
                                postDetailViewModel.loadNextPage()
                            }
                        }
                    }
                }
            }
        }
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        binding.toolbar.setOnMenuItemClickListener { item ->
            val postResource: Resource<Post>? = postDetailViewModel.post.value
            if (postResource != null) {
                if (postResource.data != null) {
                    val post: Post = postResource.data
                    when (item.itemId) {
                        R.id.action_view_in_web -> {
                            navigateToWebView(post)
                            return@setOnMenuItemClickListener true
                        }
                        R.id.action_share -> {
                            var shareText = post.group?.name
                            if (post.tagId != null) {
                                shareText += "|" + post.tagName
                            }
                            shareText += """
                                @${post.author.name}： ${post.title}
                                ${post.url}
                                ${post.content}
                                """.trimIndent()
                            ShareUtil.Share(requireContext(), shareText)
                            return@setOnMenuItemClickListener true
                        }
                        R.id.action_view_in_douban -> {
                            val urlString: String = post.uri
                            OpenInUtil.openInDouban(requireContext(), urlString)
                            return@setOnMenuItemClickListener true
                        }
                        R.id.action_view_in_browser -> {
                            val urlString: String = post.url
                            OpenInUtil.openInBrowser(requireContext(), urlString)
                            return@setOnMenuItemClickListener true
                        }
                        else -> return@setOnMenuItemClickListener false
                    }
                }
            }
            false
        }
        setHasOptionsMenu(true)
        postDetailViewModel.loadMoreStatus
            .observe(viewLifecycleOwner) { loadingMore: LoadMoreState? ->
                if (loadingMore == null) {
                    binding.loadingMore = false
                } else {
                    binding.loadingMore = loadingMore.isRunning
                    val error: String? = loadingMore.errorMessageIfNotHandled
                    if (error != null) {
                        Snackbar.make(binding.loadMoreBar, error, Snackbar.LENGTH_LONG).show()
                    }
                }
                binding.executePendingBindings()
            }
        binding.callback = object : RetryCallback {
            override fun retry() {
                postDetailViewModel.refreshPostComments()
            }
        }
        binding.swiperefresh.setOnRefreshListener { postDetailViewModel.refreshPostComments() }
        return binding.root
    }

    private fun subscribeUi(
        commentAdapter: PostCommentAdapter,
        postDetailScrollViewListener: NestedScrollView.OnScrollChangeListener
    ) {
        postDetailViewModel.postComments
            .observe(viewLifecycleOwner) { result: Resource<PostComments> ->
                binding.findResource = result
                binding.resultCount =
                    if (result.data?.allComments == null) 0 else result.data.allComments.size
                if (result.data != null) {
                    val spinner: Spinner = binding.sortCommentsBySpinner
                    val arrayAdapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
                        requireContext(),
                        R.array.sort_comments_by_array,
                        R.layout.support_simple_spinner_dropdown_item
                    )
                    arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
                    spinner.adapter = arrayAdapter
                    if (result.data.topComments.isNullOrEmpty()) spinner.setSelection(1)
                    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            when (getSortByAt(position)) {
                                PostCommentSortBy.TOP -> {
                                    commentAdapter.submitList(result.data.topComments)
                                    binding.postDetailScrollview.setOnScrollChangeListener(null as NestedScrollView.OnScrollChangeListener?)
                                }
                                PostCommentSortBy.ALL -> {
                                    commentAdapter.submitList(result.data.allComments)
                                    binding.postDetailScrollview.setOnScrollChangeListener(
                                        postDetailScrollViewListener
                                    )
                                }
                                else -> {}
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
                }
                binding.swiperefresh.isRefreshing = false
            }
    }

    private fun navigateToWebView(post: Post) {
        val direction = MobileNavigationDirections.actionGlobalNavigationWebView(post.url)
        findNavController().navigate(direction)
    }

    private fun navigateToGroupTab(post: Post) {
        val direction = PostDetailFragmentDirections.actionPostDetailToGroupDetail(post.groupId!!)
            .setDefaultTabId(post.tagId)
        findNavController().navigate(direction)
    }

    private fun navigateToGroup(post: Post) {
        val direction = PostDetailFragmentDirections.actionPostDetailToGroupDetail(post.groupId!!)
        findNavController().navigate(direction)
    }

    private fun getSortByAt(position: Int): PostCommentSortBy? {
        when (position) {
            0 -> return PostCommentSortBy.TOP
            1 -> return PostCommentSortBy.ALL
        }
        return null
    }
}