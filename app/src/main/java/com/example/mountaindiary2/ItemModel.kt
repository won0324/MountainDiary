package com.example.mountaindiary2

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name="response")
data class responseInfo(
    @Element
    val header : Header,
    @Element
    val body : myBody
)

@Xml(name="header")
data class Header(
    @PropertyElement
    val resultCode : Int,
    @PropertyElement
    val resultMsg : String
)

@Xml(name="body")
data class myBody(
    @Element
    val items : myItems,
    @PropertyElement
    val numOfRows : Int,
    @PropertyElement
    val pageNo : Int,
    @PropertyElement
    val totalCount : Int
)

@Xml(name="items")
data class myItems(
    @Element(name="item")
    val item : MutableList<myItem>
)

@Xml
data class myItem(
    @PropertyElement
    val mntnm : String?,
    @PropertyElement
    val subnm : String?,
    @PropertyElement
    val etccourse : String?
){
    constructor() : this(null, null, null)
}
