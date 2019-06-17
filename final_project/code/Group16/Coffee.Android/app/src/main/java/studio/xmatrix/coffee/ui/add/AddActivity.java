package studio.xmatrix.coffee.ui.add;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import studio.xmatrix.coffee.R;
import studio.xmatrix.coffee.databinding.AddActivityBinding;
import studio.xmatrix.coffee.ui.BaseActionBarActivity;

import java.util.List;
import java.util.Objects;

public class AddActivity extends BaseActionBarActivity {
    AddActivityBinding binding;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.add_activity);
        AddHandler handler = new AddHandler(this, binding);
        binding.setHandler(handler);
        Objects.requireNonNull(getSupportActionBar()).setTitle("发布");
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_cancel);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem moreItem = menu.add(Menu.NONE, Menu.FIRST, Menu.FIRST, null);
        moreItem.setIcon(R.drawable.ic_send);
        moreItem.setOnMenuItemClickListener((MenuItem item) -> {
            binding.getHandler().send();
            return true;
        });
        moreItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    binding.getHandler().addImage(selectList);
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != 0) return;
        if (grantResults.length == 0 ||
                grantResults[0] != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
            Toast.makeText(this, "您关闭了存储权限，将无法添加图片。", Toast.LENGTH_SHORT).show();
        } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            binding.getHandler().openPicker();
        }
    }
}
