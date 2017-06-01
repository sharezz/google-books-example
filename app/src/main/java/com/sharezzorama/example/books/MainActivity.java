package com.sharezzorama.example.books;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sharezzorama.example.books.data.entity.Book;
import com.sharezzorama.example.books.favorite.FavoritesActivity;
import com.sharezzorama.example.books.favorite.FavoritesContract;
import com.sharezzorama.example.books.search.SearchContract;

import java.util.List;

public class MainActivity extends BaseActivity implements SearchContract.View {
    private static final int ERROR_SNACKBAR_DURATION = 8000;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private TextView mEmptyView;

    private SearchContract.Presenter mSearchPresenter;
    private BooksAdapter mAdapter;
    private SearchView mSearchItem;
    private String mSearchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mEmptyView = (TextView) findViewById(R.id.emptyView);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new BooksAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setListener(new BooksAdapter.OnBookClickListener() {
            @Override
            public void onFavoriteIconClick(int pos) {
                mSearchPresenter.changeFavoriteStatus(pos);
            }

            @Override
            public void onPreviewClick(String previewLink) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(previewLink));
                startActivity(browserIntent);
            }
        });

        mSearchPresenter = MainApplication.getInstance().getSearchPresenter();
    }

    @Override
    public void onStart() {
        super.onStart();
        mSearchPresenter.attachView(this);
    }

    @Override
    public void onStop() {
        mSearchPresenter.detachView(this);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        final MenuItem item = menu.findItem(R.id.action_search_book);
        mSearchItem = (SearchView) MenuItemCompat.getActionView(item);
        mSearchItem.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchQuery = query;
                search(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorites:
                Intent intent = new Intent(this, FavoritesActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mSearchItem.isIconified()) {
            mSearchItem.setIconified(true);
        } else {
            super.onBackPressed();
        }
    }

    private void search(String query) {
        mAdapter.setBooks(null);
        mSearchPresenter.search(query);
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

    // Search view implementation
    @Override
    public void showBooks(List<Book> books) {
        mAdapter.setBooks(books);
        updateEmptyView();
    }

    @Override
    public void hideLoading() {
        mProgressBar.setVisibility(View.GONE);
        updateEmptyView();
    }

    @Override
    public void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
        updateEmptyView();
    }

    @Override
    public void showError() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinator),
                R.string.search_error_message, ERROR_SNACKBAR_DURATION);
        snackbar.setAction(R.string.try_again, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(mSearchQuery);
            }
        });
        snackbar.show();
        updateEmptyView();
    }

    @Override
    public void updateItem(int pos) {
        mAdapter.notifyItemChanged(pos);
    }
}
