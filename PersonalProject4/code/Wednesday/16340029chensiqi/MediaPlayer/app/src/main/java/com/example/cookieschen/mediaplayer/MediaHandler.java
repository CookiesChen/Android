package com.example.cookieschen.mediaplayer;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.SeekBar;

import com.example.cookieschen.mediaplayer.databinding.ActivityMediaPlayerBinding;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.cookieschen.mediaplayer.util.Convert.getPicFromBytes;

public class MediaHandler {

    private MediaPlayerActivity activity;
    private ActivityMediaPlayerBinding binding;

    private volatile boolean isPlaying;
    private MusicService.MyBinder myBinder;

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat time = new SimpleDateFormat("mm:ss");

    private ObjectAnimator objectAnimator;

    MediaHandler(MediaPlayerActivity activity, ActivityMediaPlayerBinding binding){
        this.activity = activity;
        this.binding = binding;

        initView();
    }

    @SuppressLint("ObjectAnimatorBinding")
    private void initView() {

        this.binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    Parcel data = Parcel.obtain();
                    data.writeInt(progress);
                    Parcel reply = Parcel.obtain();
                    try {
                        myBinder.transact(4, data, reply, 0);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    binding.seekBar.setProgress(progress);
                    binding.nowTime.setText(time.format(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        this.binding.playPause.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                playOrPause();
            }

        });

        this.binding.stop.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                stop();
            }
        });

        this.binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        this.binding.pickFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFile();
            }
        });


        objectAnimator = ObjectAnimator.ofFloat(binding.profileImage, "rotation", 0f, 360f);//添加旋转动画，旋转中心默认为控件中点
        objectAnimator.setDuration(3000);//设置动画时间
        objectAnimator.setInterpolator(new LinearInterpolator());//动画时间线性渐变
        objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        objectAnimator.setRepeatMode(ObjectAnimator.RESTART);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void playOrPause() {
        if(isPlaying){
            objectAnimator.pause();
            binding.playPause.setImageResource(R.mipmap.play);
        } else {
            if (objectAnimator.isPaused()){
                objectAnimator.resume();
            } else {
                objectAnimator.start();
            }
            binding.playPause.setImageResource(R.mipmap.pause);
        }
        isPlaying = !isPlaying;
        sendCode(2);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void stop() {
        objectAnimator.end();
        binding.seekBar.setProgress(0);
        binding.nowTime.setText(time.format(0));
        binding.playPause.setImageResource(R.mipmap.play);
        isPlaying = false;
        sendCode(3);
    }

    private void back() {
        try {
            activity.finish();
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pickFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        activity.startActivityForResult(intent, 1);
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setFile(Uri uri){

        stop();

        assert uri != null;
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(activity, uri);
        String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

        binding.title.setText(title);
        binding.artist.setText(artist);

        byte[] artBytes =  mmr.getEmbeddedPicture();
        if(artBytes != null)
        {
            InputStream is = new ByteArrayInputStream(mmr.getEmbeddedPicture());
            binding.profileImage.setImageBitmap(BitmapFactory.decodeStream(is));
        }
        else
        {
            binding.profileImage.setImageDrawable(activity.getResources().getDrawable(R.mipmap.img));
        }

        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();

        data.writeString(uri.toString());
        data.writeString(title);
        data.writeString(artist);
        data.writeByteArray(artBytes);


        try {
            myBinder.transact(5, data, reply, 0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        int totalTime = reply.readInt();
        binding.totalTime.setText(time.format(totalTime));
        binding.seekBar.setMax(totalTime);
        mmr.release();
    }

    // 获取服务后初始化
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void initialize(MusicService.MyBinder binder){
        myBinder = binder;
        /* transact*/
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            myBinder.transact(0, data, reply, 0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        binding.title.setText(reply.readString());
        binding.artist.setText(reply.readString());
        binding.profileImage.setImageBitmap(getPicFromBytes(reply.createByteArray()));
        int totalTime = reply.readInt();
        binding.totalTime.setText(time.format(totalTime));
        binding.seekBar.setMax(totalTime);
        boolean[] isPlayings = new boolean[1];
        reply.readBooleanArray(isPlayings);
        isPlaying = isPlayings[0];

        if(isPlaying) {
            objectAnimator.start();
            binding.playPause.setImageResource(R.mipmap.pause);
        } else {
            objectAnimator.pause();
            binding.playPause.setImageResource(R.mipmap.play);
        }

        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /* mediaplayer control code
    *  2-playOrpPause
    *  3-stop
    *  4-set progress
    *  5-set song
    * */
    private void sendCode(int code){
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            myBinder.transact(code, data, reply, 0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private final Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
        @Override
        public void subscribe(ObservableEmitter<Integer> e) throws Exception {
            while (true){
                if (isPlaying){
                    Thread.sleep(100);
                    Parcel data = Parcel.obtain();
                    Parcel reply = Parcel.obtain();
                    myBinder.transact(1, data, reply, 0);
                    e.onNext(reply.readInt());
                }
            }
        }
    });

    private final Observer<Integer> observer = new Observer<Integer>(){

        @Override
        public void onSubscribe(Disposable d) {

        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onNext(Integer integer) {
            if (integer == -1){
                binding.nowTime.setText(time.format(0));
                binding.seekBar.setProgress(0);
                objectAnimator.end();
                binding.playPause.setImageResource(R.mipmap.play);
                isPlaying = false;
            } else {
                binding.nowTime.setText(time.format(integer));
                binding.seekBar.setProgress(integer);
            }
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onComplete() {

        }
    };
}
