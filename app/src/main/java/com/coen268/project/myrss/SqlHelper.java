package com.coen268.project.myrss;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SqlHelper extends SQLiteOpenHelper {

    private static final String TAG = "SqlHelper";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MyArticleDB";
    private static final String TABLE_ARTICLE = "article";
    private static final String TABLE_LIBRARY = "library";
    private static final String TABLE_USER = "user_app";
    private static final String KEY_AUTHOR= "author";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_URL = "url";
    private static final String KEY_IMAGELINKS = "imageLinks";
    private static final String KEY_PUBLISHED_TIME = "publishedTime";
    private static final String KEY_ARTICLE_TITLE = "tilte_article";
    private static final String KEY_ID_USER = "id_user";
    private static final String KEY_IS_READ = "is_read";
    private static final String KEY_IS_FAVORITE = "is_favorite";

    private static final String KEY_TOKEN_USER = "token";
    private static final String KEY_NAME_USER = "name";
    private static final String KEY_MAIL_USER = "mail";

    public SqlHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_ARTICLE_TABLE = "CREATE TABLE " + TABLE_ARTICLE  + " ( " +
                KEY_TITLE + " TEXT PRIMARY KEY, "+
                KEY_AUTHOR + " TEXT, " +
                KEY_DESCRIPTION + " TEXT, " +
                KEY_URL + " TEXT, "+
                KEY_IMAGELINKS + " TEXT, " +
                KEY_PUBLISHED_TIME + " TEXT)";

        String CREATE_LIBRARY_TABLE = "CREATE TABLE " + TABLE_LIBRARY + " ( " +
                KEY_ARTICLE_TITLE + " TEXT PRIMARY KEY, " +
                KEY_ID_USER + " TEXT, " +
                KEY_IS_READ + " INTEGER, "+
                KEY_IS_FAVORITE + " INTEGER, " +
                "FOREIGN KEY( " + KEY_ARTICLE_TITLE + " ) REFERENCES " + TABLE_ARTICLE + " ( " + KEY_TITLE + "), "+
                "FOREIGN KEY( " + KEY_ID_USER + " ) REFERENCES " + TABLE_USER + " ( " + KEY_TOKEN_USER + "))";

        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + " ( " +
                "token TEXT PRIMARY KEY, " +
                "name TEXT, "+
                "mail TEXT)";

        db.execSQL(CREATE_ARTICLE_TABLE);
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_LIBRARY_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICLE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIBRARY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        this.onCreate(db);
    }

    public void addUser(GoogleSignInAccount userAccount){
        Log.i(TAG,"add user");

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME_USER, userAccount.getDisplayName());
        values.put(KEY_MAIL_USER, userAccount.getEmail());
        values.put(KEY_TOKEN_USER, userAccount.getId());

        db.insert(TABLE_USER,null,values);

        Log.i("TAG", "new user added");
        db.close();
    }

    public boolean isUserInDatabase(String userID){

        Log.i(TAG, "Check if user already in database");
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM user_app WHERE token=?", new String[] {userID} );
        if (cursor.moveToFirst()) {
            Log.i("TAG", "User already in Database");
            db.close();
            return true;
        }else {
            Log.i("TAG", "User not in Database");
            db.close();
            return false;
        }

    }

    public void addBook(Article article,String userID){
        Log.i(TAG,"add article: " + article.getTitle());

        SQLiteDatabase db = this.getWritableDatabase();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date_added = new Date();

        ContentValues values = new ContentValues();
        values.put(KEY_AUTHOR, article.getAuthor());
        values.put(KEY_TITLE, article.getTitle());
        values.put(KEY_DESCRIPTION, article.getDescription());
        values.put(KEY_URL, article.getUrl());
        values.put(KEY_IMAGELINKS, article.getImageLinks());
        values.put(KEY_PUBLISHED_TIME, article.getPublishedTime());

        ContentValues values_library = new ContentValues();
        values_library.put(KEY_ARTICLE_TITLE,article.getTitle());
        values_library.put(KEY_ID_USER,userID);
        values_library.put(KEY_IS_READ,0);
        values_library.put(KEY_IS_FAVORITE, 0);

        if(!articleAlreadyInDatabase(article)){
            db.insert(TABLE_ARTICLE, null, values);
        }

        db.insert(TABLE_LIBRARY,null,values_library);

        Log.d("Book added: ",article.getTitle());
        db.close();
    }


    public ArrayList<Article> getReadArticles(String userID){

        Log.i(TAG, "getReadBooks");
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<Article> articles = new ArrayList<>();

        String query = "SELECT " + TABLE_ARTICLE + "." + KEY_AUTHOR + ", " + TABLE_ARTICLE + "." + KEY_TITLE + ", " +
                TABLE_ARTICLE + "." + KEY_DESCRIPTION + ", " + TABLE_ARTICLE + "." + KEY_URL + "," +
                TABLE_ARTICLE + "." + KEY_IMAGELINKS + "," + TABLE_ARTICLE + "." + KEY_PUBLISHED_TIME + " FROM " +
                TABLE_ARTICLE + " WHERE " + TABLE_LIBRARY + "." + KEY_ID_USER + "=? AND " + TABLE_LIBRARY + "." +
                KEY_ARTICLE_TITLE + "=" + TABLE_ARTICLE + "." + KEY_TITLE + " AND " + TABLE_LIBRARY + "." + KEY_IS_READ +
                "=1";

        Cursor cursor = db.rawQuery(query, new String[] {userID} );

        Article article = null;
        if (cursor.moveToFirst()){
            do{
                article = new Article();
                article.setAuthor(cursor.getString(0));
                article.setTitle(cursor.getString(1));
                article.setDescription(cursor.getString(2));
                article.setUrl(cursor.getString(3));
                article.setImageLinks(cursor.getString(4));
                article.setPublishedTime(cursor.getString(5));

                articles.add(article);
            }while(cursor.moveToNext());
        }
        db.close();
        return articles;

    }

    public ArrayList<Article> getFavoriteBooks(String userID){

        Log.i(TAG, "getFavoriteBooks");
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<Article> articles = new ArrayList<>();

        String query = "SELECT " + TABLE_ARTICLE + "." + KEY_AUTHOR + ", " + TABLE_ARTICLE + "." + KEY_TITLE + ", " +
                TABLE_ARTICLE + "." + KEY_DESCRIPTION + ", " + TABLE_ARTICLE + "." + KEY_URL + "," +
                TABLE_ARTICLE + "." + KEY_IMAGELINKS + "," + TABLE_ARTICLE + "." + KEY_PUBLISHED_TIME + " FROM " +
                TABLE_ARTICLE + " WHERE " + TABLE_LIBRARY + "." + KEY_ID_USER + "=? AND " + TABLE_LIBRARY + "." +
                KEY_ARTICLE_TITLE + "=" + TABLE_ARTICLE + "." + KEY_TITLE + " AND " + TABLE_LIBRARY + "." + KEY_IS_FAVORITE +
                "=1";

        Cursor cursor = db.rawQuery(query, new String[] {userID} );

        Article article = null;
        if (cursor.moveToFirst()){
            do{
                article = new Article();
                article.setAuthor(cursor.getString(0));
                article.setTitle(cursor.getString(1));
                article.setDescription(cursor.getString(2));
                article.setUrl(cursor.getString(3));
                article.setImageLinks(cursor.getString(4));
                article.setPublishedTime(cursor.getString(5));

                articles.add(article);
            }while(cursor.moveToNext());
        }
        db.close();
        return articles;

    }

    public void deleteArticle(Article article, String userID){

        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_LIBRARY,
                KEY_ARTICLE_TITLE + "=? AND " + KEY_ID_USER + "=?",
                new String[] {article.getTitle(),userID});
        db.delete(TABLE_ARTICLE,
                KEY_TITLE + "=?",
                new String[] {article.getTitle()});
        Log.i(TAG, "Delete successful");
        db.close();
    }

