package com.example.cookieschen.network;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.cookieschen.network.databinding.ActivityBilibiliBinding;

public class BilibiliActivity extends AppCompatActivity {

    ActivityBilibiliBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(BilibiliActivity.this, R.layout.activity_bilibili);
        binding.setHandler(new BilibiliActivityHandler(this, binding));
    }
}

