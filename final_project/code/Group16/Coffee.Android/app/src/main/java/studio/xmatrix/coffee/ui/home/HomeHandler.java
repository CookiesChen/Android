package studio.xmatrix.coffee.ui.home;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import studio.xmatrix.coffee.data.store.DefaultSharedPref;
import studio.xmatrix.coffee.databinding.HomeFragmentBinding;
import studio.xmatrix.coffee.inject.AppInjector;
import studio.xmatrix.coffee.inject.Injectable;
import studio.xmatrix.coffee.ui.ListStatus;
import studio.xmatrix.coffee.ui.admin.AdminActivity;

import javax.inject.Inject;

public class HomeHandler implements Injectable {
    private FragmentActivity activity;
    private HomeFragmentBinding binding;
    private HomeListManger listManger;
    private String id = "init";

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private HomeViewModel viewModel;

    HomeHandler(FragmentActivity activity, HomeFragmentBinding binding) {
        this.activity = activity;
        this.binding = binding;
        initView();
        AppInjector.Companion.inject(this);
        viewModel = ViewModelProviders.of(activity, viewModelFactory).get(HomeViewModel.class);
        listManger = new HomeListManger(activity, binding.homeContent, viewModel);
        refreshData();
    }


    public void refreshData() {
        String userId = DefaultSharedPref.INSTANCE.get(DefaultSharedPref.Key.UserId);
        if (!userId.equals("")) {
            if (!id.equals(userId)) {
                id = userId;
                listManger.setId(userId);
            } else {
                listManger.setId("self");
            }
        } else {
            listManger.setId("");
        }
    }

    private void initView() {
        binding.homeContent.homeStatus.statusLogin.setOnClickListener(v -> activity.startActivity(new Intent(activity, AdminActivity.class)));
    }


}
