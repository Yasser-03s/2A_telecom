#include <stdio.h>
#include <stdlib.h>

#include <omp.h>


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
  }
  
  return 0;  
}
