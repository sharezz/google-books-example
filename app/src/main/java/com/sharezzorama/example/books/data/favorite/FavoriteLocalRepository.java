package com.sharezzorama.example.books.data.favorite;


import android.content.Context;

import com.sharezzorama.example.books.data.DBHelper;
import com.sharezzorama.example.books.data.entity.Book;
import com.sharezzorama.example.books.data.entity.BookImageLinks;
import com.sharezzorama.example.books.data.entity.BookInfo;
import com.sharezzorama.example.books.data.tables.FavoriteTableRow;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

public class FavoriteLocalRepository implements FavoriteDataSource {

    private final DBHelper mDbHelper;

    public FavoriteLocalRepository(Context context) {
        mDbHelper = new DBHelper(context);
    }

    @Override
    public Observable<Long> saveFavorite(Book book) {
        FavoriteTableRow favoriteTableRow = new FavoriteTableRow();
        BookInfo bookInfo = book.getBookInfo();
        favoriteTableRow.remoteId = book.getId();
        favoriteTableRow.title = bookInfo.getTitle();
        favoriteTableRow.description = bookInfo.getDescription();

        List<String> authors = bookInfo.getAuthors();
        if (authors != null) {
            favoriteTableRow.authors = new String[authors.size()];
            authors.toArray(favoriteTableRow.authors);
        }
        favoriteTableRow.preview = bookInfo.getPreviewLink();
        if (bookInfo.getImageLinks() != null) {
            favoriteTableRow.thumbnail = bookInfo.getImageLinks().getThumbnail();
            favoriteTableRow.thumbnailSmall = bookInfo.getImageLinks().getSmallThumbnail();
        }
        return Observable.just(favoriteTableRow.insert(mDbHelper.getWritableDatabase()));
    }

    @Override
    public Observable<List<Book>> getAllFavorites() {
        return Observable.fromIterable(FavoriteTableRow.getAll(mDbHelper.getWritableDatabase()))
                .map(new Function<FavoriteTableRow, Book>() {
                    @Override
                    public Book apply(FavoriteTableRow favoriteTableRow) throws Exception {
                        Book book = new Book();
                        book.setId(favoriteTableRow.remoteId);
                        book.setFavorite(true);
                        BookInfo bookInfo = new BookInfo();
                        bookInfo.setTitle(favoriteTableRow.title);
                        bookInfo.setDescription(favoriteTableRow.description);
                        bookInfo.setPreviewLink(favoriteTableRow.preview);
                        if (favoriteTableRow.authors != null) {
                            bookInfo.setAuthors(Arrays.asList(favoriteTableRow.authors));
                        }
                        bookInfo.setImageLinks(new BookImageLinks(favoriteTableRow.thumbnailSmall, favoriteTableRow.thumbnail));
                        book.setBookInfo(bookInfo);
                        return book;
                    }
                }).toList()
                .toObservable();
    }

    @Override
    public Observable<Boolean> removeFavorite(String remoteId) {
        return Observable.just(FavoriteTableRow.delete(mDbHelper.getReadableDatabase(), remoteId));
    }

    @Override
    public Observable<Boolean> isFavorite(String remoteId) {
        return Observable.just(FavoriteTableRow.isFavorite(mDbHelper.getWritableDatabase(), remoteId));
    }
}
