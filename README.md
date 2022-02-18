doubean
=======
豆瓣非官方app，主要用于小组浏览。**项目仍在起步阶段**。
![preview](https://github.com/Bumblebee202111/doubean/raw/master/Screenshot_20220213_234545.png)

开发的动机
----------

* 日常使用
* 学习Android开发、Java编程以及Git和Github的使用。

计划
----
功能、关键的库、bug修复

* 已实现（仅列出关键内容）
  * 良好地展示小组话题列表
  * 小组和话题的收藏
  * 完成话题详情的基本框架（暂保留话题网页的加载以作备用）
  * 搜索小组
  * 常用的Jetpack库（具体见下）
* **待实现（画饼，从细枝末节到宏观目标）**
  * 能建立自定义话题类别，根据多个小组、话题标签（tag aka tab）订阅话题，支持关键词过滤（试试看）
  * 用.xml重写话题详情，可能会被迫引入分页（？）
  * 搜索话题
  * 🛠从话题页返回话题列表时丢失位置的问题（有空再解决）
  * 🛠部分接口的缺失字段覆盖已有字段导致模型丢失缓存（临时解决方案：不保存不完整的对象）
  * 通知、推送（试试看）
  * Widgets（试试看）
  * Deep links（试试看）
  * ……
* 看未来
  * 豆瓣除小组外其他模块
  * Paging
  * 🛠MIUI 10 Android 8.1加载含WebView的NestedScrollView时子视图会因滑动或WebView中选中本文等操作而消失或
    复现，原因未知，初步判断为旧版MIUI的问题，由于本人水平有限且系统较旧，暂时搁置
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

* 项目仅用于作者小白的个人学习和轻度日常使用。
  部分可能侵权的代码已经.gitignore了，感兴趣的同学可以搜一搜。方法来自各路大神的博客和仓库，本人具体实现时采用了现成的第三方类库，极大地简化代码。
* 编写代码时抄袭了大量Android官方样例（“东拼西凑”)，原创性极低。

使用的库
-------

* [Foundation][foundation]
    * [AppCompat][appcompat]
    * [Test][test]（待使用）
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

