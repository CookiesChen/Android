package com.example.cookieschen.network;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.example.cookieschen.network.databinding.ActivityIssueBinding;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class IssueActivityHandler {

    private IssueActivity activity;
    ActivityIssueBinding binding;
    private String username;
    private String repoName;
    private String baseURL = "https://api.github.com";

    IssueActivityHandler(IssueActivity activity, ActivityIssueBinding binding, String username, String repoName) {
        this.activity = activity;
        this.binding = binding;
        this.username = username;
        this.repoName = repoName;

        initView();
    }

    public interface IssueRequest {
        @GET("/repos/{username}/{repoName}/issues")
        Observable<List<Issue>> getIssues(@Path("username") String username, @Path("repoName") String repoName);

        @Headers({"Content-Type: application/json","Accept: application/json"})
        @POST("/repos/{username}/{repoName}/issues")
        Observable<Issue> addIssue(@Path("username") String username, @Path("repoName") String repoName,
                                   @Body RequestBody route, @Query("access_token") String token);
    }

    private void initView() {
        List<Issue> issues = new ArrayList<>();
        this.binding.issueRecyclerView.setAdapter(new IssueAdapter(issues));
        this.binding.issueRecyclerView.setLayoutManager(new LinearLayoutManager(activity));

        /* POST*/
        this.binding.issueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                IssueRequest request = retrofit.create(IssueRequest.class);

                String title = binding.title.getText().toString();
                String body = binding.body.getText().toString();
                String token = "ebb5c03cd0cb319548d7e1baa50ebc54b0eb3abd";

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("title", title);
                jsonObject.addProperty("body", body);
                RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

                request.addIssue(username, repoName, requestBody, token)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Issue>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(Issue value) {
                                IssueAdapter issueAdapter = (IssueAdapter) binding.issueRecyclerView.getAdapter();
                                assert issueAdapter != null;
                                issueAdapter.addIssue(value);
                                Toast.makeText(activity, "Create issue successfully", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Throwable e) {
                                // HTTP 404
                                Toast.makeText(activity, "Fail to create issue", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        });

        getIssues();
    }
    /* GET*/
    private void getIssues(){
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

        IssueRequest request = retrofit.create(IssueRequest.class);

        request.getIssues(username, repoName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Issue>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Issue> value) {
                        if (value.size() == 0) {
                            Toast.makeText(activity, "no issues", Toast.LENGTH_SHORT).show();
                        }
                        IssueAdapter issueAdapter = (IssueAdapter) binding.issueRecyclerView.getAdapter();
                        assert issueAdapter != null;
                        issueAdapter.setIssues(value);
                        issueAdapter.refresh();
                    }

                    @Override
                    public void onError(Throwable e) {
                        // HTTP 404
                        Toast.makeText(activity, "network error", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
