package com.pam_228779.todoapppro.view.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.preference.MultiSelectListPreference
import androidx.preference.PreferenceFragmentCompat
import com.pam_228779.todoapppro.R
import com.pam_228779.todoapppro.viewModel.TaskViewModel

class SettingsFragment : PreferenceFragmentCompat() {

    private val taskViewModel: TaskViewModel by viewModels()
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val categoriesList: MultiSelectListPreference? = findPreference("categories_to_show")

        taskViewModel.getAllCategories().observe(viewLifecycleOwner) {
            categoriesList?.entries = it.toTypedArray()
            categoriesList?.entryValues = it.toTypedArray()
        }
    }
}