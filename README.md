# Logic Breadboard Simulator

An Android application for interactive digital logic schematic editing, simulation, and virtual breadboard prototyping.

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

Environment setup (e.g. in `~/.bashrc`):
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

### Option B: Using the Command Line
1. Create an Android Virtual Device (AVD) if you haven't already:
   ```bash
   avdmanager create avd -n Pixel_Test -k "system-images;android-33;google_apis_playstore;x86_64" --device "pixel"
   ```
2. Start the emulator:
   ```bash
   emulator -avd Pixel_Test &
   ```
3. Build and install directly with Gradle:
   ```bash
   ./gradlew installDebug
   ```
4. Launch the app:
   ```bash
   adb shell am start -n com.logicbreadboard.simulator/.MainActivity
   ```
5. Run connected instrumentation tests on the emulator:
   ```bash
   ./gradlew connectedAndroidTest
   ```

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
  adb install -r app/build/outputs/apk/debug/app-debug.apk
  ```
- **Manual APK installation:** Transfer `app/build/outputs/apk/debug/app-debug.apk` to the phone (via Drive, email, or USB storage) and open it with a file manager to install.

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

## License

This project is licensed under the terms of the [MIT License](LICENSE).
