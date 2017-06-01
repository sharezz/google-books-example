package com.sharezzorama.example.books.data.book;


import com.sharezzorama.example.books.data.entity.BookSearchResult;
import com.sharezzorama.example.books.data.favorite.FavoriteDataSource;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class BookRemoteRepository implements BookDataSource {

    private final RetrofitApiHelper mRetrofitApiHelper;

    public BookRemoteRepository(Retrofit retrofit) {
        mRetrofitApiHelper = retrofit.create(RetrofitApiHelper.class);
    }

    @Override
    public Observable<BookSearchResult> searchBooks(String string) {
        return mRetrofitApiHelper.searchBooks(string);
    }

    interface RetrofitApiHelper {
        @GET("v1/volumes")
        Observable<BookSearchResult> searchBooks(@Query("q") String query);
    }
}
