# 中山大学数据科学与计算机学院本科生实验报告
## （2018年秋季学期）
| 课程名称 | 手机平台应用开发 | 任课老师 | 郑贵锋 |
| :------------: | :-------------: | :------------: | :-------------: |
| 年级 | 16级 | 专业（方向） | 计应 |
| 学号 | 16340029 | 姓名 | 陈思琦 |
| 电话 | 13060840852 | Email | 1109349604@qq.com |
| 开始日期 | 2018.11.25 | 完成日期 |2018.11.25|

---

## 期中项目完成工作简介

本次期中项目使用`MVVM`设计模式。

首先介绍一下`Model`，数据的来源有两种，一是本地保存的数据，二是从官网获取的数据。采取这种形式的原因有两个。

* 避免没有网络时，无法获取数据，这时候就可以从本地保存的数据中获取
* 每次有网络的时候，就会去获取数据，如果数据有更新，就把数据保存到本地，这样可以实现数据的及时更新。

`ViewModel`则是沟通View与Model的桥梁，`ViewModel`使用`LiveData`类，LiveData类是一个数据持有类，数据能够被观察者订阅，只有组件在激活状态下才会通知观察者有数据更新，`LiveData`能够保证数据和UI统一，`LiveData`是被观察者，在这里View为观察者，`LiveData`的数据发生改变时，才会通知View进行更新，在这里对性能是有非常大的提升的，在第一次获取数据之后，如果数据没有更新，则第二次获取就会直接采用第一次的数据。

`View`则主要使用了数据绑定(`DataBinding`)。

以下是我们期中项目的结构图：

![1](C:\Users\cookieschen\Desktop\img1\1.png)

还有[UI设计图](C:\Users\cookieschen\Desktop\img1\王者荣耀布局初设计.mht)，需要使用ie浏览器打开。

本次期中项目我负责的是英雄详情界面的布局以及内部逻辑，主要工作有以下几点：

- 英雄详情布局
- 使用数据绑定
- 使用LiveData获取数据
- 测试数据接口

---

## 实验结果
###  页面UI

####  基本布局

在detail_activity.xml布局中引入，CoordinatorLayout作为最外层layout可以实现悬浮效果。

```xml
<android.support.design.widget.CoordinatorLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:fitsSystemWindows="true">
</android.support.design.widget.CoordinatorLayout>
```

作为最上层的View ,作为一个 容器与一个或者多个子View进行交互。

`AppBarLayout`是一种支持响应滚动手势的app bar布局，而`CollapsingToolbarLayout`则是专门用来控制布局内不同元素响应滚动细节的布局。在`CoordinatorLayout`中，可以把视图分为两部分，一个是上半部的`AppBarLayout`，内含折叠式布局`CollapsingToolbarLayout`，在本次项目中用于英雄皮肤的显示，下半部分则是由`Tablayout`和`ViewPager`组成的。布局基础就如以下：

```xml
<android.support.design.widget.CoordinatorLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true">

    <android.support.design.widget.AppBarLayout
        android:id="@+id/detail_appbar"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:fitsSystemWindows="true">

        <android.support.design.widget.CollapsingToolbarLayout
            android:id="@+id/detail_collapsing_toolbar"
            app:expandedTitleGravity="bottom|end"
            app:expandedTitleMarginBottom="90dp"
            app:expandedTitleMarginStart="8dp"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="#fff"
            android:backgroundTint="#fff"
            android:fitsSystemWindows="true"
            android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"
            app:contentScrim="?attr/colorPrimaryDark"
            app:layout_scrollFlags="scroll|exitUntilCollapsed">
			
            // other view
            
            <android.support.v7.widget.Toolbar
                android:id="@+id/detail_toolbar"
                android:layout_width="match_parent"
                android:layout_height="?attr/actionBarSize"
                app:layout_collapseMode="pin"
                app:navigationIcon="@drawable/ic_arrow_back_white_24dp"
                app:popupTheme="@style/ThemeOverlay.AppCompat.Light"
                app:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar" />

        </android.support.design.widget.CollapsingToolbarLayout>
    </android.support.design.widget.AppBarLayout>

    <LinearLayout
        android:id="@+id/linearLayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical"
        app:layout_behavior="@string/appbar_scrolling_view_behavior">

        <android.support.v4.widget.NestedScrollView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:layout_behavior="@string/appbar_scrolling_view_behavior">

            <android.support.design.widget.TabLayout
                android:id="@+id/tabLayout"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_gravity="bottom"
                app:tabGravity="fill"
                app:tabMode="fixed" />

        </android.support.v4.widget.NestedScrollView>

        <android.support.v4.view.ViewPager
            android:id="@+id/viewPager"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />

    </LinearLayout>


</android.support.design.widget.CoordinatorLayout>
```

