#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <stdint.h>
#include <inttypes.h>
#include <semaphore.h>
#include <sys/time.h>
#include <string.h>
#include <assert.h>

#define M 1000000

pthread_mutex_t minlock;
int64_t global_min;

typedef struct th_arg{
  int64_t *base_ptr;
  int64_t num_elems;
} th_arg_t;

void *sort(void *arg)
{
  int64_t *ptr   = ((th_arg_t *)arg)->base_ptr;
  int64_t nelems = ((th_arg_t *)arg)->num_elems;
  int64_t min    = *ptr;
  
  for(int64_t i = 1 ; i < nelems ; i++)
    if( ptr[i] < min)
      min = ptr[i];
  
  pthread_mutex_lock(&minlock);
  if( min <  global_min)
    global_min = min ;  
  pthread_mutex_unlock(&minlock);
  
  pthread_exit(NULL);
}

int main(int argc, char *argv[])
{
  int64_t    min;
  int64_t   *array = NULL;
  pthread_t *tid = NULL;
  th_arg_t  *args = NULL;

  int num_threads = 10;
  int seed = 666;
  
  if(3 == argc) {
    num_threads = atoi(argv[1]);
    seed = atoi(argv[2]);
  }
    
  fprintf(stdout,"creating %i threads\n",num_threads);
  srand(seed);  
  
  tid   = (pthread_t *)malloc(num_threads * sizeof(pthread_t));
  args  = (th_arg_t *)malloc(num_threads * sizeof(th_arg_t));
  array = (int64_t *)malloc(M * sizeof(int64_t));

  for(int64_t i = 0 ; i < M ; i++)
    array[i] = rand();

  global_min = array[0];

  pthread_mutex_init(&minlock,NULL);
  
  for(int i = 0 ; i < num_threads ; i++){
    args[i].base_ptr  = array + i*(M/num_threads);
    args[i].num_elems = M/num_threads;
  }
  args[num_threads-1].num_elems += M%num_threads;
  
  for(int i = 0 ; i < num_threads ; i++)
    pthread_create(&tid[i],NULL,sort,(void *)&(args[i]));
  
  for(int i = 0 ; i < num_threads ; i++)
    pthread_join(tid[i],NULL);

  /* sanity check */
  min = array[0];
  for(int i = 1 ; i < M ; i++)
    if(array[i] < min)
      min = array[i];    

  assert(min == global_min);
  
  free(tid);
  free(args);
  free(array);
  
  fprintf(stdout,"End of main: Min is %" PRId64 "\n",global_min);
  return 0;  
}
