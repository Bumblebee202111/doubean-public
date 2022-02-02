doubean
=======
豆瓣非官方app，主要用于小组浏览，仍在起步阶段。 功能:

* 已实现的功能
  * 根据收藏的小组ID（fav_group数据库表）在线或本地加载小组基本信息（不含话题）
* 待实现的功能
  * 支持使用关键词黑名单和白名单、小组话题标签（tag aka tab）订阅话题
* 不会实现的功能
  * 豆瓣除小组外其他模块和基于登录的服务 特点：
* 基于MVVM架构和Java语言，优先选择Android Jetpack库的组件
* 能够使用某种方式获取豆瓣实时数据
* 使用ROOM作为豆瓣服务器数据的缓存
* 根据多条件订阅关注的话题
* 轻量app（既由于作者水平和精力有限，又由于官方app诸多“累赘”的特性不在开发计划之内） 注意：
* 仅用于作者小白的个人学习和轻度日常使用，部分“非技术”细节已经.gitignore了，参考自Internet，感兴趣的可以搜一搜。
* 参考大量Android官方资料，代码“东拼西凑”，原创性极低。

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
  * [Glide][glide]\(待使用\)

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

[sunflower]: https://github.com/android/sunflower

[guides]: https://developer.android.google.cn/guide

[github-browser-sample]: https://github.com/android/architecture-components-samples/tree/master/GithubBrowserSample

[todo-mvvm-live]: https://github.com/android/architecture-samples/tree/todo-mvvm-live

