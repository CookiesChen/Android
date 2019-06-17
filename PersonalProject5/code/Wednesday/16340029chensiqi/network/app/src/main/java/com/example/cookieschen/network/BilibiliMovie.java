package com.example.cookieschen.network;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class BilibiliMovie {
    private Boolean status;
    private Data data;
    private Bitmap bitmap;
    private List<Bitmap> previews;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public List<Bitmap> getPreviews() {
        return previews;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setPreviews(List<Bitmap> previews) {
        this.previews = previews;
    }

    void addPreviews(List<Bitmap> newPreviews){
        if (this.previews == null) this.previews = new ArrayList<>();
        this.previews.addAll(newPreviews);
    }

    public class Data{
        private int aid;
        private int state;
        private String cover;
        private String title;
        private int play;
        private int video_review;
        private String duration;
        private String create;
        private String content;

        int getAid() {
            return aid;
        }

        public String getCover() {
            return cover;
        }

        public String getTitle() {
            return title;
        }

        public int getPlay() {
            return play;
        }

        public int getVideo_review() {
            return video_review;
        }

        public String getDuration() {
            return duration;
        }

        public String getCreate() {
            return create;
        }

        public String getContent() {
            return content;
        }

        public void setAid(int aid) {
            this.aid = aid;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setPlay(int play) {
            this.play = play;
        }

        public void setVideo_review(int video_review) {
            this.video_review = video_review;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public void setCreate(String create) {
            this.create = create;
        }

        public void setContent(String content) {
            this.content = content;
        }

    }
}
