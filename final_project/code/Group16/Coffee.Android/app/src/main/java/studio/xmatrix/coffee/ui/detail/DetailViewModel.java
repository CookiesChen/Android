package studio.xmatrix.coffee.ui.detail;

import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.graphics.Bitmap;
import studio.xmatrix.coffee.App;
import studio.xmatrix.coffee.data.common.network.Resource;
import studio.xmatrix.coffee.data.repository.CommentRepository;
import studio.xmatrix.coffee.data.repository.ContentRepository;
import studio.xmatrix.coffee.data.repository.LikeRepository;
import studio.xmatrix.coffee.data.repository.UserRepository;
import studio.xmatrix.coffee.data.service.LikeService;
import studio.xmatrix.coffee.data.service.resource.CommentsResource;
import studio.xmatrix.coffee.data.service.resource.CommonResource;
import studio.xmatrix.coffee.data.service.resource.ContentResource;
import studio.xmatrix.coffee.data.service.resource.LikeResource;
import studio.xmatrix.coffee.ui.nav.ImageViewModel;

import javax.inject.Inject;

public class DetailViewModel extends AndroidViewModel implements ImageViewModel {

    private ContentRepository contentRepository;
    private CommentRepository commentRepository;
    private LikeRepository likeRepository;
    private UserRepository userRepository;

    @Inject
    DetailViewModel(App app, ContentRepository contentRepository, CommentRepository commentRepository,
                    LikeRepository likeRepository, UserRepository userRepository) {
        super(app);
        this.contentRepository = contentRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
    }

    LiveData<Resource<ContentResource>> getDetail(String id) {
        return contentRepository.getContentById(id);
    }

    LiveData<Resource<CommonResource>> deleteContent(String id) {
        return contentRepository.deleteContentById(id);
    }

    LiveData<Resource<CommentsResource>> getComments(String id) {
        return commentRepository.getCommentsByContentId(id);
    }

    LiveData<Resource<CommonResource>> addComment(String contentId, String fatherId, String content, Boolean reply) {
        return commentRepository.addComment(contentId, fatherId, content, reply);
    }

    LiveData<Resource<CommonResource>> deleteComment(String id) {
        return commentRepository.deleteCommentById(id);
    }

    LiveData<Resource<LikeResource>> getLikes() {
        return likeRepository.getLikes();
    }

    LiveData<Resource<CommonResource>> like(String id, LikeService.LikeType type) {
        return likeRepository.likeById(id, type);
    }

    LiveData<Resource<CommonResource>> unlike(String id,LikeService.LikeType type) {
        return likeRepository.unlikeById(id, type);
    }

    LiveData<Resource<Bitmap>> getUserAvatar(String url) {
        return userRepository.getAvatarByUrl(url);
    }

    public LiveData<Resource<Bitmap>> getThumb(String file) {
        return contentRepository.getThumbByFilename(file);
    }

    public LiveData<Resource<Bitmap>> getImage(String id, String path) { return contentRepository.getImageByIdAndPath(id, path); }

}
