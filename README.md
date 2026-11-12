# Logic Breadboard Simulator

An Android application for interactive digital logic schematic editing, simulation, and virtual breadboard prototyping.

> [!WARNING]
> **No Support Provided:** This application is shared strictly **"as is"** with **no technical support, active maintenance, bug fixes, or troubleshooting assistance**. Feel free to fork, build, and modify the source code yourself under the terms of the MIT License.

## Features

- **Schematic Editor**: Interactive logic gate capture and real-time digital circuit simulation.
- **Breadboard Mode**: Virtual breadboard prototyping featuring interactive DIP ICs, tactile switches, LEDs, resistors, and pseudo-3D jumper wire routing.
- **Subcircuits & ICs**: Built-in 7400-series TTL integrated circuits and modular subcircuits.
- **Signal Scope**: Multi-channel timing and waveform inspection.
- **Hardware Description Language**: Verilog import and parsing support.
- **Native Routing Engine**: C-based routing engine running via JNI.

## Building from Source

### Prerequisites

- Android SDK (API 34 / Android 14+)
- Android NDK (r25c+) and CMake (3.22+)
- JDK 17 or later

Environment setup (e.g. in `~/.bashrc`; run `hash -r` if updating `PATH` in an already-open terminal):
```bash
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$PATH
```

### Build Instructions

Using the Gradle wrapper:

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run unit tests
./gradlew test
```

## Running on an Emulator / Simulator

### Option A: Using Android Studio
1. Open the project folder in Android Studio.
2. Open **Device Manager** and start a virtual device (e.g., Pixel 7 on Android 13/14).
3. Click the green **Run ▶** button.

### Option B: Using the Command Line (Linux)

> [!IMPORTANT]
> Many Linux distributions (Debian/Ubuntu `android-sdk` packages) install old copies of `adb`, `avdmanager` and `sdkmanager` into `/usr/bin`. These do **not** see the SDK in `~/Android/Sdk` and fail with errors like `Valid system image paths are: null`. The commands below therefore use explicit paths into the Android Studio SDK.

**0. Set the SDK location** (in every new terminal, or add it to `~/.bashrc`):
```bash
export ANDROID_HOME="$HOME/Android/Sdk"
export ANDROID_SDK_ROOT="$ANDROID_HOME"
export PATH="$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$PATH"
hash -r   # forget cached /usr/bin/... command locations
```
Verify that the SDK tools are picked up (not `/usr/bin/...`):
```bash
which avdmanager adb emulator
# expected: /home/<user>/Android/Sdk/... for all three
```
If `cmdline-tools/latest` is missing, install **Android SDK Command-line Tools (latest)** in Android Studio under *Settings > Languages & Frameworks > Android SDK > SDK Tools*.

**1. Check hardware acceleration (KVM):**
```bash
emulator -accel-check
# expected: "KVM (version 12) is installed and usable."
```
If KVM is not usable, enable virtualization (VT-x/AMD-V) in the BIOS and add your user to the `kvm` group, then log out and back in:
```bash
sudo apt install qemu-kvm      # Debian/Ubuntu
sudo usermod -aG kvm "$USER"
```

**2. Install a system image** (skip if already installed):
```bash
sdkmanager --install "system-images;android-33;google_apis_playstore;x86_64" "platform-tools" "emulator"
sdkmanager --licenses
```

**3. Create the virtual device** (one line):
```bash
avdmanager create avd -n Pixel_Test -k "system-images;android-33;google_apis_playstore;x86_64" --device "pixel"
```
Check that it exists with `emulator -list-avds`.

**4. Start the emulator:**
```bash
emulator -avd Pixel_Test &
```
If the window stays black or the emulator crashes because of GPU drivers, use software rendering:
```bash
emulator -avd Pixel_Test -gpu swiftshader_indirect &
```
On a headless machine or over SSH, add `-no-window -no-audio`.

**5. Wait until Android has finished booting:**
```bash
adb wait-for-device
until [ "$(adb shell getprop sys.boot_completed | tr -d '\r')" = "1" ]; do sleep 2; done
adb devices      # should list: emulator-5554   device
```

**6. Build and install the app:**
```bash
./gradlew installDebug
```

**7. Launch the app:**
```bash
adb shell am start -n com.logicbreadboard.simulator/.MainActivity
```

**8. (Optional) Run instrumentation tests on the emulator:**
```bash
./gradlew connectedAndroidTest
```

**Stop the emulator:**
```bash
adb emu kill
```

#### Troubleshooting

| Problem | Fix |
| --- | --- |
| `Valid system image paths are: null` | A `/usr/bin/avdmanager` is being used. Repeat step 0 and check with `which avdmanager`. |
| `Package path (-k) not specified` | The command was split across lines. Put it on one line or end each line with `\`. |
| `adb server version (...) doesn't match this client` | Two different `adb` versions are installed. Run `adb kill-server` and use only `$ANDROID_HOME/platform-tools/adb`. |
| `Running multiple emulators with the same AVD` | An emulator is already running (`adb devices`). Stop it with `adb emu kill`. |
| `AAPT2 ... Daemon startup failed` | Restart Gradle with `./gradlew --stop`, then run `./gradlew installDebug` again. |
| `SDK location not found` | Create `local.properties` in the project root containing `sdk.dir=/home/<user>/Android/Sdk`, or export `ANDROID_HOME`. |
| `Activity class ... does not exist` | The app is not installed yet. Run step 6 first. |

## Installing on a Physical Android Device

### 1. Enable Developer Options & USB Debugging
1. Open **Settings** > **About phone** on your device.
2. Tap **Build number** 7 times until developer options are unlocked.
3. In **Settings** > **System** > **Developer options**, enable **USB debugging** (and "Install via USB" if on Xiaomi/MIUI).

### 2. Connect and Verify
Connect your phone with a USB cable, accept the *"Allow USB debugging?"* prompt on the screen, and verify:
```bash
adb devices
```

### 3. Install
- **Direct install via Gradle:**
  ```bash
  ./gradlew installDebug
  ```
- **Or install pre-built APK via ADB:**
  ```bash
  adb install -r apk/LogicBreadboardSimulator-v1.1-debug.apk
  ```
- **Manual APK installation:** Download [`apk/LogicBreadboardSimulator-v1.1-debug.apk`](apk/LogicBreadboardSimulator-v1.1-debug.apk) (or use the freshly built `app/build/outputs/apk/debug/app-debug.apk`), transfer it to your phone (via Drive, email, or USB storage), and open it with a file manager to install.

## Useful Commands

- **Monitor Logs:**
  ```bash
  adb logcat -s MainActivity:V CRouter:V AndroidRuntime:E
  ```
- **Uninstall App:**
  ```bash
  adb uninstall com.logicbreadboard.simulator
  ```
- **Circuit Save Files Location on Device:**
  ```
  /sdcard/Android/data/com.logicbreadboard.simulator/files/
  ```

## Support

**No support is available for this application.** Bug reports, feature requests, compatibility issues, and usage questions are not actively monitored or maintained. This repository is published as an archive for reference and self-directed use.

## License

This project is licensed under the terms of the [MIT License](LICENSE).
