package com.github.bumblebee202111.doubean.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.TopicItemWithGroup
import com.github.bumblebee202111.doubean.util.DEEP_LINK_SCHEME_AND_HOST
import com.github.bumblebee202111.doubean.util.GROUP_PATH
import com.github.bumblebee202111.doubean.util.TOPIC_PATH
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton


private const val TARGET_ACTIVITY_NAME = "com.github.bumblebee202111.doubean.MainActivity"
private const val POST_NOTIFICATION_CHANNEL_ID = ""
private const val POST_NOTIFICATION_SUMMARY_ID = 0
private const val GROUP_KEY_POST_NOTIFICATION = "com.android.example.POST_NOTIFICATIONS"
private const val POST_NOTIFICATION_REQUEST_CODE = 0

@Singleton
class Notifier @Inject constructor(@ApplicationContext private val context: Context) {
    fun postRecommendedPostNotifications(posts: List<TopicItemWithGroup>) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        context.ensureNotificationChannelExists()


        val postNotifications = posts.map { post ->
            NotificationCompat.Builder(
                context,
                POST_NOTIFICATION_CHANNEL_ID,
            )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(post.title)
                .setContentText(post.notificationContentText())
                .setContentIntent(postPendingIntent(context, post))
                .setGroup(GROUP_KEY_POST_NOTIFICATION)
                .setAutoCancel(true)
                .setWhen(post.lastUpdated.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .build()
        }

        val summaryNotification = NotificationCompat.Builder(context, POST_NOTIFICATION_CHANNEL_ID)
            .setContentTitle(
                context.getString(
                    R.string.post_notification_group_summary,
                    posts.size,
                )
            )
            
            .setContentText(posts.first().title)
            .setSmallIcon(R.drawable.ic_notifications)
            
            .setGroup(GROUP_KEY_POST_NOTIFICATION)
            
            .setGroupSummary(true)
            .build()

        
        NotificationManagerCompat.from(context).apply {
            postNotifications.forEachIndexed { index, notification ->
                notify(
                    posts[index].id.hashCode(),
                    notification,
                )
            }
            notify(POST_NOTIFICATION_SUMMARY_ID, summaryNotification)
        }
    }
}

fun postPendingIntent(context: Context, post: TopicItemWithGroup): PendingIntent =
    PendingIntent.getActivity(
        context, POST_NOTIFICATION_REQUEST_CODE,
        Intent().apply {
            action = Intent.ACTION_VIEW
            data = post.postDeepLinkUri()
            component = ComponentName(
                context.packageName,
                TARGET_ACTIVITY_NAME,
            )
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )



private fun Context.ensureNotificationChannelExists() {

    val channel = NotificationChannel(
        POST_NOTIFICATION_CHANNEL_ID,
        getString(R.string.post_notification_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description = getString(R.string.post_notification_channel_description)
    }
    
    NotificationManagerCompat.from(this).createNotificationChannel(channel)
}

private fun TopicItemWithGroup.postDeepLinkUri() =
    "$DEEP_LINK_SCHEME_AND_HOST/$GROUP_PATH/$TOPIC_PATH/$id".toUri()

private fun TopicItemWithGroup.notificationContentText(): String {
    return with(StringBuilder()) {
        append(group.name[0])
        tag?.let { append("·${it.name[0]}") }
        append(" $title")
    }.toString()
}