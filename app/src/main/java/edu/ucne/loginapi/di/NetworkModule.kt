package edu.ucne.loginapi.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.ucne.loginapi.data.remote.*
import edu.ucne.loginapi.data.remote.repository.ServicesRepositoryImpl
import edu.ucne.loginapi.domain.repository.ServicesRepository
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

/* ---------------------------------------- */
/* YOUR QUALIFIERS                          */
/* ---------------------------------------- */

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NominatimRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://mycarsettingapi.azurewebsites.net/"
    private const val NOMINATIM_BASE = "https://nominatim.openstreetmap.org/"

    /* ---------------------------------------- */
    /* MOSHI                                    */
    /* ---------------------------------------- */

    @Provides
    @Singleton
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

    /* ---------------------------------------- */
    /* OKHTTP                                   */
    /* ---------------------------------------- */

    @Provides
    @Singleton
    fun provideOkHttp(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BASIC

        val uaInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", "MyCarSetting/1.0 (frannielar09@gmail.com)")
                .build()
            chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .addInterceptor(uaInterceptor)
            .addInterceptor(logging)
            .build()
    }

    /* ---------------------------------------- */
    /* RETROFIT PRINCIPAL                       */
    /* ---------------------------------------- */

    @Provides
    @Singleton
    @MainRetrofit
    fun provideMainRetrofit(
        okHttp: OkHttpClient,
        moshi: Moshi
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttp)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    /* ---------------------------------------- */
    /* RETROFIT NOMINATIM                       */
    /* ---------------------------------------- */

    @Provides
    @Singleton
    @NominatimRetrofit
    fun provideNominatimRetrofit(
        okHttp: OkHttpClient,
        moshi: Moshi
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(NOMINATIM_BASE)
            .client(okHttp)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    /* ---------------------------------------- */
    /* SERVICIOS API PRINCIPALES                */
    /* ---------------------------------------- */

    @Provides
    @Singleton
    fun provideUsuariosApiService(@MainRetrofit retrofit: Retrofit): UsuariosApiService =
        retrofit.create(UsuariosApiService::class.java)

    @Provides
    @Singleton
    fun provideCarApiService(@MainRetrofit retrofit: Retrofit): CarApiService =
        retrofit.create(CarApiService::class.java)

    @Provides
    @Singleton
    fun provideMaintenanceApiService(@MainRetrofit retrofit: Retrofit): MaintenanceApiService =
        retrofit.create(MaintenanceApiService::class.java)

    @Provides
    @Singleton
    fun provideManualApiService(@MainRetrofit retrofit: Retrofit): ManualApiService =
        retrofit.create(ManualApiService::class.java)

    @Provides
    @Singleton
    fun provideVehicleCatalogApiService(@MainRetrofit retrofit: Retrofit): VehicleCatalogApiService =
        retrofit.create(VehicleCatalogApiService::class.java)

    /* ---------------------------------------- */
    /* API NOMINATIM                             */
    /* ---------------------------------------- */

    @Provides
    @Singleton
    fun provideNominatimApi(@NominatimRetrofit retrofit: Retrofit): NominatimApiService =
        retrofit.create(NominatimApiService::class.java)

    /* ---------------------------------------- */
    /* REPOSITORY                                */
    /* ---------------------------------------- */

    @Provides
    @Singleton
    fun provideServicesRepository(api: NominatimApiService): ServicesRepository =
        ServicesRepositoryImpl(api)
}
