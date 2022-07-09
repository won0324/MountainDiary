package com.example.mountaindiary2

import androidx.multidex.MultiDexApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.kakao.sdk.common.KakaoSdk
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginApplication: MultiDexApplication() {
    companion object{
        lateinit var auth: FirebaseAuth
        var email:String? = null

        lateinit var db : FirebaseFirestore
        lateinit var storage:FirebaseStorage

        //유튜브 동영상 검색
        var networkService : NetworkService
        val retrofit: Retrofit
            get() = Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        init{
            networkService = retrofit.create(NetworkService::class.java)
        }

        //검증된 이메일인지 확인
        fun checkAuth() : Boolean{
            var currentUser = auth.currentUser
            return currentUser?.let{
                email = currentUser.email
                currentUser.isEmailVerified
            }?: let{
                false
            }
        }

        var networkServiceXml : NetworkService
        val parser = TikXml.Builder().exceptionOnUnreadXml(false).build()
        val retrofitXml : Retrofit
            get() = Retrofit.Builder()
                .baseUrl("http://openapi.forest.go.kr/")
                .addConverterFactory(TikXmlConverterFactory.create(parser))
                .build()
        init {
            networkServiceXml = retrofitXml.create(NetworkService::class.java)
        }

    }

    override fun onCreate() {
        super.onCreate()
        auth = Firebase.auth

        // 카카오 sdk 초기화
        KakaoSdk.init(this, "ba98e5558c68581e330f86c5cfc8a733")

        db = FirebaseFirestore.getInstance()
        storage = Firebase.storage
    }
}

