package studio.xmatrix.coffee.ui.person;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
import studio.xmatrix.coffee.R;
import studio.xmatrix.coffee.data.common.network.Status;
import studio.xmatrix.coffee.databinding.PersonActivityBinding;
import studio.xmatrix.coffee.inject.AppInjector;
import studio.xmatrix.coffee.inject.Injectable;
import studio.xmatrix.coffee.ui.AvatarImageBehavior;
import studio.xmatrix.coffee.ui.notice.NoticeViewModel;

import javax.inject.Inject;

import java.net.URL;
import java.util.Objects;

import static studio.xmatrix.coffee.data.service.resource.CommonResource.StatusSuccess;
import static studio.xmatrix.coffee.ui.AvatarImageBehavior.startAlphaAnimation;

public class PersonHandler implements AppBarLayout.OnOffsetChangedListener, Injectable {
    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.6f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;
    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    private PersonActivity activity;
    private PersonActivityBinding binding;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private PersonViewModel viewModel;

    PersonHandler(PersonActivity activity, PersonActivityBinding binding) {
        this.activity = activity;
        this.binding = binding;
        AppInjector.Companion.inject(this);
        viewModel = ViewModelProviders.of(activity, viewModelFactory).get(PersonViewModel.class);
        initView();
    }

    @SuppressLint("DefaultLocale")
    private void initView() {
        Toolbar toolbar = binding.personToolbar;
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setTitle("");
        activity.setSupportActionBar(toolbar);

        viewModel.getInfo().observe(activity, res ->{
            assert res != null;
            if (res.getStatus() == Status.SUCCESS){
                switch (Objects.requireNonNull(res.getData()).getState()){
                    case StatusSuccess:
                        String bio = Objects.requireNonNull(Objects.requireNonNull(res.getData()).getResource()).getBio();
                        String name = Objects.requireNonNull(Objects.requireNonNull(res.getData()).getResource()).getName();
                        float maxSize = Objects.requireNonNull(Objects.requireNonNull(res.getData()).getResource()).getMaxSize() / 1024 / 1024;
                        float usedSize =  Objects.requireNonNull(Objects.requireNonNull(res.getData()).getResource()).getUsedSize() / 1024 / 1024;
                        String uri = Objects.requireNonNull(Objects.requireNonNull(res.getData()).getResource()).getAvatar();

                        viewModel.getAvatarByUrl(uri).observe(activity, resource ->{
                            assert resource != null;
                            if (resource.getStatus() == Status.SUCCESS){
                                Bitmap bitmap = resource.getData();
                                binding.userBigAvatar.setImageBitmap(bitmap);
                            } else if (res.getStatus() == Status.ERROR){
                                Toast.makeText(activity, "请检查网络连接", Toast.LENGTH_SHORT).show();
                            }
                        });

                        binding.personTitle.setText(name);
                        binding.personBio.setText(bio);
                        binding.personName.setText(String.format("昵称: %s", name));
                        binding.personTextCap.setText(String.format("我的空间：%.1fMB/%.1fMB", usedSize, maxSize));
                        binding.circularFillableLoaders.setProgress(Math.round((maxSize-usedSize)/maxSize * 100));
                        break;
                }
            } else if (res.getStatus() == Status.ERROR){
                Toast.makeText(activity, "请检查网络连接", Toast.LENGTH_SHORT).show();
            }
        });

        binding.personNameEdit.setOnClickListener(v -> {
            NameDialog dialog = new NameDialog(activity, binding.personTitle.getText().toString(), myListener);
            dialog.setCancelable(true);
            Objects.requireNonNull(dialog.getWindow()).setGravity(Gravity.CENTER);
            dialog.show();
        });

        binding.personAppBar.addOnOffsetChangedListener(this);
        startAlphaAnimation(binding.personToolbarTitle, 0, View.INVISIBLE);
    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(binding.personLinear, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }
        } else if (!mIsTheTitleContainerVisible) {
            startAlphaAnimation(binding.personLinear, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
            mIsTheTitleContainerVisible = true;
        }

        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
            if (!mIsTheTitleVisible) {
                startAlphaAnimation(binding.personToolbarTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }
        } else if (mIsTheTitleVisible) {
            startAlphaAnimation(binding.personToolbarTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
            mIsTheTitleVisible = false;
        }
    }

    interface MyInterface {
        void changName(String s);
    }

    private MyInterface myListener = new MyInterface(){

        @Override
        public void changName(String s) {
            viewModel.updateUserName(s).observe(activity, res->{
                assert res != null;
                if (res.getStatus() == Status.SUCCESS){
                    switch (Objects.requireNonNull(res.getData()).getState()){
                        case StatusSuccess:
                            Toast.makeText(activity, "已修改" , Toast.LENGTH_SHORT).show();
                            break;
                    }
                } else if (res.getStatus() == Status.ERROR){
                    Toast.makeText(activity, "请检查网络连接", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

}