//    public void updateArticle(Article article, String userID){
//
//        SQLiteDatabase db = getWritableDatabase();
//
//        String query = "UPDATE library set personal_rate=" + article.getPersonalRate() + ", comment=?" +
//                " WHERE isbn_book=?" + " AND id_user=?";
//
//        db.execSQL(query,new String[] {article.getComment(),article.getISBN(),userID});
//        Log.i(TAG, "Book updated");
//
//    }

    public void updateIsReadColumn(Article article,String userID){

        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE library SET is_read=" + (article.getIsRead() ? 1 : 0) + " WHERE title=?"
                + " AND id_user=?";
        db.execSQL(query,new String[]{ article.getTitle(), userID});
        Log.d("UpdateReadColumn: ", "column updated with: " + (article.getIsRead() ? 1 : 0) );
        db.close();
    }

    public void updateIsFavoriteColumn(Article article,String userID){

        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE library SET is_favorite=" + (article.getIsFavorite() ? 1 : 0) + " WHERE title=?"
                + " AND id_user=?";
        db.execSQL(query,new String[]{ article.getTitle(), userID});
        Log.d("UpdateFavoriteColumn: ", "column updated with: " + (article.getIsFavorite() ? 1 : 0) );
        db.close();
    }

    public boolean bookAlreadyInLibrary(Article article, String userID){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM library where title=? AND id_user=?";
        Cursor cursor = db.rawQuery(query,new String[] {article.getTitle(),userID});
        if (cursor.moveToFirst()){
            db.close();
            return true;
        }else{
            db.close();
            return false;
        }

    }



    private boolean articleAlreadyInDatabase(Article article){
        SQLiteDatabase db = getWritableDatabase();
        String query="SELECT * from "+ TABLE_ARTICLE + " WHERE title = ?";
        Cursor cursor = db.rawQuery(query, new String[] {article.getTitle()});

        if(cursor.moveToFirst()){
            db.close();
            return true;
        }else{
            db.close();
            return false;
        }
    }

