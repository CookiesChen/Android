package com.example.cookieschen.network;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.cookieschen.network.databinding.ItemIssueBinding;

import java.util.List;

public class IssueAdapter extends RecyclerView.Adapter<IssueAdapter.IssueViewHolder> {

    private List<Issue> issues;

    IssueAdapter(List<Issue> issues) {
        this.issues = issues;
    }

    @NonNull
    @Override
    public IssueViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemIssueBinding binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.item_issue, viewGroup, false);
        return new IssueViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull IssueViewHolder issueViewHolder, int i) {
        issueViewHolder.binding.setIssue(issues.get(i));
        issueViewHolder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        if (issues == null) return 0;
        return issues.size();
    }

    void addIssue(Issue value) {
        issues.add(value);
        refresh();
    }

    class IssueViewHolder extends RecyclerView.ViewHolder {
        ItemIssueBinding binding;

        IssueViewHolder(@NonNull ItemIssueBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    void setIssues(List<Issue> issues) {
        this.issues = issues;
    }

    void refresh(){
        notifyDataSetChanged();
    }
}
