package com.nabil.instagram.model

class Post {

    private var postid: String = ""
    private var postimage: String = ""
    private var publisher: String = ""
    private var description: String = ""

    constructor()
    constructor(postId: String, postimage: String, publisher: String, description: String) {
        this.postid = postId
        this.postimage = postimage
        this.publisher = publisher
        this.description = description
    }

    //get
    fun getPostid(): String{
        return postid
    }

    fun getPostimage(): String{
        return postimage
    }

    fun getPublisher(): String{
        return publisher
    }

    fun getDescription(): String{
        return description
    }

    //set
    fun setPostid(postid: String){
        this.postid = postid
    }

    fun setPostimage(postimage: String){
        this.postimage = postimage
    }

    fun setPublisher(publisher: String){
        this.publisher = publisher
    }

    fun Description(description: String){
        this.description = description
    }
}