package com.sharezzorama.example.books;


import android.app.Application;

import com.sharezzorama.example.books.data.book.BookRemoteRepository;
import com.sharezzorama.example.books.data.favorite.FavoriteLocalRepository;
import com.sharezzorama.example.books.favorite.FavoritesContract;
import com.sharezzorama.example.books.favorite.FavoritesPresenter;
import com.sharezzorama.example.books.search.SearchContract;
import com.sharezzorama.example.books.search.SearchPresenter;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainApplication extends Application {

    private Retrofit mRetrofit;
    private static MainApplication sAppInstance;
    private SearchContract.Presenter mSearchPresenter;
    private FavoritesContract.Presenter mFavoritesPresenter;

    public static MainApplication getInstance() {
        return sAppInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sAppInstance = this;
        mRetrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.google_books_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(getClient())
                .build();
    }

    public SearchContract.Presenter getSearchPresenter() {
        if (mSearchPresenter == null) {
            mSearchPresenter = new SearchPresenter(new BookRemoteRepository(mRetrofit),
                    new FavoriteLocalRepository(this));
        }
        return mSearchPresenter;
    }

    public FavoritesContract.Presenter getFavoritesPresenter() {
        if (mFavoritesPresenter == null) {
            mFavoritesPresenter = new FavoritesPresenter(new FavoriteLocalRepository(this));
        }
        return mFavoritesPresenter;
    }

    private OkHttpClient getClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        HttpUrl originalHttpUrl = original.url();
                        HttpUrl url = originalHttpUrl.newBuilder()
                                .addQueryParameter("key", getString(R.string.api_key))
                                .build();
                        Request request = original.newBuilder()
                                .url(url)
                                .build();
                        return chain.proceed(request);
                    }
                })
                .build();
    }
}
