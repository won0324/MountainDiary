package com.example.mountaindiary2

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo: 37.7005, 127.0157"))
        startActivity(intent)
    }
}