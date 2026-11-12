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
- Android NDK and CMake
- JDK 17 or later

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

## License

This project is licensed under the terms of the [MIT License](LICENSE).

