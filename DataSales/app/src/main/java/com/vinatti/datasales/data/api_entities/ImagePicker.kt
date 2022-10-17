package com.vinatti.datasales.data.api_entities

data class ImagePicker(
    var uri: String="",
    var isChecked: Boolean = false,
    var isCamera: Boolean=false,
    var path:String =""

)