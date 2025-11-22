package edu.ucne.loginapi.di

import android.content.Context
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edu.ucne.loginapi.data.ChatMessageDao
import edu.ucne.loginapi.data.ChatRepositoryImpl
import edu.ucne.loginapi.data.MaintenanceHistoryDao
import edu.ucne.loginapi.data.MaintenanceHistoryRepositoryImpl
import edu.ucne.loginapi.data.MaintenanceTaskDao
import edu.ucne.loginapi.data.MaintenanceTaskRepositoryImpl
import edu.ucne.loginapi.data.MyCarSettingDatabase
import edu.ucne.loginapi.data.UserCarDao
import edu.ucne.loginapi.data.remote.UsuariosApi
import edu.ucne.loginapi.data.remote.repository.UsuariosRepositoryImpl
import edu.ucne.loginapi.domain.repository.ChatRepository
import edu.ucne.loginapi.domain.repository.MaintenanceHistoryRepository
import edu.ucne.loginapi.domain.repository.MaintenanceTaskRepository
import edu.ucne.loginapi.domain.repository.UserCarRepository
import edu.ucne.loginapi.domain.repository.UsuariosRepository
import edu.ucne.loginapi.repository.data.UserCarRepositoryImpl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val BASE_URL = "https://gestionhuacalesapi.azurewebsites.net/"

    @Provides
    @Singleton
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideApi(moshi: Moshi, okHttpClient: OkHttpClient): UsuariosApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
            .create(UsuariosApi::class.java)
    }

    @InstallIn(SingletonComponent::class)
    @Module
    abstract class RepositoryModule{
        @Binds
        @Singleton
        abstract fun bindUsuariosRepository(
            usuariosRepositoryImpl: UsuariosRepositoryImpl
        ): UsuariosRepository
    }
    @Provides
    @Singleton
    fun provideMyCarSettingDatabase(
        @ApplicationContext context: Context
    ): MyCarSettingDatabase {
        return Room.databaseBuilder(
            context,
            MyCarSettingDatabase::class.java,
            "my_car_setting_db"
        ).build()
    }

    @Provides
    fun provideUserCarDao(
        db: MyCarSettingDatabase
    ): UserCarDao = db.userCarDao

    @Provides
    fun provideMaintenanceTaskDao(
        db: MyCarSettingDatabase
    ): MaintenanceTaskDao = db.maintenanceTaskDao

    @Provides
    fun provideMaintenanceHistoryDao(
        db: MyCarSettingDatabase
    ): MaintenanceHistoryDao = db.maintenanceHistoryDao

    @Provides
    fun provideChatMessageDao(
        db: MyCarSettingDatabase
    ): ChatMessageDao = db.chatMessageDao

    @Provides
    @Singleton
    fun provideUserCarRepository(
        dao: UserCarDao
    ): UserCarRepository = UserCarRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideMaintenanceTaskRepository(
        dao: MaintenanceTaskDao
    ): MaintenanceTaskRepository = MaintenanceTaskRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideMaintenanceHistoryRepository(
        dao: MaintenanceHistoryDao
    ): MaintenanceHistoryRepository = MaintenanceHistoryRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideChatRepository(
        dao: ChatMessageDao
    ): ChatRepository = ChatRepositoryImpl(dao)
}