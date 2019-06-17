package studio.xmatrix.coffee.ui.admin;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import studio.xmatrix.coffee.R;
import studio.xmatrix.coffee.data.common.network.Status;
import studio.xmatrix.coffee.data.repository.ContentRepository;
import studio.xmatrix.coffee.data.service.ContentService;
import studio.xmatrix.coffee.databinding.AdminActivityBinding;
import studio.xmatrix.coffee.inject.AppInjector;
import studio.xmatrix.coffee.inject.Injectable;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.Objects;

public class AdminActivity extends AppCompatActivity implements Injectable {

    @Inject
    ContentService service;
    @Inject
    ContentRepository repository;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        AdminActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.admin_activity);
        binding.setHandler(new AdminActivityHandler(this, binding));

        AppInjector.Companion.inject(this);
        repository.getContentById("5c2347677a2bdd00017ffc74").observe(this, res -> {
            if (res.getStatus() == Status.SUCCESS) {
                Timber.d("AAAA %s", res.getData().getState());
            }
        });
    }
}
