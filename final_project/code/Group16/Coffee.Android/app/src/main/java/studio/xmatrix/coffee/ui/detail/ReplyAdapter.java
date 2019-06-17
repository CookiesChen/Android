package studio.xmatrix.coffee.ui.detail;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import studio.xmatrix.coffee.R;
import studio.xmatrix.coffee.data.service.resource.CommentsResource;
import studio.xmatrix.coffee.data.store.DefaultSharedPref;
import studio.xmatrix.coffee.databinding.ReplyItemBinding;
import studio.xmatrix.coffee.ui.user.UserActivity;

import java.util.ArrayList;
import java.util.List;

import static studio.xmatrix.coffee.ui.detail.DetailHandler.getTime;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder> {
    private DetailActivity activity;
    private List<CommentsResource.ReplyForComment> data;
    private OnClickReply onClickReply;
    private List<String> likeData;
    private DetailViewModel viewModel;

    public void setOnClickReply(ReplyAdapter.OnClickReply onClickReply) {
        this.onClickReply = onClickReply;
    }

    public void setLikeData(List<String> likeData) {
        if (likeData == null) {
            this.likeData.clear();
        } else {
            this.likeData = likeData;
        }
        notifyDataSetChanged();
    }

    public interface OnClickReply {
        void onClickLike(String id);

        void onClickReply(String fatherId, String fatherName, String contentId);

        void onClickDelete(String id);
    }

    public ReplyAdapter(DetailActivity activity, DetailViewModel viewModel) {
        this.activity = activity;
        this.viewModel = viewModel;
        this.data = new ArrayList<>();
        this.likeData = new ArrayList<>();
    }

    public void setData(List<CommentsResource.ReplyForComment> data) {
        if (data == null) {
            this.data.clear();
        } else {
            this.data = data;
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.reply_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(i);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ReplyItemBinding binding;

        ViewHolder(ReplyItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(int pos) {
            if (pos == getItemCount() - 1) {
                binding.replyDivider.setVisibility(View.GONE);
            }
            CommentsResource.ReplyForComment itemData = data.get(pos);
            binding.setModel(itemData);
            binding.replyTime.setText(getTime(itemData.getReply().getDate()));
            binding.replyLike.setOnClickListener(v -> {
                onClickReply.onClickLike(itemData.getReply().getId());
            });
            binding.replyContent.setOnLongClickListener(v -> {
                if (DefaultSharedPref.INSTANCE.get(DefaultSharedPref.Key.UserId).equals(itemData.getReply().getUserId())) {
                    final AlertDialog.Builder normalDialog =
                            new AlertDialog.Builder(activity);
                    normalDialog.setTitle("操作确认");
                    normalDialog.setMessage("删除这条回复?");
                    normalDialog.setPositiveButton("确定", (dialog, which) -> {
                        Toast.makeText(activity, "删除", Toast.LENGTH_SHORT).show();
                        onClickReply.onClickDelete(itemData.getReply().getId());
                        dialog.dismiss();
                    });
                    normalDialog.setNegativeButton("取消", (dialog, which) -> {
                        dialog.dismiss();
                    });
                    normalDialog.show();
                }
                return true;
            });
            binding.replyContent.setOnClickListener(v -> {
                onClickReply.onClickReply(itemData.getReply().getUserId(), itemData.getUser().getName(), itemData.getReply().getContentId());
            });

            if (likeData.contains(itemData.getReply().getId())) {
                binding.replyLike.setImageResource(R.drawable.ic_like);
            } else {
                binding.replyLike.setImageResource(R.drawable.ic_like_none);
            }

            binding.replyName.setOnClickListener(v -> UserActivity.start(activity, itemData.getReply().getUserId(), ActivityOptionsCompat
                    .makeSceneTransitionAnimation(activity,
                            Pair.create(binding.replyName, "userName"))
                    .toBundle()));

            binding.replyAt.setOnClickListener(v -> UserActivity.start(activity, itemData.getReply().getFatherId(), ActivityOptionsCompat
                    .makeSceneTransitionAnimation(activity,
                            Pair.create(binding.replyAt, "userName"))
                    .toBundle()));
        }

    }
}

