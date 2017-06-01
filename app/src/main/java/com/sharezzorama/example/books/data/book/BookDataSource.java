package com.sharezzorama.example.books.data.book;


import com.sharezzorama.example.books.data.entity.Book;
import com.sharezzorama.example.books.data.entity.BookSearchResult;

import io.reactivex.Observable;

public interface BookDataSource {

    Observable<BookSearchResult> searchBooks(String string);

}
