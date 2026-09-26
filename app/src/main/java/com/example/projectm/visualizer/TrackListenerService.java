package com.example.projectm.visualizer;

import android.service.notification.NotificationListenerService;

/**
 * Only a key: Android lets an app read other apps' media sessions (the playing track) when it is
 * an enabled notification listener. Notifications themselves are not used. Android TV has no
 * settings screen for this access, so it is granted once over adb (see README).
 */
public class TrackListenerService extends NotificationListenerService {
}
