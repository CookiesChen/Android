package studio.xmatrix.coffee.ui.home;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.SpacingItemDecoration;
import com.github.vipulasri.timelineview.TimelineView;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import io.objectbox.relation.ToMany;
import studio.xmatrix.coffee.R;
import studio.xmatrix.coffee.data.common.network.Status;
import studio.xmatrix.coffee.data.model.Album;
import studio.xmatrix.coffee.data.model.Content;
import studio.xmatrix.coffee.data.model.Image;
import studio.xmatrix.coffee.databinding.HomeItemBinding;
import studio.xmatrix.coffee.ui.detail.DetailActivity;
import studio.xmatrix.coffee.ui.square.TagAdapter;

import java.util.ArrayList;
import java.util.List;

import static studio.xmatrix.coffee.ui.detail.DetailHandler.getTime;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    private Activity activity;
    private List<Content> data;
    private List<String> likeData;
    private OnClickHome onClickHome;

    void setOnClickHome(OnClickHome onClickHome) {
        this.onClickHome = onClickHome;
    }

    public interface OnClickHome {
        void onClickLike(String id);
    }


    void setData(List<Content> data) {
        if (data == null) {
            this.data.clear();
        } else {
            data.sort((o1, o2) -> Long.compare(o2.getPublishDate(), o1.getPublishDate()));
            this.data = data;
        }
        notifyDataSetChanged();
    }

    public HomeAdapter(Activity activity) {
        this.activity = activity;
        this.data = new ArrayList<>();
        this.likeData = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, getItemCount());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(i);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        HomeItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.home_item, parent, false);
        return new ViewHolder(binding, viewType);
    }

    void setLikeData(List<String> likeData) {
        if (likeData != null) {
            this.likeData = likeData;
        } else {
            this.likeData.clear();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TimelineView mTimelineView;
        HomeItemBinding binding;
        private TagAdapter adapter;

        ViewHolder(HomeItemBinding binding, int viewType) {
            super(binding.getRoot());
            mTimelineView = binding.timeline;
            this.binding = binding;
            mTimelineView.initLine(viewType);
            if (viewType == 1 || viewType == 3) {
                binding.homeFather.setPadding(0, 30, 0, 0);
            }
        }

        public void bind(int pos) {
            Content itemData = data.get(pos);

            // Tag列表
            if (adapter == null) {
                adapter = new TagAdapter(activity);
                ChipsLayoutManager chipsLayoutManager = ChipsLayoutManager.newBuilder(activity)
                        .setChildGravity(Gravity.TOP)
                        .setScrollingEnabled(true)
                        .setGravityResolver(position -> Gravity.START)
                        .setOrientation(ChipsLayoutManager.HORIZONTAL)
                        .build();
                binding.homeTagLayout.setLayoutManager(chipsLayoutManager);
                binding.homeTagLayout.addItemDecoration(new SpacingItemDecoration(10, 10));
                binding.homeText.setMaxLines(3);
            }
            binding.homeTagLayout.setAdapter(adapter);
            adapter.setData(itemData.getTags());

            // 图片列表
            ArrayList<ImageInfo> imageInfo = new ArrayList<>();
            Album album = itemData.getAlbum().getTarget();
            if (album != null) {
                ToMany<Image> images = album.getImages();
                for (Image i : images) {
                    ImageInfo info = new ImageInfo();
                    info.setThumbnailUrl(i.getThumb());
                    info.setBigImageUrl(i.getThumb());
                    // info.setBigImageUrl(i.getFile().getTarget().getFile() + "@" + itemData.getId());
                    imageInfo.add(info);
                }
            }
            binding.homeNineGridImage.setAdapter(new NineGridViewClickAdapter(activity, imageInfo));


            binding.homeClickLayout.setOnClickListener(v -> gotoDetail(itemData.getId()));
            binding.homeComment.setOnClickListener(v ->gotoDetail(itemData.getId()));
            binding.homeLike.setOnClickListener(v -> onClickHome.onClickLike(itemData.getId()));
            binding.homeTime.setText(getTime(itemData.getPublishDate()));
            binding.setModel(itemData);

            if (likeData.contains(itemData.getId())) {
                binding.homeLike.setImageResource(R.drawable.ic_like);
            } else {
                binding.homeLike.setImageResource(R.drawable.ic_like_none);
            }
        }

        private void gotoDetail(String id) {
            DetailActivity.start(activity, id, ActivityOptionsCompat
                    .makeSceneTransitionAnimation(
                            activity,
                            Pair.create(binding.homeCard, "contentCard")
                    ).toBundle());
        }

    }
}
