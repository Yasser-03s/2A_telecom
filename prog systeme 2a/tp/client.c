//envoyer numero avec un signal à l'aide de sigqueue
// exo3 est page 89

#define _POSIX_C_SOURCE 199309L


#include <stdio.h>
#include <unistd.h>
#include <signal.h>
#include <assert.h>
#include <string.h>
#include <fcntl.h>
#include <signal.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <stdlib.h>


#include "protocol.h"


int main(int argc, char* argv[]){
    //chercher le pid du serveur
    int fd_pid_server= -1;
    char buffer[128];
    fd_pid_server= open(PID_FILE_PATH, O_RDONLY);
    die(fd_pid_server,"erreur de lecture du PID du serveur");
    printf("fichier ouvert!\n");
    assert(protocol_receive(fd_pid_server, buffer, 128));
    close(fd_pid_server);
    printf("received!\n");
    int pid_server=atoi(buffer);
    printf("%d\n", pid_server);
    //creation des tubes
    int pid= getpid();
    //pipe1 pour ecriture
    char nom_pipe1[128];
    //pipe2 pour lecture
    char nom_pipe2[128];
    sprintf(nom_pipe1, "/tmp/PIPE_%d_numero1", pid);
    sprintf(nom_pipe2, "/tmp/PIPE_%d_numero2", pid);
    assert(mkfifo(nom_pipe1, S_IWUSR)!=-1);
    assert(mkfifo(nom_pipe2, S_IRUSR)!=-1);
    printf("pipes créées avec succès!\n");
    //client va envoyer au serveur un signal de type SIGUSR1, avec deux numéros qui vont
    //permettre d’identifier deux tubes nommés
    union sigval id;
    id.sival_int= 12;
    int ret= -1;
    ret= sigqueue(pid_server, SIGUSR1, id);
    die(ret,"sigqueue failure");
}