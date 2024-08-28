package com.github.bumblebee202111.doubean.feature.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.compose.content
import androidx.navigation.fragment.findNavController
import com.github.bumblebee202111.doubean.ui.theme.AppTheme

class GroupDefaultNotificationsPreferencesSettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = content {
        AppTheme {
            GroupDefaultNotificationsPreferencesSettingsScreen {
                findNavController().navigateUp()
            }
        }
    }
}