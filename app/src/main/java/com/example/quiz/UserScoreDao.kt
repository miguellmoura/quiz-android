package com.example.quiz

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserScoreDao {
    @Insert
    suspend fun insertUserScore(userScore: UserScore)

    @Delete
    suspend fun deleteUserScore(userScore: UserScore)

@Query("SELECT * FROM user_scores ORDER BY score DESC, time ASC")
suspend fun getAllUserScores(): List<UserScore>
}