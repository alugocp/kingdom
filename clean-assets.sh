#!/bin/bash

# Enter the /assets folder
pushd assets

# Grab a list of our .obj files
projects=($(ls *.obj))

# Convert each one using this Blockbench utility
for model in "${projects[@]}"; do
    filename=${model%.obj}
    echo "Converting $filename..."
    fbx-conv -f "$filename.obj" "$filename.g3db"
done

# Return to the previous working directory
popd