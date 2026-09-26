package com.example.projectm.visualizer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TrackWatcherTest {
    @Test
    public void labelJoinsTitleAndArtist() {
        assertEquals("Underground — Silver Panda, Ruback", TrackWatcher.label("Underground", "Silver Panda, Ruback"));
    }

    @Test
    public void labelUsesWhateverIsKnown() {
        assertEquals("Underground", TrackWatcher.label(" Underground ", ""));
        assertEquals("Silver Panda", TrackWatcher.label(null, "Silver Panda"));
        assertEquals("", TrackWatcher.label(null, "  "));
    }
}
