package com.github.bumblebee202111.doubean.feature.groups.postDetail

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.webkit.WebView
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.github.bumblebee202111.doubean.MobileNavigationDirections
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.databinding.FragmentPostDetailBinding
import com.github.bumblebee202111.doubean.model.PostCommentSortBy
import com.github.bumblebee202111.doubean.model.PostDetail
import com.github.bumblebee202111.doubean.ui.common.DoubeanWebViewClient
import com.github.bumblebee202111.doubean.ui.common.RetryCallback
import com.github.bumblebee202111.doubean.util.APP_LIGHT_CSS_FILENAME
import com.github.bumblebee202111.doubean.util.NOTE_V2_CSS_FILENAME
import com.github.bumblebee202111.doubean.util.OpenInUtil
import com.github.bumblebee202111.doubean.util.ShareUtil
import com.github.bumblebee202111.doubean.util.showSnackbar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@AndroidEntryPoint
class PostDetailFragment : Fragment() {

    private lateinit var binding: FragmentPostDetailBinding
    private val postDetailViewModel: PostDetailViewModel by viewModels()
    lateinit var commentAdapter: PostCommentAdapter
    lateinit var spinner: Spinner
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var downloadImage: () -> Unit

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPostDetailBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = postDetailViewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        binding.toolbar.setOnMenuItemClickListener { item ->
            postDetailViewModel.post.value?.data?.let { post ->
                when (item.itemId) {
                    R.id.action_view_in_web -> {
                        navigateToWebView(post)
                        true
                    }

                    R.id.action_share -> {
                        val shareText = StringBuilder()
                        post.group?.name.let { groupName ->
                            shareText.append(groupName)
                        }
                        post.tag?.let { tag -> shareText.append("|" + tag.name) }
                        shareText.append("@${post.author.name}： ${post.title}${post.url}${post.content}")
                        ShareUtil.share(requireContext(), shareText)
                        true
                    }

                    R.id.action_view_in_douban -> {
                        val urlString = post.uri
                        OpenInUtil.openInDouban(requireContext(), urlString)
                        true
                    }

                    R.id.action_view_in_browser -> {
                        val urlString = post.url
                        OpenInUtil.openInBrowser(requireContext(), urlString)
                        true
                    }

                    else -> false
                }
            }
            false
        }
        setHasOptionsMenu(true)

        val postTagOnClickListener = View.OnClickListener {
            postDetailViewModel.post.value?.data?.let {
                val postTag = it.tag
                if (it.group != null && postTag != null)
                    navigateToGroupTab(it.group.id, postTag.id)
            }
        }
        binding.postTag.setOnClickListener(postTagOnClickListener)

        val groupOnClickListener = View.OnClickListener {
            postDetailViewModel.post.value?.data?.group?.let {
                navigateToGroup(it.id)
            }
        }
        binding.groupName.setOnClickListener(groupOnClickListener)
        binding.groupAvatar.setOnClickListener(groupOnClickListener)


        setupContent()
        setupSpinner()
        setupCommentList()
        initCommentList()

