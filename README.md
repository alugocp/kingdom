# Kingdom
A turn-based strategy game written in Java with [libGDX](https://libgdx.com/).
Manage resources and train units as you grow your empire and change the world around you.

## Getting Started
Run the following commands after a fresh install:

```bash
gradle cleanAssets run
```

## Gradle
This project uses [Gradle](https://gradle.org/) as a build system and dependency manager.
- `gradle build`: builds sources and archives of every project.
- `gradle clean`: removes `build` folders, which store compiled classes and built archives.
- `gradle lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `gradle lwjgl3:run`: starts the application.
- `gradle test`: runs unit tests (if any).
- `gradle spotlessApply`: reformats the business logic code
- `gradle cleanAssets`: generates g3db files for game assets

## 3D Models
You can create new models for this game using Blockbench and [fbx-conv](https://github.com/libgdx/fbx-conv).
Export a Blockbench project as an `obj` file (this will also generate `mtl` and `png` files).
Then, run `fbx-conv -f <filename>.obj <filename>.g3db` to make the model usable by the game code.
You can also run `gradle cleanAssets` to convert all models at once.

## Modifying content
If you modify code under the `content` directory then you must run the following before playing the game again:

```bash
rm -f mods/*.jar
gradle content:vanilla:build
```

This will recompile the content JARs located in `mods`.

## Notes
- [libGDX JavaDocs](https://javadoc.io/doc/com.badlogicgames.gdx/gdx/latest/index.html)
