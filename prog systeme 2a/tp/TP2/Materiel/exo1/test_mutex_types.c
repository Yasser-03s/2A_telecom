#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <unistd.h>
#include <errno.h>
#define _GNU_SOURCE
#include <pthread.h>
#include <sys/time.h>
#include <signal.h>

#define NUM_THREADS 2

pthread_mutex_t     mutex;
pthread_mutexattr_t attr;
pthread_t           tid[NUM_THREADS];

struct itimerval timer_on = {.it_interval = {.tv_sec = 0, .tv_usec = 0},
                             .it_value    = {.tv_sec = 5, .tv_usec = 0}};

struct itimerval timer_off = {.it_interval = {.tv_sec = 0, .tv_usec = 0},
                              .it_value    = {.tv_sec = 0, .tv_usec = 0}};

/* Signal handlers for possible deadlock management */
/* only for Test #3  (V2) */
void proper_end(int sig)
{
  pthread_exit(NULL);
}

void clean_up(int sig)
{

  fprintf(stdout,"TIMER TRIGGERED: a possible deadlock prevents a thread from finishing\n");
  fprintf(stdout,"===> Forced termination\n");
  /* disarm timer */
  setitimer(ITIMER_REAL,&timer_off,NULL);
  
  for(int i = 0; i < NUM_THREADS; i++)
    {
      int ret   = pthread_kill(tid[i],0);
      char *str = ret > 0 ? "already finished.\n" : "alive (and possibly blocked) => ";
      fprintf(stdout,"Thread %i status: %s",i,str);

      if (ret == 0) {
	if (pthread_equal(pthread_self(),tid[i]))
	  pthread_exit(NULL);
	else {
	  fprintf(stdout,"Terminating Thread %i ...",i);
	  pthread_kill(tid[i],SIGUSR1);
	  fprintf(stdout," done\n");
	}
      }
    }
}

/* thread function to execute and assess */

void *function1(void *arg)
{
  pthread_mutex_lock(&mutex);
  fprintf(stdout,"Function 1\n");

  int ret = pthread_mutex_lock(&mutex);
  if(ret == EDEADLK) //j'ai deja pris ce verrou, je ne peux pas le reprendre
    fprintf(stdout,"Deadlock in function 1 : impossible to lock mutex again\n");

  fprintf(stdout,"Function 1 ... again\n");

  pthread_exit(NULL);
}

void *function2(void *arg)
{
  int ret = pthread_mutex_unlock(&mutex);
  if(ret == EPERM)//je n'ai pas le droit de liberer ce verrou je ne suis pas le proprietaire
    fprintf(stdout,"Could not unlock mutex in Function 2\n");
  
  fprintf(stdout,"Function 2\n");

  pthread_exit(NULL);
}

