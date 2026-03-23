package com.example.nexuslaunch.domain.usecase

import com.example.nexuslaunch.domain.repository.AppRepository
import javax.inject.Inject

class RecordAppLaunchUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    suspend operator fun invoke(packageName: String) {
        appRepository.recordLaunch(packageName)
    }
}