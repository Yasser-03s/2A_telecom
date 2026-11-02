#include <stdio.h>
#include <stdlib.h>

#include <omp.h>

#define N (15) 

int main(int argc, char *argv[])
{
  int num_threads = 4;
  
  if(2 == argc) {
    num_threads = atoi(argv[1]);
    omp_set_num_threads(num_threads);
  }
  
#pragma omp parallel 
  {
    fprintf(stdout,"Thread number %i says hello!\n",omp_get_thread_num());

#pragma omp for
    for(int i = 0 ; i < N ; i++){
      fprintf(stdout,"Thread number %i in index %i\n",omp_get_thread_num(),i);
    }    

  }
  
  return 0;  
}
