package com.sharezzorama.example.books.data.favorite;


import com.sharezzorama.example.books.data.entity.Book;

import java.util.List;

import io.reactivex.Observable;

public interface FavoriteDataSource {

    Observable<Long> saveFavorite(Book book);

    Observable<List<Book>> getAllFavorites();

    Observable<Boolean> removeFavorite(String remoteId);

    Observable<Boolean> isFavorite(String remoteId);
}
