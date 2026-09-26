# ProjectM TV

[![Android CI](https://github.com/johnneerdael/ProjectM-TV/actions/workflows/android.yml/badge.svg)](https://github.com/johnneerdael/ProjectM-TV/actions/workflows/android.yml)

ProjectM TV is a music visualizer for Android TV. It runs [projectM](https://github.com/projectM-visualizer/projectm) 4.1.7, an open-source reimplementation of Winamp's MilkDrop, with 9,606 presets from Jason Fletcher's *Cream of the Crop* collection. It visualizes the music another app plays on the TV, such as SoundCloud. It is not a music player itself.

> **Install on your TV with the Downloader app: code `4821216`**
>
> Install *Downloader* by AFTVnews on the TV, open it, enter **4821216** and install the APK it downloads. The code always points to the newest stable release. Details under [Install](#install).

This README describes what the app does as of version 1.9.5, and where it falls short. Everything under *What it does* was checked on the only device it has been tested on: an NVIDIA SHIELD Android TV (2019, Android 11).

## What it does

- **Visualizes music from another app.** Play music in a music app, then start ProjectM TV. On the SHIELD, the visuals react to SoundCloud about 10 seconds after launch, and within about 5 seconds after you pause and resume. The app only looks for the music while Android reports that music is playing.
- **Shows 9,606 presets in shuffled order.** It changes preset every 30 seconds by default, or when you press Left or Right on the remote, and blends the old preset into the new one over 7 seconds. The next preset's shaders are compiled in the background beforehand, so a switch does not freeze the picture.
- **Shows the track that is playing.** When the music app starts a new track, its title and artist appear in the lower left for 20 seconds (taken from the app's media session, e.g. SoundCloud or Flow). This needs *notification access*, see [Track titles](#track-titles) below; without it, nothing is shown. The preset name is in the settings panel.
- **Replaces presets that stay black.** If a preset shows only black for about 7 seconds while music plays, the app moves on. A preset that is black a second time is skipped from then on. Since 1.9.5, the presets that were black on the SHIELD render; this rule remains as a safety net (details under *Presets* below).
- **Adapts the resolution.** *Auto* resolution lowers or raises the render resolution between presets to hold the frame rate. The TV's scaler upscales to the panel.
- **Protects the music app from being closed.** On TVs with little memory, Android closes other apps when projectM uses too much. The app caps its resolution by installed memory (on a 2 GB SHIELD: 1260p), and in *Auto* resolution it lowers the resolution when Android reports memory pressure.
- **Starts quickly.** About 3–6 seconds from launch to the first preset on the SHIELD.
- **Shows its own measurements.** *Settings › Advanced › Diagnostics* shows the render size, frame rate, audio source and audio level.

## What it does not do, and known limits

**Tested on one device.** All measurements come from an NVIDIA SHIELD Android TV (2019, `sif`, 2 GB RAM, Android 11), which runs the app 32-bit. Other Android TV devices are untested. The app should run on any Android TV with Android 5.0 or later and OpenGL ES 3.0, but performance, audio behaviour and memory limits will differ.

**Audio**
- The app does not play music, and it has no microphone or line-in input. It can only visualize audio that another app plays on the same TV.
- On a SHIELD with Dolby or passthrough output, Android's standard visualizer hears nothing. The app works around this by finding the audio session of the playing app. That takes about 10 seconds after launch. If a search finds nothing, the next one waits 15, then 30, then 60 seconds, so it can take up to a minute. It works with SoundCloud; **other music apps (Spotify, YouTube Music, Plex, …) have not been tested**.
- The *Media capture* audio source receives no audio on the SHIELD. It relies on Android's playback capture, which the SHIELD's Dolby audio path bypasses. When the app finds the player's session instead, it switches back to *Standard* by itself. On other devices, Media capture may work; it asks for screen-cast consent at every launch.
- Apps that block audio capture, and audio that reaches the TV already encoded (for example Dolby bitstreams from a video app), cannot be visualized.
- The audio the visualizer receives is 8-bit mono, which is what Android's visualizer API provides.

**Picture and performance (SHIELD)**
- **Most preset changes freeze the picture for a moment, typically about 0.9 seconds and up to about 1.8 seconds.** projectM compiles the new preset's shaders on the render thread. This is not fixed yet.
- **4K is possible but not smooth.** At a fixed 4K the SHIELD averaged about 30 fps. With the default memory limit it never goes above 1260p; a fixed 4K needs *Memory limit* set to Off.
- **Android often reports low memory shortly after launch.** *Auto* resolution then stays at 720p for that session.
- Heavy presets drop below 60 fps, sometimes to about 20 fps, even at 720p.
- projectM is a reimplementation of MilkDrop. Some presets look different from MilkDrop on Windows, or still render incorrectly.

**Presets**
- 189 of the 9,795 *Cream of the Crop* presets are not included: 116 that cannot react to music, 73 that use images with text, logos or people (one preset is in both groups), and 1 whose texture could not be found.
- You cannot choose or search for a preset, or build playlists. Presets play in shuffled order.
- The black-preset check has limits. It judges each preset only in the first 20 seconds or so after it starts, and only after 3 seconds of uninterrupted music. "Black" means every sampled pixel is at or below about 8% brightness, so a very dark preset can count as black. After 3 black presets in a row it stops acting until a preset shows something, in case the fault is the renderer rather than the presets.
- Versions before 1.9.5 marked some presets as black that now render. If you used an earlier version, reset the skip list: *Settings › Advanced › Skipped presets*.

**Other**
- There is no touch or phone support. The app requires Android TV (Leanback).

## Requirements

- An Android TV device with Android 5.0 (API 21) or later
- OpenGL ES 3.0
- A music app that plays on the same device
- The *Media capture* audio source needs Android 10 or later

## Install

**On the TV, with Downloader (easiest)**
1. Install *Downloader* by AFTVnews from the TV's app store.
2. Allow it to install apps: Android TV asks for this the first time (*Install unknown apps* for Downloader).
3. Open Downloader, enter the code **4821216** and select *Go*. It downloads the newest stable release; confirm the installation.

The code is an AFTVnews short link to https://github.com/johnneerdael/ProjectM-TV/releases/latest/download/projectM-TV.apk, which always points to the newest stable release. Specific versions (`projectM-TV-<version>.apk`) are under [Releases](https://github.com/johnneerdael/ProjectM-TV/releases).

**From a computer, with adb**
```bash
curl -LO https://github.com/johnneerdael/ProjectM-TV/releases/latest/download/projectM-TV.apk
adb install -r projectM-TV.apk
```

**Then:** start the music in your music app and open ProjectM TV. Android asks for permission to record audio; the app needs it to receive the music.

From 1.9.7 on, updates install over the previous version and keep your settings. Two one-time steps if you used an earlier version:
- **1.9.7 changed the app ID** to `nl.neerdael.projectmtv`. It installs as a new app next to the old one; uninstall the old *ProjectM Visualizer* (`com.example.projectm.visualizer`) afterwards.
- Versions up to 1.9.5 were each signed with a different temporary key; 1.9.6 and later use one permanent key.

A version you built yourself is signed with your own debug key: uninstall it before installing a release (this resets the settings).

## Remote control

| Key | Action |
|---|---|
| Right, Next, Fast forward | Random preset (instant cut) |
| Left, Previous, Rewind | Previous preset (instant cut) |
| Up, Down, Info | Show the current track again (with notification access) |
| Center, Enter, Menu | Open the settings panel |
| Back | Exit the app |

In the panel, Up and Down move between rows, Left and Right change a value, and Center cycles a value or runs an action. Back closes the panel (in *Advanced*, it returns to the main panel), Menu closes it, and it hides itself after 10 seconds without input.

## Settings

The main panel shows the current preset and a live audio level (*Listening*, *Very quiet*, *No sound* or *No access*).

| Setting | Values | Default |
|---|---|---|
| Auto change | Off, On | On |
| Preset duration | 10, 15, 20, 30, 45, 60, 90 s | 30 s |
| Transition | Instant, 1–10 s | 7 s (2 s on low-end devices) |
| Resolution | Auto, or a fixed height up to the panel resolution and the memory limit | Auto |
| Frame rate | The TV's refresh rate, half or a quarter of it, at least 24 fps (e.g. 30 or 60 fps at 60 Hz) | 60 fps (30 on low-end devices) |

*Advanced ›* opens a second panel:

| Setting | What it does | Default |
|---|---|---|
| Detail | Mesh detail for preset motion: Minimal, Low, Medium, High, Ultra | Depends on the device |
| Transitions | *Auto* blends the two running presets and keeps the frame rate up: when the GPU is the limit, both render at a lower resolution during the blend (75% to start, down to 50%, back up when there is headroom); when the CPU is the limit, the outgoing preset renders every second frame. *Classic* always blends at full resolution. *Lightweight* fades a still image of the old preset for at most 3 s. | Auto |
| Cut on loud beats | Lets projectM cut to the next preset on a loud beat, like MilkDrop, instead of only blending | Off |
| Memory limit | Caps the resolution by installed memory: under 1.6 GB 1080p, under 2.6 GB 1260p, under 3.6 GB 1440p, otherwise no cap | On |
| Skip slow presets | Skips presets that stay far below the target frame rate even at the lowest resolution | On only on low-end devices |
| Skip blank presets | Moves on from presets that stay black while music plays; skips them for good the second time | On |
| Audio source | *Standard* or *Media capture* (Android 10+), see *Audio* above | Standard |
| Skipped presets | Shows how many presets are skipped; select it to reset the list | – |
| Diagnostics | Render size, panel, UI size, frame rate, transition style, audio source and level, device tier | – |

## Troubleshooting

**The visuals don't react to the music.** Open *Settings › Advanced* and look at the *Audio* line under *Diagnostics*.
- *silent / no data* right after launch: wait about 10 seconds while the app looks for the music app's audio.
- Still silent with *Media capture* on a SHIELD: switch *Audio source* to *Standard*.
- Still silent with *Standard*: the music app may block capture or send encoded audio, or it may not have been tested (see *Audio* above). Try SoundCloud to confirm the setup works.

**It stutters.** Keep *Resolution* and *Transitions* on *Auto*, set *Frame rate* to 30 fps, and lower *Detail* in *Advanced*.

<a id="track-titles"></a>**No track titles.** Android only shares the playing track with apps that have *notification access* (the app reads no notifications, it needs the access for the media session). At launch the app opens the system screen for it, or select *Settings › Advanced › Track titles*. Many Android TVs, including the NVIDIA SHIELD, have no such screen; then grant it once from a computer with adb (enable *Network debugging* in the TV's developer options):

```sh
adb connect <TV IP address>:5555
adb shell cmd notification allow_listener nl.neerdael.projectmtv/com.example.projectm.visualizer.TrackListenerService
```

*Diagnostics* shows the command too, and whether access is granted. It stays granted across updates; `disallow_listener` with the same argument revokes it.

**The music app closes while the visualizer runs.** Keep *Memory limit* on and *Resolution* on *Auto*. Only *Auto* lowers the resolution when memory runs low.

**A preset is black.** The app moves on by itself after about 7 seconds of music, as long as *Skip blank presets* is on. To bring back presets skipped earlier, reset *Skipped presets* in *Advanced*.

## For developers

### Build

```bash
git clone --recurse-submodules https://github.com/johnneerdael/ProjectM-TV.git
# in an existing clone: git submodule update --init --recursive
./gradlew assembleRelease     # non-debuggable APK, signed with your local debug key
adb install -r app/build/outputs/apk/release/app-release.apk
```

Release builds on GitHub are signed with the release key; see [docs/RELEASING.md](docs/RELEASING.md). CI builds every push, and publishes a release when `versionName` changes on `main`.

### projectM

projectM is built from source with the app. The git submodule `third_party/projectm` is pinned to the 4.1.7 release; the app's CMake applies the patches in `tools/projectm-patches/` (a transition fix from upstream; rendering into the app's own framebuffer, keeping the presets' frames when the render size changes, a cache of linked shader programs, fewer redundant GL calls, and a faster HLSL parser on Android) and links projectM statically into `libprojectmtv.so`, for armeabi-v7a and arm64-v8a. The first build per ABI takes a few minutes longer; later builds reuse it. There are no prebuilt binaries in the repository. After pulling a change to one of the patches, reset the submodule first (`git submodule foreach --recursive git checkout -- .`) so the new version applies.

### Tests

```bash
app/src/test/native/run_native_tests.sh   # native engine against fakes (ASan/UBSan)
./gradlew testReleaseUnitTest             # JVM tests
```

### On-device diagnostics

```bash
tools/tv-diagnostics.sh <tv-ip>:5555 --sweep
```

By default it builds this checkout and installs it on the TV; `--no-install` tests the version already installed. It measures startup, frame rate per resolution, audio source and level, preset load times and memory, and writes a Markdown summary. See [docs/DIAGNOSTICS.md](docs/DIAGNOSTICS.md).

### Documentation

- [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md): design, threading, preset skipping and measurements
- [docs/DIAGNOSTICS.md](docs/DIAGNOSTICS.md): the diagnostics script
- [docs/RELEASING.md](docs/RELEASING.md): signing and releases
- [docs/THIRD_PARTY.md](docs/THIRD_PARTY.md): sources and licences of bundled content
- [RELEASE_NOTES.md](RELEASE_NOTES.md): changes per version

## Credits and third-party content

- [projectM](https://github.com/projectM-visualizer/projectm), the visualization engine (LGPL 2.1)
- *Cream of the Crop* presets, curated by Jason Fletcher (ISOSCELES), via [presets-cream-of-the-crop](https://github.com/projectM-visualizer/presets-cream-of-the-crop)
- The MilkDrop texture pack, and textures from the community *MilkDrop 135k+ Presets MegaPack* collected by Incubo_
- The authors of the MilkDrop presets

projectM is LGPL 2.1; the presets and textures are distributed under CC0 1.0 (see *License*). Sources are listed in [docs/THIRD_PARTY.md](docs/THIRD_PARTY.md).

## License

The app's own code is licensed under the GNU Lesser General Public License, version 2.1; see [LICENSE](LICENSE). This matches projectM.

The bundled presets and textures are distributed under CC0 1.0 ([LICENSES/CC0-1.0.txt](LICENSES/CC0-1.0.txt)): free for any use. The presets and textures themselves were freely released by their authors; authors who want their work removed can open an issue. Details in [docs/THIRD_PARTY.md](docs/THIRD_PARTY.md).
