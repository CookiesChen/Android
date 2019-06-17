package com.example.cookieschen.mediaplayer;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import java.io.IOException;
import java.nio.ByteBuffer;

import static com.example.cookieschen.mediaplayer.util.Convert.getBytesByBitmap;
import static com.example.cookieschen.mediaplayer.util.Convert.getPicFromBytes;

public class MusicService extends Service {

    MediaPlayer mediaPlayer;

    String title;
    String artist;
    Bitmap img;

    boolean isStop = true;
    public IBinder binder = new MyBinder();
    public class MyBinder extends Binder {

        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            boolean[] isPlaying = new boolean[1];
            switch (code){
                case 0:
                    // 获得数据
                    reply.writeString(title);
                    reply.writeString(artist);
                    reply.writeByteArray(getBytesByBitmap(img));
                    reply.writeInt(mediaPlayer.getDuration());
                    isPlaying[0] = mediaPlayer.isPlaying();
                    reply.writeBooleanArray(isPlaying);
                    break;
                case 1:
                    // 获得状态
                    if (!isStop) reply.writeInt(mediaPlayer.getCurrentPosition());
                    else reply.writeInt(-1);
                    break;
                case 2:
                    playOrPause();
                    break;
                case 3:
                    stop();
                    break;
                case 4:
                    setProgress(data.readInt());
                    break;
                case 5:
                    try {
                        isStop = false;
                        setSong(Uri.parse(data.readString()));
                        title = data.readString();
                        artist = data.readString();
                        img = getPicFromBytes(data.createByteArray());
                        reply.writeInt(mediaPlayer.getDuration());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            return super.onTransact(code, data, reply, flags);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.music);
        title = "山高水长";
        artist = "中山大学合唱团";
        img = BitmapFactory.decodeResource(getResources(), R.mipmap.img);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stop();
                isStop = true;
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void playOrPause() {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
            isStop = false;
        }
    }

    private void stop() {
        mediaPlayer.seekTo(0);
        mediaPlayer.pause();
    }

    private void setProgress(int progress){
        mediaPlayer.seekTo(progress);
    }

    private void setSong(Uri uri) throws IOException {
        mediaPlayer.reset();
        mediaPlayer.setDataSource(getBaseContext(), uri);
        mediaPlayer.prepare();
    }
}
