package com.coen268.project.myrss;


import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class LattestFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private ArticleAdapter articleAdapter;
    //private ArrayList<Article> articles;
    private Context context;
    final String TAG = "LattestFragment";
    ArticleAsyncTask articleAsyncTask;

    final static String BASE_URL =
            "https://newsapi.org/v1/articles?source=techcrunch&sortBy=latest&apiKey=c5282269b2d44bcf943b953bc7b814a1";

    public LattestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_lattest, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.allArticles_list);
        emptyView = (TextView)view.findViewById(R.id.empty_view);
        articleAdapter = new ArticleAdapter();
        //articles = new ArrayList<>();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        //URL articleSearchUrl = NetworkUtils.buildUrl();
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
            /*Log.d("ArticleAsyncTask", "doInBackground: !!!");
            URL searchUrl = params[0];
            String articleSearchResults = null;
            try {
                articleSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return articleSearchResults;*/
        }

        @Override
        protected void onPostExecute(ArrayList<Article> articles) {
            if (articles.size() == 0 || articles == null) {
                return;
            }
            updateUi(articles);

            /*Log.d("ArticleAsyncTask", "onPostExecute: !!!");
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

                    Log.d("ArticleAsyncTask", "onPostExecute: articleAdapter !!!");
                    articleAdapter.setData(articles);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                    recyclerView.setLayoutManager(layoutManager);
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                            layoutManager.getOrientation());
                    recyclerView.addItemDecoration(dividerItemDecoration);
                    recyclerView.setAdapter(articleAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }*/
        }

        private void updateUi(final ArrayList<Article> articles) {

            articleAdapter.setData(articles);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    layoutManager.getOrientation());
            recyclerView.addItemDecoration(dividerItemDecoration);
            recyclerView.setAdapter(articleAdapter);

            /*recyclerView.set(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Article article = articles.get(position);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl()));
                    startActivity(intent);
                }
            });*/

        }
    }
}
