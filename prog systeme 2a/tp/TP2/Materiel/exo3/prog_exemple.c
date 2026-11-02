#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <assert.h>
#include <sys/stat.h>

int main(int argc, char *argv[])
{
  int fd    = open("output.txt", O_RDWR | O_CREAT | O_EXCL, S_IRWXU);
  int ret   = 0;
  
  assert(fd != -1);

  fprintf(stdout,"Writing to file ... \n");
  
  for(int i = 0 ; i < 100000 ; i++){
    char c = 'a' + (char)(i%26);
    ret = write(fd,&c,sizeof(char));
    assert(1 == ret);
  }

  fprintf(stdout,"Done writing file\n");

  close(fd);
  unlink("output.txt");
  
  exit(EXIT_SUCCESS);
}

