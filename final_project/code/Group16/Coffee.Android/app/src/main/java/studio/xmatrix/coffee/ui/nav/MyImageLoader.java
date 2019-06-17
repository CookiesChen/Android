package studio.xmatrix.coffee.ui.nav;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.SupportActivity;
import android.widget.ImageView;
import android.widget.Toast;
import com.lzy.ninegrid.NineGridView;
import studio.xmatrix.coffee.R;
import studio.xmatrix.coffee.data.common.network.Status;

public class MyImageLoader implements NineGridView.ImageLoader {
    private ImageViewModel viewModel;
    private SupportActivity activity;

    public MyImageLoader(SupportActivity activity, ImageViewModel viewModel) {
        this.viewModel = viewModel;
        this.activity = activity;
    }
    @Override
    public void onDisplayImage(Context context, ImageView imageView, String url) {
        String[] data = url.split("@");
        if (data.length == 2) {
            String path = data[0];
            path = path.replace('/', '|');
            viewModel.getImage(data[1], path).observe(activity, res -> {
                if (res != null) {
                    switch (res.getStatus()) {
                        case SUCCESS:
                            imageView.setImageBitmap(res.getData());
                            break;
                        case ERROR:
                            Toast.makeText(context, res.getMessage(), Toast.LENGTH_SHORT).show();
                            break;
                    }
                }

            });
        } else if (data.length == 1) {
            viewModel.getThumb(data[0]).observe(activity, res -> {
                if (res != null && res.getStatus() == Status.SUCCESS) {
                    imageView.setImageBitmap(res.getData());
                }
            });
        } else {
            imageView.setImageDrawable(activity.getDrawable(R.drawable.ic_like));
        }
    }
    @Override
    public Bitmap getCacheImage(String url) {
        return null;
    }
}