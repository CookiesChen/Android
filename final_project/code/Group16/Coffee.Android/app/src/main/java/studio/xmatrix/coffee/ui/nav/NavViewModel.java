package studio.xmatrix.coffee.ui.nav;

import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.graphics.Bitmap;
import studio.xmatrix.coffee.App;
import studio.xmatrix.coffee.data.common.network.Resource;
import studio.xmatrix.coffee.data.repository.ContentRepository;
import studio.xmatrix.coffee.data.repository.UserRepository;
import studio.xmatrix.coffee.data.service.resource.CommonResource;
import studio.xmatrix.coffee.data.service.resource.UserResource;

import javax.inject.Inject;

public class NavViewModel extends AndroidViewModel implements ImageViewModel {

    private UserRepository userRepository;
    private ContentRepository contentRepository;

    @Inject
    NavViewModel(App app, UserRepository userRepository, ContentRepository contentRepository) {
        super(app);
        this.userRepository = userRepository;
        this.contentRepository = contentRepository;
    }

    LiveData<Resource<UserResource>> getUserInfo() {
        return userRepository.getInfo();
    }

    LiveData<Resource<Bitmap>> getUserAvatar(String url) {
        return userRepository.getAvatarByUrl(url);
    }

    LiveData<Resource<CommonResource>> logout() {
        return userRepository.logout();
    }

    public LiveData<Resource<Bitmap>> getThumb(String file) {
        return contentRepository.getThumbByFilename(file);
    }

    public LiveData<Resource<Bitmap>> getImage(String id, String path) { return contentRepository.getImageByIdAndPath(id, path); }

}
