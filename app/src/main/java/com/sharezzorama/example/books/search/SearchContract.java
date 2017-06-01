package com.sharezzorama.example.books.search;


import com.sharezzorama.example.books.data.entity.Book;
import com.sharezzorama.example.books.mvp.BasePresenter;

import java.util.List;

public class SearchContract {

    public interface View {

        void showBooks(List<Book> books);

        void hideLoading();

        void showLoading();

        void showError();

        void updateItem(int pos);
    }

    public interface Presenter extends BasePresenter<View> {

        void search(String text);

        void changeFavoriteStatus(int position);

    }
}
