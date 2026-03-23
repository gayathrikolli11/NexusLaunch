package com.example.nexuslaunch.domain.usecase

import com.example.nexuslaunch.domain.model.AppInfo
import com.example.nexuslaunch.domain.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAppsUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    operator fun invoke(): Flow<List<AppInfo>> = appRepository.getInstalledApps()
}