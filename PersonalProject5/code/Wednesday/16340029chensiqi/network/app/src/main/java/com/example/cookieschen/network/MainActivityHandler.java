package com.example.cookieschen.network;

import android.content.Intent;
import android.view.View;

import com.example.cookieschen.network.databinding.ActivityMainBinding;

public class MainActivityHandler {

    MainActivity activity;
    ActivityMainBinding binding;

    MainActivityHandler(MainActivity activity, ActivityMainBinding binding){
        this.activity = activity;
        this.binding = binding;
        initView();
    }

    private void initView() {
        // Bilibili
        this.binding.bilibiliButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity.getBaseContext(), BilibiliActivity.class);
                activity.startActivity(intent);
            }
        });

        // Github
        this.binding.githubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity.getBaseContext(), GitHubActivity.class);
                activity.startActivity(intent);
            }
        });

    }

}
