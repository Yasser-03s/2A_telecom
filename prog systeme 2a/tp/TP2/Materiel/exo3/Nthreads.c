#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <assert.h>
#include <pthread.h>

void *thread_func(void* arg){
    int toto= 0;
    while(++toto);
    pthread_exit(NULL);
}


int main(int argc, char* argv[]){
    assert(argc==2);
    int nthreads= atoi(argv[1]);
    pthread_t tid[nthreads];
    for (int i=0; i<nthreads; i++){
        pthread_create(tid+i,NULL,thread_func,NULL);
        printf("Thread num: %d entred!\n", i+1);
    }

    for (int i=0; i<nthreads; i++){
        pthread_join(tid[i],NULL);
        printf("Thread num: %d done!\n", i+1);
    }
    return 0;
}