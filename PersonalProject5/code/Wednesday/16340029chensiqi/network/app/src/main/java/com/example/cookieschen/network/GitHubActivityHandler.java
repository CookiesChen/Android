package com.example.cookieschen.network;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.example.cookieschen.network.databinding.ActivityGithubBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class GitHubActivityHandler {
    private GitHubActivity activity;
    private ActivityGithubBinding binding;

    GitHubActivityHandler(GitHubActivity activity, ActivityGithubBinding binding) {
        this.activity = activity;
        this.binding = binding;

        initView();
    }

    public interface GitHubRequest {
        @GET("/users/{username}/repos")
        Observable<List<Repo>> getRepo(@Path("username") String username);
    }

    private void initView() {
        List<Repo> repos = new ArrayList<>();

        this.binding.recyclerViewGithub.setAdapter(new RepoAdapter(repos));
        this.binding.recyclerViewGithub.setLayoutManager(new LinearLayoutManager(activity));
        this.binding.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = binding.searchText.getText().toString();
                getRepos(username);
            }
        });
    }

    private void getRepos(final String username) {
        String baseURL = "https://api.github.com";
        OkHttpClient build = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.SECONDS)
                .writeTimeout(2, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(build)
                .build();

        GitHubRequest request = retrofit.create(GitHubRequest.class);

        request.getRepo(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Repo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Repo> value) {
                        if (value.size() == 0){
                            Toast.makeText(activity, "no repositories", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        List<Repo> repos = new ArrayList<>();
                        for (Repo repo:value){
                            if (repo.has_issues){
                                repos.add(repo);
                            }
                        }
                        RepoAdapter repoAdapter = (RepoAdapter) binding.recyclerViewGithub.getAdapter();
                        assert repoAdapter != null;
                        repoAdapter.setRepos(repos);
                        repoAdapter.setUsername(username);
                        repoAdapter.refresh();
                    }

                    @Override
                    public void onError(Throwable e) {
                        // HTTP 404
                        Toast.makeText(activity, "Get repositories fail", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
