package com.example.projectm.visualizer;

import android.content.ComponentName;
import android.content.Context;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Reports the track the music app is playing, from its media session, each time a new one starts.
 * Needs notification-listener access for {@link TrackListenerService}; without it, nothing is
 * reported. Main thread only.
 */
final class TrackWatcher {
    interface Listener {
        /** A new track started, or its label was completed (e.g. the artist arrived after the title). */
        void onTrack(String label, boolean newTrack);
    }

    private static final String TAG = "ProjectMTV";

    private final Context context;
    private final Handler handler;
    private final Listener listener;
    private final ComponentName component;
    private final List<MediaController> controllers = new ArrayList<>();
    private MediaSessionManager sessions;
    private String lastTitle = "";
    private String lastLabel = "";

    private final MediaSessionManager.OnActiveSessionsChangedListener sessionsChanged = this::watch;
    private final MediaController.Callback controllerCallback = new MediaController.Callback() {
        @Override
        public void onMetadataChanged(MediaMetadata metadata) {
            report();
        }

        @Override
        public void onPlaybackStateChanged(PlaybackState state) {
            report();
        }
    };

    TrackWatcher(Context context, Handler handler, Listener listener) {
        this.context = context;
        this.handler = handler;
        this.listener = listener;
        this.component = new ComponentName(context, TrackListenerService.class);
    }

    /** Whether the user granted notification-listener access (Settings › Apps › Special app access). */
    boolean hasAccess() {
        String enabled = Settings.Secure.getString(context.getContentResolver(), "enabled_notification_listeners");
        return enabled != null && (enabled.contains(component.flattenToString())
                || enabled.contains(component.flattenToShortString()));
    }

    /** Starts watching if access was granted; returns whether it did. */
    boolean start() {
        if (sessions != null) return true;
        if (!hasAccess()) return false;
        try {
            MediaSessionManager manager = (MediaSessionManager) context.getSystemService(Context.MEDIA_SESSION_SERVICE);
            manager.addOnActiveSessionsChangedListener(sessionsChanged, component, handler);
            sessions = manager;
            watch(manager.getActiveSessions(component));
            return true;
        } catch (SecurityException e) {  // access revoked in the meantime
            Log.w(TAG, "Track watch unavailable: " + e.getMessage());
            stop();
            return false;
        }
    }

    void stop() {
        if (sessions != null) sessions.removeOnActiveSessionsChangedListener(sessionsChanged);
        sessions = null;
        watch(null);
    }

    private void watch(List<MediaController> active) {
        for (MediaController controller : controllers) controller.unregisterCallback(controllerCallback);
        controllers.clear();
        if (active != null) {
            for (MediaController controller : active) {
                controller.registerCallback(controllerCallback, handler);
                controllers.add(controller);
            }
        }
        report();
    }

    /** Reports the playing session's track if it differs from the last one reported. */
    private void report() {
        for (MediaController controller : controllers) {
            PlaybackState state = controller.getPlaybackState();
            MediaMetadata metadata = controller.getMetadata();
            if (state == null || state.getState() != PlaybackState.STATE_PLAYING || metadata == null) continue;
            String title = first(metadata, MediaMetadata.METADATA_KEY_TITLE, MediaMetadata.METADATA_KEY_DISPLAY_TITLE);
            String artist = first(metadata, MediaMetadata.METADATA_KEY_ARTIST,
                    MediaMetadata.METADATA_KEY_ALBUM_ARTIST, MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE);
            String label = label(title, artist);
            if (label.isEmpty() || label.equals(lastLabel)) return;
            boolean newTrack = !title.equals(lastTitle) || lastTitle.isEmpty();
            lastTitle = title;
            lastLabel = label;
            Log.i(TAG, "Track: " + label + " (" + controller.getPackageName() + ")");
            listener.onTrack(label, newTrack);
            return;
        }
    }

    private static String first(MediaMetadata metadata, String... keys) {
        for (String key : keys) {
            CharSequence value = metadata.getText(key);
            if (value != null && value.toString().trim().length() > 0) return value.toString().trim();
        }
        return "";
    }

    /** "Title — Artist", or whichever of the two is known. */
    static String label(String title, String artist) {
        title = title == null ? "" : title.trim();
        artist = artist == null ? "" : artist.trim();
        if (title.isEmpty()) return artist;
        if (artist.isEmpty()) return title;
        return title + " — " + artist;
    }
}
