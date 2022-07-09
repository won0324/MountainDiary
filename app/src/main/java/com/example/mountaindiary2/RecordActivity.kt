package com.example.mountaindiary2

import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mountaindiary2.databinding.ActivityRecordBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class RecordActivity : AppCompatActivity() {
    lateinit var binding : ActivityRecordBinding
    lateinit var filePath: String
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_record)
        binding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val bgColor = sharedPreferences.getString("color", "")
        binding.recordLayout.setBackgroundColor(Color.parseColor(bgColor))

        //뒤로가기 버튼 누를 때
        binding.recordBack.setOnClickListener {
            //intent.putExtra("result", binding.addEditView.text.toString())
            setResult(RESULT_CANCELED, intent)
            finish()
        }

        //Gallery 버튼 누를 때
        val requestLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode === android.app.Activity.RESULT_OK){
                Glide
                    .with(applicationContext)
                    .load(it.data?.data)
                    .apply(RequestOptions().override(250,200))
                    .centerCrop()
                    .into(binding.addImageView)
                val cursor = contentResolver.query(it.data?.data as Uri,
                    arrayOf<String>(MediaStore.Images.Media.DATA),null,null,null)
                cursor?.moveToFirst().let{
                    filePath = cursor?.getString(0) as String
                }
            }
        }
        binding.menuAddGallery.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*")
            requestLauncher.launch(intent)
        }



        val requestCameraFileLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult() ){
            val calRatio = calculateInSampleSize(Uri.fromFile(File(filePath)), 150, 150)
            val option = BitmapFactory.Options()
            option.inSampleSize = calRatio
            val bitmap = BitmapFactory.decodeFile(filePath, option)
            bitmap?.let{
                binding.addImageView.setImageBitmap(bitmap)
            } ?:let{
                Log.d("mobileApp", "bitmap null")
            }
        }
        val timeS:String = SimpleDateFormat("yyyymmdd_HHmmss").format(Date())
        val storeDir:File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile("JPEG_${timeS}_", ".jpeg", storeDir)
        filePath = file.absolutePath
        val photoURI: Uri = FileProvider.getUriForFile(
            this,
            "com.example.mountaindiary2.fileprovider",
            file
        )

        binding.cameraBtn.setOnClickListener{
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)  // 두 번째 인자: URI
            requestCameraFileLauncher.launch(intent)
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        //글쓰기 완료할 때
        binding.finishBtn.setOnClickListener {
            if(binding.addImageView.drawable != null && binding.addEditView.text.isNotEmpty()){
                saveStore()

                val soundNotification = sharedPreferences.getString("noti_sound", "")
                if (soundNotification.equals("YES")) {
                    val notification : Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    val rington = RingtoneManager.getRingtone(applicationContext, notification)
                    rington.play()
                }
            }
            else{
                Toast.makeText(this, "데이터가 모두 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }
//            intent.putExtra("result", binding.addEditView.text.toString())
//            setResult(RESULT_OK, intent)
//            finish()

        }

    }

    override fun onResume() {
        super.onResume()
        val bgColor = sharedPreferences.getString("color", "")
        binding.recordLayout.setBackgroundColor(Color.parseColor(bgColor))
    }


    private fun saveStore(){
        val data = mapOf(
            "email" to LoginApplication.email,
            "content" to binding.addEditView.text.toString(),
            "date" to dateToString(Date())
        )

        LoginApplication.db.collection("news")
            .add(data)
            .addOnSuccessListener {
                uploadImage(it.id)
            }
            .addOnFailureListener {
                Log.d("mobileApp", "data save error")
            }
    }

    private fun uploadImage(docId:String){
        val storage = LoginApplication.storage
        val storageRef = storage.reference //스토리지가 갖고 있는 레퍼런스 값
        val imageRef = storageRef.child("images/${docId}.jpg")// 스토리지를 이용해서 해당 위치에 저장

        //원본 이미지 가져오기
        val file = Uri.fromFile(File(filePath))
        imageRef.putFile(file)  //file에 저장된 이미지를 imageRef의 형태로 저장
            //업로드가 성공되었을 때
            .addOnSuccessListener {
                Toast.makeText(this, "save ok",Toast.LENGTH_SHORT).show()
                finish()  //작업 종료
            }
            .addOnFailureListener {
                Log.d("mobileApp", "file save error")
            }

    }

    private fun calculateInSampleSize(fileUri: Uri, reqWidth: Int, reqHeight: Int): Int {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        try {
            var inputStream = contentResolver.openInputStream(fileUri)

            //inJustDecodeBounds 값을 true 로 설정한 상태에서 decodeXXX() 를 호출.
            //로딩 하고자 하는 이미지의 각종 정보가 options 에 설정 된다.
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream!!.close()
            inputStream = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //비율 계산........................
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1
        //inSampleSize 비율 계산
        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}