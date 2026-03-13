# Legends of T'ahn
![Static Badge](https://img.shields.io/badge/Version-1.0.0--alpha-a?color=%23009900)

Legends of T'ahn is a turn-based strategy game written in Java with the [libGDX](https://libgdx.com/) library.
Manage resources and train units as you explore and maintain the game world.

No generative AI tools were used during development of this project.

## Dependencies
![Static Badge](https://img.shields.io/badge/Java-17-a?color=%23ED8B00)
![Static Badge](https://img.shields.io/badge/libGDX-1.14.0-a?color=%23E74A45)
![Static Badge](https://img.shields.io/badge/Gradle-8-a?color=%23209BC4)
![Static Badge](https://img.shields.io/badge/Python-3.11-a?color=%23FFD43B)

## Getting Started
Run the following commands after cloning the repo:

```bash
gradle installHooks cleanAssets run
```

## Gradle
This project uses [Gradle](https://gradle.org/) as a build system and dependency manager.
- `gradle installHooks`: installs git hooks for the project
- `gradle build`: builds sources and archives of every project.
- `gradle clean`: removes `build` folders, which store compiled classes and built archives.
- `gradle lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `gradle lwjgl3:run`: starts the application.
- `gradle test`: runs unit tests (if any).
- `gradle spotlessApply`: reformats the business logic code
- `gradle cleanAssets`: generates g3db files for game assets
- `gradle exportGame`: copies the built standalone JAR file to the project root directory

## 3D Models
You can create new models for this game using Blockbench and [fbx-conv](https://github.com/libgdx/fbx-conv).
Export a Blockbench project as an `obj` file (this will also generate `mtl` and `png` files).
Then, run `fbx-conv -f <filename>.obj <filename>.g3db` to make the model usable by the game code.
You can also run `gradle cleanAssets` to convert all models at once.

## Modifying content
If you change the 3D models then run `gradle cleanAssets` *before* rerunning `gradle build` or `gradle run`.

If you want to add new content to an official mod, modify a `content/**/labels.json` file and then run the following:

```bash
python3 create_labels.py
```

This will re-generate the `Labels` classes that each official mod uses.

## Notes
- [libGDX JavaDocs](https://javadoc.io/doc/com.badlogicgames.gdx/gdx/latest/index.html)
