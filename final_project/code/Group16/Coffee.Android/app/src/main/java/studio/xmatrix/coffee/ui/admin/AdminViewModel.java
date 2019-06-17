package studio.xmatrix.coffee.ui.admin;

import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import studio.xmatrix.coffee.App;
import studio.xmatrix.coffee.data.common.network.Resource;
import studio.xmatrix.coffee.data.repository.UserRepository;
import studio.xmatrix.coffee.data.service.resource.CommonResource;

import javax.inject.Inject;

public class AdminViewModel extends AndroidViewModel {

    private UserRepository userRepository;

    @Inject
    AdminViewModel(App app, UserRepository userRepository) {
        super(app);
        this.userRepository = userRepository;
    }

    LiveData<Resource<CommonResource>> login(String name, String password) {
        return userRepository.login(name, password);
    }

    LiveData<Resource<CommonResource>> register(String name, String email, String password) {
        return userRepository.register(name, email, password);
    }

    LiveData<Resource<CommonResource>> getEmailValidCode() {
        return userRepository.getEmailValidCode();
    }

    LiveData<Resource<CommonResource>> validEmail(String vcode) {
        return userRepository.validEmail(vcode);
    }
}
