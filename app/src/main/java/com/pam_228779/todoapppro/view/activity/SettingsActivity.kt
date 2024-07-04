package com.pam_228779.todoapppro.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pam_228779.todoapppro.R
import com.pam_228779.todoapppro.view.fragment.SettingsFragment

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_container, SettingsFragment())
            .commit()
    }
}
