package studio.xmatrix.coffee.ui.notice;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.daimajia.swipe.SwipeLayout;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;
import studio.xmatrix.coffee.R;
import studio.xmatrix.coffee.data.common.network.Status;
import studio.xmatrix.coffee.data.service.resource.NotificationsResource;
import studio.xmatrix.coffee.databinding.NoticeItemBinding;
import studio.xmatrix.coffee.ui.detail.DetailActivity;
import studio.xmatrix.coffee.ui.user.UserActivity;

import java.util.ArrayList;
import java.util.List;

import static q.rorbin.badgeview.Badge.OnDragStateChangedListener.STATE_SUCCEED;
import static studio.xmatrix.coffee.ui.detail.DetailHandler.getTime;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder> {
    private FragmentActivity activity;
    private List<NotificationsResource.Notification> data;
    private OnClickNotice onClickNotice;
    private NoticeViewModel viewModel;

    public void setOnClickNotice(OnClickNotice onClickNotice) {
        this.onClickNotice = onClickNotice;
    }

    public interface OnClickNotice {
        void onClickDelete(String id);
        void onClickRead(String id);
    }

    public NoticeAdapter(FragmentActivity activity, NoticeViewModel viewModel) {
        this.activity = activity;
        this.viewModel = viewModel;
        data = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.notice_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(i);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<NotificationsResource.Notification> data) {
        if (data == null) {
            this.data.clear();
        } else {
            this.data = data;
        }
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final NoticeItemBinding binding;
        private Badge badge;

        ViewHolder(NoticeItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.badge = new QBadgeView(activity)
                    .bindTarget(binding.noticeAction)
                    .setBadgeGravity(Gravity.END | Gravity.CENTER)
                    .setShowShadow(false)
                    .setBadgeText("New");
        }

        void bind(int pos) {
            NotificationsResource.Notification item = data.get(pos);
            if (item.getData().getRead()) {
                badge.hide(false);
            } else {
                badge.setBadgeText("New");
                badge.setOnDragStateChangedListener((dragState, badge, targetView) -> {
                    if (dragState == STATE_SUCCEED) {
                        onClickNotice.onClickRead(item.getData().getId());
                    }
                });
            }
            binding.setModel(item);

            binding.noticeLayout.setOnClickListener(v -> {
                DetailActivity.start(activity, item.getData().getTargetId(), null);
            });
            binding.noticeUserAvatar.setOnClickListener(v -> goToUser(item.getData().getSourceId()));
            binding.noticeUserName.setOnClickListener(v -> goToUser(item.getData().getSourceId()));

            binding.noticeSwipe.setShowMode(SwipeLayout.ShowMode.PullOut);
            binding.noticeSwipe.addDrag(SwipeLayout.DragEdge.Right, binding.removeLayout);

            binding.noticeDelete.setOnClickListener(v -> onClickNotice.onClickDelete(item.getData().getId()));

            binding.noticeTime.setText(getTime(item.getData().getCreateTime()));

            viewModel.getUserAvatar(item.getUser().getAvatar()).observe(activity, res -> {
                if (res != null && res.getStatus() == Status.SUCCESS) {
                    binding.noticeUserAvatar.setImageBitmap(res.getData());
                }
            });
        }

        void goToUser(String userId) {
            UserActivity.start(activity, userId, ActivityOptionsCompat
                    .makeSceneTransitionAnimation(activity,
                            Pair.create(binding.noticeUserAvatar, "userAvatar"),
                            Pair.create(binding.noticeUserName, "userName"))
                    .toBundle());
        }
    }
}
