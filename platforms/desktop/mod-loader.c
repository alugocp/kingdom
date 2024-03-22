#include "interface.h"
#include <stdio.h>
#include <dlfcn.h>

int loadMod(const char *filepath, void *times) {
    printf("%s\n", filepath);
    char *error;
    void (*kingdom_mod_init)();

    // Open the mod binary
    void *mod = dlopen(filepath, RTLD_NOW | RTLD_LOCAL);
    if (!mod) {
        error = dlerror();
        printf("%s\n", error);
        return 1;
    }

    // Search for the init function and run it
    dlerror();
    kingdom_mod_init = (void (*)())dlsym(mod, "kingdom_mod_init");
    error = dlerror();
    if (error != NULL) {
        printf("%s\n", error);
        dlclose(mod);
        return 1;
    }

    // Run the init function and close the library
    (*kingdom_mod_init)(times);
    dlclose(mod);
    return 0;
}