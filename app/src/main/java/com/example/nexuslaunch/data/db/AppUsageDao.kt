package com.example.nexuslaunch.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppUsageDao {

    @Query("SELECT * FROM app_usage ORDER BY launchCount DESC")
    fun getAllUsage(): Flow<List<AppUsageEntity>>

    @Query("SELECT * FROM app_usage WHERE packageName = :pkg LIMIT 1")
    suspend fun getUsage(pkg: String): AppUsageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: AppUsageEntity)

    @Query("UPDATE app_usage SET launchCount = launchCount + 1, lastLaunched = :time WHERE packageName = :pkg")
    suspend fun incrementLaunch(pkg: String, time: Long)
}