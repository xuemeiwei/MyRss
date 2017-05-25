package com.coen268.project.myrss;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    RecyclerView recyclerView;
    ArrayList<Article> articles;
    private SqlHelper database;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate:");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        database = new SqlHelper(MainActivity.this);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        recyclerView = (RecyclerView) findViewById(R.id.allArticles_list);;
        articles = new ArrayList<>();
        URL articleSearchUrl = NetworkUtils.buildUrl();
        ArticleAsyncTask articleAsyncTask= (ArticleAsyncTask) new ArticleAsyncTask().execute(articleSearchUrl);

        ArticleAdapter articleAdapter = new ArticleAdapter();
        articleAdapter.setData(articles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(articleAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.lattest) {
            Toast.makeText(this,"lattest",Toast.LENGTH_LONG).show();

        } else if (id == R.id.toRead) {

        } else if (id == R.id.fav) {

        } else if (id == R.id.sign_out) {
            Intent goLogin = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(goLogin);
            finish();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public class ArticleAsyncTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String articleSearchResults = null;
            try {
                articleSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return articleSearchResults;
        }

        @Override
        protected void onPostExecute(String articleSearchResults) {
            Log.d("ArticleAsyncTask", "onPostExecute:");
            if (articleSearchResults != null && !articleSearchResults.equals("")) {
                try {
                    JSONObject root = new JSONObject(articleSearchResults);
                    JSONArray articlesSearched = root.getJSONArray("articles");
                    for (int i = 0; i < articlesSearched.length(); ++i) {
                        JSONObject jsonArticle = articlesSearched.getJSONObject(i);

                        String title = jsonArticle.getString("title");
                        String author = jsonArticle.getString("author");
                        String description = jsonArticle.getString("description");
                        String url = jsonArticle.getString("url");
                        String image = jsonArticle.getString("urlToImage");
                        String publishedTime = jsonArticle.getString("publishedAt");

                        Article searchedArticle = new Article();
                        searchedArticle.setAuthor(author);
                        searchedArticle.setDescription(description);
                        searchedArticle.setTitle(title);
                        searchedArticle.setImageLinks(image);
                        searchedArticle.setUrl(url);
                        searchedArticle.setPublishedTime(publishedTime);
                        articles.add(searchedArticle);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
