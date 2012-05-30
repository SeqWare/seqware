#include "ReadsIndex.h"
#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>

int main(int argc, char *argv[]) 
{
  if (argc < 2) {
    fprintf(stderr, "Syntax: ./raw_reads file_name.slo\n");
    exit(1);
  }

  FILE *fp = fopen(argv[1], "r");
  ReadsIndex index;
  ReadsIndexInitialize(&index);
  ReadsIndexCreate(&index, fp);

printf("1\n");
  ReadsIndexDelete(&index);
printf("2\n");
  exit(0);
}
