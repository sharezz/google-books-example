package com.sharezzorama.example.books.favorite;


import com.sharezzorama.example.books.data.entity.Book;
import com.sharezzorama.example.books.data.favorite.FavoriteDataSource;
import com.sharezzorama.example.books.mvp.AbstractPresenter;
import com.sharezzorama.example.books.mvp.BasePresenter;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FavoritesPresenter extends AbstractPresenter<FavoritesContract.View> implements
        FavoritesContract.Presenter {
    private List<Book> mBooks;

    private FavoriteDataSource mFavoriteDataSource;

    public FavoritesPresenter(FavoriteDataSource favoriteDataSource) {
        mFavoriteDataSource = favoriteDataSource;
    }

    @Override
    public void loadAll() {
        mFavoriteDataSource.getAllFavorites()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Book>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Book> favorites) {
                        mBooks = favorites;
                        getView().showFavorites(favorites);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void removeFavorite(final int pos) {
        final Book book = mBooks.get(pos);
        if (book == null) {
            return;
        }

        mFavoriteDataSource.removeFavorite(book.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean deleted) throws Exception {
                        if (deleted) {
                            mBooks.remove(book);
                            getView().favoriteRemoved(pos);
                        }
                    }
                });
    }

    @Override
    protected Class<FavoritesContract.View> getViewClass() {
        return FavoritesContract.View.class;
    }
}