//    public ArrayList<article> findArticleInLibrary(String text, String userID){
//        SQLiteDatabase db = getWritableDatabase();
//        ArrayList<article> books = new ArrayList<>();
//        SimpleDateFormat dateAddedFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        SimpleDateFormat getDateFormat = new SimpleDateFormat("MM-dd-yyyy");
//
//        String query = "SELECT book.isbn,book.title,book.author,book.official_rate," +
//                "book.year,book.cover_url,book.description,book.date_added,book.categories," +
//                "library.is_read,library.is_favorite,library.personal_rate,library.comment" +
//                " FROM book,library WHERE library.id_user=? AND library.isbn_book=book.isbn AND " +
//                "(book.title LIKE ? OR book.author LIKE ? ) ORDER BY book.date_added DESC";
//        Cursor cursor = db.rawQuery(query,new String[]{userID,text + "%",text + "%"});
//
//        Log.d(TAG, "query: " + cursor.toString());
//
//        Book book = null;
//        if (cursor.moveToFirst()){
//            do{
//                book = new Book();
//                book.setIsbns(cursor.getString(0));
//                book.setTitle(cursor.getString(1));
//                book.setAuthors(cursor.getString(2));
//                book.setRate(Float.parseFloat(cursor.getString(3)));
//                book.setYear(cursor.getString(4));
//                book.setUrlCover(cursor.getString(5));
//                book.setDescription(cursor.getString(6));
//
//                try {
//                    book.setDate_added(getDateFormat.format(dateAddedFormat.parse(cursor.getString(7))));
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                book.setCategories(cursor.getString(8));
//                book.setIsRead(Integer.parseInt(cursor.getString(9)) != 0);
//                book.setIsFavorite(Integer.parseInt(cursor.getString(10)) != 0);
//                book.setPersonalRate(Float.parseFloat(cursor.getString(11)));
//                book.setComment(cursor.getString(12));
//
//                books.add(book);
//            }while(cursor.moveToNext());
//        }
//
//        return books;
//
//    }

//    public ArrayList<Article> getAllBooks(String userID){
//
//        Log.i(TAG, "getAllBooks");
//        SQLiteDatabase db = getWritableDatabase();
//        ArrayList<Article> books = new ArrayList<>();
//        SimpleDateFormat dateAddedFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        SimpleDateFormat getDateFormat = new SimpleDateFormat("MM-dd-yyyy");
//
//        String query = "SELECT book.isbn,book.title,book.author,book.official_rate," +
//                "book.year,book.cover_url,book.description,book.date_added,book.categories," +
//                "library.is_read,library.is_favorite,library.personal_rate,library.comment" +
//                " FROM book,library WHERE library.id_user=? " +
//                "AND library.isbn_book=book.isbn ORDER BY book.date_added DESC";
//
//        Cursor cursor = db.rawQuery(query, new String[] {userID} );
//
//        Book book = null;
//        if (cursor.moveToFirst()){
//            do{
//                book = new Book();
//                book.setIsbns(cursor.getString(0));
//                book.setTitle(cursor.getString(1));
//                book.setAuthors(cursor.getString(2));
//                book.setRate(Float.parseFloat(cursor.getString(3)));
//                book.setYear(cursor.getString(4));
//                book.setUrlCover(cursor.getString(5));
//                book.setDescription(cursor.getString(6));
//
//                try {
//                    book.setDate_added(getDateFormat.format(dateAddedFormat.parse(cursor.getString(7))));
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                book.setCategories(cursor.getString(8));
//                book.setIsRead(Integer.parseInt(cursor.getString(9)) != 0);
//                book.setIsFavorite(Integer.parseInt(cursor.getString(10)) != 0);
//                book.setPersonalRate(Float.parseFloat(cursor.getString(11)));
//                book.setComment(cursor.getString(12));
//
//                books.add(book);
//            }while(cursor.moveToNext());
//        }
//
//        return books;
//
//    }
}
