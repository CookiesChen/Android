package studio.xmatrix.coffee.ui.square;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;
import com.scwang.smartrefresh.header.DeliveryHeader;
import studio.xmatrix.coffee.data.model.Content;
import studio.xmatrix.coffee.data.service.LikeService;
import studio.xmatrix.coffee.data.store.DefaultSharedPref;
import studio.xmatrix.coffee.databinding.SquareFragmentBinding;
import studio.xmatrix.coffee.inject.AppInjector;
import studio.xmatrix.coffee.inject.Injectable;
import studio.xmatrix.coffee.ui.ListStatus;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SquareHandler implements Injectable {
    private FragmentActivity activity;
    private SquareFragmentBinding binding;
    private SquareAdapter adapter;
    private List<String> likeData;
    private boolean hasMore = true;
    private static int EACH_PAGE = 7;
    private int currentPage = 1;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private SquareViewModel viewModel;

    SquareHandler(FragmentActivity activity, SquareFragmentBinding binding) {
        AppInjector.Companion.inject(this);
        viewModel = ViewModelProviders.of(activity, viewModelFactory).get(SquareViewModel.class);
        this.activity = activity;
        this.binding = binding;
        this.likeData = new ArrayList<>();
        setStatus(ListStatus.StatusType.Loading);
        initView();
        refreshData();
    }

    private void refreshLike() {
        viewModel.getLikes().observe(activity, res -> {
            if (res != null) {
                switch (res.getStatus()) {
                    case ERROR:
                        Toast.makeText(activity, "网络错误", Toast.LENGTH_SHORT).show();
                        break;
                    case SUCCESS:
                        List<String> data = Objects.requireNonNull(res.getData()).getResource();
                        if (data != null)  {
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

    private void initView() {
        binding.squareList.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new SquareAdapter(activity, viewModel);
        adapter.setOnClickContent(id -> {
            if (DefaultSharedPref.INSTANCE.get(DefaultSharedPref.Key.UserId).equals("")) {
                Toast.makeText(activity, "请先登陆", Toast.LENGTH_SHORT).show();
                return;
            }
            if (likeData.contains(id)) {
                unlike(id);
            } else {
                like(id);
            }
        });
        binding.squareList.setAdapter(adapter);
        // 刷新数据
        binding.refreshLayout.setOnRefreshListener(refreshLayout -> {
            currentPage = 1;
            viewModel.getPublic(currentPage, EACH_PAGE).observe(activity, resource -> {
                if (resource != null) {
                    switch (resource.getStatus()) {
                        case SUCCESS:
                            setData(Objects.requireNonNull(resource.getData()).getResource());
                            refreshLayout.finishRefresh(200);
                            break;
                        case ERROR:
                            refreshLayout.finishRefresh(false);
                            setStatus(ListStatus.StatusType.Error);
                            break;
                    }
                }
            });
        });
        binding.squareStatus.statusError.setOnClickListener(v -> refreshData());

        // 加载更多数据
        binding.refreshLayout.setEnableLoadMore(true);
        binding.refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            if (!hasMore) {
                refreshLayout.setEnableLoadMore(false);
            } else {
                currentPage++;
                viewModel.getPublic(currentPage, EACH_PAGE).observe(activity, resource -> {
                    if (resource != null) {
                        switch (resource.getStatus()) {
                            case SUCCESS:
                                setData(Objects.requireNonNull(resource.getData()).getResource());
                                refreshLayout.finishLoadMore(200);
                                break;
                            case ERROR:
                                refreshLayout.finishLoadMore(false);
                                setStatus(ListStatus.StatusType.Error);
                                break;
                        }
                    }
                });
            }
        });
        binding.refreshLayout.setRefreshHeader(new DeliveryHeader(activity));

    }

    public void refreshData() {
        refreshLike();
        viewModel.getPublic(currentPage, EACH_PAGE).observe(activity, resource -> {
            if (resource != null) {
                switch (resource.getStatus()) {
                    case SUCCESS:
                        setData(Objects.requireNonNull(resource.getData()).getResource());
                        break;
                    case ERROR:
                        setStatus(ListStatus.StatusType.Error);
                        break;
                }
            }
        });
    }

    private void setData(List<Content> contents) {
        if (currentPage == 1) {
            adapter.setData(contents);
        } else {
            adapter.addData(contents);
        }
        if (contents == null || contents.size() != EACH_PAGE) {
            this.hasMore = false;
            binding.refreshLayout.setEnableLoadMore(false);
            if (contents == null && currentPage == 1)  {
                setStatus(ListStatus.StatusType.Nothing);
            } else {
                setStatus(ListStatus.StatusType.Done);
            }
        } else {
            this.hasMore = true;
            setStatus(ListStatus.StatusType.Done);
        }
        binding.refreshLayout.setEnableLoadMore(this.hasMore);
    }

    private void setStatus(ListStatus.StatusType status) {
        ListStatus.setStatus(binding.squareStatus, status);
        binding.refreshLayout.setVisibility(status == ListStatus.StatusType.Done ?  View.VISIBLE : View.GONE);
    }
}
