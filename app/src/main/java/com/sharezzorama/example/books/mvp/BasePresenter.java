package com.sharezzorama.example.books.mvp;

/**
 * Created by sharezzorama on 11/22/16.
 */

public interface BasePresenter<V> {

    void attachView(V view);

    void detachView(V view);
}
