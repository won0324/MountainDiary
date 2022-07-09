package com.example.mountaindiary2

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.PreferenceManager
import com.example.mountaindiary2.databinding.ActivityHikingBinding

class HikingActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var binding : ActivityHikingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //etContentView(R.layout.activity_hiking)

        binding = ActivityHikingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val bgColor = sharedPreferences.getString("color", "")
        binding.hikingLayout.setBackgroundColor(Color.parseColor(bgColor))

        //뒤로가기 버튼 누를 때
        binding.hikingBack.setOnClickListener {
            finish()
        }

        val xmlfragment = XmlFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.activity_content, xmlfragment)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        val bgColor = sharedPreferences.getString("color", "")
        binding.hikingLayout.setBackgroundColor(Color.parseColor(bgColor))
    }
}