package studio.xmatrix.coffee.data.repository

import android.arch.lifecycle.LiveData
import studio.xmatrix.coffee.data.common.network.AppExecutors
import studio.xmatrix.coffee.data.common.network.NetworkDirectiveResource
import studio.xmatrix.coffee.data.common.network.Resource
import studio.xmatrix.coffee.data.service.NotificationService
import studio.xmatrix.coffee.data.service.resource.CommonResource
import studio.xmatrix.coffee.data.service.resource.NotificationCountResource
import studio.xmatrix.coffee.data.service.resource.NotificationsResource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
data class NotificationRepository @Inject constructor(
    private val executors: AppExecutors,
    private val service: NotificationService
) {

    fun readNotificationById(id: String): LiveData<Resource<CommonResource>> {
        return object : NetworkDirectiveResource<CommonResource, CommonResource>(executors) {
            override fun convertToResource(data: CommonResource) = data
            override fun createCall() = service.readOrUnreadById(id, NotificationService.ReadRequestBody(true))
        }.asLiveData()
    }

    fun unreadNotificationById(id: String): LiveData<Resource<CommonResource>> {
        return object : NetworkDirectiveResource<CommonResource, CommonResource>(executors) {
            override fun convertToResource(data: CommonResource) = data
            override fun createCall() = service.readOrUnreadById(id, NotificationService.ReadRequestBody(false))
        }.asLiveData()
    }

    fun deleteNotificationById(id: String): LiveData<Resource<CommonResource>> {
        return object : NetworkDirectiveResource<CommonResource, CommonResource>(executors) {
            override fun convertToResource(data: CommonResource) = data
            override fun createCall() = service.deleteById(id)
        }.asLiveData()
    }

    fun getUnreadNotificationsCount(): LiveData<Resource<NotificationCountResource>> {
        return object : NetworkDirectiveResource<NotificationCountResource, CommonResource>(executors) {
            override fun convertToResource(data: CommonResource): NotificationCountResource {
                if (data.state != CommonResource.StatusSuccess) {
                    return NotificationCountResource(data.state, 0)
                }
                return NotificationCountResource(data.state, data.resource.toIntOrNull() ?: 0)
            }

            override fun createCall() = service.getUnreadCount()
        }.asLiveData()
    }

    fun getNotifications(): LiveData<Resource<NotificationsResource>> {
        return object : NetworkDirectiveResource<NotificationsResource, NotificationsResource>(executors) {
            override fun convertToResource(data: NotificationsResource) = data
            override fun createCall() = service.getList()
        }.asLiveData()
    }
}
