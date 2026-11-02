#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <string.h>
#include <assert.h>
#include <signal.h>
#include <sys/time.h>

void handler(int sig){
  if(SIGALRM == sig){
    fprintf(stdout,"===== Autodestruct for process data_exchange (pid %i)\n",getpid());
    fflush(stdout);
    exit(EXIT_FAILURE);
  }
}


int main(int argc, char *argv[])
{
  fprintf(stdout,"Process %i running ...\n",getpid());

  /* Arm a timer to stop the process after 2 minutes */
  struct sigaction action ;
  memset(&action,0,sizeof(struct sigaction));
  action.sa_handler = handler;
  sigaction(SIGALRM,&action,NULL);
  struct itimerval timer = {.it_interval = {.tv_sec = 0,   .tv_usec = 0},
                            .it_value    = {.tv_sec = 120, .tv_usec = 0}};
  setitimer(ITIMER_REAL, &timer, NULL);
  
  while(1);
  
  exit(EXIT_SUCCESS);
}

