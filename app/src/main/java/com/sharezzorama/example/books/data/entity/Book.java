package com.sharezzorama.example.books.data.entity;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Book {
    private String id;
    @SerializedName("volumeInfo")
    @Expose
    private BookInfo bookInfo;

    private boolean favorite;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BookInfo getBookInfo() {
        return bookInfo;
    }

    public void setBookInfo(BookInfo bookInfo) {
        this.bookInfo = bookInfo;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
