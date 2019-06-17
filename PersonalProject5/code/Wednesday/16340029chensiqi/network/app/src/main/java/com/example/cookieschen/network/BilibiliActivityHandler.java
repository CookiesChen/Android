package com.example.cookieschen.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.example.cookieschen.network.databinding.ActivityBilibiliBinding;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BilibiliActivityHandler {
    private BilibiliActivity activity;
    private ActivityBilibiliBinding binding;

    public BilibiliActivityHandler(BilibiliActivity activity, ActivityBilibiliBinding binding){
        this.activity = activity;
        this.binding = binding;
        initView();
    }

    private void initView() {
        List<BilibiliMovie> bilibiliMovies = new ArrayList<>();
        binding.recyclerView.setAdapter(new BilibiliAdapter(bilibiliMovies));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        binding.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
    }

    private void search() {
        final String id = binding.searchText.getText().toString();
        Observable<BilibiliMovie> observable = Observable.create(new ObservableOnSubscribe<BilibiliMovie>() {
            @Override
            public void subscribe(ObservableEmitter<BilibiliMovie> e) {
                BilibiliMovie bilibiliMovie = null;
                try {
                    URL url = new URL("https://space.bilibili.com/ajax/top/showTop?mid=" + id);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    httpURLConnection.setRequestProperty("Accept", "application/json");
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(10 * 1000);
                    httpURLConnection.setReadTimeout(1000);
                    ConnectivityManager mConnectivityManager =
                            (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
                    assert mConnectivityManager != null;
                    NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                    if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                        int code = httpURLConnection.getResponseCode();
                        if(code == 200){
                            String result = utils.GetStringByStream(httpURLConnection.getInputStream());
                            bilibiliMovie = new Gson().fromJson(result, BilibiliMovie.class);
                        }
                    } else {
                        e.onError(null);
                        return;
                    }
                } catch (IOException err) {
                    err.printStackTrace();
                } catch (JsonSyntaxException err){
                    bilibiliMovie = new BilibiliMovie();
                    bilibiliMovie.setStatus(false);
                }
                e.onNext(bilibiliMovie);
            }
        });

        Observer<BilibiliMovie> observer = new Observer<BilibiliMovie>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(BilibiliMovie value) {
                if(value.getStatus()){
                    BilibiliAdapter bilibiliAdapter = (BilibiliAdapter) binding.recyclerView.getAdapter();
                    assert bilibiliAdapter != null;
                    int index = bilibiliAdapter.addItem(value);
                    getCover(index, value);
                    getPreviewInfo(index, value);
                } else {
                    Toast.makeText(activity, "数据库中不存在记录", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(activity, "请检查网络连接", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {

            }
        };

        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    private void getPreviewInfo(final int index, final BilibiliMovie value) {
        Observable<Preview> observable = Observable.create(new ObservableOnSubscribe<Preview>() {
            @Override
            public void subscribe(ObservableEmitter<Preview> e) {
                try {
                    URL url = new URL("https://api.bilibili.com/pvideo?aid=" + value.getData().getAid());
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    httpURLConnection.setRequestProperty("Accept", "application/json");
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(10 * 1000);
                    httpURLConnection.setReadTimeout(1000);
                    int code = httpURLConnection.getResponseCode();
                    if(code == 200){
                        String result = utils.GetStringByStream(httpURLConnection.getInputStream());
                        e.onNext(new Gson().fromJson(result, Preview.class));
                    }
                } catch (ProtocolException err) {
                    err.printStackTrace();
                } catch (MalformedURLException err) {
                    err.printStackTrace();
                } catch (IOException err) {
                    err.printStackTrace();
                }
            }
        });

        Observer<Preview> observer = new Observer<Preview>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Preview value) {
                getPreview(value, index);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };

        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    private void getPreview(final Preview preview, final int index) {
        Observable<Bitmap> observable = Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(ObservableEmitter<Bitmap> e) {
                for(String urlString : preview.getData().getImage()){
                    try {
                        URL url = new URL(urlString);
                        HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                        httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        httpURLConnection.setRequestProperty("Accept", "application/json");
                        httpURLConnection.setRequestMethod("GET");
                        httpURLConnection.setConnectTimeout(10 * 1000);
                        httpURLConnection.setReadTimeout(1000);
                        int code = httpURLConnection.getResponseCode();
                        if(code == 200){
                            e.onNext(utils.GetBitmapByStream(httpURLConnection.getInputStream()));
                        }
                    } catch (ProtocolException err) {
                        err.printStackTrace();
                    } catch (MalformedURLException err) {
                        err.printStackTrace();
                    } catch (IOException err) {
                        err.printStackTrace();
                    }
                }
                e.onComplete();
            }
        });

        Observer<Bitmap> observer = new Observer<Bitmap>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Bitmap value) {
                List<Bitmap> bitmaps = utils.GetBitmaps(value, preview.getData().getImg_x_len(), preview.getData().getImg_y_len());
                BilibiliAdapter bilibiliAdapter = (BilibiliAdapter) binding.recyclerView.getAdapter();
                assert bilibiliAdapter != null;
                bilibiliAdapter.addPreviews(bitmaps, index);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                BilibiliAdapter bilibiliAdapter = (BilibiliAdapter) binding.recyclerView.getAdapter();
                assert bilibiliAdapter != null;
                bilibiliAdapter.refresh();
            }
        };

        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    private void getCover(final int index, final BilibiliMovie value) {
        Observable<Bitmap> observable = Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(ObservableEmitter<Bitmap> e) {
                try {
                    URL url = new URL(value.getData().getCover());
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    httpURLConnection.setRequestProperty("Accept", "application/json");
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(10 * 1000);
                    httpURLConnection.setReadTimeout(1000);
                    int code = httpURLConnection.getResponseCode();
                    if(code == 200){
                        e.onNext(utils.GetBitmapByStream(httpURLConnection.getInputStream()));
                    }
                } catch (ProtocolException err) {
                    err.printStackTrace();
                } catch (MalformedURLException err) {
                    err.printStackTrace();
                } catch (IOException err) {
                    err.printStackTrace();
                }
            }
        });

        Observer<Bitmap> observer = new Observer<Bitmap>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Bitmap value) {
                BilibiliAdapter bilibiliAdapter = (BilibiliAdapter) binding.recyclerView.getAdapter();
                assert bilibiliAdapter != null;
                bilibiliAdapter.setBitmapByIndex(value, index);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };

        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