        postDetailViewModel.post.observe(viewLifecycleOwner) { postResource ->
            if (postResource.data != null) {
                val content = postResource.data.content
                if (!content.isNullOrBlank()) {

                    val wrappedContent = """
<!DOCTYPE html>
<head>
    <meta name="viewport" content="width=device-width, height=device-height, user-scalable=no, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, viewport-fit=cover" />
</head>
<body>
    <section class="note-content paper">
        <div class="note-body" id="note-body">
            <div class="rich-note">
                    $content
            </div>
        </div>
    </section>
<body>
                    """.trimIndent()

                    val encodedContent = Base64.encodeToString(
                        wrappedContent.toByteArray(),
                        Base64.NO_PADDING
                    )
                    binding.content.loadData(encodedContent, "text/html", "base64")
                }

                postResource.data.group?.color?.let { groupColor ->
                    binding.toolbar.setBackgroundColor(groupColor)
                    binding.appbar.setBackgroundColor(groupColor)
                }
            }
        }


        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            requestPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                    if (isGranted)
                        downloadImage()
                }
        }
    }

    private fun downloadImage(imageURL: String) {
        val request = DownloadManager.Request(Uri.parse(imageURL))
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        val name = URLUtil.guessFileName(imageURL, null, null)
        val fullName =
            "${Environment.getExternalStorageDirectory().absolutePath}/${Environment.DIRECTORY_PICTURES}/Douban/$name"
        request.setDestinationUri(Uri.fromFile(File(fullName)))
        val downloadManager =
            requireActivity().getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
        Toast.makeText(
            context,
            "Downloading image to $fullName",
            Toast.LENGTH_LONG
        ).show()
    }

    @SuppressLint("JavascriptInterface")
    private fun setupContent() {
        val content = binding.content
        content.setPadding(0, 0, 0, 0)

        val webSettings = content.settings
        webSettings.setNeedInitialFocus(false)
        
        webSettings.useWideViewPort = true;
        val webViewClient = object : DoubeanWebViewClient(
            listOf(
                
                
                
                NOTE_V2_CSS_FILENAME,
                APP_LIGHT_CSS_FILENAME
                
            )
        ) {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url == null) return false
                val request = NavDeepLinkRequest.Builder.fromUri(url.toUri()).build()
                try {
                    findNavController().navigate(request)
                } catch (e: IllegalArgumentException) {
                    Log.i("doubean", "shouldOverrideUrlLoading: $e")
                    Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                        startActivity(this)
                    }
                }
                return true
            }
        }

        content.setOnLongClickListener {
            val webViewHitTestResult: WebView.HitTestResult = binding.content.getHitTestResult()
            if (webViewHitTestResult.type == WebView.HitTestResult.IMAGE_TYPE ||
                webViewHitTestResult.type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE
            ) {

                val imageURL = webViewHitTestResult.extra
                if (URLUtil.isValidUrl(imageURL)) {
                    downloadImage = { downloadImage(imageURL!!) }
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                        downloadImage()
                    } else {
                        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
                        if (ActivityCompat.checkSelfPermission(
                                requireContext(),
                                permission
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            requestPermissionLauncher.launch(permission)
                        } else {
                            downloadImage()
                        }
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Invalid Image Url.",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
            true
        }
        content.webViewClient = webViewClient
    }

    private fun setupSpinner() {
        spinner = binding.sortCommentsBySpinner
        spinner.visibility = View.GONE
        val arrayAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            
            R.array.sort_comments_by_array,
            android.R.layout.simple_spinner_item
        )
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinner.adapter = arrayAdapter
        val onAllCommentsScrollChangeListener =
            NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
                if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                    postDetailViewModel.loadNextPage()
                }
            }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long,
            ) {
                postDetailViewModel.postComments.value?.data?.let { comments ->
                    when (getSortByAt(position)) {
                        PostCommentSortBy.TOP -> {
                            commentAdapter.submitList(comments.topComments)
                            binding.postDetailScrollview.setOnScrollChangeListener(null as NestedScrollView.OnScrollChangeListener?)
                        }

                        PostCommentSortBy.ALL -> {
                            commentAdapter.submitList(comments.allComments)
                            binding.postDetailScrollview.setOnScrollChangeListener(
                                onAllCommentsScrollChangeListener
                            )
                        }
                    }

                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    }

    private fun setupCommentList() {
        commentAdapter = PostCommentAdapter(
            postLiveData = postDetailViewModel.post,
            lifecycleOwner = viewLifecycleOwner,
            onImageClick = {
                findNavController().navigate(
                    MobileNavigationDirections.actionGlobalNavImage(
                        it.large.url
                    )
                )
            }

        )
        binding.comments.adapter = commentAdapter
        binding.comments.addItemDecoration(
            DividerItemDecoration(binding.comments.context, DividerItemDecoration.VERTICAL)
        )
    }

    private fun initCommentList() {

        postDetailViewModel.postComments
            .observe(viewLifecycleOwner) { result ->
                spinner.visibility = View.GONE
                binding.findResource = result
                binding.resultCount = result.data?.allComments?.size ?: 0
                result.data?.let { comments ->
                    if (comments.topComments.isEmpty()) {
                        spinner.setSelection(1)
                    }
                    when (getSortByAt(spinner.selectedItemPosition)) {
                        PostCommentSortBy.TOP -> {
                            commentAdapter.submitList(comments.topComments)
                        }

                        PostCommentSortBy.ALL -> {
                            commentAdapter.submitList(comments.allComments)
                        }
                    }
                    spinner.visibility = View.VISIBLE
                }

                if (result != null) binding.swiperefresh.isRefreshing = false
            }
        postDetailViewModel.loadMoreStatus
            .observe(viewLifecycleOwner) { loadingMore ->
                if (loadingMore == null) {
                    binding.loadingMore = false
                } else {
                    binding.loadingMore = loadingMore.isRunning
                    val error = loadingMore.errorMessageIfNotHandled
                    if (error != null) {
                        binding.loadMoreBar.showSnackbar(error, Snackbar.LENGTH_LONG)
                    }
                }
                binding.executePendingBindings()
            }
        binding.callback = object : RetryCallback {
            override fun retry() {
                postDetailViewModel.refreshPostComments()
            }
        }
        binding.swiperefresh.setOnRefreshListener(postDetailViewModel::refreshPostComments)
    }

    private fun navigateToWebView(post: PostDetail) {
        val direction = MobileNavigationDirections.actionGlobalNavigationWebView(post.url)
        findNavController().navigate(direction)
    }

    private fun navigateToGroupTab(groupId: String, tabId: String) {
        val direction = PostDetailFragmentDirections.actionPostDetailToGroupDetail(groupId)
            .setDefaultTabId(tabId)
        findNavController().navigate(direction)
    }

    private fun navigateToGroup(groupId: String) {
        val direction = PostDetailFragmentDirections.actionPostDetailToGroupDetail(groupId)
        findNavController().navigate(direction)
    }

    private fun getSortByAt(position: Int) =
        when (position) {
            0 -> PostCommentSortBy.TOP
            1 -> PostCommentSortBy.ALL
            else -> throw IndexOutOfBoundsException()
        }
}