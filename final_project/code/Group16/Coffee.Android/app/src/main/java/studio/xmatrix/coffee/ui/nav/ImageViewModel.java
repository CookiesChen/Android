package studio.xmatrix.coffee.ui.nav;

import android.arch.lifecycle.LiveData;
import android.graphics.Bitmap;
import studio.xmatrix.coffee.data.common.network.Resource;

public interface ImageViewModel {

    LiveData<Resource<Bitmap>> getThumb(String file);

    LiveData<Resource<Bitmap>> getImage(String id, String path);

}