int main(int argc, char *argv[])
{
  void *(*(f_ptr[NUM_THREADS]))(void *) = {function1, function2};  
  int ret = 0;
  int idx;

  /* possible deadlock management for Test #3 (V2) */
  struct sigaction action  = {0};
  struct sigaction action2 = {0};
  action.sa_handler  = clean_up;
  sigaction(SIGALRM,&action,NULL);
  action2.sa_handler = proper_end;
  sigaction(SIGUSR1,&action2,NULL);
  /* end of deadlock management for T3, V2 */
  
  /* test 1 */
  fprintf(stdout,"======== TESTING ERRORCHECK MUTEXES =======\n");
  pthread_mutexattr_init(&attr);
  ret = pthread_mutexattr_settype(&attr,PTHREAD_MUTEX_ERRORCHECK);
  assert(!ret);
  pthread_mutex_init(&mutex,&attr);
  
  for(idx = 0 ; idx < NUM_THREADS ; idx++){
    pthread_create(&tid[idx],NULL,f_ptr[idx],NULL);
  }
  
  for(idx = 0 ; idx < NUM_THREADS ; idx++){
    pthread_join(tid[idx],NULL);
  }
  pthread_mutexattr_destroy(&attr);    
  fprintf(stdout,"===========================================\n");

  sleep(5);

  fprintf(stdout,"======= TESTING ERRORCHECK MUTEXES (V2) ===\n");
  pthread_mutexattr_init(&attr);
  ret = pthread_mutexattr_settype(&attr,PTHREAD_MUTEX_ERRORCHECK);
  assert(!ret);
  pthread_mutex_init(&mutex,&attr);
  
  for(idx = NUM_THREADS - 1 ; idx >= 0 ; idx--){
    pthread_create(&tid[idx],NULL,f_ptr[idx],NULL);
  }
  
  for(idx = 0 ; idx < NUM_THREADS ; idx++){
    pthread_join(tid[idx],NULL);
  }
  pthread_mutexattr_destroy(&attr);
  fprintf(stdout,"===========================================\n");

  sleep(5);
  
  /* test 2 */
  fprintf(stdout,"======== TESTING RECURSIVE MUTEXES ========\n");
  pthread_mutexattr_init(&attr);
  ret = pthread_mutexattr_settype(&attr,PTHREAD_MUTEX_RECURSIVE);
  assert(!ret);
  pthread_mutex_init(&mutex,&attr);
  
  for(idx = 0 ; idx < NUM_THREADS ; idx++){
    pthread_create(&tid[idx],NULL,f_ptr[idx],NULL);
  }
  
  for(idx = 0 ; idx < NUM_THREADS ; idx++){
    pthread_join(tid[idx],NULL);
  }
  pthread_mutexattr_destroy(&attr);    
  fprintf(stdout,"===========================================\n");

  sleep(5);

  fprintf(stdout,"======= TESTING RECURSIVE MUTEXES (V2) ====\n");
  pthread_mutexattr_init(&attr);
  ret = pthread_mutexattr_settype(&attr,PTHREAD_MUTEX_RECURSIVE);
  assert(!ret);
  pthread_mutex_init(&mutex,&attr);
  
  for(idx = NUM_THREADS - 1 ; idx >= 0 ; idx--){
    pthread_create(&tid[idx],NULL,f_ptr[idx],NULL);
  }
  
  for(idx = 0 ; idx < NUM_THREADS ; idx++){
    pthread_join(tid[idx],NULL);
  }
  pthread_mutexattr_destroy(&attr);    
  fprintf(stdout,"===========================================\n");

  sleep(5);
  
  /* test 3 */
  fprintf(stdout,"======== TESTING DEFAULT (FAST) MUTEXES ====\n");
  fprintf(stdout,"======== WARNING: THIS TEST MIGHT NOT FINISH PROPERLY\n");
  fprintf(stdout,"========          A TIMER HAS BEEN ARMED TO FORCE TERMINATION\n");

  setitimer(ITIMER_REAL,&timer_on,NULL);
  
  pthread_mutexattr_init(&attr);
  ret = pthread_mutexattr_settype(&attr,PTHREAD_MUTEX_DEFAULT);
  assert(!ret);
  pthread_mutex_init(&mutex,&attr);
  
  for(idx = 0 ; idx < NUM_THREADS ; idx++){
    pthread_create(&tid[idx],NULL,f_ptr[idx],NULL);
  }
  
  for(idx = 0 ; idx < NUM_THREADS ; idx++){
    pthread_join(tid[idx],NULL);
  }
  pthread_mutexattr_destroy(&attr);    
  fprintf(stdout,"===========================================\n");

  sleep(5);

  fprintf(stdout,"======== TESTING DEFAULT (FAST) MUTEXES (V2)\n");
  fprintf(stdout,"======== WARNING: THIS TEST MIGHT NOT FINISH PROPERLY\n");
  fprintf(stdout,"========          A TIMER HAS BEEN ARMED TO FORCE TERMINATION\n");

  setitimer(ITIMER_REAL,&timer_on,NULL);
  
  pthread_mutexattr_init(&attr);
  ret = pthread_mutexattr_settype(&attr,PTHREAD_MUTEX_DEFAULT);
  assert(!ret);
  pthread_mutex_init(&mutex,&attr);

  for(idx = NUM_THREADS - 1 ; idx >= 0 ; idx--){
    pthread_create(&tid[idx],NULL,f_ptr[idx],NULL);
  }

  for(idx = 0 ; idx < NUM_THREADS ; idx++)
    pthread_join(tid[idx],NULL);
  
  pthread_mutexattr_destroy(&attr);    
  fprintf(stdout,"=========================================\n");
  
  return 0;
}
