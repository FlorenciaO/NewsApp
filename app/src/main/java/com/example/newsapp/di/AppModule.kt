package com.example.newsapp.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.newsapp.R
import com.example.newsapp.data.local.Converters
import com.example.newsapp.data.local.LocalDataBase
import com.example.newsapp.data.remote.RetrofitService
import com.example.newsapp.data.remote.RetrofitService.Companion.BASE_URL
import com.example.newsapp.data.repository.NewsRepositoryImpl
import com.example.newsapp.data.repository.UserRepositoryImpl
import com.example.newsapp.domain.repository.NewsRepository
import com.example.newsapp.domain.repository.UserRepository
import com.example.newsapp.domain.use_cases.GetAllNews
import com.example.newsapp.utils.GsonParser
import com.google.gson.Gson
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofitService(): RetrofitService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitService::class.java)
    }

    @Provides
    @Singleton
    internal fun provideRoomDatabase(
        app: Application,
        @ApplicationContext context: Context
    ): LocalDataBase =
        Room.databaseBuilder(
            app,
            LocalDataBase::class.java,
            context.getString(R.string.database_name)
        )
            .addTypeConverter(Converters(GsonParser(Gson())))
            .build()

    @Provides
    @Singleton
    fun providePicasso(
        @ApplicationContext context: Context,
        downloader: OkHttp3Downloader
    ): Picasso {
        return Picasso.Builder(context).downloader(downloader).build()
    }

    @Provides
    @Singleton
    fun provideOkHttp3Downloader(okHttpClient: OkHttpClient): OkHttp3Downloader {
        return OkHttp3Downloader(okHttpClient)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient().newBuilder().build()
    }

    @Provides
    @Singleton
    fun provideNewsRepository(
        db: LocalDataBase,
        retrofitService: RetrofitService
    ): NewsRepository {
        return NewsRepositoryImpl(db.newsDao, retrofitService)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        db: LocalDataBase,
        retrofitService: RetrofitService
    ): UserRepository {
        return UserRepositoryImpl(db.userDao, retrofitService)
    }

}
