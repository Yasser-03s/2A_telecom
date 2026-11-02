#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <stdint.h>
#include <inttypes.h>
#include <string.h>
#include <assert.h>

#define M 1000000

int main(int argc, char *argv[])
{
  int     seed = 666;
  int64_t min  = 0;
  
  srand(seed);  
  
  array = (int64_t *)malloc(M * sizeof(int64_t));

  for(int64_t i = 0 ; i < M ; i++)
    array[i] = rand();

  min = array[0];
  for(int i = 1 ; i < M ; i++)
    if(array[i] < min)
      min = array[i];    

  free(array);
  
  fprintf(stdout,"End of main: Min is %" PRId64 "\n",min);
  return 0;  
}
