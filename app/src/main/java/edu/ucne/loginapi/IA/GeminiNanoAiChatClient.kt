package edu.ucne.loginapi.IA

import com.google.mlkit.genai.common.DownloadStatus
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.prompt.Generation
import com.google.mlkit.genai.prompt.GenerativeModel
import com.google.mlkit.genai.prompt.TextPart
import com.google.mlkit.genai.prompt.generateContentRequest
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class GeminiNanoAiChatClient : AiChatClient {

    private val generativeModel: GenerativeModel = Generation.getClient()
    private val mutex = Mutex()
    private var modelReady: Boolean = false

    override suspend fun generateReply(
        userMessage: String,
        history: List<Pair<String, String>>
    ): String {
        ensureModelReady()
        if (!modelReady) {
            return "GEMINI_NANO_UNAVAILABLE"
        }

        val response = generativeModel.generateContent(
            generateContentRequest(
                TextPart(userMessage)
            ) {
                temperature = 0.4f
                topK = 32
                candidateCount = 1
                maxOutputTokens = 192
            }
        )

        val text = response.candidates.firstOrNull()?.text
        return text ?: "GEMINI_NANO_EMPTY_RESPONSE"
    }

    private suspend fun ensureModelReady() {
        mutex.withLock {
            if (modelReady) return

            val status = generativeModel.checkStatus()
            when (status) {
                FeatureStatus.UNAVAILABLE -> {
                    modelReady = false
                }
                FeatureStatus.DOWNLOADABLE -> {
                    var completed = false
                    var failed = false
                    generativeModel.download().collect { downloadStatus ->
                        when (downloadStatus) {
                            is DownloadStatus.DownloadCompleted -> {
                                completed = true
                            }
                            is DownloadStatus.DownloadFailed -> {
                                failed = true
                            }
                            else -> Unit
                        }
                    }
                    modelReady = completed && !failed
                }
                FeatureStatus.DOWNLOADING -> {
                    modelReady = false
                }
                FeatureStatus.AVAILABLE -> {
                    modelReady = true
                }
            }
        }
    }
}