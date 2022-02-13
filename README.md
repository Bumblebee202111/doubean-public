doubean
=======
豆瓣非官方app，主要用于小组浏览。**项目仍在起步阶段**。

开发的动机
----------

* 日常使用
* 学习Android开发、Java编程以及Git和Github的使用。

计划
----
功能、关键的库、bug修复

* 已实现
  * 根据收藏的小组ID（fav_group数据库表）在线或本地加载小组基本信息（不含话题）
  * 较为完整地展示小组话题列表
  * 用网页显示话题详情（过渡）
  * 收藏小组和话题
  * 搜索小组
* 待实现（**画饼**）
  * 能建立自定义话题类别，根据多个小组、话题标签（tag aka tab）订阅话题，支持关键词过滤（试试看）
  * 用.xml重写话题详情，可能会被迫引入分页（？）
  * 搜索话题
  * 修复从话题页返回话题列表时丢失位置的问题（有空再解决）
  * 修复部分接口未提供的某些字段覆盖已有字段导致丢失缓存的问题（有空再解决）
  * 给APP添加图标
  * 通知、推送（试试看）
  * Widgets（试试看）
  * Deep links（试试看）
  * ……
* 看未来
  * 豆瓣除小组外其他模块
  * Paging
* 不考虑
  * 基于登录的服务
  * Kotlin
  * Dagger2/Hilt
  * RxAndroid

特点
----

* 基于MVVM架构和Java（100%）语言，优先选择Android Jetpack库中广为使用的组件和顶尖（≫优秀）第三方库
  * 同时对Hilt/Dagger2/Kotlin/WorkManager等持保守态度
* 能够使用某种方式获取豆瓣实时数据
* 使用ROOM作为豆瓣服务器数据的缓存
* 根据多条件订阅关注的话题
* 轻量app（既由于作者水平和精力有限，又由于官方app诸多“累赘”的模块和功能不在本项目开发计划之内）
* 较为原始的依赖注入：InjectorUtils.java

注意
----

* 项目仅用于作者小白的个人学习和轻度日常使用，部分“危险”的细节已经.gitignore了，感兴趣的同学可以搜一搜。方法来自各路大神的博客和仓库，具体实现时采用了现成的第三方类库，极大地简化代码。
* 编写代码时引用了大量Android官方样例（“东拼西凑”)，原创性极低。

使用的库
-------

* [Foundation][foundation]
  * [AppCompat][appcompat]
  * [Test][test]\(待使用\)
* [Architecture][arch]
  * [Data Binding][data-binding]
  * [Lifecycles][lifecycle]
  * [LiveData][livedata]
  * [Navigation][navigation]
  * [Room][room]
  * [ViewModel][viewmodel]
* [UI][ui]
  * [Animations & Transitions][animation]\(待使用\)
  * [Fragment][fragment]
  * [Layout][layout]
* Third party and miscellaneous libraries
  * Retrofit
  * Apache Commons
  * [Glide][glide]

[foundation]: https://developer.android.com/jetpack/components

[appcompat]: https://developer.android.com/topic/libraries/support-library/packages#v7-appcompat

[test]: https://developer.android.com/training/testing/

[arch]: https://developer.android.com/jetpack/arch/

[data-binding]: https://developer.android.com/topic/libraries/data-binding/

[lifecycle]: https://developer.android.com/topic/libraries/architecture/lifecycle

[livedata]: https://developer.android.com/topic/libraries/architecture/livedata

[navigation]: https://developer.android.com/topic/libraries/architecture/navigation/

[room]: https://developer.android.com/topic/libraries/architecture/room

[viewmodel]: https://developer.android.com/topic/libraries/architecture/viewmodel

[ui]: https://developer.android.com/guide/topics/ui

[animation]: https://developer.android.com/training/animation/

[fragment]: https://developer.android.com/guide/components/fragments

[layout]: https://developer.android.com/guide/topics/ui/declaring-layout

[glide]: https://bumptech.github.io/glide/

参考的资料
---------

* [Android Sunflower][sunflower]
* [Developer Guides][guides]
* [Github Browser Sample with Android Architecture Components][github-browser-sample]
* [Android Architecture Blueprints v1 (todo-mvvm-live)][todo-mvvm-live]
* 一些获取豆瓣数据的方法
* StackOverflow
* Google

[sunflower]: https://github.com/android/sunflower

[guides]: https://developer.android.google.cn/guide

[github-browser-sample]: https://github.com/android/architecture-components-samples/tree/master/GithubBrowserSample

[todo-mvvm-live]: https://github.com/android/architecture-samples/tree/todo-mvvm-live

辅助工具
---------

* HTTPCanary
* MiX

