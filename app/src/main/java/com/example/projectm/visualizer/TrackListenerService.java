package com.example.projectm.visualizer;

import android.service.notification.NotificationListenerService;

/**
 * Only a key: Android lets an app read other apps' media sessions (the playing track) when it is
 * an enabled notification listener. Notifications themselves are not used. Declaring this service
 * is what lists the app under Settings › Apps › Special app access › Notification access.
 */
public class TrackListenerService extends NotificationListenerService {
}
