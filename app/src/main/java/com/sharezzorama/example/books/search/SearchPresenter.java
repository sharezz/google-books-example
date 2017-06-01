package com.sharezzorama.example.books.search;


import com.sharezzorama.example.books.data.book.BookDataSource;
import com.sharezzorama.example.books.data.entity.Book;
import com.sharezzorama.example.books.data.entity.BookSearchResult;
import com.sharezzorama.example.books.data.favorite.FavoriteDataSource;
import com.sharezzorama.example.books.mvp.AbstractPresenter;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SearchPresenter extends AbstractPresenter<SearchContract.View> implements
        SearchContract.Presenter {
    private BookDataSource mBookDataSource;
    private FavoriteDataSource mFavoriteDataSource;
    private boolean mProgress;
    private BookSearchResult mSearchResult;

    public SearchPresenter(BookDataSource bookDataSource, FavoriteDataSource favoriteDataSource) {
        mBookDataSource = bookDataSource;
        mFavoriteDataSource = favoriteDataSource;
    }

    @Override
    public void search(String query) {
        mProgress = true;
        getView().showLoading();
        mBookDataSource.searchBooks(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BookSearchResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BookSearchResult searchResult) {
                        checkFavorites(searchResult);
                        mSearchResult = searchResult;
                        mProgress = false;
                        getView().hideLoading();
                        getView().showBooks(searchResult.getBooks());
                    }

                    @Override
                    public void onError(Throwable e) {
                        mProgress = false;
                        getView().hideLoading();
                        getView().showError();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void checkFavorites(BookSearchResult searchResult) {
        for (final Book book : searchResult.getBooks()) {
            mFavoriteDataSource.isFavorite(book.getId())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean favorite) throws Exception {
                            book.setFavorite(favorite);
                        }
                    });
        }
    }

    @Override
    public void changeFavoriteStatus(final int position) {
        final Book book = mSearchResult.getBooks().get(position);
        if (book == null) {
            return;
        }

        if (book.isFavorite()) {
            mFavoriteDataSource.removeFavorite(book.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean deleted) throws Exception {
                            if (deleted) {
                                book.setFavorite(false);
                                getView().updateItem(position);
                            }
                        }
                    });
        } else {
            mFavoriteDataSource.saveFavorite(book)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long id) throws Exception {
                            book.setFavorite(true);
                            getView().updateItem(position);
                        }
                    });
        }
    }

    @Override
    protected Class<SearchContract.View> getViewClass() {
        return SearchContract.View.class;
    }

    @Override
    public void attachView(SearchContract.View view) {
        super.attachView(view);
        if (mProgress) {
            getView().showLoading();
        } else if (mSearchResult != null) {
            checkFavorites(mSearchResult);
            getView().showBooks(mSearchResult.getBooks());
        }
    }
}
