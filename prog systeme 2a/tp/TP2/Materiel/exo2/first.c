#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>

#define NTHREADS (2)


volatile int x = 0 ;

void *thread1(void *arg)
{
  sleep(2);
  
  fprintf(stdout, "Thread 1: Setting x to 333\n");

  x = 333;
  
  fprintf(stdout, "Thread 1: Exiting\n");

  pthread_exit(NULL);
}

void *thread2(void *arg)
{

  fprintf(stdout, "Thread 2: Waiting for 333\n");

  while (x != 333); // Attente active

  fprintf(stdout, "Thread 2: Got 333 -- exiting!\n");

  pthread_exit(NULL);
}

int main(int argc, char *argv[])
{
  pthread_t tid[NTHREADS];
  void *(*thread_func[])(void *) = {thread1, thread2};

  for(int i = 0; i < NTHREADS ; i++)
    pthread_create(tid+i, NULL, thread_func[i], NULL);

  for(int i = 0; i < NTHREADS ; i++)
    pthread_join(tid[i], NULL);

  fprintf(stdout, "Main    : Threads are done, so x better be 333\n");
  fprintf(stdout, "Main    : And indeed, x == %d\n", x);

  exit(EXIT_SUCCESS);
}
