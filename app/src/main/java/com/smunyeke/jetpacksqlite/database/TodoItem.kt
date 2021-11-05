package com.smunyeke.jetpacksqlite.database

data class TodoItem(
    var itemId:Long = 0L,
    var itemName:String,
){
    constructor():this(0L,"")
}
