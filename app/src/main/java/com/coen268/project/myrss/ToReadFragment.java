package com.coen268.project.myrss;


import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ToReadFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private ArticleAdapter articleAdapter;
    private Context context;
    final String TAG = "ToReadFragment";
    private Article article;
    private SqlHelper database;
    private GoogleSignInAccount googleSignInAccount;
    private ArrayList<Article> toReadArticles;
    public ToReadFragment() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i("ToReadFragment: ","onAttach");
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

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        toReadArticles = database.getToReadArticles(googleSignInAccount.getId());
        updateUi(toReadArticles);
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
                article = articleAdapter.getItem(position);
                Uri uri = Uri.parse(article.getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivityForResult(intent, 1);
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
    public void setArticleDialog(AlertDialog.Builder builder) {
        builder.setMessage(R.string.deleteArticle);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d("save_to_ToRead", "clicked");
                article.setToRead(false);
                Log.d("google.getId(): ", googleSignInAccount.getId());
                database.updateIsToReadColumn(article, googleSignInAccount.getId());
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
