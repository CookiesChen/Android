package com.example.cookieschen.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class utils {

    public static String GetStringByStream(InputStream inputStream) throws IOException {
        int len;
        byte buffer[] = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) > 0 ){
            baos.write(buffer,0,len);
        }

        //关闭流
        inputStream.close();

        String content = new String(baos.toByteArray());
        return content;
    }

    public static Bitmap GetBitmapByStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer=new byte[1024];
        int len;
        while((len = inputStream.read(buffer))!=-1){
            bos.write(buffer,0,len);
        }
        byte[] dataImage = bos.toByteArray();
        bos.close();
        inputStream.close();
        return BitmapFactory.decodeByteArray(dataImage, 0, dataImage.length);
    }

    public static List<Bitmap> GetBitmaps(Bitmap bitmap, int xLen, int yLen) {

        List<Bitmap> pieces = new ArrayList<>();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int pieceWidth = width / xLen;
        int pieceHeight = height / yLen;
        for (int i = 0; i < xLen; i++) {
            for (int j = 0; j < yLen; j++) {
                int xValue = j * pieceWidth;
                int yValue = i * pieceHeight;
                pieces.add(Bitmap.createBitmap(bitmap, xValue, yValue,pieceWidth, pieceHeight));
            }
        }

        return pieces;
    }
}
