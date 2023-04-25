package com.doubean.ford.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceFragmentCompat;

import com.doubean.ford.BuildConfig;
import com.doubean.ford.R;
import com.google.android.material.appbar.MaterialToolbar;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        getPreferenceManager().findPreference("app_version").setSummary(BuildConfig.VERSION_NAME);
    }


    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout linearLayout = (LinearLayout) super.onCreateView(inflater, container, savedInstanceState);
        linearLayout.setFitsSystemWindows(true);
        addToolbar(linearLayout);
        return linearLayout;
    }

    private void addToolbar(LinearLayout linearLayout) {
        // https://blog.csdn.net/m0_46958584/article/details/105663403
        MaterialToolbar toolbar = (MaterialToolbar) LayoutInflater.from(getContext()).inflate(R.layout.toolbar_settings, linearLayout, false);
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        linearLayout.addView(toolbar, 0);
    }
}