package com.example.mountaindiary2

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mountaindiary2.databinding.ActivityVideoBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VideoActivity : AppCompatActivity() {
    lateinit var binding : ActivityVideoBinding
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_video)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val bgColor = sharedPreferences.getString("color", "")
        binding.videoLayout.setBackgroundColor(Color.parseColor(bgColor))

        //뒤로가기 버튼 누를 때
        binding.videoBack.setOnClickListener {
            finish()
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        binding.searchButton.setOnClickListener {
            val notification = sharedPreferences.getString("noti_push", "")
            if (notification.equals("YES")) {
                val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                val builder: NotificationCompat.Builder

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val ch_id = "Add"
                    val channel = NotificationChannel(
                        ch_id,
                        "Add Write",
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                    channel.description = "영상 검색 알림"
                    channel.setShowBadge(true)
                    channel.enableLights(true)
                    channel.lightColor = Color.RED

                    manager.createNotificationChannel(channel)
                    builder = NotificationCompat.Builder(this, ch_id)
                } else {
                    builder = NotificationCompat.Builder(this)
                }

                builder.setSmallIcon(R.drawable.navi_map)
                builder.setWhen(System.currentTimeMillis())
                builder.setContentTitle("관련 영상 검색")
                builder.setContentText("검색이 완료되었습니다.")

                manager.notify(11, builder.build())
            }

            var call: Call<SearchListResponse> = LoginApplication.networkService.getList(
                "AIzaSyBSv1XMQG10LbO2yCIzB1Ks30bZo2juklQ",
                binding.input1.text.toString(),
                "video",
                "snippet")

            call?.enqueue(object : Callback<SearchListResponse> {
                override fun onResponse(
                    call: Call<SearchListResponse>,
                    response: Response<SearchListResponse>
                ) {
                    if(response.isSuccessful){
                        binding.videoRecyclerView.layoutManager = LinearLayoutManager(this@VideoActivity)
                        binding.videoRecyclerView.adapter = VideoAdapter(this@VideoActivity, response.body()?.items)
                    }
                }

                override fun onFailure(call: Call<SearchListResponse>, t: Throwable) {
                    Log.d("mobileApp", "error..")
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        val bgColor = sharedPreferences.getString("color", "")
        binding.videoLayout.setBackgroundColor(Color.parseColor(bgColor))
    }
}