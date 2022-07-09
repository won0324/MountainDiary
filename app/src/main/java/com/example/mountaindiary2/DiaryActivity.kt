package com.example.mountaindiary2

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mountaindiary2.databinding.ActivityDiaryBinding

class DiaryActivity : AppCompatActivity() {
    var datas:MutableList<String>? = null
    lateinit var adapter : DiaryAdapter
    lateinit var binding : ActivityDiaryBinding
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_diary)
        binding = ActivityDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val bgColor = sharedPreferences.getString("color", "")
        binding.diaryLayout.setBackgroundColor(Color.parseColor(bgColor))

        myCheckPermission(this)

        binding.fab.setOnClickListener{
            startActivity(Intent(this,RecordActivity::class.java))
        }

        //뒤로가기 버튼 누를 때
        binding.diaryBack.setOnClickListener {
            finish()
        }

    }

    override fun onResume() {
        super.onResume()
        val bgColor = sharedPreferences.getString("color", "")
        binding.diaryLayout.setBackgroundColor(Color.parseColor(bgColor))
    }

    override fun onStart() {
        super.onStart()
        if(LoginApplication.checkAuth() || LoginApplication.email != null){
            binding.recyclerView.visibility = View.VISIBLE
            makeRecyclerView()
        }
        else {
            // 로그아웃 상태
            binding.recyclerView.visibility = View.GONE // 이미지 리사이클러뷰
        }
    }

    private fun makeRecyclerView(){
        //firestore에 있는 정보를 가져옴
        LoginApplication.db.collection("news")
            .get()
            .addOnSuccessListener { result ->
                val itemList = mutableListOf<ItemData>()
                //firestore에서 가져온 모든 데이터들이 itemList에 저장됨
                for(document in result){
                    //item 생성
                    val item = document.toObject(ItemData::class.java)  //ItemData의 형태로 바꿔줌
                    item.docId = document.id
                    itemList.add(item)  //itemList에 item 추가
                }
                //리사이클러뷰에 대한 설정
                binding.recyclerView.layoutManager = LinearLayoutManager(this)
                binding.recyclerView.adapter = DiaryAdapter(this, itemList)
            }
            //내용을 가져오지 못했다면
            .addOnFailureListener {
                Toast.makeText(this, "서버 데이터 획득 실패",Toast.LENGTH_SHORT).show()
            }

    }

}

