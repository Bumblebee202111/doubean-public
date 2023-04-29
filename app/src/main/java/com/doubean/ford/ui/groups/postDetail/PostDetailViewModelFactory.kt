package com.doubean.ford.ui.groups.postDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.doubean.ford.data.repository.GroupRepository

class PostDetailViewModelFactory(
    private val repository: GroupRepository,
    private val postId: String
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PostDetailViewModel(repository, postId) as T
    }
}