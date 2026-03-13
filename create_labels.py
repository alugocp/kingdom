import json

# Cleans a label and converts it into part of a Java variable name
def clean(label: str) -> str:
    parts = label.split(" ")
    if parts[0] == "The":
        parts = parts[1:]
    cleaned = "_".join(parts)
    return cleaned.lower().replace("'", "").replace("-", "_").replace("é", "e").replace(",", "")

# Generates the actual labels files for each mod
def generate_labels(modkey):

    # Load the definitions JSON file (labels.json)
    with open(f"content/{modkey}/labels.json", "r") as file:
        data = json.loads(file.read())


    # Open the output file and start writing to it
    with open(f"content/src/main/java/net/lugocorp/kingdom/content/{modkey}/Labels.java", "w") as file:

        # Write the class and package declarations
        print(f"package net.lugocorp.kingdom.content.{modkey};", file=file)
        print("", file=file)
        print("/**", file=file)
        print(f" * Contains definitions for names and labels across the official {modkey} mods", file=file)
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

# Generate those labels
generate_labels("vanilla")
