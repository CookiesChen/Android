package studio.xmatrix.coffee.ui.notice;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;
import com.scwang.smartrefresh.header.DeliveryHeader;
import studio.xmatrix.coffee.data.service.resource.NotificationsResource;
import studio.xmatrix.coffee.databinding.NoticeFragmentBinding;
import studio.xmatrix.coffee.inject.AppInjector;
import studio.xmatrix.coffee.inject.Injectable;
import studio.xmatrix.coffee.ui.ListStatus;
import studio.xmatrix.coffee.ui.admin.AdminActivity;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NoticeHandler implements Injectable {
    private FragmentActivity activity;
    private NoticeFragmentBinding binding;
    private NoticeAdapter adapter;


    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private NoticeViewModel viewModel;


    NoticeHandler(FragmentActivity activity, NoticeFragmentBinding binding) {
        this.activity = activity;
        this.binding = binding;
        AppInjector.Companion.inject(this);
        viewModel = ViewModelProviders.of(activity, viewModelFactory).get(NoticeViewModel.class);
        setStatus(ListStatus.StatusType.Loading);
        initView();
        refreshData();
    }

    public void refreshData() {
        viewModel.getNotice().observe(activity, resource -> {
            if (resource != null) {
                switch (resource.getStatus()) {
                    case SUCCESS:
                        List<NotificationsResource.Notification> data = Objects.requireNonNull(resource.getData()).getResource();
                        if (data != null && data.size() != 0) {
                            List<NotificationsResource.Notification> list = new ArrayList<>();
                            for (int i = 0; i < data.size(); i++) {
                                NotificationsResource.Notification item = data.get(i);
                                if (item.getData().getType().equals("reply")) {
                                    list.add(item);
                                }
                            }
                            if (list.size() == 0) {
                                setStatus(ListStatus.StatusType.Nothing);
                            } else {
                                adapter.setData(list);
                                setStatus(ListStatus.StatusType.Done);
                            }
                        } else if (resource.getData().getState().equals("not_login")){
                            setStatus(ListStatus.StatusType.NotLogin);
                        } else {
                            setStatus(ListStatus.StatusType.Nothing);
                        }
                        adapter.setData(data);
                        break;
                    case ERROR:
                        setStatus(ListStatus.StatusType.Error);
                        break;
                }
            }
        });
    }

    private void initView () {
        binding.noticeStatus.statusError.setOnClickListener(v -> refreshData());
        binding.noticeStatus.statusNothing.setOnClickListener(v -> refreshData());
        binding.noticeList.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new NoticeAdapter(activity, viewModel);
        adapter.setOnClickNotice(new NoticeAdapter.OnClickNotice() {
            @Override
            public void onClickDelete(String id) {
                viewModel.deleteNotice(id).observe(activity, resource -> {
                    if (resource != null) {
                        switch (resource.getStatus()) {
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

            @Override
            public void onClickRead(String id) {
                viewModel.readNotice(id).observe(activity, resource -> {
                    if (resource != null) {
                        switch (resource.getStatus()) {
                            case ERROR:
                                Toast.makeText(activity, "网络错误", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
            }
        });
        binding.noticeList.setAdapter(adapter);
        binding.noticeRefreshLayout.setRefreshHeader(new DeliveryHeader(activity));
        binding.noticeRefreshLayout.setOnRefreshListener(refreshLayout -> viewModel.getNotice().observe(activity, resource -> {
            if (resource != null) {
                switch (resource.getStatus()) {
                    case SUCCESS:
                        List<NotificationsResource.Notification> data = Objects.requireNonNull(resource.getData()).getResource();
                        adapter.setData(data);
                        refreshLayout.finishRefresh(true);
                        break;
                    case ERROR:
                        setStatus(ListStatus.StatusType.Error);
                        refreshLayout.finishRefresh(false);
                        break;
                }
            }
        }));
        binding.noticeStatus.statusLogin.setOnClickListener(v -> activity.startActivity(new Intent(activity, AdminActivity.class)));
    }

    private void setStatus(ListStatus.StatusType status) {
        ListStatus.setStatus(binding.noticeStatus, status);
        binding.noticeRefreshLayout.setVisibility(status == ListStatus.StatusType.Done ? View.VISIBLE : View.GONE);
    }
}
