package edu.ucne.loginapi.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.ucne.loginapi.data.remote.CarApiService
import edu.ucne.loginapi.data.remote.MaintenanceApiService
import edu.ucne.loginapi.data.remote.ManualApiService
import edu.ucne.loginapi.data.remote.UsuariosApiService
import edu.ucne.loginapi.data.remote.VehicleCatalogApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://mycarsettingapi.azurewebsites.net/"

    @Provides
    @Singleton
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .build()

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
    fun provideUsuariosApiService(retrofit: Retrofit): UsuariosApiService =
        retrofit.create(UsuariosApiService::class.java)

    @Provides
    @Singleton
    fun provideCarApiService(retrofit: Retrofit): CarApiService =
        retrofit.create(CarApiService::class.java)

    @Provides
    @Singleton
    fun provideMaintenanceApiService(retrofit: Retrofit): MaintenanceApiService =
        retrofit.create(MaintenanceApiService::class.java)

    @Provides
    @Singleton
    fun provideManualApiService(retrofit: Retrofit): ManualApiService =
        retrofit.create(ManualApiService::class.java)

    @Provides
    @Singleton
    fun provideVehicleCatalogApiService(retrofit: Retrofit): VehicleCatalogApiService =
        retrofit.create(VehicleCatalogApiService::class.java)
}