//definir une limite de clients?
//le pid doit etre connu.. je le met dans un fichier tmp
//cree un processus par client 



#include <stdio.h>
#include <unistd.h>
#include <signal.h>
#include <stdlib.h>
#include <assert.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <string.h>

#include "protocol.h"



int main(int argc, char* argv[]){
    char pid[128];
    sprintf(pid,"%d"+'\0',getpid());
    int length= strlen(pid);
    printf("le pid: %s de taille: %d\n", pid, length);
    int fd_pid= -1;
    fd_pid= open(PID_FILE_PATH, O_WRONLY|O_CREAT|O_TRUNC, S_IRWXU);
    die(fd_pid,"fichier pas ouvert");
    assert(protocol_send(fd_pid ,pid, length));
    close(fd_pid);
    
    printf("serveur à l'écoute");
    while(1){
        
    }

    return 0;
}