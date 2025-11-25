package edu.ucne.loginapi.di

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.ucne.loginapi.data.remote.UsuariosApi
import edu.ucne.loginapi.data.remote.api.ChatApiService
import edu.ucne.loginapi.data.remote.api.ManualApiService
import edu.ucne.loginapi.data.remote.api.MaintenanceApiService
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://TU_BASE_URL_AQUI/"

    @Provides
    @Singleton
    fun provideMoshi(): Moshi =
        Moshi.Builder().build()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder().build()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    fun provideUsuariosApi(retrofit: Retrofit): UsuariosApi =
        retrofit.create(UsuariosApi::class.java)

    @Provides
    @Singleton
    fun provideChatApiService(retrofit: Retrofit): ChatApiService =
        retrofit.create(ChatApiService::class.java)

    @Provides
    @Singleton
    fun provideManualApiService(retrofit: Retrofit): ManualApiService =
        retrofit.create(ManualApiService::class.java)

    @Provides
    @Singleton
    fun provideMaintenanceApiService(retrofit: Retrofit): MaintenanceApiService =
        retrofit.create(MaintenanceApiService::class.java)
}