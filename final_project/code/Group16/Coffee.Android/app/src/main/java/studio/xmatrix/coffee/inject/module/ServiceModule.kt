package studio.xmatrix.coffee.inject.module

import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import dagger.Module
import dagger.Provides
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import studio.xmatrix.coffee.App
import studio.xmatrix.coffee.data.common.adapter.LiveDataCallAdapterFactory
import studio.xmatrix.coffee.data.common.converter.BitmapConverterFactory
import studio.xmatrix.coffee.data.service.*
import javax.inject.Singleton

@Module
class ServiceModule {

    @Singleton
    @Provides
    fun provideCookieJar(app: App): CookieJar {
        return PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(app))
    }

    @Singleton
    @Provides
    fun provideCommentService(app: App, cookieJar: CookieJar): CommentService {
        val okHttpClient = OkHttpClient.Builder().cookieJar(cookieJar).build()
        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(CommentService.BASE_URL)
            .client(okHttpClient)
            .build()
        return retrofit.create(CommentService::class.java)
    }

    @Singleton
    @Provides
    fun provideContentService(app: App, cookieJar: CookieJar): ContentService {
        val okHttpClient = OkHttpClient.Builder().cookieJar(cookieJar).build()
        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .addConverterFactory(BitmapConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(ContentService.BASE_URL)
            .client(okHttpClient)
            .build()
        return retrofit.create(ContentService::class.java)
    }

    @Singleton
    @Provides
    fun provideImageService(): ImageService {
        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .addConverterFactory(BitmapConverterFactory.create())
            .baseUrl(ImageService.BASE_URL)
            .build()
        return retrofit.create(ImageService::class.java)
    }

    @Singleton
    @Provides
    fun provideLikeService(app: App, cookieJar: CookieJar): LikeService {
        val okHttpClient = OkHttpClient.Builder().cookieJar(cookieJar).build()
        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(LikeService.BASE_URL)
            .client(okHttpClient)
            .build()
        return retrofit.create(LikeService::class.java)
    }

    @Singleton
    @Provides
    fun provideNotificationService(app: App, cookieJar: CookieJar): NotificationService {
        val okHttpClient = OkHttpClient.Builder().cookieJar(cookieJar).build()
        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(NotificationService.BASE_URL)
            .client(okHttpClient)
            .build()
        return retrofit.create(NotificationService::class.java)
    }

    @Singleton
    @Provides
    fun provideUserService(app: App, cookieJar: CookieJar): UserService {
        val okHttpClient = OkHttpClient.Builder().cookieJar(cookieJar).build()
        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(UserService.BASE_URL)
            .client(okHttpClient)
            .build()
        return retrofit.create(UserService::class.java)
    }
}
