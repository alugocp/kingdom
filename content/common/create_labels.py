import json

# Cleans a label and converts it into part of a Java variable name
def clean(label: str) -> str:
    parts = label.split(" ")
    if parts[0] == "The":
        parts = parts[1:]
    cleaned = "_".join(parts)
    return cleaned.lower().replace("'", "").replace("-", "_").replace("é", "e").replace(",", "")


# Load the definitions JSON file (labels.json)
with open("content/common/labels.json", "r") as file:
    data = json.loads(file.read())


# Open the output file and start writing to it
with open("content/common/src/main/net/lugocorp/kingdom/mod/common/Labels.java", "w") as file:

    # Write the class and package declarations
    print("package net.lugocorp.kingdom.mod.common;", file=file)
    print("", file=file)
    print("/**", file=file)
    print(" * Contains definitions for names and labels across all official content mods", file=file)
    print(" */", file=file)
    print("public class Labels {", file=file)

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
            cleaned = clean(label)
            print(f"    public static final String {prefix}_{cleaned} = \"{label}\";", file=file)

    # Write the closing bracket
    print("}", file=file)
