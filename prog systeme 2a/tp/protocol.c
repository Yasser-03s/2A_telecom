
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <assert.h>
#include <fcntl.h>
#include <sys/stat.h>

void die(int ret, const char* msg){
    if (ret<0){
        perror(msg);
        exit(EXIT_FAILURE);
    }
}

int protocol_receive(int fd, char* buff, size_t length){
    int ret= -1;
    int received= 0;
    do{
        ret= read(fd, (char*)buff + received, length-received);
        die(ret,"erreur receive");
        if (ret==0){
            printf("done reading!\n");
            break;
        }
        received+= ret;
    }while(received< length);
    return 1;
}

int protocol_send(int fd, char* buffer, int length){
    int written= 0;
    int ret= -1;
    do{
        ret= write(fd, (char*)buffer+written, length-written);
        die(ret,"erreur send");
        written+= ret;
    } while(written!= length);
    return 1;
}