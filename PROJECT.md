## Project Layout
This file explains all the code folders inside this repository.

- `content` defines all content in the game
    - `assets` contains all the assets for all content
    - `src` contains all Java code for all content

<br>

- `core` contains all business logic to make the game happen on top of LibGDX
    - `ai` implements our CPU player logic
        - `goals` contains a collection of abstract gameplay requirements that the CPU player strives towards
        - `memory` allows CPU players to remember what they previously saw under the fog of war
        - `prediction` allows CPU players to better choose from options (with event handlers)
        - `stats` allows CPU players to track their performance
    - `builtin`
        - `animation` contains a set of game animation utilities
        - `logic` congregates useful logic for defining game content
    - `color` deals with color value logic
    - `engine` provides an interface between the game logic and LibGDX
        - `animation` defines our animation system
        - `assets` loads all of our assets, including models and music
        - `controllers` allows user input to travel to our menus and the game view
        - `fonts` contains utility classes for using different font styles
        - `projection` contains logic for translating mouse input to different coordinate systems
        - `render` defines common interfaces for renderable elements
        - `shaders` allows us to use GLSL shaders to render the game
        - `userdata` contains definitions of shader inputs attached to game elements
    - `game` defines all entities and aspects of the game world
        - `glyph` handles logic for glyphs
        - `layers` contains interfaces and shared logic for entities in the game world
        - `model` defines all entities within the game
        - `player` implements logic for players of the game
        - `properties` defines fields that are relevant to entities within the game world
        - `unit` contains logic that is only relevant to units
        - `world` implements the game world itself
    - `gameplay` contains all systems that drive gameplay
        - `actions` keeps track of which units can do what in each turn
        - `combat` contains all logic for combat
        - `events` implements the event handler system
        - `future` helps keep track of scheduled events in the game
        - `mechanics` defines the systems behind game mechanics
    - `math` contains helpful implementations for mathematical concepts
    - `menu` defines the entire menu system
        - `game` contains menu elements that are only used in the game view
        - `icon` contains icon-based menu elements
        - `input` contains menu elements that handle user input
        - `structure` contains structural menu elements
        - `text` contains text-based menu elements
    - `mods` allows us to load game content
    - `pathfinding` implements pathfinding algorithms for the game UI
    - `serial` allows us to save and load game state
    - `settings` allows us to load and save game settings
    - `ui` contains all logic for game UI
        - `hud` handles the heads-up display in the game view
        - `overlay` defines logic for rising text and symbols in the game view
        - `selection` defines how user clicks work in the game world
        - `tutorial` controls the popup-based in-game tutorial
        - `views` contains all major view classes in the game
    - `utils` has code that is useful throughout `core`
