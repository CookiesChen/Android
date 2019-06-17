package com.example.cookieschen.network;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.cookieschen.network.databinding.ActivityGithubBinding;

public class GitHubActivity extends AppCompatActivity {
    ActivityGithubBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(GitHubActivity.this, R.layout.activity_github);
        binding.setHandler(new GitHubActivityHandler(this, binding));
    }
}
