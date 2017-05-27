package com.coen268.project.myrss;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;

public class Lattest extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private ArticleAdapter adapter;
    private ArrayList<Article> articles;
    private Context context;
    final String TAG = "LattestFragment";
    //MainActivity.articleAsyncTask;
    public Lattest() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_lattest, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.allArticles_list);
        emptyView = (TextView)view.findViewById(R.id.empty_view);
        adapter = new ArticleAdapter();
        articles = new ArrayList<>();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        URL articleSearchUrl = NetworkUtils.buildUrl();
//        articleAsyncTask= (MainActivity.ArticleAsyncTask) new MainActivity.ArticleAsyncTask().execute(articleSearchUrl);
    }
}
