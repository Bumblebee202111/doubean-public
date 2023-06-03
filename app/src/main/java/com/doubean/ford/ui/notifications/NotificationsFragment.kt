package com.doubean.ford.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.doubean.ford.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {
    private val notificationsViewModel: NotificationsViewModel by viewModels()
    private lateinit var binding: FragmentNotificationsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root = binding.root
        val textView = binding.textNotifications
        notificationsViewModel.text.observe(viewLifecycleOwner) { s -> textView.text = s }
        return root
    }

}