
# Rebuild the entire game from scratch
all: clean .kingdom .platforms/desktop .vanilla

# Clean up all generated files
clean:
	rm -rf out
	mkdir out

# Compile Nim source into the game executable
.kingdom:
	nim c src/kingdom.nim
	mv out/kingdom out/libkingdom.so

# Compile the vanilla game mod library
.vanilla:
	python3 modmanager.py build vanilla

# Build a platform interface library
.platforms/%:
	gcc -I platforms $(subst .platforms, platforms, $@)/platform.c -Lout -lkingdom -o out/game

# Lint the entire project
lint: lint-c lint-py

# Lint the platform interfaces
lint-c:
	cppcheck platforms

# Lint Python scripts
lint-py:
	python3 -m pylint ./**/*.py