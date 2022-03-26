doubean
=======

豆瓣非官方app，主要用于小组浏览。**练手项目，仍未初步完成**。

<p><img src="screenshots/phone_group_detail.png" alt="phone_group_detail" width="200" />
<img src="screenshots/phone_group_search.png" alt="phone_group_search" width="200" />
<img src="screenshots/phone_topic_detail.png" alt="phone_topic_detail" width="200" /></p>


注意
----

* 即日起随缘更新。
* 担心侵权，虽然发布了apk，但同时把源代码中的DoubanService.java列入了.gitignore
* 编写代码时抄袭了大量Android官方样例，原创性极低，但代码质量自觉不高，请谨慎参考。
* 所有列表最多加载100个元素，如需查看完整列表，请点击按钮在豆瓣网页中查看（逐步实现中，现仅话题详情支持）

App受众
-------

* 学习Android、Java、版本控制的作者小白本人和其他感兴趣的开发者
* 本人和其他有轻度豆瓣小组浏览需求的用户

## 功能

该app包含三个主界面

### GroupFragment

#### 小组主界面

* 收藏小组和标签

#### 小组详情

* 小组简介
* 小组话题

#### 小组话题详情

* 话题内容
* 话题评论

#### 小组搜索

* 搜索小组

### HomeFragment

空，来自模板，placeholder

### NotificationFragment

同上

计划
----

包含新增功能、引入库、bug修复（大致按实现顺序排列）。

目标：逐个消灭计划项！！！

* \[GroupTopicFragment\]适当补充尚未使用的字段，如显示评论中的图片、在评论中标注楼主、显示回复的评论
* \[GroupsFragment\]增加小组日榜
* 重新命名变量和UI文本，以便于理解（可能与官方叫法冲突，如“话题”->“帖子”），重新整理packages
* 🛠 响应的保存（缓存）方式不合理，造成如小组首页加载收藏会触发NPE（解决方法为暴力简化，直接保存嵌套对象，修复后为话题详情页应用小组主题色）
* \[GroupFragment\] 显示/隐藏标题的时机
* 🛠 \[GroupFragment\] 返回至任何位置标题均不显示
* \[GroupTopicFragment\]保存WebView中的图片
* \[RecyclerView\]SwipeRefreshLayout
* \[GroupFragment\]\[GroupTopicFragment\]分享功能
* \[GroupFragment\]\[GroupTopicFragment\]添加记录已读、取消已读的功能
* \[GroupFragment\]小组或标签内搜索话题
* \[GroupsFragment\]全局搜索话题
* \[GroupFragment\]将Tab的收藏按钮移至Tab内部（考虑增加工具栏或efab）
* \[GroupsFragment\]收藏列表中允许移除收藏、调整顺序
* \[GroupFragment\]重新设计收藏（或称关注）的模型，支持收藏（关注）小组、话题、标签……，并建立自定义类别，根据多个收藏项订阅话题，支持关键词过滤（有难度，但不一定有用）
* \[GroupFragment\]小组的快捷方式
* \[ListView\]仿Reddit折叠/展开列表项
* 🛠\[GroupTopicFragment\]处理话题详情中的URL：在浏览器中打开或直接跳转对应页，而不应在原WebView中加载
* 支持默认或手动屏蔽不友善、负能量的内容
* \[GroupFragment\]话题排序：发布时间（伪，由于接口限制）、回复时间、热度
* Color
* Typography
* 🛠 \[ListView\]列表如GroupFragment中的Tab加载时出现卡顿（Pagination） （重中之重，基本需求）
* \[ListView\]结合SharedPref实现个性化推荐
* \[ListView\]话题排序：自创的多参数排序规则
* Widgets
* 通知、推送（考虑其意义、国情和作者自身条件，搁置）
* 🛠MIUI 10 Android 8.1加载含WebView的NestedScrollView时子视图会因滑动或WebView中选中本文等操作而消失或
  复现（原因未知，初步判断为旧版MIUI的问题，由于本人水平有限且系统较旧）
* 豆瓣除小组外其他模块（如图书）

非目标
-----    

* 基于登录的服务
* Kotlin
* Dagger2/Hilt
* RxAndroid
* 深度使用Material Design

特点
----
在实现官方app最基本功能的基础上又具有自己的特点，改善了小组较为封闭的现状。

* 基于100% Java语言，尽自己所能选用权威的库（Jetpack中的库和优秀第三方库）、架构（MVVM）和理念（Android
  Jetpack），保证了较好的性能，便于代码编写和阅读，同时对Hilt/Dagger2/Kotlin等新技术或争议技术持保守的观望态度
* 支持在豆瓣网页的WebView（已注入JS优化）中查看界面，以备不时之需
* 对UI的字符串常量提供中英双语支持
* 支持离线加载缓存
* 根据多条件订阅关注的话题（待实现）
* 无广告的轻量app
* 访问接口采用了Apache Commons的现成工具，极大地简化代码。
* 支持豆瓣网页URL的deep links

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
  * [Animations & Transitions][animation]
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
* 他人博客和仓库中有关获取豆瓣数据的方法
* StackOverflow
* Google
* CSDN/cnblogs

[sunflower]: https://github.com/android/sunflower

[guides]: https://developer.android.google.cn/guide

[github-browser-sample]: https://github.com/android/architecture-components-samples/tree/master/GithubBrowserSample

[todo-mvvm-live]: https://github.com/android/architecture-samples/tree/todo-mvvm-live

辅助工具
---------

* HTTPCanary for douban access
* jadx-gui for understanding models
* Google Chrome for CSS debugging
* Twitter, Chrome, CoolApk, Reddit, Google News apps as references for UI design
