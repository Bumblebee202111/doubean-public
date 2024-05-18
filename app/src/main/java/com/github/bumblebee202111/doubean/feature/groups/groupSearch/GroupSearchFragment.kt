package com.github.bumblebee202111.doubean.feature.groups.groupSearch

import android.content.Context
import android.os.Bundle
import android.os.IBinder
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.databinding.FragmentGroupSearchBinding
import com.github.bumblebee202111.doubean.databinding.ListItemGroupBinding
import com.github.bumblebee202111.doubean.model.GroupSearchResultGroupItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupSearchFragment : Fragment() {
    private val groupSearchViewModel: GroupSearchViewModel by viewModels()
    lateinit var binding: FragmentGroupSearchBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentGroupSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.groupList.setContent {
            val groupPagingItems = groupSearchViewModel.results.collectAsLazyPagingItems()
            val navigateToGroup = { group: GroupSearchResultGroupItem ->
                val direction =
                    GroupSearchFragmentDirections.actionGroupSearchToGroupDetail(group.id)
                findNavController().navigate(direction)
            }
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(400.dp),
                verticalItemSpacing = 4.dp,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                content = {
                    items(
                        count = groupPagingItems.itemCount,
                        key = groupPagingItems.itemKey { it.id },
                        contentType = groupPagingItems.itemContentType { "groupPagingItem" }) { index ->
                        val group = groupPagingItems[index]
                        AndroidViewBinding(
                            factory = ListItemGroupBinding::inflate,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            onReset = {}) {
                            this.group = group
                            setClickListener {
                                if (group != null) {
                                    navigateToGroup(group)
                                }
                            }
                            avatar.setContent {
                                AsyncImage(
                                    model = group?.avatarUrl,
                                    contentDescription = stringResource(id = R.string.a11y_group_item_image),
                                    modifier = Modifier
                                        .size(dimensionResource(id = R.dimen.icon_size_extra_large))
                                        .clip(RoundedCornerShape(dimensionResource(id = R.dimen.corner_size_small))),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        initSearchInputListener()
    }

    private fun initSearchInputListener() {
        binding.input.setOnEditorActionListener { v: View, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearch(v)
                true
            } else {
                false
            }
        }
        binding.input.setOnKeyListener { view: View, keyCode: Int, event: KeyEvent ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                doSearch(view)
                true
            } else {
                false
            }
        }
    }

    private fun doSearch(v: View) {
        val query = binding.input.text.toString()
        dismissKeyboard(v.windowToken)
        groupSearchViewModel.setQuery(query)
    }

    private fun dismissKeyboard(windowToken: IBinder) {
        val activity = activity
        if (activity != null) {
            val imm = activity.getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            imm.hideSoftInputFromWindow(windowToken, 0)
        }
    }
}