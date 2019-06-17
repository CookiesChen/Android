package studio.xmatrix.coffee.data.repository

import android.arch.lifecycle.LiveData
import studio.xmatrix.coffee.data.common.network.AppExecutors
import studio.xmatrix.coffee.data.common.network.NetworkDirectiveResource
import studio.xmatrix.coffee.data.common.network.Resource
import studio.xmatrix.coffee.data.service.CommentService
import studio.xmatrix.coffee.data.service.resource.CommentsResource
import studio.xmatrix.coffee.data.service.resource.CommonResource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepository @Inject constructor(
    private val executors: AppExecutors,
    private val service: CommentService
) {

    fun getCommentsByContentId(id: String): LiveData<Resource<CommentsResource>> {
        return object : NetworkDirectiveResource<CommentsResource, CommentsResource>(executors) {
            override fun convertToResource(data: CommentsResource) = data
            override fun createCall() = service.getListByContentId(id)
        }.asLiveData()
    }

    fun addComment(
        contentId: String,
        fatherId: String,
        content: String,
        reply: Boolean
    ): LiveData<Resource<CommonResource>> {
        return object : NetworkDirectiveResource<CommonResource, CommonResource>(executors) {
            override fun convertToResource(data: CommonResource) = data
            override fun createCall() =
                service.add(CommentService.CommentRequestBody(contentId, fatherId, content, reply))
        }.asLiveData()
    }

    fun deleteCommentById(id: String): LiveData<Resource<CommonResource>> {
        return object : NetworkDirectiveResource<CommonResource, CommonResource>(executors) {
            override fun convertToResource(data: CommonResource) = data
            override fun createCall() = service.deleteById(id)
        }.asLiveData()
    }
}
