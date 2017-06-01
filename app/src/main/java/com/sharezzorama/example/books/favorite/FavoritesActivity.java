package com.sharezzorama.example.books.favorite;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sharezzorama.example.books.BaseActivity;
import com.sharezzorama.example.books.MainApplication;
import com.sharezzorama.example.books.R;
import com.sharezzorama.example.books.BooksAdapter;
import com.sharezzorama.example.books.data.entity.Book;

import java.util.List;

public class FavoritesActivity extends BaseActivity implements FavoritesContract.View {
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private TextView mEmptyView;
    private BooksAdapter mAdapter;

    private FavoritesContract.Presenter mFavoritesPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mEmptyView = (TextView) findViewById(R.id.emptyView);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new BooksAdapter();
        mAdapter.setRemoveOnly(true);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setListener(new BooksAdapter.OnBookClickListener() {
            @Override
            public void onFavoriteIconClick(int pos) {
                mFavoritesPresenter.removeFavorite(pos);
            }

            @Override
            public void onPreviewClick(String previewLink) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(previewLink));
                startActivity(browserIntent);
            }
        });

        mFavoritesPresenter = MainApplication.getInstance().getFavoritesPresenter();
    }

    @Override
    public void showFavorites(List<Book> books) {
        mAdapter.setBooks(books);
        updateEmptyView();
    }

    @Override
    public void favoriteRemoved(int pos) {
        mAdapter.notifyItemRemoved(pos);
        updateEmptyView();
    }

    private void updateEmptyView() {
        if (mAdapter.getItemCount() == 0) {
            if (mProgressBar.getVisibility() == View.VISIBLE) {
                mEmptyView.setVisibility(View.GONE);
            } else {
                mEmptyView.setVisibility(View.VISIBLE);
            }
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mFavoritesPresenter.attachView(this);
        mFavoritesPresenter.loadAll();
    }

    @Override
    public void onStop() {
        mFavoritesPresenter.detachView(this);
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
