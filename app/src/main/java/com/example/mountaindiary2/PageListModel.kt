package com.example.mountaindiary2

data class SearchListResponse(
    val items:ArrayList<SearchResult> = ArrayList<SearchResult>()
)

data class SearchResult(
    val id:SearchResultId?,
    val snippet:SearchResultSnippet?
)

data class SearchResultId(
    val kind:String?,
    val videoId:String?
)

data class SearchResultSnippet(
    val publishedAt:String?,
    val title:String?,
    val description:String,
    val thumbnails:Thumbnails
)

data class Thumbnails(
    val default:ThumbnailsInfo?,
    val medium:ThumbnailsInfo?,
    val high:ThumbnailsInfo?
)

data class ThumbnailsInfo(
    val url:String?,
    val width:Int?,
    val height:Int?
)
