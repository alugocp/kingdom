
# Rebuild the entire game from scratch
all: clean .platforms/desktop .kingdom .vanilla

# Clean up all generated files
clean:
	rm -rf out
	mkdir out

# Compile Nim source into the game executable
.kingdom:
	nim c src/main.nim

# Compile the vanilla game mod library
.vanilla:
	nim c vanilla/main.nim

# Build a platform interface library
.platforms/%:
	gcc -I platforms -c $(subst .platforms, platforms, $@)/mod-loader.c -o out/libplatform.so
