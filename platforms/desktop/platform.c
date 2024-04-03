#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>
#include "interface.h"

void inflate_mod(char *modname) {
    // Set up target filepath
    char filepath[255];
    sprintf(filepath, "%s/%s.zip", modname, modname);

    // Fork the process to inflate a ZIP
    int status;
    pid_t child = fork();
    if (child == 0) {
        char *argv[] = { "unzip", filepath, "-d", "out/mods", NULL };
        status = execvp(argv[0], argv);
        if (status < 0) {
            sprintf(filepath, "Error while inflating mod '%s'", modname);
            printf("%s\n", filepath);
        }
        exit(status < 0 ? 1 : 0);
    } else {
        waitpid(child, &status, 0);
    }
}

int main(int n, char **argv) {
    init_kingdom();
    return 0;
}