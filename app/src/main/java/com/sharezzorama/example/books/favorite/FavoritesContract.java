package com.sharezzorama.example.books.favorite;


import com.sharezzorama.example.books.data.entity.Book;
import com.sharezzorama.example.books.mvp.BasePresenter;

import java.util.List;

public class FavoritesContract {

    public interface View {

        void showFavorites(List<Book> books);

        void favoriteRemoved(int pos);
    }

    public interface Presenter extends BasePresenter<View> {

        void loadAll();

        void removeFavorite(int pos);

    }
}
