# Coffee Inject Example

Coffee.Android中使用了Dagger 2实现类与类之间的依赖注入，本文将介绍本项目中的依赖注入技术和简单的使用方法。

> 由于本人对Dagger 2理解不深，本项目中技术的用法可能不是最优雅。

## 为什么不用Android Injection和Android Injector？

`AndroidInjection`、`AndroidInjector`是Dagger 2提供的Android Activity依赖注入的两个支持类，可以大大减少依赖注入的架构代码。

不过，Coffee.Android的UI架构是`Activity`-`Handler`-`DataBinding`架构，由于逻辑代码被设计在Handler中，这意味着依赖注入的入口在于Handler层，而不是Activity层。因此我们不用AndroidInjection和AndroidInjector，而是自己搭建架构。

## 如何进行依赖注入？

在Coffee.Android的注入架构中，存在一个`Injectable`接口用于标识可注入。

```kotlin
// Kotlin
interface Injectable
```

如果我们需要对类MainHandler进行依赖注入类A，假设有如下的代码。

```java
// Java
class A {
    private int value;
    
    @Inject
    A() { this.value = 3 }
    A(int value) { this.value = value; }
}
```

首先，我们需要将MainHandler声明为可依赖注入。

```java
// Java
class MainHandler implements Injectable {
    @Inject
    A a;
}
```

其次，在`AppComponent`中实现依赖注入接口，因为依赖注入不具有继承性质，所以必须指定需要依赖的类型。

```kotlin
// Kotlin
interface AppComponent {
    fun inject(injectable: MainHandler)
}
```

然后，在`AppInjector`中调用该依赖注入接口。

```kotlin
// Kotlin
class AppInjector private constructor() {
    companion object {
        fun inject(injectable: Injectable) {
            when (injectable) {
                is MainHandler -> component.inject(injectable)
                is AnotherHandler -> component.inject(injectable)
                is ThirdHandler -> component.inject(injectable)
                else -> throw IllegalArgumentException("Class not found in AppComponent")
            }
        }
    }
}
```

接下来，在需要依赖注入的类MainHandler的构造函数中进行依赖注入。

```java
// Java
class MainHandler implements Injectable {
    @Inject
    A a;
    
    MainHandler() {
        AppInjector.Companion.inject(this);
    }
}
```

依赖注入结束后，MainHandler所有被标识为`@Inject`的成员均被注入。

## 特殊的依赖注入：Retrofit Interface

依赖注入的过程是对某个类进行`AppInjector.Companion.inject(this)`注入后，将该类的所有被标记为`@Inject`的成员执行注入操作。其中，会寻找这些成员对应的类被`@Inject`修饰的构造函数，然后对该构造函数的每一个参数进行依赖注入。嵌套循环，直到最终没有参数注入为止。

但是，接口是没有构造函数的，因此需要借助`@Module`、`@Provides`来辅助注入。

在Coffee.Android中，Retrofit的Service都是接口，因此需要辅助注入，比如`UserService`，有如下的注入方式。

```kotlin
// Kotlin
@Module
class ServiceModule {
    @Singleton
    @Provides
    fun provideUserService(app: App): UserService {
        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(UserService.BASE_URL)
            .build()
        return retrofit.create(UserService::class.java)
    }
}
```

## 特殊的依赖注入：ViewModel

在Coffee.Android中，通常我们仅对UI架构的Handler进行依赖注入，并且通常仅注入继承于`ViewModel`或者`AndroidViewModel`的ViewModel类。

但是这两个类的子类的对象都是需要通过工厂类生成的，并且工厂类也是单独针对唯一一个ViewModel类的，因此在依赖注入架构中，针对工厂类提供了Provider。

我们注入一个ViewModel的方式如下。

首先，需要注入ViewModel的工厂类，其次使用该工厂类来生成ViewModel。

```java
// Java
class MainHandler implements Injectable {
    
    private MainActivity activity;
    
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    MainViewModel viewModel;
    
    MainHandler(MainActivity activity) {
        AppInjector.Companion.inject(this);
        
        this.activity = activity;
        viewModel = ViewModelProviders.of(activity, viewModelFactory).get(MainViewModel.class);
    }
}
```

