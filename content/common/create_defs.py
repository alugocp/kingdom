import json

# Cleans a label and converts it into part of a Java variable name
def clean(label: str) -> str:
    parts = label.split(" ")
    if parts[0] == "The":
        parts = parts[1:]
    cleaned = "_".join(parts)
    return cleaned.lower().replace("'", "").replace("-", "_").replace("é", "e")


# Load the definitions JSON file (defs.json)
with open("content/common/defs.json", "r") as file:
    data = json.loads(file.read())


# Open the output file and start writing to it
with open("content/common/src/main/net/lugocorp/kingdom/mod/common/Defs.java", "w") as file:

    # Write the package declaration
    print("package net.lugocorp.kingdom.mod.common;", file=file)

    # Write the required import statements
    for i in data["imports"]:
        print(f"import {i};", file=file)

    # Write the class definition
    print("", file=file)
    print("/**", file=file)
    print(" * Contains definitions for names and labels across all official content mods", file=file)
    print(" */", file=file)
    print("public class Defs {", file=file)

    # Write each section header
    for section in data["sections"]:
        name = section["name"]
        prefix = section["prefix"]
        print(f"", file=file)
        print(f"    /**", file=file)
        print(f"     * SECTION {name}", file=file)
        print(f"     */", file=file)

        # Write a variable for each label
        for label in section["labels"]:

            # Species are handled differently
            if name == "Species":
                if type(label) == str:
                    cleaned = clean(label)
                    print(f"    public static final Species {prefix}_{cleaned} = new Species(\"{label}\");", file=file)
                else:
                    species = label[1]
                    cleaned1 = clean(label[0])
                    cleaned2 = clean(label[1])
                    print(f"    public static final Species {prefix}_{cleaned2} = new Species(\"{species}\", {prefix}_{cleaned1});", file=file)

            # Standard variable output
            else:
                cleaned = clean(label)
                print(f"    public static final String {prefix}_{cleaned} = \"{label}\";", file=file)

    # Write the closing bracket
    print("}", file=file)
