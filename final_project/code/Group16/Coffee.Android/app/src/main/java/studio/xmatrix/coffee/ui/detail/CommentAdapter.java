package studio.xmatrix.coffee.ui.detail;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import studio.xmatrix.coffee.R;
import studio.xmatrix.coffee.data.common.network.Status;
import studio.xmatrix.coffee.data.service.resource.CommentsResource;
import studio.xmatrix.coffee.data.store.DefaultSharedPref;
import studio.xmatrix.coffee.databinding.CommentItemBinding;
import studio.xmatrix.coffee.ui.user.UserActivity;

import java.util.ArrayList;
import java.util.List;

import static studio.xmatrix.coffee.ui.detail.DetailHandler.getTime;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private DetailActivity activity;
    private List<CommentsResource.CommentForContent> data;
    private List<String> likeData;
    private OnClickComment onClickComment;
    private ReplyAdapter.OnClickReply onClickReply;
    private DetailViewModel viewModel;

    public void setOnClickComment(OnClickComment onClickComment) {
        this.onClickComment = onClickComment;
    }

    public void setOnClickReply(ReplyAdapter.OnClickReply onClickReply) {
        this.onClickReply = onClickReply;
    }


    public interface OnClickComment {
        void onClickDelete(String id);

        void onClickLike(String id);

        void onClickReply(String fatherId, String fatherName, String contentId);
    }

    public CommentAdapter(DetailActivity activity, DetailViewModel viewModel) {
        this.activity = activity;
        this.viewModel = viewModel;
        data = new ArrayList<>();
        this.likeData = new ArrayList<>();
    }

    public void setData(List<CommentsResource.CommentForContent> data) {
        if (data == null) {
            this.data.clear();
        } else {
            this.data = data;
        }
        notifyDataSetChanged();
    }

    public void setLikeData(List<String> likeData) {
        if (likeData == null) {
            this.likeData.clear();
        } else {
            this.likeData = likeData;
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.comment_item, viewGroup, false));
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
        private final CommentItemBinding binding;
        private ReplyAdapter adapter;

        ViewHolder(CommentItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(int pos) {
            if (adapter == null) {
                adapter = new ReplyAdapter(activity, viewModel);
                adapter.setOnClickReply(onClickReply);
                binding.replyList.setAdapter(adapter);
                binding.replyList.setLayoutManager(new LinearLayoutManager(activity));
            }
            adapter.setLikeData(likeData);
            CommentsResource.CommentForContent itemData = data.get(pos);
            binding.commentLike.setOnClickListener(v-> {
                onClickComment.onClickLike(itemData.getComment().getId());
            });
            binding.commentContentText.setOnClickListener(v -> {
                onClickComment.onClickReply(itemData.getComment().getUserId(), itemData.getUser().getName(), itemData.getComment().getId());
            });

            // 删除按钮
            if (DefaultSharedPref.INSTANCE.get(DefaultSharedPref.Key.UserId).equals(itemData.getComment().getUserId())) {
                binding.commentDelete.setVisibility(View.VISIBLE);
            } else {
                binding.commentDelete.setVisibility(View.GONE);
            }
            binding.commentDelete.setOnClickListener(v -> {
                if (DefaultSharedPref.INSTANCE.get(DefaultSharedPref.Key.UserId).equals(itemData.getComment().getUserId())) {
                    final AlertDialog.Builder normalDialog =
                            new AlertDialog.Builder(activity);
                    normalDialog.setTitle("操作确认");
                    normalDialog.setMessage("删除这条评论?");
                    normalDialog.setPositiveButton("确定", (dialog, which) -> {
                        Toast.makeText(activity, "删除", Toast.LENGTH_SHORT).show();
                        onClickComment.onClickDelete(itemData.getComment().getId());
                        dialog.dismiss();
                    });
                    normalDialog.setNegativeButton("取消", (dialog, which) -> {
                        dialog.dismiss();
                    });
                    normalDialog.show();
                }
            });
            binding.setModel(itemData);
            adapter.setData(itemData.getReplies());
            binding.commentTime.setText(getTime(itemData.getComment().getDate()));
            if (likeData.contains(itemData.getComment().getId())) {
                binding.commentLike.setImageResource(R.drawable.ic_like);
            } else {
                binding.commentLike.setImageResource(R.drawable.ic_like_none);
            }

            viewModel.getUserAvatar(itemData.getUser().getAvatar()).observe(activity, res -> {
                if (res != null && res.getStatus() == Status.SUCCESS) {
                    binding.commentUserAvatar.setImageBitmap(res.getData());
                }
            });

            binding.commentUserAvatar.setOnClickListener(v -> gotoUser(itemData.getComment().getUserId()));
            binding.commentUserName.setOnClickListener(v -> gotoUser(itemData.getComment().getUserId()));
        }

        private void gotoUser(String id) {
            UserActivity.start(activity, id, ActivityOptionsCompat
                    .makeSceneTransitionAnimation(activity,
                            Pair.create(binding.commentUserAvatar, "userAvatar"),
                            Pair.create(binding.commentUserName, "userName"))
                    .toBundle());
        }
    }
}
