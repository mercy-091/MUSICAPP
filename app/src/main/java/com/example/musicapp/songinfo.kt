package com.example.musicapp

class songinfo {
    var sermon: String? = null
    var teacher: String? = null
    var sermonurl: String? = null
    constructor(sermon:String, teacher: String, sermonurl: String){
        this.sermon = sermon
        this.teacher = teacher
        this.sermonurl = sermonurl
    }
}