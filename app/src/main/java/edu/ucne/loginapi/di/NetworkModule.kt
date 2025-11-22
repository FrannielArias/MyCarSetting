package edu.ucne.loginapi.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import edu.ucne.loginapi.data.remote.api.CarApiService
import edu.ucne.loginapi.data.remote.api.MaintenanceApiService
import edu.ucne.loginapi.data.remote.api.ManualApiService
import edu.ucne.loginapi.data.remote.api.ChatApiService

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://your-api-base-url/"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        client: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideCarApiService(
        retrofit: Retrofit
    ): CarApiService = retrofit.create(CarApiService::class.java)

    @Provides
    @Singleton
    fun provideMaintenanceApiService(
        retrofit: Retrofit
    ): MaintenanceApiService = retrofit.create(MaintenanceApiService::class.java)

    @Provides
    @Singleton
    fun provideManualApiService(
        retrofit: Retrofit
    ): ManualApiService = retrofit.create(ManualApiService::class.java)

    @Provides
    @Singleton
    fun provideChatApiService(
        retrofit: Retrofit
    ): ChatApiService = retrofit.create(ChatApiService::class.java)
}
