package studio.xmatrix.coffee.inject.module

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import studio.xmatrix.coffee.inject.key.ViewModelKey
import studio.xmatrix.coffee.ui.add.AddHandler
import studio.xmatrix.coffee.ui.add.AddViewModel
import studio.xmatrix.coffee.ui.admin.AdminViewModel
import studio.xmatrix.coffee.ui.common.ViewModelFactory
import studio.xmatrix.coffee.ui.detail.DetailViewModel
import studio.xmatrix.coffee.ui.home.HomeViewModel
import studio.xmatrix.coffee.ui.nav.NavViewModel
import studio.xmatrix.coffee.ui.notice.NoticeViewModel
import studio.xmatrix.coffee.ui.person.PersonViewModel
import studio.xmatrix.coffee.ui.square.SquareViewModel

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(AdminViewModel::class)
    abstract fun bindAdminViewModel(adminViewModel: AdminViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SquareViewModel::class)
    abstract fun bindSquareViewModel(squareViewModel: SquareViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DetailViewModel::class)
    abstract fun bindDetailViewModel(detailViewModel: DetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(homeViewModel: HomeViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(NoticeViewModel::class)
    abstract fun bindNoticeViewModel(noticeViewModel: NoticeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PersonViewModel::class)
    abstract fun bindPersonViewModel(personViewModel: PersonViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NavViewModel::class)
    abstract fun bindNavViewModel(navViewModel: NavViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AddViewModel::class)
    abstract fun bindAddViewModel(addViewModel: AddViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

}
