package edu.ucne.loginapi.domain.useCase

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import edu.ucne.loginapi.data.syncWorker.ChatSyncWorker
import javax.inject.Inject

class TriggerChatSyncUseCase @Inject constructor(
    private val context: Context
) {
    operator fun invoke() {
        val request = OneTimeWorkRequestBuilder<ChatSyncWorker>().build()
        WorkManager.getInstance(context).enqueue(request)
    }
}
