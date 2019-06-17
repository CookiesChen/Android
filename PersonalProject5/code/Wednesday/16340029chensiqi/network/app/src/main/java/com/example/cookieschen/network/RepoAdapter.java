package com.example.cookieschen.network;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cookieschen.network.databinding.ItemGithubBinding;

import java.util.List;

public class RepoAdapter extends RecyclerView.Adapter<RepoAdapter.RepoViewHolder> {
    private List<Repo> repos;
    private String username;

    RepoAdapter(List<Repo> repos) {
        this.repos = repos;
    }

    @NonNull
    @Override
    public RepoViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, int i) {
        final ItemGithubBinding binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.item_github, viewGroup, false);
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String repoName = binding.repoName.getText().toString();
                Intent intent = new Intent(viewGroup.getContext(), IssueActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                bundle.putString("repoName", repoName);
                intent.putExtras(bundle);
                viewGroup.getContext().startActivity(intent);
            }
        });
        return new RepoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RepoViewHolder repoViewHolder, int i) {
        repoViewHolder.binding.setRepo(repos.get(i));
        repoViewHolder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        if (repos == null) return 0;
        return repos.size();
    }

    class RepoViewHolder extends RecyclerView.ViewHolder {
        ItemGithubBinding binding;

        RepoViewHolder(@NonNull ItemGithubBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    void setRepos(List<Repo> repos) {
        this.repos = repos;
    }

    void refresh(){
        notifyDataSetChanged();
    }

    void setUsername(String username){
        this.username = username;
    }
}
