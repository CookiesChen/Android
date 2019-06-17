package studio.xmatrix.coffee.inject

import studio.xmatrix.coffee.App
import studio.xmatrix.coffee.inject.component.AppComponent
import studio.xmatrix.coffee.inject.component.DaggerAppComponent
import studio.xmatrix.coffee.ui.add.AddHandler
import studio.xmatrix.coffee.ui.admin.*
import studio.xmatrix.coffee.ui.detail.DetailHandler
import studio.xmatrix.coffee.ui.square.SquareHandler
import studio.xmatrix.coffee.ui.admin.AdminActivityHandler
import studio.xmatrix.coffee.ui.home.HomeHandler
import studio.xmatrix.coffee.ui.nav.NavActivity
import studio.xmatrix.coffee.ui.notice.NoticeHandler
import studio.xmatrix.coffee.ui.person.NameDialogHandler
import studio.xmatrix.coffee.ui.person.PersonHandler
import studio.xmatrix.coffee.ui.user.UserHandler

class AppInjector private constructor() {

    companion object {

        private lateinit var component: AppComponent

        fun init(app: App) {
            component = DaggerAppComponent.builder().app(app).build()
        }

        fun inject(injectable: Injectable) {
            when (injectable) {
                is AdminActivity -> component.inject(injectable)
                is AdminActivityHandler -> component.inject(injectable)
                is SignUpActivityHandler -> component.inject(injectable)
                is ValidActivityHandler -> component.inject(injectable)
                is DetailHandler -> component.inject(injectable)
                is SquareHandler -> component.inject(injectable)
                is HomeHandler -> component.inject(injectable)
                is UserHandler -> component.inject(injectable)
                is NoticeHandler -> component.inject(injectable)
                is PersonHandler -> component.inject(injectable)
                is NameDialogHandler -> component.inject(injectable)
                is NavActivity -> component.inject(injectable)
                is AddHandler -> component.inject(injectable)
                else -> throw IllegalArgumentException("Class not found in AppComponent")
            }
        }
    }
}
