package com.application.fasrecon.ui.languagesettings

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.application.fasrecon.R
import com.application.fasrecon.data.preferences.SettingsPreference
import com.application.fasrecon.data.preferences.settingsDataStore
import com.application.fasrecon.databinding.ActivityLanguageSettingsBinding
import com.application.fasrecon.ui.BaseActivity
import com.application.fasrecon.ui.MainActivity
import com.application.fasrecon.ui.viewmodelfactory.SettingsViewModelFactory
import java.util.Locale

class LanguageSettingsActivity : BaseActivity() {

    private lateinit var binding: ActivityLanguageSettingsBinding
    private lateinit var pref: SettingsPreference
    private val languageSettingsViewModel: LanguageSettingsViewModel by viewModels { SettingsViewModelFactory(pref) }
    private lateinit var locale: Locale

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()
        pref = SettingsPreference.getInstance(this.settingsDataStore)

        val adapter = setListLanguage()
        val layoutManager = LinearLayoutManager(this)
        binding.listLanguage.layoutManager = layoutManager
        (binding.listLanguage.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false

        binding.saveLanguageSettings.setOnClickListener {
            setLanguage(adapter)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setListLanguage(): LanguageListAdapter {
        val languageData = LanguageObject(this).allLanguage()
        val adapter = LanguageListAdapter()

        languageSettingsViewModel.getLanguageSetting().observe(this) { langCode ->
            if (langCode != "") {
                languageData.forEach { it.checked = it.id == langCode }
            } else {
                languageData.first().checked = true
            }
            adapter.submitList(languageData)
            binding.listLanguage.adapter = adapter
        }
        return adapter
    }

    private fun setLanguage(adapter: LanguageListAdapter) {
        val selectedItem = adapter.getSelectedItem()?.id
        if (selectedItem != null) {
            setLocaleCode(selectedItem)
        }
    }

    private fun setLocaleCode(langCode: String) {
        locale = Locale(langCode)
        resources.configuration.setLocale(locale)

        @Suppress("DEPRECATION")
        resources.updateConfiguration(resources.configuration, resources.displayMetrics)
        languageSettingsViewModel.saveLanguageSettings(langCode)
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun setActionBar() {
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}