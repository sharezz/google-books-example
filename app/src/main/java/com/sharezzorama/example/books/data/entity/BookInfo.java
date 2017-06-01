package com.sharezzorama.example.books.data.entity;


import java.util.List;

public class BookInfo {
    private String title;
    private String description;
    private List<String> authors;
    private String previewLink;
    private BookImageLinks imageLinks;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getPreviewLink() {
        return previewLink;
    }

    public void setPreviewLink(String previewLink) {
        this.previewLink = previewLink;
    }

    public BookImageLinks getImageLinks() {
        return imageLinks;
    }

    public void setImageLinks(BookImageLinks imageLinks) {
        this.imageLinks = imageLinks;
    }
}
