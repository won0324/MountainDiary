package com.example.mountaindiary2

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.preference.PreferenceManager
import com.example.mountaindiary2.databinding.ActivityMainBinding
import com.google.android.youtube.player.internal.f
import com.kakao.sdk.common.util.Utility


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //val keyHash = Utility.getKeyHash(this)
        //Log.d("mobileApp", keyHash)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val bgColor = sharedPreferences.getString("color", "")
        binding.rootLayout.setBackgroundColor(Color.parseColor(bgColor))

        val nickName = sharedPreferences.getString("id", "")
        if (!nickName.isNullOrBlank()) {
            if ((LoginApplication.checkAuth() || LoginApplication.email != null) && nickName != null)
                binding.authTv.text = "${nickName}님의 Diary"
            else if ((LoginApplication.checkAuth() || LoginApplication.email == null) && nickName == null)
                binding.authTv.text = "${LoginApplication.email}님의 Diary"
            else
                binding.authTv.text = "Mountain Diary"
        }

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            //로그아웃된 상태
            if (binding.btnLogin.text.equals("Login"))
                intent.putExtra("data", "logout")
            else if (binding.btnLogin.text.equals("Logout"))
                intent.putExtra("data", "login")
            startActivity(intent)
        }

        //Hiking Course 버튼
        binding.hikeBtn.setOnClickListener {
            if (LoginApplication.checkAuth() || binding.btnLogin.text.equals("Logout")) {
                startActivity(Intent(this, HikingActivity::class.java))

            } else {
                Toast.makeText(this, "인증을 진행해주세요", Toast.LENGTH_SHORT).show()
            }
        }

            //다이어리버튼 눌렀을 때
            binding.diaryBtn.setOnClickListener {
                if (LoginApplication.checkAuth() || binding.btnLogin.text.equals("Logout")) {
                    startActivity(Intent(this, DiaryActivity::class.java))
                } else {
                    Toast.makeText(this, "인증을 진행해주세요", Toast.LENGTH_SHORT).show()
                }
            }

            binding.videoBtn.setOnClickListener {
                if (LoginApplication.checkAuth() || binding.btnLogin.text.equals("Logout")) {
                    startActivity(Intent(this, VideoActivity::class.java))
                } else {
                    Toast.makeText(this, "인증을 진행해주세요", Toast.LENGTH_SHORT).show()
                }
            }


            //설정버튼 눌렀을 때
            binding.settingBtn.setOnClickListener {
                if (LoginApplication.checkAuth() || binding.btnLogin.text.equals("Logout")) {
                    startActivity(Intent(this, SettingActivity::class.java))
                } else {
                    Toast.makeText(this, "인증을 진행해주세요", Toast.LENGTH_SHORT).show()
                }
            }

            //지도버튼 눌렀을 때
            binding.mapBtn.setOnClickListener {
                if (LoginApplication.checkAuth() || binding.btnLogin.text.equals("Logout")) {
                    startActivity(Intent(this, MapActivity::class.java))
                } else {
                    Toast.makeText(this, "인증을 진행해주세요", Toast.LENGTH_SHORT).show()
                }
            }

    }

        override fun onResume() {
            super.onResume()
            val bgColor = sharedPreferences.getString("color", "")
            binding.rootLayout.setBackgroundColor(Color.parseColor(bgColor))

            val nickName = sharedPreferences.getString("id", "")
            if (!nickName.isNullOrBlank()) {
                if ((LoginApplication.checkAuth() || LoginApplication.email != null) && nickName != null)
                    binding.authTv.text = "${nickName}님의 Diary"
                else if ((LoginApplication.checkAuth() || LoginApplication.email == null) && nickName == null)
                    binding.authTv.text = "${LoginApplication.email}님의 Diary"
                else
                    binding.authTv.text = "Mountain Diary"
            }
        }

        override fun onStart() {
            super.onStart()
            if (LoginApplication.checkAuth() || LoginApplication.email != null) {
                binding.btnLogin.text = "Logout"
                binding.authTv.text = "${LoginApplication.email}님의 Diary"
                binding.authTv.textSize = 16F
            } else {
                binding.btnLogin.text = "Login"
                binding.authTv.text = "Mountain Diary"
                binding.authTv.textSize = 24F
            }
        }

}