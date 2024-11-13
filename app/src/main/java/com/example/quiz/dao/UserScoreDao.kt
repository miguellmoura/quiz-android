package com.example.quiz.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.quiz.entity.UserScore

@Dao
interface UserScoreDao {
    @Insert
    suspend fun insertUserScore(userScore: UserScore)

@Query("SELECT * FROM user_scores ORDER BY score DESC, time ASC")
suspend fun getAllUserScores(): List<UserScore>
}