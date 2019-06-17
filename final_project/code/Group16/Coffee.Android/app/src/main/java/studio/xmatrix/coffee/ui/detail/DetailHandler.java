package studio.xmatrix.coffee.ui.detail;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.SpacingItemDecoration;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import io.objectbox.relation.ToMany;
import studio.xmatrix.coffee.R;
import studio.xmatrix.coffee.data.common.network.Status;
import studio.xmatrix.coffee.data.model.Album;
import studio.xmatrix.coffee.data.model.Content;
import studio.xmatrix.coffee.data.model.Image;
import studio.xmatrix.coffee.data.service.LikeService;
import studio.xmatrix.coffee.data.service.resource.CommentsResource;
import studio.xmatrix.coffee.data.store.DefaultSharedPref;
import studio.xmatrix.coffee.databinding.DetailActivityBinding;
import studio.xmatrix.coffee.inject.AppInjector;
import studio.xmatrix.coffee.inject.Injectable;
import studio.xmatrix.coffee.ui.ListStatus;
import studio.xmatrix.coffee.ui.add.AddActivity;
import studio.xmatrix.coffee.ui.nav.MyImageLoader;
import studio.xmatrix.coffee.ui.square.TagAdapter;
import studio.xmatrix.coffee.ui.user.UserActivity;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

import static android.app.Activity.RESULT_OK;

public class DetailHandler implements Injectable {
    private DetailActivity activity;
    private DetailActivityBinding binding;
    private CommentFragment commentFragment;
    private CommentAdapter commentAdapter;
    private TagAdapter tagAdapter;
    private String id;
    private Content detailData;
    private List<String> likeData;
    private String fatherId;
    private String contentId;
    private String fatherName;
    private Boolean isReply;


    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private DetailViewModel viewModel;

    public DetailHandler(DetailActivity activity, DetailActivityBinding binding) {
        this.activity = activity;
        this.binding = binding;
        Bundle bundle = activity.getIntent().getExtras();
        this.id = Objects.requireNonNull(bundle).getString("id", "");
        if (this.id.equals("")) {
            activity.finish();
            return;
        }
        AppInjector.Companion.inject(this);
        viewModel = ViewModelProviders.of(activity, viewModelFactory).get(DetailViewModel.class);
        initView();
        refreshData();
    }

    void setImageLoader() {
        NineGridView.setImageLoader(new MyImageLoader(activity, viewModel));
    }

    private void setLikeStatus() {
        if (likeData != null && detailData != null) {
            if (likeData.contains(detailData.getId())) {
                binding.detailContent.btnLike.setImageResource(R.drawable.ic_like);
            } else {
                binding.detailContent.btnLike.setImageResource(R.drawable.ic_like_none);
            }
        }
    }


    private void refreshLike() {
        viewModel.getLikes().observe(activity, resource -> {
            if (resource != null) {
                switch (resource.getStatus()) {
                    case ERROR:
                        Toast.makeText(activity, "网络错误", Toast.LENGTH_SHORT).show();
                        break;
                    case SUCCESS:
                        List<String> data = Objects.requireNonNull(resource.getData()).getResource();
                        if (data != null) {
                            likeData = data;
                        } else {
                            likeData.clear();
                        }
                        commentAdapter.setLikeData(likeData);
                        setLikeStatus();
                        break;
                }
            }
        });
    }

