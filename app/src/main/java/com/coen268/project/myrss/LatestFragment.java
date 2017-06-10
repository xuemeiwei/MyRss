package com.coen268.project.myrss;


import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

public class LatestFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private ArticleAdapter articleAdapter;
    private Context context;
    final String TAG = "LatestFragment";
    private ArticleAsyncTask articleAsyncTask;
    private Article article;
    private SqlHelper database;
    private GoogleApiClient mGoogleApiClient;
    private  GoogleSignInAccount googleSignInAccount;
    final static String BASE_URL =
            "https://newsapi.org/v1/articles?source=techcrunch&sortBy=latest&apiKey=c5282269b2d44bcf943b953bc7b814a1";

    public LatestFragment() {
        // Required empty public constructor

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i("LatestFragment: ","onAttach");
        googleSignInAccount=((MainActivity)context).getGoogleAccountInfoFromMain();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_latest, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.allArticles_list);
        context = getActivity();
        emptyView = (TextView)view.findViewById(R.id.empty_view);
        articleAdapter = new ArticleAdapter();
        article = new Article();
        database = new SqlHelper(context);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        articleAsyncTask= (ArticleAsyncTask) new ArticleAsyncTask().execute(BASE_URL);
    }


    public class ArticleAsyncTask extends AsyncTask<String, Void, ArrayList<Article>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Article> doInBackground(String... urls) {
            if (urls == null || urls.length < 1) {
                return null;
            }
            ArrayList<Article> result = QueryUtils.fetchArticleData(urls[0]);
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<Article> articles) {
            if (articles.size() == 0 || articles == null) {
                return;
            }
            updateUi(articles);
        }

        private void updateUi(final ArrayList<Article> articles) {
            articleAdapter.setData(articles);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    layoutManager.getOrientation());
            recyclerView.addItemDecoration(dividerItemDecoration);
            recyclerView.setAdapter(articleAdapter);
            articleAdapter.setOnItemClickListener(new ArticleAdapter.onItemClickListener(){
                @Override
                public void onItemClick(View v, int position) {
                    Toast.makeText(v.getContext(), "item view clicked", Toast.LENGTH_SHORT).show();
                    article = articleAdapter.getItem(position);
                    Uri uri = Uri.parse(article.getUrl());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
            articleAdapter.setOnItemLongClickListener(new ArticleAdapter.onItemLongClickListener(){
                @Override
                public void onItemLongClick(View v, int position) {
                    Toast.makeText(v.getContext(), "item view long clicked", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    article = articleAdapter.getItem(position);
                    setArticleDialog(builder);
                }
            });
        }

    }
    public void setArticleDialog(AlertDialog.Builder builder) {
        builder.setMessage(R.string.set_article);
        builder.setPositiveButton(R.string.save_to_ToRead, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d("save_to_ToRead", "clicked");
                article.setToRead(true);
                Log.d("google.getId(): ", googleSignInAccount.getId());
                if(!database.isArticleInLibrary(article, googleSignInAccount.getId())) {
                    database.addArticle(article, googleSignInAccount.getId());
                }else{
                    database.updateIsToReadColumn(article, googleSignInAccount.getId());
                }
            }
        });
        builder.setNeutralButton(R.string.save_to_Fav, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                article.setFavorite(true);
                Log.d("save_to_Fav", "clicked");
                if(!database.isArticleInLibrary(article, googleSignInAccount.getId())) {
                    database.addArticle(article, googleSignInAccount.getId());
                }else{
                    database.updateIsFavoriteColumn(article, googleSignInAccount.getId());
                }

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}