import json


# Cleans a label and converts it into part of a Java variable name
def clean(label: str) -> str:
    parts = label.split(" ")
    if parts[0] == "The":
        parts = parts[1:]
    cleaned = "_".join(parts)
    return (
        cleaned.lower()
        .replace("'", "")
        .replace("-", "_")
        .replace("é", "e")
        .replace(",", "")
    )


# Generates the actual labels files for each mod
def generate_labels(*modkeys):
    sections = {}

    # Load the definitions JSON file (labels.json)
    for modkey in modkeys:
        with open(f"content/assets/{modkey}.json", "r") as file:
            data = json.loads(file.read())
            for section in data["sections"]:
                name = section["name"]
                if not name in sections:
                    sections[name] = {"prefix": section["prefix"], "labels": []}
                sections[name]["labels"] += section["labels"]

    # Open the output file and start writing to it
    with open(
        f"content/src/main/java/net/lugocorp/kingdom/content/Labels.java", "w"
    ) as file:

        # Write the class and package declarations
        print(f"package net.lugocorp.kingdom.content;", file=file)
        print("", file=file)
        print("/**", file=file)
        print(
            f" * Contains definitions for names and labels from official game content",
            file=file,
        )
        print(" */", file=file)
        print("public class Labels {", file=file)

        # Write each section header
        for name in sections:
            section = sections[name]
            prefix = section["prefix"]
            print(f"", file=file)
            print(f"    /**", file=file)
            print(f"     * SECTION {name}", file=file)
            print(f"     */", file=file)

            # Write a variable for each label
            for label in section["labels"]:
                cleaned = clean(label)
                print(
                    f'    public static final String {prefix}_{cleaned} = "{label}";',
                    file=file,
                )

        # Write the closing bracket
        print("}", file=file)


# Generate those labels
generate_labels("vanilla")
