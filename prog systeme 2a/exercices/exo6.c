//Écrivez un programme qui écrit un entier dans un fichier (i.e. pas sous forme de chaîne de
//caractères). Dans un terminal, affichez le contenu de ce fichier (à l’aide d’une commande shell
//comme cat, less ou more et non pas d’un programme C).

#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <assert.h>
#include <unistd.h>
#include <string.h>

int main(int argc, char *argv[]){
    int fd = -1;
    int value = 42;


    
    fd = open("entier.txt",O_RDWR | O_CREAT, S_IRWXU);
    assert(fd > 0);

    int ret = write(fd, &value, sizeof(value));
    assert(ret > 0);



    exit(EXIT_SUCCESS);
}