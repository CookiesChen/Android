package studio.xmatrix.coffee.ui.user;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.lzy.ninegrid.NineGridView;
import studio.xmatrix.coffee.R;
import studio.xmatrix.coffee.data.common.network.Status;
import studio.xmatrix.coffee.data.model.User;
import studio.xmatrix.coffee.databinding.UserActivityBinding;
import studio.xmatrix.coffee.inject.AppInjector;
import studio.xmatrix.coffee.inject.Injectable;
import studio.xmatrix.coffee.ui.home.HomeAdapter;
import studio.xmatrix.coffee.ui.home.HomeHandler;
import studio.xmatrix.coffee.ui.home.HomeListManger;
import studio.xmatrix.coffee.ui.home.HomeViewModel;
import studio.xmatrix.coffee.ui.nav.MyImageLoader;

import javax.inject.Inject;

import java.util.Objects;

import static studio.xmatrix.coffee.ui.AvatarImageBehavior.startAlphaAnimation;

public class UserHandler implements AppBarLayout.OnOffsetChangedListener, Injectable {
    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.6f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;
    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;
    private UserActivity activity;
    private UserActivityBinding binding;
    private String id;
    public HomeListManger listManger;


    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private HomeViewModel viewModel;

    UserHandler(UserActivity activity, UserActivityBinding binding) {
        this.activity = activity;
        this.binding = binding;
        Bundle bundle = activity.getIntent().getExtras();
        this.id = Objects.requireNonNull(bundle).getString("id", "");
        if (this.id.equals("")) {
            activity.finish();
            return;
        }
        AppInjector.Companion.inject(this);
        viewModel = ViewModelProviders.of(activity, viewModelFactory).get(HomeViewModel.class);
        initView();
        listManger =  new HomeListManger(activity, binding.userInclude, viewModel);
        listManger.setId(id);
        initData();
    }

    void setImageLoader() {
        NineGridView.setImageLoader(new MyImageLoader(activity, viewModel));
    }

    private void initData() {
        viewModel.getUserInfo(id).observe(activity, res -> {
            if (res != null) {
                switch (res.getStatus()) {
                    case SUCCESS:
                        User info = Objects.requireNonNull(res.getData()).getResource();
                        if (info != null) {
                            binding.userToolbarTitle.setText(info.getName());
                            binding.userTitle.setText(info.getName());
                            binding.userBio.setText(info.getBio());
                            viewModel.getUserAvatar(info.getAvatar()).observe(activity, avatarRes -> {
                                if (avatarRes != null && avatarRes.getStatus() == Status.SUCCESS) {
                                    binding.userBigAvatar.setImageBitmap(avatarRes.getData());
                                }
                            });
                        }
                        break;
                }
            }
        });
    }

    private void initView() {
        binding.userInclude.homeList.setLayoutManager(new LinearLayoutManager(activity));
        binding.userInclude.homeList.setAdapter(new HomeAdapter(activity));

        Toolbar toolbar = binding.userToolbar;
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setTitle("");
        activity.setSupportActionBar(toolbar);

        binding.userAppBar.addOnOffsetChangedListener(this);
        startAlphaAnimation(binding.userToolbarTitle, 0, View.INVISIBLE);

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
            if (!mIsTheTitleVisible) {
                startAlphaAnimation(binding.userToolbarTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }
        } else if (mIsTheTitleVisible) {
            startAlphaAnimation(binding.userToolbarTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
            mIsTheTitleVisible = false;
        }
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(binding.userLinear, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }
        } else if (!mIsTheTitleContainerVisible) {
            startAlphaAnimation(binding.userLinear, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
            mIsTheTitleContainerVisible = true;
        }
    }
}
