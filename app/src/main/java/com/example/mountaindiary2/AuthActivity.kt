package com.example.mountaindiary2

import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.mountaindiary2.databinding.ActivityAuthBinding
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient

class AuthActivity : AppCompatActivity() {
    lateinit var  binding: ActivityAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_auth)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        changeVisivility(intent.getStringExtra("data").toString())
        binding.goSignInBtn.setOnClickListener {
            changeVisivility("signin")
        }

        binding.signBtn.setOnClickListener {
            val email = binding.authEmailEditView.text.toString()
            val password = binding.authPasswordEditView.text.toString()
            LoginApplication.auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this){task ->
                    binding.authEmailEditView.text.clear()
                    binding.authPasswordEditView.text.clear()
                    if(task.isSuccessful){
                        LoginApplication.auth.currentUser?.sendEmailVerification()
                            ?.addOnCompleteListener{sendTask ->
                                if(sendTask.isSuccessful){
                                    Toast.makeText(baseContext,"회원가입 성공! 메일을 확인해주세요", Toast.LENGTH_SHORT).show()
                                    changeVisivility("logout")
                                }
                                else{
                                    Toast.makeText(baseContext,"메일발송 실패", Toast.LENGTH_SHORT).show()
                                    changeVisivility("logout")
                                }
                            }
                    }
                    else{
                        Toast.makeText(baseContext,"회원가입 실패", Toast.LENGTH_SHORT).show()
                        changeVisivility("logout")
                    }
                }
        }
        binding.loginBtn.setOnClickListener {
            val email = binding.authEmailEditView.text.toString()
            val password = binding.authPasswordEditView.text.toString()
            LoginApplication.auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this){task ->
                    binding.authEmailEditView.text.clear()
                    binding.authPasswordEditView.text.clear()
                    if(task.isSuccessful){
                        if(LoginApplication.checkAuth()){
                            LoginApplication.email = email
                            finish()
                        }
                        else{
                            Toast.makeText(baseContext,"이메일 인증이 되지 않았습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else{
                        Toast.makeText(baseContext,"로그인 실패", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        binding.logoutBtn.setOnClickListener {

            // 카카오 로그아웃
//            UserApiClient.instance.logout { error ->
//                if(error != null){
//                    Toast.makeText(baseContext, "로그아웃 ", Toast.LENGTH_SHORT).show()
//                }
//                else{
//
//                }
//            }
            Toast.makeText(baseContext, "로그아웃 성공", Toast.LENGTH_SHORT).show()
            LoginApplication.auth.signOut()
            LoginApplication.email = null

            finish()
        }

        binding.btnKakaoLogin.setOnClickListener {
            // 토큰 정보 보기
            UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                if (error != null) {
                    Log.e("mobileApp", "토큰 정보 보기 실패", error)
                }
                else if (tokenInfo != null) {
                    Log.i("mobileApp", "토큰 정보 보기 성공")
                    finish()
                }
            }

            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Log.e("mobileApp", "카카오계정으로 로그인 실패", error)
                } else if (token != null) {
                    Log.i("mobileApp", "카카오계정으로 로그인 성공 ${token.accessToken}")
                    // 사용자 정보 요청 (기본)
                    UserApiClient.instance.me { user, error ->
                        if (error != null) {
                            Log.e("mobileApp", "사용자 정보 요청 실패", error)
                        } else if (user != null) {
                            Log.i("mobileApp", "사용자 정보 요청 성공 ${user.kakaoAccount?.email}")

                            var scopes = mutableListOf<String>()
                            if(user.kakaoAccount?.email != null){  // 이메일을 가져왔다면
                                LoginApplication.email = user.kakaoAccount?.email
                                finish()
                            }
                            //----
                            else if(user.kakaoAccount?.emailNeedsAgreement == true){ // 이메일 정보를 가져오는 데에 사용자에게 추가적인 동의를 받아야 한다면
                                Log.i("mobileApp", "사용자에게 추가 동의 필요")
                                // 사용자에게 허용받아야 할 항목을 미리 변수로 만들어 둠 (scope)
                                scopes.add("account_email")  // 카카오 개발자 홈페이지 - 동의항목 참고
                                UserApiClient.instance.loginWithNewScopes(this, scopes){ token, error ->
                                    if(error != null) {
                                        Log.e("mobileApp", "추가 동의 실패", error)
                                    } else {  // 추가 동의 후 에러 발생하지 않은 경우
                                        // 사용자 정보 재요청
                                        UserApiClient.instance.me { user, error ->
                                            if(error!=null){
                                                Log.e("mobileApp", "사용자 정보 요청 실패", error)
                                            }
                                            else if(user != null){ // 사용자 정보 요청 성공
                                                LoginApplication.email = user.kakaoAccount?.email.toString()
                                                finish()  // MainActivity로 돌아감
                                            }
                                        }
                                    }
                                }
                            }
                            else {  // 이메일 정보가 없고, 추가 동의도 구하지 못한 경우
                                Log.e("mobileApp", "이메일 획득 불가", error)
                            }
                        }
                    }
                }
            }

            if(UserApiClient.instance.isKakaoTalkLoginAvailable(this)){
                UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
            } else {  // 사용자의 폰에 카카오톡이 없는 경우
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }
    }

    fun changeVisivility(mode:String){
        if(mode.equals("login")){
            binding.run{
                topText.text = "LOGOUT"
                topText.visibility = View.VISIBLE
                logoutBtn.visibility = View.VISIBLE
                goSignInBtn.visibility = View.GONE
                authEmailEditView.visibility = View.GONE
                authPasswordEditView.visibility = View.GONE
                signBtn.visibility = View.GONE
                loginBtn.visibility = View.GONE
                btnKakaoLogin.visibility = View.GONE
            }
        }
        else if(mode.equals("logout")){
            binding.run{
                topText.text = "LOGIN"
                topText.visibility = View.VISIBLE
                logoutBtn.visibility = View.GONE
                goSignInBtn.visibility = View.VISIBLE
                authEmailEditView.visibility = View.VISIBLE
                authPasswordEditView.visibility = View.VISIBLE
                signBtn.visibility = View.GONE
                loginBtn.visibility = View.VISIBLE
                btnKakaoLogin.visibility = View.VISIBLE
            }
        }
        else if(mode.equals("signin")){
            binding.run{
                topText.text = "SIGN UP"
                topText.visibility = View.VISIBLE
                logoutBtn.visibility = View.GONE
                goSignInBtn.visibility = View.GONE
                authEmailEditView.visibility = View.VISIBLE
                authPasswordEditView.visibility = View.VISIBLE
                signBtn.visibility = View.VISIBLE
                loginBtn.visibility = View.GONE
                btnKakaoLogin.visibility = View.GONE
            }
        }
    }

}