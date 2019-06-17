package studio.xmatrix.coffee.ui.home;

import android.support.v4.app.SupportActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;
import com.scwang.smartrefresh.header.DeliveryHeader;
import studio.xmatrix.coffee.data.common.network.Resource;
import studio.xmatrix.coffee.data.model.Content;
import studio.xmatrix.coffee.data.service.LikeService;
import studio.xmatrix.coffee.data.service.resource.ContentsResource;
import studio.xmatrix.coffee.data.store.DefaultSharedPref;
import studio.xmatrix.coffee.databinding.HomeContentBinding;
import studio.xmatrix.coffee.ui.ListStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class HomeListManger {
    private SupportActivity activity;
    private HomeContentBinding binding;
    private HomeViewModel viewModel;
    private HomeAdapter adapter;
    private List<String> likeData = new ArrayList<>();
    private List<Content> tempData = new ArrayList<>();
    private String id = "self";
    private AtomicBoolean finish = new AtomicBoolean(false);

    public HomeListManger(SupportActivity activity, HomeContentBinding contentBinding, HomeViewModel viewModel) {
        this.activity = activity;
        this.binding = contentBinding;
        this.viewModel = viewModel;
        initView();
    }

    public void setId(String id) {
        this.id = id;
        setStatus(ListStatus.StatusType.Loading);
        refreshData();
    }

    private void initView() {
        binding.homeList.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new HomeAdapter(activity);
        adapter.setOnClickHome(id -> {
            if (likeData.contains(id)) {
                unlike(id);
            } else {
                like(id);
            }
        });
        binding.homeList.setAdapter(adapter);

        binding.homeRefreshLayout.setOnRefreshListener(refreshLayout -> {
            finish.set(false);
            switch (id) {
                case "self":
                    viewModel.getSelfText().observe(activity, this::setData);
                    viewModel.getSelfAlbum().observe(activity, this::setData);
                    refreshLayout.finishRefresh(200);
                    break;
                case "":
                    setStatus(ListStatus.StatusType.NotLogin);
                    break;
                default:
                    viewModel.getText(id).observe(activity, this::setData);
                    viewModel.getAlbum(id).observe(activity, this::setData);
                    refreshLayout.finishRefresh(200);
                    break;
            }
        });

        binding.homeRefreshLayout.setRefreshHeader(new DeliveryHeader(activity));
    }

    private void setData(Resource<ContentsResource> res) {
        if (res != null) {
            switch (res.getStatus()) {
                case SUCCESS:
                    List<Content> data = Objects.requireNonNull(res.getData()).getResource();
                    if (finish.compareAndSet(false, true)) { // 第一次请求
                        tempData.clear();
                        if (data != null && data.size() != 0) {
                            tempData.addAll(data);
                        }
                    } else if (finish.compareAndSet(true, false)) { // 第二次请求
                        if (data != null && data.size() != 0) {
                            tempData.addAll(data);
                        }
                        if (tempData.size() == 0) {
                            setStatus(ListStatus.StatusType.Nothing);
                        } else {
                            adapter.setData(tempData);
                            setStatus(ListStatus.StatusType.Done);
                        }
                    }
                    break;
                case ERROR:
                    setStatus(ListStatus.StatusType.Error);
                    break;
            }
        }
    }

    public void refreshData() {
        refreshLike();
        finish.set(false);
        switch (id) {
            case "self":
                viewModel.getSelfText().observe(activity, this::setData);
                viewModel.getSelfAlbum().observe(activity, this::setData);
                break;
            case "":
                setStatus(ListStatus.StatusType.NotLogin);
                break;
            default:
                viewModel.getText(id).observe(activity, this::setData);
                viewModel.getAlbum(id).observe(activity, this::setData);
                break;
        }
    }

    private void refreshLike() {
        if (DefaultSharedPref.INSTANCE.get(DefaultSharedPref.Key.UserId).equals("")) {
            return;
        }
        viewModel.getLikes().observe(activity, res -> {
            if (res != null) {
                switch (res.getStatus()) {
                    case ERROR:
                        Toast.makeText(activity, "网络错误", Toast.LENGTH_SHORT).show();
                        break;
                    case SUCCESS:
                        List<String> data = Objects.requireNonNull(res.getData()).getResource();
                        if (data != null) {
                            likeData = data;
                        } else {
                            likeData.clear();
                        }
                        adapter.setLikeData(likeData);
                        break;
                }
            }
        });

    }

    public void setStatus(ListStatus.StatusType status) {
        ListStatus.setStatus(binding.homeStatus, status);
        binding.homeRefreshLayout.setVisibility(status == ListStatus.StatusType.Done ? View.VISIBLE : View.GONE);
    }

    private void like(String id) {
        viewModel.like(id, LikeService.LikeType.Content).observe(activity, res -> {
            if (res != null) {
                switch (res.getStatus()) {
                    case ERROR:
                        Toast.makeText(activity, "网络错误", Toast.LENGTH_SHORT).show();
                        break;
                    case SUCCESS:
                        refreshData();
                        break;
                }
            }
        });
    }

    private void unlike(String id) {
        viewModel.unlike(id, LikeService.LikeType.Content).observe(activity, res -> {
            if (res != null) {
                switch (res.getStatus()) {
                    case ERROR:
                        Toast.makeText(activity, "网络错误", Toast.LENGTH_SHORT).show();
                        break;
                    case SUCCESS:
                        refreshData();
                        break;
                }
            }
        });
    }
}
