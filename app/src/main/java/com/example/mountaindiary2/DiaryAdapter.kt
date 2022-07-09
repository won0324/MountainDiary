package com.example.mountaindiary2

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mountaindiary2.databinding.ItemRecyclerviewBinding

class MyViewHolder(val binding:ItemRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root)

class DiaryAdapter(val context: Context, val itemList: MutableList<ItemData>) : RecyclerView.Adapter<MyViewHolder>(){
    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MyViewHolder(ItemRecyclerviewBinding.inflate(layoutInflater))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = itemList.get(position)
        holder.binding.run{
            itemEmailView.text = data.email
            itemDateView.text = data.date
            itemContentView.text = data.content
        }

        //리사이클러뷰에 해당되는 이미지를 storage로부터 모바일쪽으로 가져와야 함
        val imageRef = LoginApplication.storage.reference.child("images/${data.docId}.jpg")  //다운 받은 이미지에 대한 정보 확보
        //이미지를 다운로드 받음
        imageRef.downloadUrl.addOnCompleteListener { task ->
            //성공적으로 다운로드된 경우
            if(task.isSuccessful){
                Glide.with(context)
                    .load(task.result)  //이미지에 대한 정보 로드
                    .into(holder.binding.itemImageView)  //ImageView에 넣겠다
            }
        }
    }
}