package studio.xmatrix.coffee.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import com.lzy.ninegrid.NineGridView;
import studio.xmatrix.coffee.R;
import studio.xmatrix.coffee.databinding.UserActivityBinding;
import studio.xmatrix.coffee.ui.BaseActionBarActivity;
import studio.xmatrix.coffee.ui.detail.DetailActivity;
import studio.xmatrix.coffee.ui.nav.MyImageLoader;

public class UserActivity extends BaseActionBarActivity {
    UserActivityBinding binding;
    UserHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.user_activity);
        handler = new UserHandler(this, binding);
    }

    public static void start(Activity activity, String id, Bundle bundle) {
        Intent intent = new Intent(activity, UserActivity.class);
        intent.putExtra("id", id);
        activity.startActivity(intent, bundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.setImageLoader();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (data.getBooleanExtra("update", false)) {
                handler.listManger.refreshData();
            }
        }
    }
}
