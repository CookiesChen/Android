package studio.xmatrix.coffee.ui.admin;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Toast;
import studio.xmatrix.coffee.R;
import studio.xmatrix.coffee.data.common.network.Status;
import studio.xmatrix.coffee.databinding.SignupActivityBinding;
import studio.xmatrix.coffee.inject.AppInjector;
import studio.xmatrix.coffee.inject.Injectable;

import javax.inject.Inject;
import java.util.Objects;

import static studio.xmatrix.coffee.data.service.resource.CommonResource.*;

public class SignUpActivityHandler implements Injectable {

    private SignUpActivity activity;
    private SignupActivityBinding binding;
    private boolean passwordVisibility;
    private boolean confirmVisibility;

    private static class errMsg{
        static String nullEmail = "请输入邮箱";
        static String nullUsername = "请输入用户名";
        static String nullPassword = "请输入密码";
        static String errConfirm = "两次输入密码不一致";
        static String errNetwork = "请检查网络连接";
        static String invalidEmail = "邮箱格式错误";
        static String invalidUsername = "用户名格式错误";
        static String existUsername = "用户名已存在";
        static String existEmail = "邮箱已存在";
    }

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    AdminViewModel viewModel;

    SignUpActivityHandler(SignUpActivity activity, SignupActivityBinding binding){
        this.activity = activity;
        this.binding = binding;

        AppInjector.Companion.inject(this);
        viewModel = ViewModelProviders.of(activity, viewModelFactory).get(AdminViewModel.class);

        initView();
    }

    private void initView() {
        binding.signupButton.setOnClickListener(v -> signupEvent());
        binding.signupButton.getBackground().setAlpha(50);
        binding.signupButtonCardView.getBackground().setAlpha(50);
        binding.linearLayout.getBackground().setAlpha(210);

        passwordVisibility = false;
        confirmVisibility = false;
        binding.signupPasswordVisibility.setOnClickListener(v -> handlerPasswordVisibility());
        binding.signupConfirmVisibility.setOnClickListener(v -> handlerConfirmVisibility());

        binding.signupCardView.getBackground().setAlpha(50);
    }

    private void signupEvent() {
        String email = binding.signupEmail.getText().toString();
        String username = binding.signupUsername.getText().toString();
        String password = binding.signupPassword.getText().toString();
        String confirm = binding.signupConfirm.getText().toString();
        // 正则表达式
//        String emailReg = "/^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$/";
//        String usernameReg = "/^[a-zA-Z][a-zA-Z0-9_]{0,31}$/";

        if (email.length() == 0){
            Toast.makeText(activity, errMsg.nullEmail, Toast.LENGTH_SHORT).show();
            return;
        }
//        else if(!email.matches(emailReg)){
//            Toast.makeText(activity, errMsg.invalidEmail, Toast.LENGTH_SHORT).show();
//            return;
//        }
        if (username.length() == 0){
            Toast.makeText(activity, errMsg.nullUsername, Toast.LENGTH_SHORT).show();
            return;
        }
//        else if (!username.matches(usernameReg)){
//            Toast.makeText(activity, errMsg.invalidUsername, Toast.LENGTH_SHORT).show();
//            return;
//        }
        if (password.length() == 0){
            Toast.makeText(activity, errMsg.nullPassword, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirm)){
            Toast.makeText(activity, errMsg.errConfirm, Toast.LENGTH_SHORT).show();
            return;
        }
        viewModel.register(username, email, password).observe(activity, res -> {
            assert res != null;
            if (res.getStatus() == Status.SUCCESS){
                switch (Objects.requireNonNull(res.getData()).getState()){
                    case StatusNotValidUsername:
                    case StatusReservedUsername:
                        Toast.makeText(activity, errMsg.invalidUsername, Toast.LENGTH_SHORT).show();
                        break;
                    case StatusNotValidEmail:
                        Toast.makeText(activity, errMsg.invalidEmail, Toast.LENGTH_SHORT).show();
                        break;
                    case StatusExistUsername:
                        Toast.makeText(activity, errMsg.existUsername, Toast.LENGTH_SHORT).show();
                        break;
                    case StatusExistEmail:
                        Toast.makeText(activity, errMsg.existEmail, Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Intent intent = new Intent(activity.getBaseContext(), ValidActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("email", email);
                        intent.putExtras(bundle);
                        activity.startActivity(intent);
                }
            } else if (res.getStatus() == Status.ERROR){
                Toast.makeText(activity, errMsg.errNetwork, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handlerPasswordVisibility() {
        passwordVisibility = !passwordVisibility;
        if (passwordVisibility){
            binding.signupPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            binding.signupPasswordVisibility.setImageResource(R.drawable.ic_password_visible);
        } else {
            binding.signupPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            binding.signupPasswordVisibility.setImageResource(R.drawable.ic_password_invisible);
        }
        binding.signupPassword.setSelection(binding.signupPassword.getText().length());
    }

    private void handlerConfirmVisibility() {
        confirmVisibility = !confirmVisibility;
        if (confirmVisibility){
            binding.signupConfirm.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            binding.signupConfirmVisibility.setImageResource(R.drawable.ic_password_visible);
        } else {
            binding.signupConfirm.setTransformationMethod(PasswordTransformationMethod.getInstance());
            binding.signupConfirmVisibility.setImageResource(R.drawable.ic_password_invisible);
        }
        binding.signupConfirm.setSelection(binding.signupConfirm.getText().length());
    }
}
