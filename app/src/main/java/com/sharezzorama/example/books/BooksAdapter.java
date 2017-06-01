package com.sharezzorama.example.books;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sharezzorama.example.books.data.entity.Book;
import com.sharezzorama.example.books.data.entity.BookInfo;

import java.util.List;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BookViewHolder> {
    public static final CharSequence AUTHORS_DELIMITER = ", ";
    private List<Book> mBooks;
    private OnBookClickListener mListener;
    private boolean mRemoveOnly;

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BookViewHolder holder, final int position) {
        Context context = holder.itemView.getContext();
        final Book book = mBooks.get(position);
        final BookInfo bookInfo = book.getBookInfo();
        holder.mTitle.setText(bookInfo.getTitle());
        holder.mDescription.setText(!TextUtils.isEmpty(bookInfo.getDescription())
                ? bookInfo.getDescription()
                : context.getString(R.string.no_description));
        // favorite icon
        int favoriteIconResId;
        if (book.isFavorite()) {
            favoriteIconResId = !mRemoveOnly ? R.drawable.ic_favorite_24px : R.drawable.ic_delete;
        } else {
            favoriteIconResId = R.drawable.ic_favorite_border_24px;
        }
        holder.mFavorite.setImageResource(favoriteIconResId);
        holder.mFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onFavoriteIconClick(holder.getAdapterPosition());
                }
            }
        });

        // authors
        if (bookInfo.getAuthors() != null && !bookInfo.getAuthors().isEmpty()) {
            holder.mAuthor.setVisibility(View.VISIBLE);
            holder.mAuthor.setText(TextUtils.join(AUTHORS_DELIMITER, bookInfo.getAuthors()));
        } else {
            holder.mAuthor.setVisibility(View.GONE);
        }

        // thumbnail
        loadThumbnail(holder.mThumbnail, bookInfo);

        // preview
        holder.mPreviewLink.setVisibility(!TextUtils.isEmpty(bookInfo.getPreviewLink()) ? View.VISIBLE : View.GONE);
        holder.mPreviewLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onPreviewClick(bookInfo.getPreviewLink());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBooks != null ? mBooks.size() : 0;
    }

    public void setBooks(List<Book> books) {
        mBooks = books;
        notifyDataSetChanged();
    }

    public void setListener(OnBookClickListener listener) {
        mListener = listener;
    }

    public void setRemoveOnly(boolean removeOnly) {
        mRemoveOnly = removeOnly;
    }

    private void loadThumbnail(ImageView imageView, BookInfo bookInfo) {
        String url = bookInfo.getImageLinks() != null ? bookInfo.getImageLinks().getSmallThumbnail() : null;
        imageView.setVisibility(View.VISIBLE);
        Glide
                .with(imageView.getContext())
                .load(url)
                .crossFade()
                .error(R.drawable.ic_image_error)
                .into(imageView);
    }

    class BookViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private TextView mAuthor;
        private TextView mDescription;
        private ImageView mThumbnail;
        private ImageView mFavorite;
        private TextView mPreviewLink;

        public BookViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mAuthor = (TextView) itemView.findViewById(R.id.author);
            mDescription = (TextView) itemView.findViewById(R.id.description);
            mThumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            mFavorite = (ImageView) itemView.findViewById(R.id.favButton);
            mPreviewLink = (TextView) itemView.findViewById(R.id.previewLink);
        }
    }

    public interface OnBookClickListener {
        void onFavoriteIconClick(int pos);

        void onPreviewClick(String previewLink);
    }
}
