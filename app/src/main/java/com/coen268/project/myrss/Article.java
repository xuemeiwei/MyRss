package com.coen268.project.myrss;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


public class Article implements Parcelable {
    @SerializedName("author")
    private String author;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("url")
    private String url;

    @SerializedName("imageLinks")
    private String imageLinks;

    @SerializedName("publishedTime")
    private String publishedTime;

    private float personalRate;
    private boolean is_read;
    private boolean is_favorite;

    public Article(String author, String title, String description, String url,
                   String imageLinks, String publishedTime){
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.imageLinks = imageLinks;
        this.publishedTime = publishedTime;
    }
    public Article(){

    }
    public boolean is_favorite() {
        return is_favorite;
    }

    public boolean is_read() {

        return is_read;
    }

    public String getPublishedTime() {

        return publishedTime;
    }

    public String getImageLinks() {

        return imageLinks;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() { return title; }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        if (description == null){
            description = "";

        }
        return description;
    }

    public boolean getIsRead() {return is_read; }
    public boolean getIsFavorite() {return is_favorite; }

    @Override
    public int describeContents() { return 0; }

    public void setIs_favorite(boolean is_favorite) {
        this.is_favorite = is_favorite;
    }

    public void setIs_read(boolean is_read) {

        this.is_read = is_read;
    }

    public void setPersonalRate(float personalRate) {

        this.personalRate = personalRate;
    }

    public void setPublishedTime(String publishedTime) {

        this.publishedTime = publishedTime;
    }

    public void setImageLinks(String imageLinks) {

        this.imageLinks = imageLinks;
    }

    public void setUrl(String url) {

        this.url = url;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public void setAuthor(String author) {

        this.author = author;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(author);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(url);
        dest.writeString(imageLinks);
        dest.writeString(publishedTime);
        dest.writeByte((byte) (is_read ? 1 : 0));
        dest.writeByte((byte) (is_favorite ? 1 : 0));
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        public Article createFromParcel(Parcel pc) {
            return new Article(pc);
        }
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    public Article(Parcel pc){

        author = pc.readString();
        title = pc.readString();
        description = pc.readString();
        url = pc.readString();
        imageLinks = pc.readString();
        publishedTime = pc.readString();
        is_read = pc.readByte() != 0;
        is_favorite = pc.readByte() != 0;

    }
}
