# Snake Game Android App

A Snake game implementation for Android using Java.

## Overview

This is a port of the classic Snake game to Android. The project includes:

- Touch-based controls
- Score tracking
- Pause/restart functionality
- Smooth gameplay with segment movement

## Requirements

To build and run this project, you need:

1. **Android Studio** or **Gradlew** to build the APK
2. **JDK 8 or higher** (but not JDK 11+)
3. **Android SDK** with NDK
4. **Android SDK Platform-Tools**
5. **Gradle** (1.4+)

## Setup

1. Clone this repository
2. Open `projects/my-bot` in Android Studio
3. Configure SDK paths if needed (check `gradle.properties`)
4. Update `google-services.json` if using Firebase (optional)

## Building and Running

### Using Android Studio

1. Open the project in Android Studio
2. Connect your Android device or start an emulator
3. Click "Run" button to build and install the app

### Using Command Line

```bash
cd projects/my-bot
./gradlew.sh
```

## Project Structure

```
projects/my-bot/
├── app/
│   ├── src/main/java/com/example/mygame/
│   │   ├── MainActivity.java        # Main activity
│   │   ├── GameView.java           # Game view and logic
│   │   ├── Segment.java            # Snake segment class
│   │   └── Food.java               # Food class
│   ├── src/main/res/layout/
│   │   └── activity_main.xml       # Layout file
│   ├── build.gradle               # App build configuration
│   └── ...                         # Other Android resources
├── gradlew.sh                      # Gradle wrapper script
├── settings.gradle                 # Project settings
└── ...                             # Optional config files
```

## Features

- **Touch Controls**: Snake can be controlled with touch gestures
- **Score System**: Score increases when eating food
- **Game Over**: Game ends on collision with walls or itself
- **Restart**: Tap restart button or screen to restart
- **Pause**: Game pauses when app is paused (e.g., phone call)

## Controls

- **Touch**: Move the snake direction
- **Restart Button**: Restart game after game over
- **Screen Tap**: Restart game on game over
- **Pause**: App enters pause state when minimized

## Known Issues

1. **Performance**: May have performance issues on older devices
2. **Controls**: Touch controls could be more responsive
3. **Assets**: Basic colors and shapes, could use better graphics

## Contributions

Fork this project and make changes. Consider:

1. Adding more food types
2. Implementing power-ups
3. Adding score-based difficulty increasing
4. Adding sound effects
5. Improving the UI/UX

## Troubleshooting

If you encounter build errors:

1. **Gradle version too high**: Ensure you're using Gradle 1.4+
2. **JDK issue**: Use JDK 8 if possible
3. **SDK missing**: Install required Android SDK components
4. **Run gradlew clean**: Try cleaning the build

## License

This project is licensed under the MIT License.