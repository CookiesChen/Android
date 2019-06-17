package studio.xmatrix.coffee.data.repository

import android.arch.lifecycle.LiveData
import studio.xmatrix.coffee.data.common.network.AppExecutors
import studio.xmatrix.coffee.data.common.network.NetworkDirectiveResource
import studio.xmatrix.coffee.data.common.network.Resource
import studio.xmatrix.coffee.data.service.LikeService
import studio.xmatrix.coffee.data.service.resource.CommonResource
import studio.xmatrix.coffee.data.service.resource.LikeResource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LikeRepository @Inject constructor(
    private val executors: AppExecutors,
    private val service: LikeService
) {

    fun getLikes(): LiveData<Resource<LikeResource>> {
        return object : NetworkDirectiveResource<LikeResource, LikeResource>(executors) {
            override fun convertToResource(data: LikeResource) = data
            override fun createCall() = service.getList()
        }.asLiveData()
    }

    fun likeById(id: String, type: LikeService.LikeType): LiveData<Resource<CommonResource>> {
        return object : NetworkDirectiveResource<CommonResource, CommonResource>(executors) {
            override fun convertToResource(data: CommonResource) = data
            override fun createCall() = service.addByObjectId(
                id,
                LikeService.LikeRequestBody(
                    type == LikeService.LikeType.Content,
                    type == LikeService.LikeType.Comment,
                    type == LikeService.LikeType.Reply
                )
            )
        }.asLiveData()
    }

    fun unlikeById(id: String, type: LikeService.LikeType): LiveData<Resource<CommonResource>> {
        return object : NetworkDirectiveResource<CommonResource, CommonResource>(executors) {
            override fun convertToResource(data: CommonResource) = data
            override fun createCall() =
                service.removeByObjectId(
                    id, LikeService.LikeRequestBody(
                        type == LikeService.LikeType.Content,
                        type == LikeService.LikeType.Comment,
                        type == LikeService.LikeType.Reply
                    )
                )
        }.asLiveData()
    }
}
