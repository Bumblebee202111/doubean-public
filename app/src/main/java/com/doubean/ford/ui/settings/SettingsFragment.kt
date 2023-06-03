package com.doubean.ford.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.Navigation.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.doubean.ford.BuildConfig
import com.doubean.ford.R
import com.google.android.material.appbar.MaterialToolbar

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        preferenceManager.findPreference<Preference>("app_version")!!.summary =
            BuildConfig.VERSION_NAME
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val linearLayout =
            super.onCreateView(inflater, container, savedInstanceState) as LinearLayout
        linearLayout.fitsSystemWindows = true
        addToolbar(linearLayout)
        return linearLayout
    }

    private fun addToolbar(linearLayout: LinearLayout) {
        // https://blog.csdn.net/m0_46958584/article/details/105663403
        val toolbar = LayoutInflater.from(context)
            .inflate(R.layout.toolbar_settings, linearLayout, false) as MaterialToolbar
        toolbar.setNavigationOnClickListener { v ->
            findNavController(v!!).navigateUp()
        }
        linearLayout.addView(toolbar, 0)
    }
}