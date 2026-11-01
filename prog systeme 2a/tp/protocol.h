#ifndef PROTOCOL_H
#define PROTOCOL_H

#ifdef _WIN32
    #define PID_FILE_PATH "serverPID"
#else  
    #define PID_FILE_PATH "/tmp/serverPID"
#endif

#define CLIENT_MAX 10

void die(int ret, const char* msg);
int protocol_receive(int fd, char* buff, size_t length);
int protocol_send(int fd, char* buffer, int length);



#endif