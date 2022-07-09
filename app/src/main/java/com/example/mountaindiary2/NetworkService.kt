package com.example.mountaindiary2

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkService {
    @GET("openapi/service/cultureInfoService/gdTrailInfoOpenAPI")
    fun getXmlList(
        @Query("serviceKey") apiKey:String?,
        @Query("pageNo") pageNo:Int,
        @Query("startPage") startPage:Int,
        @Query("numOfRows") numOfRows:Int,
        @Query("pageSize") pageSize:Int
    ): Call<responseInfo>

    //유튜브 동영상 검색색
   @GET("youtube/v3/search")
   fun getList(
        @Query("key") key:String,
        @Query("q") search_query : String,
        @Query("type") returnType : String,
        @Query("part") returnData : String
   ): Call<SearchListResponse>
}