布局效果：

![2](C:\Users\cookieschen\Desktop\img1\2.png)

#### 可折叠布局

在这里需要注意的是，在`CoordinatorLayout`内并且在`AppBarLayout`外的控件，如果想实现上下滑动控制`AppBarLayout`的可见性，需要嵌套在`NestedScrollView`或者`RecyclerView`等中，这次嵌套在`NestedScrollView`，上面说到了`AppbarLayout`用于响应滚动，因此注意到NestedScrollView中设置的属性，`app:layout_behavior="@string/appbar_scrolling_view_behavior"`，如果没有设置该属性，`AppbarLayout`将不会响应布局的滚动事件，因此当你滑动`AppBarLayout`以外的地方，可折叠布局`CollapsingToolbarLayout`不会因为上下滑动而进行折叠。效果如下：

![hero_detail](C:\Users\cookieschen\Desktop\img1\hero_detail.gif)

#### 皮肤切换

在`CollapsingToolbarLayout`布局中，主要使用了一个`HorizontalScrollView`，用于存放背景皮肤图片，因此可以通过左右滑动该处进行皮肤的查看。然后就是一个`RecyclerView`，用于背景皮肤图片的切换。要实现`RecyclerView`切换，还是需要编写一个`SkinAdapter`类，该类继承了`RecyclerView.Adapter<SkinAdapter.MyViewHolder>`。

实现效果：

![horizontal](C:\Users\cookieschen\Desktop\img1\horizontal.gif)

![3](C:\Users\cookieschen\Desktop\img1\3.png)

#### 选项卡

选项卡使用`TabLayout`实现，要实现选项卡的功能，需要将其与`ViewPager`和`Fragment`配合起来使用，先设置好选项卡的选项和要新建的`Fragment`，然后传给`DetailFragmentAdapter`，最后通过已有的`ViewPager`创建`TabLayout`，这样就拥有了一个能够左右滑动的选项卡。

```java
ArrayList<String> tabList = new ArrayList<>();
tabList.add("资料");
tabList.add("技能");
tabList.add("出装");

ArrayList<Fragment> fragmentList = new ArrayList<>();
fragmentList.add(new IntroductionFragment(activity, viewModel, item));
fragmentList.add(new SkillFragment(activity, viewModel, item));
fragmentList.add(new EquipmentFragment(activity, viewModel, item));

this.binding.viewPager.setAdapter(new DetailFragmentAdapter(activity.getSupportFragmentManager(), fragmentList, tabList));

this.binding.tabLayout.setupWithViewPager(this.binding.viewPager);
```

```java
public class DetailFragmentAdapter  extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragmentList;
    private ArrayList<String> list_Title;

    public DetailFragmentAdapter(FragmentManager fm, ArrayList<Fragment> fragmentList, ArrayList<String> list_Title) {
        super(fm);
        this.fragmentList = fragmentList;
        this.list_Title = list_Title;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return list_Title.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return list_Title.get(position);
    }
}
```

效果图如下：

![detail_switch](C:\Users\cookieschen\Desktop\img1\detail_switch.gif)

#### 英雄资料界面

#####  属性条

