from typing import Tuple, List
import subprocess
import sys
import re
import os

# Platforms supported in the mod manager
PLATFORMS = [
    "linux"
]

# Writes some string contents to a filepath
def create_file(path: str, contents: List[str]):
    with open(path, "w", encoding="utf-8") as file:
        file.write("\n".join(contents))

# Initialize a new mod project
def create_new_mod(name) -> Tuple[bool, str]:

    # Check mod name
    if not re.match(r"^\w+$", name):
        return False, f"Invalid mod name \"{name}\""

    # Create directory
    if os.path.exists(name):
        return False, "Mod directory already exists"
    os.makedirs(name)

    # Populate files
    create_file(f"{name}/.gitignore", [
        "/out",
        "/%s.zip" % (name)
    ])
    create_file(f"{name}/archive.json", [
        "{",
        "    \"version\": 1.0,",
        "    \"name\": \"%s\"" % (name),
        "}"
    ])
    create_file(f"{name}/nim.cfg", [
        "#nim.cfg",
        "-d:useNimRtl",
        "--app:lib",
        "path=\"<path to kingdom game src directory>\""
    ])
    create_file(f"{name}/mod.nim", [
        "import kingdom/game",
        "",
        "proc initKingdomMod(game: Game): void {.exportc,dynlib.} =",
        "    echo(\"Write your mod here!\")"
    ])
    return True, None


# Build the mod project into a ZIP file
def build_mod_archive(name) -> Tuple[bool, str]:

    # Create out directory
    if not os.path.exists(f"{name}/out"):
        os.makedirs(f"{name}/out")

    # Compile the mod for each platform
    for platform in PLATFORMS:
        print(f"Compiling {platform}...")
        subprocess.run(["nim", "c", f"--os:{platform}", f"--out:{name}-{platform}", f"{name}/mod.nim"])
        os.rename(f"{name}-{platform}", f"{name}/out/{name}-{platform}")

    # Zip the files together
    print("Packaging the mod library...")
    subprocess.run(["zip", f"{name}/{name}.zip", f"{name}/archive.json", f"{name}/out/{name}-linux"])
    print("Mod packaging complete!")
    return True, None


# CLI argument processing
if __name__ == "__main__":
    if len(sys.argv) == 3 and sys.argv[1] in ["init", "build"]:
        success, msg = create_new_mod(sys.argv[2]) if sys.argv[1] == "init" else build_mod_archive(sys.argv[2])
        if not success:
            print(msg)
        sys.exit(0 if success else 1)
    print("Usage: python modmanager.py <command> <mod name>")
    print("   init  -  Initializes an empty mod project")
    print("   build -  Packages a mod project")
    sys.exit(0)
