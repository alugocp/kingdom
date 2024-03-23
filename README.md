# Kingdom Game
This is a cross between a resource management and character collector game set in a fantasy world.

## Development
You will need to install Nim in order to develop the game engine or mods.
I recommend using [choosenim](https://nim-lang.org/install_unix.html).

### Build
First you'll have to install everything needed for [Raylib](https://github.com/raysan5/raylib).
Then you can build the game engine from source like so:

```bash
# You will need to compile nimrtl.so before you can build this project
# First, find your installation of Nim and make note of the Path field
choosenim show

# Then, compile nimrtl.nim like so
nim c <Path>/lib/nimrtl.nim

# Install the Nimble dependencies
nimble install

# Run this to build the project for the first time
make

# Run this to just rebuild the game executable
make .kingdom

# Run this to rebuild a platform interface
make .platforms/desktop

# Run this to rebuild the vanilla game mod library
make .vanilla

# Run the game
./kingdom
```

## Notes
- [Nim compiler user guide](https://nim-lang.org/docs/nimc.html)