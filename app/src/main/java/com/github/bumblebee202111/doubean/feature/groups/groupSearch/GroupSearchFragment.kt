package com.github.bumblebee202111.doubean.feature.groups.groupSearch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
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
        binding.textField.setContent {
            val keyboardController = LocalSoftwareKeyboardController.current
            var query by rememberSaveable { mutableStateOf("") }
            TextField(
                value = query,
                onValueChange = { query = it },
                label = { Text(stringResource(id = R.string.search_hint)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        groupSearchViewModel.setQuery(query)
                    }
                ),
                singleLine = true
            )
        }
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
    }
}