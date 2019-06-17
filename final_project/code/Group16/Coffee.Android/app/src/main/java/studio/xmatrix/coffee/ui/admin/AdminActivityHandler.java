package studio.xmatrix.coffee.ui.admin;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.widget.Toast;
import studio.xmatrix.coffee.R;
import studio.xmatrix.coffee.data.common.network.Status;
import studio.xmatrix.coffee.databinding.AdminActivityBinding;
import studio.xmatrix.coffee.inject.AppInjector;
import studio.xmatrix.coffee.inject.Injectable;

import javax.inject.Inject;
import java.util.Objects;

import static studio.xmatrix.coffee.data.service.resource.CommonResource.*;

public class AdminActivityHandler implements Injectable {

    private AdminActivity activity;
    private AdminActivityBinding binding;
    private boolean passwordVisibility;

    private static class errMsg{
        static String nullUsername = "请输入用户名";
        static String nullPassword = "请输入密码";
        static String errNetwork = "请检查网络连接";
        static String loginFail = "用户名或密码错误";
    }

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private AdminViewModel viewModel;

    AdminActivityHandler(AdminActivity activity, AdminActivityBinding binding){
        this.activity = activity;
        this.binding = binding;

        AppInjector.Companion.inject(this);
        viewModel = ViewModelProviders.of(activity, viewModelFactory).get(AdminViewModel.class);

        initView();
    }

    private void initView() {

        binding.loginButton.setOnClickListener(v -> loginEvent());
        binding.loginButton.getBackground().setAlpha(50);
        binding.loginButtonCardView.getBackground().setAlpha(50);
        binding.linearLayout.getBackground().setAlpha(210);

        binding.loginForget.setClickable(true);
        SpannableString ss = new SpannableString(binding.loginForget.getText().toString());
        ss.setSpan(new URLSpan("https://oauth.xmatrix.studio/reset"),0,binding.loginForget.getText().length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(Color.WHITE), 0,binding.loginForget.getText().length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.loginForget.setText(ss);

        binding.loginForget.setMovementMethod(LinkMovementMethod.getInstance());

        binding.loginSignup.setClickable(true);
        binding.loginSignup.setOnClickListener(v -> gotoSignUp());
        passwordVisibility = false;
        binding.loginPasswordVisibility.setOnClickListener(v -> handlerPasswordVisibility());

        binding.loginCardView.getBackground().setAlpha(50);
    }

    // 登录
    private void loginEvent() {
        String username = binding.loginUsername.getText().toString();
        String password = binding.loginPassword.getText().toString();
        if (username.length() == 0){
            Toast.makeText(activity, errMsg.nullUsername, Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() == 0){
            Toast.makeText(activity, errMsg.nullPassword, Toast.LENGTH_SHORT).show();
            return;
        }
        viewModel.login(username, password).observe(activity, res -> {
            assert res != null;
            if (res.getStatus() == Status.SUCCESS){
                switch (Objects.requireNonNull(res.getData()).getState()){
                    case StatusSuccess:
                        activity.finish();
                        break;
                    case StatusError:
                        Toast.makeText(activity, errMsg.loginFail, Toast.LENGTH_SHORT).show();
                        break;
                    case StatusNotValid:
                        Intent intent = new Intent(activity.getBaseContext(), ValidActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("email", res.getData().getResource());
                        intent.putExtras(bundle);
                        activity.startActivity(intent);
                }
            } else if (res.getStatus() == Status.ERROR){
                Toast.makeText(activity, errMsg.errNetwork, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 跳转注册
    private void gotoSignUp(){
        Intent intent = new Intent(activity.getBaseContext(), SignUpActivity.class);
        activity.startActivity(intent);
    }

    private void handlerPasswordVisibility() {
        passwordVisibility = !passwordVisibility;
        if (passwordVisibility){
            binding.loginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            binding.loginPasswordVisibility.setImageResource(R.drawable.ic_password_visible);
        } else {
            binding.loginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            binding.loginPasswordVisibility.setImageResource(R.drawable.ic_password_invisible);
        }
        binding.loginPassword.setSelection(binding.loginPassword.getText().length());
    }
}
