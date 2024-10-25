package com.example.quiz

object Singleton {

    private var userName: String? = null

    fun setUserName(name: String) {
        userName = name
    }

}