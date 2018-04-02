package moviester.ornoyman.com.moviester.di

import com.squareup.moshi.Moshi
import io.reactivex.schedulers.Schedulers
import moviester.ornoyman.com.moviester.BuildConfig
import moviester.ornoyman.com.moviester.data.MoviesRepository
import moviester.ornoyman.com.moviester.data.MoviesRestService
import moviester.ornoyman.com.moviester.data.json.ApplicationJsonAdapterFactory
import moviester.ornoyman.com.moviester.domain.api.MoviesProvider
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber

// TODO: dummy di for simplicity, possibly convert to real di framework such as dagger or koin

val moviesProvider by lazy<MoviesProvider> {
    MoviesRepository(moviesRestService)
}

val moviesRestService by lazy<MoviesRestService> {
    retrofit.create(MoviesRestService::class.java)
}

val retrofit by lazy<Retrofit> {
    Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .client(httpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .validateEagerly(BuildConfig.DEBUG)
            .build()
}

val moshi by lazy<Moshi> {
    Moshi.Builder()
            .add(ApplicationJsonAdapterFactory.INSTANCE)
            .build()
}

val httpClient by lazy<OkHttpClient> {
    OkHttpClient.Builder()
            .addInterceptor(httploggingInterceptor)
            .addInterceptor(httpAuthInterceptor)
            .build()
}

val httploggingInterceptor by lazy<HttpLoggingInterceptor> {
    HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { msg ->
        Timber.tag("http-logging").v(msg)
    }).setLevel(HttpLoggingInterceptor.Level.BODY)
}

val httpAuthInterceptor by lazy {
    Interceptor { chain ->
        val original = chain.request()
        val originalHttpUrl = original.url()

        val url = originalHttpUrl.newBuilder()
                .addQueryParameter("api_key", "6d9cc0d896240fa7eef218d8b24a7379")
                .build()
        val request = original.newBuilder()
                .url(url).build()

        chain.proceed(request)
    }
}