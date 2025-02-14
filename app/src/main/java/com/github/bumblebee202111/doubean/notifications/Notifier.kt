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
private const val TOPIC_NOTIFICATION_CHANNEL_ID = ""
private const val TOPIC_NOTIFICATION_SUMMARY_ID = 0
private const val GROUP_KEY_TOPIC_NOTIFICATION = "com.android.example.TOPIC_NOTIFICATIONS"
private const val TOPIC_NOTIFICATION_REQUEST_CODE = 0

@Singleton
class Notifier @Inject constructor(@ApplicationContext private val context: Context) {
    fun postTopicNotifications(topics: List<TopicItemWithGroup>) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        context.ensureNotificationChannelExists()

        val topicNotifications = topics.map { topic ->
            NotificationCompat.Builder(
                context,
                TOPIC_NOTIFICATION_CHANNEL_ID,
            )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(topic.title)
                .setContentText(topic.notificationContentText())
                .setContentIntent(topicPendingIntent(context, topic))
                .setGroup(GROUP_KEY_TOPIC_NOTIFICATION)
                .setAutoCancel(true)
                .setWhen(topic.updateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .build()
        }

        val summaryNotification = NotificationCompat.Builder(context, TOPIC_NOTIFICATION_CHANNEL_ID)
            .setContentTitle(
                context.getString(
                    R.string.topic_notification_group_summary,
                    topics.size,
                )
            )
            
            .setContentText(topics.first().title)
            .setSmallIcon(R.drawable.ic_notifications)
            
            .setGroup(GROUP_KEY_TOPIC_NOTIFICATION)
            
            .setGroupSummary(true)
            .build()

        
        NotificationManagerCompat.from(context).apply {
            topicNotifications.forEachIndexed { index, notification ->
                notify(
                    topics[index].id.hashCode(),
                    notification,
                )
            }
            notify(TOPIC_NOTIFICATION_SUMMARY_ID, summaryNotification)
        }
    }
}

fun topicPendingIntent(context: Context, topic: TopicItemWithGroup): PendingIntent =
    PendingIntent.getActivity(
        context, TOPIC_NOTIFICATION_REQUEST_CODE,
        Intent().apply {
            action = Intent.ACTION_VIEW
            data = topic.postDeepLinkUri()
            component = ComponentName(
                context.packageName,
                TARGET_ACTIVITY_NAME,
            )
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )



private fun Context.ensureNotificationChannelExists() {

    val channel = NotificationChannel(
        TOPIC_NOTIFICATION_CHANNEL_ID,
        getString(R.string.topic_notification_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description = getString(R.string.topic_notification_channel_description)
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