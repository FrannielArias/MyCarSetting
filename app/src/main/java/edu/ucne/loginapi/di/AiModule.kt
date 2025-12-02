package edu.ucne.loginapi.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.ucne.loginapi.IA.AiChatClient
import edu.ucne.loginapi.IA.GeminiNanoAiChatClient
import edu.ucne.loginapi.IA.HybridAiChatClient
import edu.ucne.loginapi.IA.StubAiChatClient
import edu.ucne.loginapi.data.chat.LocalAiChatDataSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiModule {

    @Provides
    @Singleton
    fun provideAiChatClient(): AiChatClient {
        val nano = GeminiNanoAiChatClient()
        val stub = StubAiChatClient()
        return HybridAiChatClient(nano, stub)
    }

    @Provides
    @Singleton
    fun provideLocalAiChatDataSource(
        aiChatClient: AiChatClient
    ): LocalAiChatDataSource {
        return LocalAiChatDataSource(aiChatClient)
    }
}