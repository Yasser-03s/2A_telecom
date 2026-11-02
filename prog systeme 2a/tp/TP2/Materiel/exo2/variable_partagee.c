#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
//#include <stdatomic.h>
#include <signal.h>

#define NTHREADS (2)
#define NTIMES (100000000)

//pthread_mutex_t mutex= PTHREAD_MUTEX_INITIALIZER;

//int x= 0;
_Atomic int x=0;

void *thread_func(void *arg){
    
    for (int i=0; i<NTIMES; i++){
        //pthread_mutex_lock(&mutex);
        x++;
        //pthread_mutex_unlock(&mutex);
    } 
    pthread_exit(NULL);
    
}

int main(int argc, char* argv[]){

    pthread_t tid[NTHREADS];
    for (int i= 0; i<NTHREADS; i++)
        pthread_create(tid+i,NULL,thread_func,NULL);

    for (int i= 0; i<NTHREADS; i++)
        pthread_join(tid[i],NULL);
    //pthread_mutex_destroy(&mutex);   
    printf("Finally x= %d\n", x);
    return 0;
}