    private void refreshData() {
        // 刷新数据
        refreshLike();
        viewModel.getDetail(id).observe(activity, resource -> {
            if (resource != null) {
                switch (resource.getStatus()) {
                    case SUCCESS:
                        if (Objects.requireNonNull(resource.getData()).getState().equals("not_found")) {
                            Toast.makeText(activity, "内容已删除", Toast.LENGTH_SHORT).show();
                            activity.finish();
                            return;
                        }
                        detailData = Objects.requireNonNull(resource.getData()).getResource();
                        if (detailData != null) {
                            binding.detailContent.setModel(detailData);
                            binding.detailContent.tagLayout.setAdapter(tagAdapter);
                            tagAdapter.setData(detailData.tags);
                            binding.detailContent.contentTime.setText(getTime(detailData.getPublishDate()));
                            setStatus(ListStatus.StatusType.Done);
                            setLikeStatus();
                            if (DefaultSharedPref.INSTANCE.get(DefaultSharedPref.Key.UserId).equals(detailData.getOwnId())) {
                                binding.detailContent.btnEdit.setVisibility(View.VISIBLE);
                                binding.detailContent.btnRemove.setVisibility(View.VISIBLE);
                            }
                            viewModel.getUserAvatar(detailData.getUserAvatar()).observe(activity, res -> {
                                if (res != null && res.getStatus() == Status.SUCCESS) {
                                    binding.detailContent.userAvatar.setImageBitmap(res.getData());
                                }
                            });
                            ArrayList<ImageInfo> imageInfo = new ArrayList<>();
                            Album album = detailData.getAlbum().getTarget();
                            if (album != null) {
                                ToMany<Image> images = album.getImages();
                                for (Image i :images) {
                                    ImageInfo info = new ImageInfo();
                                    info.setThumbnailUrl(i.getThumb());
                                    info.setBigImageUrl(i.getThumb());
                                    // info.setBigImageUrl(i.getFile().getTarget().getFile() + "@" + itemData.getId());
                                    imageInfo.add(info);
                                }
                            }
                            binding.detailContent.nineGridImage.setAdapter(new NineGridViewClickAdapter(activity, imageInfo));
                        } else {
                            setStatus(ListStatus.StatusType.Nothing);
                        }
                        break;
                    case ERROR:
                        setStatus(ListStatus.StatusType.Error);
                        break;
                }
            }
        });

        viewModel.getComments(id).observe(activity, resource -> {
            if (resource != null) {
                switch (resource.getStatus()) {
                    case SUCCESS:
                        List<CommentsResource.CommentForContent> content = Objects.requireNonNull(resource.getData()).getResource();
                        if (content != null) {
                            commentAdapter.setData(content);
                            binding.commentNothing.setVisibility(View.GONE);
                            binding.commentList.setVisibility(View.VISIBLE);
                        } else {
                            binding.commentNothing.setVisibility(View.VISIBLE);
                            binding.commentList.setVisibility(View.GONE);
                        }
                }
            }
        });
    }

    private void deleteComment(String id) {
        if (DefaultSharedPref.INSTANCE.get(DefaultSharedPref.Key.UserId).equals("")) {
            Toast.makeText(activity, "请先登陆", Toast.LENGTH_SHORT).show();
            return;
        }
        viewModel.deleteComment(id).observe(activity, res -> {
            if (res != null) {
                switch (res.getStatus()) {
                    case ERROR:
                        Toast.makeText(activity, "网络错误", Toast.LENGTH_SHORT).show();
                        break;
                    case SUCCESS:
                        refreshData();
                        Intent intent = new Intent();
                        intent.putExtra("update", true);
                        activity.setResult(RESULT_OK, intent);
                        break;
                }
            }
        });
    }


    private void like(String id, LikeService.LikeType type) {
        viewModel.like(id, type).observe(activity, res -> {
            if (res != null) {
                switch (res.getStatus()) {
                    case ERROR:
                        Toast.makeText(activity, "网络错误", Toast.LENGTH_SHORT).show();
                        break;
                    case SUCCESS:
                        refreshData();
                        Intent intent = new Intent();
                        intent.putExtra("update", true);
                        activity.setResult(RESULT_OK, intent);
                        break;
                }
            }
        });
    }


    private void unlike(String id, LikeService.LikeType type) {
        viewModel.unlike(id, type).observe(activity, res -> {
            if (res != null) {
                switch (res.getStatus()) {
                    case ERROR:
                        Toast.makeText(activity, "网络错误", Toast.LENGTH_SHORT).show();
                        break;
                    case SUCCESS:
                        refreshData();
                        Intent intent = new Intent();
                        intent.putExtra("update", true);
                        activity.setResult(RESULT_OK, intent);
                        break;
                }
            }
        });
    }


