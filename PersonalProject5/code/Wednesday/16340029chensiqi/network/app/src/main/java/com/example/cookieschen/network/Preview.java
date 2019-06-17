package com.example.cookieschen.network;

public class Preview {

    private Data data;

    public Data getData() {
        return data;
    }

    public class Data{

        private int img_x_len;
        private int img_y_len;
        private String[] image;

        int getImg_x_len() {
            return img_x_len;
        }

        int getImg_y_len() {
            return img_y_len;
        }

        public String[] getImage() {
            return image;
        }

        public void setImage(String[] image) {
            this.image = image;
        }
    }
}
