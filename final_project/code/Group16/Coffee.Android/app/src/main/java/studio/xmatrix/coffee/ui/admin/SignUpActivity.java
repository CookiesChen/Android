package studio.xmatrix.coffee.ui.admin;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import studio.xmatrix.coffee.R;
import studio.xmatrix.coffee.databinding.SignupActivityBinding;

import javax.annotation.Nullable;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    SignupActivityBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        this.binding = DataBindingUtil.setContentView(this, R.layout.signup_activity);
        binding.setHandler(new SignUpActivityHandler(this, binding));
    }
}
