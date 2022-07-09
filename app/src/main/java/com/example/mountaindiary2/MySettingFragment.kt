package com.example.mountaindiary2

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.BuildCompat
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

/**
 * A simple [Fragment] subclass.
 * Use the [MySettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MySettingFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        val idPreference:EditTextPreference? = findPreference("id")
        idPreference?.title = "닉네임 설정"
        idPreference?.summaryProvider = Preference.SummaryProvider<EditTextPreference> {
            preference ->
                val text = preference.text
                if(TextUtils.isEmpty(text)){
                    "닉네임 설정이 되지 않았습니다."
                }
                else{
                    "설정된 닉네임은 $text 입니다."
                }
        }
        val colorPreference:ListPreference? = findPreference("color")
        colorPreference?.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()

        val notificationPreference: ListPreference? = findPreference("noti_push")
        notificationPreference?.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()

        val soundNotificationPreference:ListPreference? = findPreference("noti_sound")
        soundNotificationPreference?.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()

        val fontPreference: ListPreference ?= findPreference(getString(R.string.pref_key_night))
        fontPreference?.onPreferenceChangeListener = modeChangeListener
        fontPreference?.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
    }

    private val modeChangeListener = object : Preference.OnPreferenceChangeListener {
        override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
            Log.i("newValue", newValue.toString())
            newValue as? String
            when (newValue) {
                getString(R.string.pref_night_on) -> {
                    updateTheme(AppCompatDelegate.MODE_NIGHT_YES)
                }
                getString(R.string.pref_night_off) -> {
                    updateTheme(AppCompatDelegate.MODE_NIGHT_NO)
                }
                else -> {
                    if (BuildCompat.isAtLeastQ()) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                    }
                }
            }
            return true
        }
    }

    private fun updateTheme(nightMode: Int): Boolean {
        AppCompatDelegate.setDefaultNightMode(nightMode)
        requireActivity().recreate()
        return true
    }


}