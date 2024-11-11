package com.example.quiz
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_scores")
data class UserScore(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val time : Long,
    val score: Int
)