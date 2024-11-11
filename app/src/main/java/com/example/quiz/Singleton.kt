package com.example.quiz

import android.content.Context

object Singleton {

    private var userName: String? = null
    private var database: AppDatabase? = null

    fun setUserName(name: String) {
        userName = name
    }
    fun getUserName(): String? {
        return userName
    }

    fun initializeDatabase(context: Context) {
        if(database == null) {
            database = AppDatabase.getDatabase(context)
        }
    }

    fun getDatabase(): AppDatabase? {
        return database
    }

}