    private void initView() {
        Objects.requireNonNull(activity.getSupportActionBar()).setTitle("内容详情");

        binding.detailStatus.statusError.setOnClickListener(v -> refreshData());

        // 评论列表
        binding.commentList.setLayoutManager(new LinearLayoutManager(activity));
        commentAdapter = new CommentAdapter(activity, viewModel);
        commentAdapter.setOnClickComment(new CommentAdapter.OnClickComment() {
            @Override
            public void onClickDelete(String id) {
                deleteComment(id);
            }

            @Override
            public void onClickLike(String id) {
                if (DefaultSharedPref.INSTANCE.get(DefaultSharedPref.Key.UserId).equals("")) {
                    Toast.makeText(activity, "请先登陆", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (likeData.contains(id)) {
                    unlike(id, LikeService.LikeType.Comment);
                } else {
                    like(id, LikeService.LikeType.Comment);
                }
            }

            @Override
            public void onClickReply(String fatherId, String fatherName, String contentId) {
                DetailHandler.this.fatherId = fatherId;
                DetailHandler.this.fatherName = fatherName;
                DetailHandler.this.contentId = contentId;
                DetailHandler.this.isReply = true;
                onClickAddComment(null);
            }

        });
        commentAdapter.setOnClickReply(new ReplyAdapter.OnClickReply() {
            @Override
            public void onClickLike(String id) {
                if (DefaultSharedPref.INSTANCE.get(DefaultSharedPref.Key.UserId).equals("")) {
                    Toast.makeText(activity, "请先登陆", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (likeData.contains(id)) {
                    unlike(id, LikeService.LikeType.Reply);
                } else {
                    like(id, LikeService.LikeType.Reply);
                }
            }

            @Override
            public void onClickReply(String fatherId, String fatherName, String contentId) {
                DetailHandler.this.fatherId = fatherId;
                DetailHandler.this.fatherName = fatherName;
                DetailHandler.this.contentId = contentId;
                DetailHandler.this.isReply = true;
                onClickAddComment(null);
            }

            @Override
            public void onClickDelete(String id) {
                deleteComment(id);
            }

        });
        binding.commentList.setAdapter(commentAdapter);


        // 底部栏
        binding.addTitle.setOnClickListener(v -> {
            this.isReply = false;
            this.fatherId = detailData.getOwnId();
            this.fatherName = detailData.getUserName();
            this.contentId = detailData.getId();
            this.onClickAddComment(v);
        });

        // 详情卡片
        binding.detailContent.btnComment.setOnClickListener(v -> {
            this.isReply = false;
            this.fatherId = detailData.getOwnId();
            this.fatherName = detailData.getUserName();
            this.contentId = detailData.getId();
            this.onClickAddComment(v);
        });
        // 点击事件
        binding.detailContent.userAvatar.setOnClickListener(this::onClickUser);
        binding.detailContent.userName.setOnClickListener(this::onClickUser);
        binding.detailContent.btnRemove.setOnClickListener(this::onClickDelete);
        binding.detailContent.btnEdit.setOnClickListener(this::onClickEdit);
        binding.detailContent.btnLike.setOnClickListener(this::onClickContentLike);

        // 标签
        tagAdapter = new TagAdapter(activity);
        binding.detailContent.tagLayout.setAdapter(tagAdapter);
        ChipsLayoutManager chipsLayoutManager = ChipsLayoutManager.newBuilder(activity)
                .setChildGravity(Gravity.TOP)
                .setScrollingEnabled(true)
                .setGravityResolver(position -> Gravity.START)
                .setOrientation(ChipsLayoutManager.HORIZONTAL)
                .build();
        binding.detailContent.tagLayout.setLayoutManager(chipsLayoutManager);
        binding.detailContent.tagLayout.addItemDecoration(new SpacingItemDecoration(10, 10));
    }

    public static String getTime(Long publish) {
        long now = new Date().getTime();
        if (now - publish > 1000 * 60 * 60 * 24 * 7) {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(publish);
        } else if (now - publish > 1000 * 60 * 60 * 24) {
            return Long.toString((now - publish) / (1000 * 60 * 60 * 24)) + "天前";
        } else if (now - publish > 1000 * 60 * 60) {
            return Long.toString((now - publish) / (1000 * 60 * 60)) + "小时前";
        } else if (now - publish > 1000 * 60) {
            return Long.toString((now - publish) / (1000 * 60)) + "分钟前";
        } else {
            return "刚刚";
        }

    }

    private void onClickUser(View v) {
        UserActivity.start(activity, detailData.getOwnId(), ActivityOptionsCompat
                .makeSceneTransitionAnimation(activity,
                        Pair.create(binding.detailContent.userAvatar, "userAvatar"),
                        Pair.create(binding.detailContent.userName, "userName"))
                .toBundle());
    }

    @SuppressLint("SetTextI18n")
    private void onClickAddComment(View v) {
        if (DefaultSharedPref.INSTANCE.get(DefaultSharedPref.Key.UserId).equals("")) {
            Toast.makeText(activity, "请先登陆", Toast.LENGTH_SHORT).show();
            return;
        }
        commentFragment = new CommentFragment((binding) -> {
            if (isReply) {
                binding.commentFather.setText("回复: @" + fatherName);
            } else {
                binding.commentFather.setText("评论: @" + fatherName);
            }
            binding.btnAddComment.setOnClickListener(view -> {
                String content = binding.editComment.getText().toString();
                if (content.equals("")) {
                    Toast.makeText(activity, "评论不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                viewModel.addComment(contentId, fatherId, content, isReply).observe(activity, res -> {
                    if (res != null) {
                        switch (res.getStatus()) {
                            case SUCCESS:
                                // Toast.makeText(activity, res.getData().getState(), Toast.LENGTH_SHORT).show();
                                refreshData();
                                commentFragment.dismiss();
                                Intent intent = new Intent();
                                intent.putExtra("update", true);
                                activity.setResult(RESULT_OK, intent);
                                break;
                            case ERROR:
                                Toast.makeText(activity, "网络错误", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
            });
            (new Handler()).postDelayed(() -> {
                binding.editComment.setFocusable(true);
                binding.editComment.requestFocus();
                InputMethodManager inputManager = (InputMethodManager) binding.editComment.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                Objects.requireNonNull(inputManager).showSoftInput(binding.editComment, 0);
            }, 333);
        });
        commentFragment.show(activity.getSupportFragmentManager(), "addComment");
    }

    private void onClickEdit(View v) {
        if (DefaultSharedPref.INSTANCE.get(DefaultSharedPref.Key.UserId).equals("")) {
            Toast.makeText(activity, "请先登陆", Toast.LENGTH_SHORT).show();
            return;
        }
        // todo
        // 获取旧内容填充
        activity.startActivity(new Intent(activity, AddActivity.class));
    }

    private void onClickDelete(View v) {
        if (DefaultSharedPref.INSTANCE.get(DefaultSharedPref.Key.UserId).equals("")) {
            Toast.makeText(activity, "请先登陆", Toast.LENGTH_SHORT).show();
            return;
        }
        if (DefaultSharedPref.INSTANCE.get(DefaultSharedPref.Key.UserId).equals(detailData.getOwnId())) {
            final AlertDialog.Builder normalDialog =
                    new AlertDialog.Builder(activity);
            normalDialog.setTitle("操作确认");
            normalDialog.setMessage("删除这条内容?");
            normalDialog.setPositiveButton("确定", (dialog, which) -> {
                viewModel.deleteContent(detailData.getId()).observe(activity, res -> {
                    if (res != null) {
                        switch (res.getStatus()) {
                            case SUCCESS:
                                Toast.makeText(activity, "删除成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                intent.putExtra("update", true);
                                activity.setResult(RESULT_OK, intent);
                                activity.finish();
                                break;
                            case ERROR:
                                Toast.makeText(activity, "网络错误", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
                dialog.dismiss();
            });
            normalDialog.setNegativeButton("取消", (dialog, which) -> {
                dialog.dismiss();
            });
            normalDialog.show();
        }
    }

    private void onClickContentLike(View v) {
        if (DefaultSharedPref.INSTANCE.get(DefaultSharedPref.Key.UserId).equals("")) {
            Toast.makeText(activity, "请先登陆", Toast.LENGTH_SHORT).show();
            return;
        }
        if (likeData.contains(detailData.getId())) {
            unlike(detailData.getId(), LikeService.LikeType.Content);
        } else {
            like(detailData.getId(), LikeService.LikeType.Content);
        }
    }

    private void setStatus(ListStatus.StatusType status) {
        ListStatus.setStatus(binding.detailStatus, status);
        binding.detailScrollView.setVisibility(status == ListStatus.StatusType.Done ? View.VISIBLE : View.GONE);
        binding.commentList.setVisibility(status == ListStatus.StatusType.Done ? View.VISIBLE : View.GONE);
    }
}
