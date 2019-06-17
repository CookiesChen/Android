package com.example.cookieschen.network;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.cookieschen.network.databinding.ActivityIssueBinding;

public class IssueActivity extends AppCompatActivity {
    ActivityIssueBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getIntent().getExtras();
        assert bundle != null;
        String username = bundle.getString("username");
        String repoName = bundle.getString("repoName");

        binding = DataBindingUtil.setContentView(IssueActivity.this, R.layout.activity_issue);
        binding.setHandler(new IssueActivityHandler(this, binding, username, repoName));
    }
}