这里主要是`RoundCornerProgressBar`控件的使用，需要在`Gradle`中添加

```
compile 'com.akexorcist:RoundCornerProgressBar:2.0.3'
```

使用如下，具体属性可以在[github](https://github.com/akexorcist/Android-RoundCornerProgressBar)上查看

```
<com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar
                    android:id="@+id/progress_skill"
                    android:layout_width="150dp"
                    android:layout_height="20dp"
                    android:layout_marginStart="8dp"
                    app:layout_constraintBottom_toBottomOf="@+id/attribute_skill_text"
                    app:layout_constraintStart_toEndOf="@id/attribute_skill_text"
                    app:layout_constraintTop_toTopOf="@+id/attribute_skill_text"
                    app:rcBackgroundColor="@color/colorTip"
                    app:rcBackgroundPadding="3dp"
                    app:rcProgressColor="#5bd374"
                    app:rcRadius="5dp"
                    app:rcReverse="false" />
```



#####  英雄关系

这里使用了三个`ImageView`，图标可以在[这里](http://www.iconfont.cn/)下载，选择的效果通过修改边框可以实现，具体的实现就是编写两个border，只不过颜色不一样，然后在代码中控制。

```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:width="200dp"
    android:height="200dp"
    android:viewportWidth="1024"
    android:viewportHeight="1024">
    <path
        android:fillColor="@color/colorAccent"
        android:pathData="M511.96388 0.03612c-282.710688 0-511.89164 229.217072-511.89164 512.03612 0 282.602328 229.180952 511.89164 511.89164 511.89164 282.746808 0 512-229.289312 512-511.89164C1023.96388 229.289312 794.710688 0.03612 511.96388 0.03612zM512 979.536367c-258.149136 0-467.428007-209.314991-467.428007-467.464127 0-258.185256 209.278871-467.464127 467.428007-467.464127 258.113016 0 467.428007 209.278871 467.428007 467.464127C979.428007 770.221376 770.149136 979.536367 512 979.536367zM644.95746 167.957672 254.464903 512.397319 537.283951 559.858907 397.716543 853.008254 765.923104 469.848042 504.161975 433.439153Z"
        tools:ignore="VectorPath" />
</vector>
```

![5](C:\Users\cookieschen\Desktop\img1\5.png)

```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:width="200dp"
    android:height="200dp"
    android:viewportWidth="1024"
    android:viewportHeight="1024">
    <path
        android:fillColor="#d1c8be"
        android:pathData="M511.96388 0.03612c-282.710688 0-511.89164 229.217072-511.89164 512.03612 0 282.602328 229.180952 511.89164 511.89164 511.89164 282.746808 0 512-229.289312 512-511.89164C1023.96388 229.289312 794.710688 0.03612 511.96388 0.03612zM512 979.536367c-258.149136 0-467.428007-209.314991-467.428007-467.464127 0-258.185256 209.278871-467.464127 467.428007-467.464127 258.113016 0 467.428007 209.278871 467.428007 467.464127C979.428007 770.221376 770.149136 979.536367 512 979.536367zM644.95746 167.957672 254.464903 512.397319 537.283951 559.858907 397.716543 853.008254 765.923104 469.848042 504.161975 433.439153Z"
        tools:ignore="VectorPath" />
</vector>
```

![6](C:\Users\cookieschen\Desktop\img1\6.png)

实现效果图：

![7](C:\Users\cookieschen\Desktop\img1\7.png)

#### 技能界面

技能的实现也如英雄关系切换一样，在这里就不赘述了。

![8](C:\Users\cookieschen\Desktop\img1\8.png)

在这里主要说明一下召唤师技能的实现点击的实现，弹出窗口是继承`DialogFragment`类实现的，通过设置布局以及动画，使得这个对话框看起来像一个弹出框。

```java
@SuppressLint("SetTextI18n")
public static void showCard(AppCompatActivity activity, final ViewModel viewModel, ItemItem target, boolean isCommon) {
    final CardFragment fragment = new CardFragment(activity);
    final ItemCardBinding itemBinding = fragment.getBinding();
    if (viewModel.isExistCollection(target.getId())) {
        itemBinding.itemCardBtnLove.setImageResource(R.drawable.ic_love_red);
    }
    // 收藏按钮
    fragment.getHandler().setOnClickCardLike(v -> {
        if (viewModel.isExistCollection(target.getId())) {
            itemBinding.itemCardBtnLove.setImageResource(R.drawable.ic_love);
            viewModel.removeCollection(target.getId());
        } else {
            itemBinding.itemCardBtnLove.setImageResource(R.drawable.ic_love_red);
            viewModel.addCollection(new CollectionItem(target.getId(),
                                                       isCommon ? CollectionItem.Type.Item : CollectionItem.Type.SpecialItem, "", new Date()));
        }
    });
    itemBinding.itemCardName.setText(target.getName());
    if (isCommon) {
        itemBinding.itemCardLayoutPrice.setVisibility(View.VISIBLE);
        itemBinding.itemCardLv.setVisibility(View.GONE);
        itemBinding.itemCardPrice.setText("" + target.getPrice());
        itemBinding.itemCardPriceTotal.setText("" + target.getTotalPrice());
    } else {
        itemBinding.itemCardLv.setText("等级：" + target.getLevel());
    }
    itemBinding.itemCardAttrText.setText(target.getEffect());
    if (target.getPassiveEffect() == null) {
        itemBinding.itemCardDetail.setVisibility(View.GONE);
    } else {
        itemBinding.itemCardDetail.setText(target.getPassiveEffect());
    }
    viewModel.getItemIcon(target.getId()).observe(activity, (resIcon) -> {
        if (resIcon != null && resIcon.status == Resource.Status.SUCCESS && resIcon.data != null) {
            itemBinding.itemCardIcon.setImageBitmap(resIcon.data);
        }
    });
    fragment.show(activity.getSupportFragmentManager(), "ItemDetail");

}
```

#### 出装和铭文界面

这个界面因为有推荐出装1和推荐出装2，因此需要做一个更换功能，为了让更换显得更平滑，使用了动画，动画实现如下：

```java
for (int i = 0; i < linearLayouts.length; i++) {
    // 动画效果
    final int finalI = i;
    AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
    alphaAnimation.setDuration(200 + i * 30);
    alphaAnimation.setFillBefore(true);
    linearLayouts[i].startAnimation(alphaAnimation);
    // 点击事件
    linearLayouts[i].setOnClickListener(v -> ListItemHandler.showCard(activity,viewModel, items.get(finalI), true));
    // 设置图标和数据
    this.viewModel.getItemIcon(Integer.parseInt(ItemList[i])).observe(activity, res -> {
        if (res == null || res.data == null || res.status != Resource.Status.SUCCESS)
            return;
        (new Handler()).postDelayed(() -> {
            equipmentImages[finalI].setImageBitmap(res.data);
            equipmentTexts[finalI].setText(items.get(finalI).getName());
        }, 200);
    });
}
```

###  数据获取

####  数据绑定

想使用数据绑定，首先需要在`Gradle`中android下设置

```
dataBinding {
	enabled = true
}
```

然后就是在xml布局文件中最外层添加`layout`标签，并且添加新的标签`data`，在`data`中声明需要绑定的变量，本次实验中每个layout文件都会有一个对应的handler处理该布局的逻辑以及UI。

```xml
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tool="http://schemas.android.com/tools">

    <data>
        <variable
            name="handler"
            type="studio.xmatrix.qqpvp.assistant.ui.fragment.detail.IntroductionHandler" />
    </data>

    // your layout
</layout>
```

在对应的java文件中，通过DataBindingUtil加载布局，需要通过`setHandler`绑定到特定的类上。

```java
private DetailIntroductionFragmentBinding binding;
@SuppressLint("ValidFragment")
public IntroductionFragment(HeroDetailActivity activity,ViewModel viewModel, HeroDetailItem item) {

    this.binding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.detail_introduction_fragment, null, false);
    binding.setHandler(new IntroductionHandler(binding, activity, viewModel, item));
}
```

然后就可以通过`binding`愉快的进行处理了，`binding`可以获取布局中的所有控件，例如id为progress_skill的一个进度条，可以通过`binding.progressSkill`获得。

在布局文件中，可以通过绑定handler的变量进行数据绑定，比如一个`TextView`，可以设置其属性如下:

```xml
<TextView
    	  android:id="@+id/hero_relation_text"
          android:layout_width="wrap_content"
          android:layout_height="wrap_content"
          android:layout_marginStart="5dp"
          android:layout_marginTop="5dp"
          android:text="@{handler.title}"
          android:textSize="15sp"
          android:textStyle="bold" />
```

在handler中设置属性`title`，并且设置好setter和getter方法，就可以实现数据绑定了。其他的三元运算等操作，在英雄界面中并没有太多的使用，就不做太多的介绍了。

 #### LiveData获取数据

以获取英雄详情数据，看到秀秀写的`ViewModel`中提供的`LiveData`

```java
public LiveData<Resource<HeroDetailItem>> getHeroDetail() {
    if (heroDetail == null) {
        heroDetail = heroRepository.getHeroDetail(heroDetailId);
    }
    return heroDetail;
}
```

通过`LiveData`的`observe`方法，只有当数据库中的数据发生更改的时候，才会回调传入的值，当第一次获取数据的时候，`activity`观察到数据变化，会把数据保存并通过回调函数传出，当后续需要使用该数据的时候，activity并没被通知数据发生变化，因此直接使用上一次的数据，这样性能将会非常的好。

```java
this.viewModel.getHeroDetail().observe(activity, resource -> {
    if (resource == null || resource.data == null) return;
    switch (resource.status) {
        case SUCCESS:
            initView(resource.data);
            break;
    }
});
```

---

## 实验思考及感想

本次实验中在布局中遇到了很多坑

* 首先是`NestedScrollView`，在最初的布局中，是把`ViewPager`放在`NestedScrollView`中，但是发现`AppBarLayout`不响应下方`ViewPager`中`Fragment`的滑动，通过实践，发现ViewPager只是充当占位符的作用，因此Fragment的父布局不是`NestedScrollView`，因此导致了问题，解决的方法是，将每个Fragment都嵌套在`NestedScrollView`中就可以了。
* 透明背景自动转换成黑色背景问题，本次实验中，我在使用LiveData获取图片数据时，技能和铭文的图片背景色都应该是透明的，然而获取到的`Bitmap`显示的背景是黑色的，发现是Bitmap转化时的问题，从数据库中获取图片的时候，使用了compress函数，选择压缩的方式是JPEG，该方法会自动把所有没有设置的像素值设置为0，因此导致了问题，只需要使用PNG压缩，因为PNG压缩是无损的，因此就不会对原图产生影响。
* 数据绑定双向绑定问题，因为android提供的数据绑定提倡的是将数据绑定到model上，而model需要拥有setter方法才能够成功绑定，因此在使用的时候需要注意。本次实验因为数据都是动态的并且需要实现的逻辑比较复杂，就没有使用双向绑定。
* 布局问题，因为使用了数据绑定，实验过程中曾经使用标签include，后面发现数据绑定的时候并不兼容，无法获取include加载的布局中的控件，因此就放弃了。
* `Fragment`中使用`ViewPager`无法实现`ViewPager`高度自适应，通过`android:layout_height="wrap_content"`无法使得`ViewPager`根据其高度自适应，修改了很多次都没有解决，就放弃使用了，本来想使用在技能切换的效果上的。

---

#### 作业要求
* 命名要求：学号_姓名_实验编号，例如12345678_张三_lab1.md
* 实验报告提交格式为md
* 实验内容不允许抄袭，我们要进行代码相似度对比。如发现抄袭，按0分处理