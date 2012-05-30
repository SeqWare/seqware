#include <stdlib.h>
#include <stdio.h>

#include "ReadsIndex.h"

/* Create an index */
void ReadsIndexCreate(ReadsIndex *index, FILE *fp)
{
	/* TODO */
	/* You will want to change the parameter list
	 * to include the data-structure for the reads
	 * as a parameter.  Remember to check all return
	 * codes for allocations.
	 * */

  // Read into data structure from SLO
  GetReadsFromSLO(fp, index);

  // Test print everything
  int i;
  for ( i = 0 ; i < index->numPositions ; i++ ) {
//    printf("%d XX %s \n", index->Reads[i].position , index->Reads[i].read );
  }

  // Sort it. Look in bfast for mergesort

/*
printf( "read: %s\n", r->read );
printf( "chr: %c\n", r->contig );
printf( "position: %d\n", r->position );
printf( "orientation: %c\n", r->orientation );
printf( "ref: %s\n", r->ref );
printf("while done\n");
*/
}


/* Parse a SLO file into a Read[]. Return 1 on success, false on problem */
int GetReadsFromSLO(FILE *fp, ReadsIndex *index) {
  if ( fp == NULL ) {
    fprintf( stderr, "Could not open file\n");
    return -1;
  }

  // FIXME: 10000 should be a parameter
  char line [100000];
  while ( fgets(line, 10000, fp) != NULL && line != "\n") {
    /* Skip if line is commented out */
    if ( line[0] == '#' )
      continue;

    /* Create space */
    // Every 1000 reads, we need some more memory
    if ( (index->numPositions % 1000 == 0) && index->numPositions != 0 ) {
      index->Reads = (Read*)realloc(index->Reads, sizeof(Read)*(index->numPositions + 1000) );

      if ( index->Reads == NULL) {
        fprintf( stderr, "ERROR: Could not reallocate more memory\n" );
        exit(1);
      }
    }

    /* Parse line */
    Read* r = &index->Reads[index->numPositions];
    // Sequencer position
    char * UserPosition = strtok(line, "\t");

    // Read
    char * tRead = strtok(NULL, "\t");
    r->read = malloc(sizeof(char)*(strlen(tRead)+1));
    if ( r->read == NULL ) {
      fprintf( stderr, "ERROR: Could not malloc r->read\n" );
      exit(1);
    }
    strcpy(r->read, tRead);

    // chrX ... We want the 4th character (X)
    r->contig = strtok(NULL, ":\t")[3];

    // This should be paired, but is optional
    char * t = strtok(NULL, ":\t");
    if ( strstr( t, "pair") != NULL ) {
      // If it is paired, should be pairX, so want 5th char
      r->pair = t[4];

      // This mean we need to fetch again to get position
      r->position = atoi( strtok(NULL, "\t") );
    }
    else {
      // Other it is position
      r->position = atoi( t );
    }
  
    // Orientation -- is it F or R. Convert char* to char
    t = strtok(NULL, "\t");
    r->orientation = t[0];

    // Get the refernce string
    char * tRef = strtok(NULL, "\t");
    r->ref= malloc(sizeof(char)*(strlen(tRef)+1));
    if ( r->ref == NULL ) {
      fprintf( stderr, "ERROR: Could not malloc r->read\n" );
      exit(1);
    }
    strcpy(r->ref, tRef);

//printf("%d \t %s\n", r->position, r->read);

    // Increment our working and position counter
    index->numPositions++;
//printf("%d\n" , index->numPositions );
  }

  return 0;
}
 

/* Delete an index */
void ReadsIndexDelete(ReadsIndex *index)
{
	free(index->packageVersion);
	free(index->positions);
	free(index->bytes_32);
	free(index->bytes_64);

	int i;
	for ( i = 0 ; i < index->numPositions ; i++ ) {
		free(index->Reads[i].read);
		free(index->Reads[i].ref);
	}
	free(index->Reads);
}

/* Initialize an index */
void ReadsIndexInitialize(ReadsIndex *index)
{
	index->packageVersionLength=0;
	index->packageVersion=NULL;
	index->contig=0;
	index->numBytes=0;
	index->bytesType=0;
	index->startPosition=0;
	index->endPosition=0;
	index->numPositions=0;
	index->positions=NULL;
	index->bytes_32=NULL;
	index->bytes_64=NULL;
	index->Reads = malloc(sizeof(Read*) * 1000);
}

/* Read an index */
void ReadsIndexRead(ReadsIndex *index,
		FILE *fp)
{
	/* TODO */
	/* Use fread and check all return values */
}

/* Write an index */
void ReadsIndexWrite(ReadsIndex *index,
		FILE *fp)
{
	/* TODO */
	/* Use fwrite and check all return values */
}

/* Given a position, return the byte index */
int64_t ReadsIndexGetStartByte(ReadsIndex *index,
		uint32_t position)
{
	/* Binary search */
	int64_t low, mid, high;

	if(position < index->startPosition ||
			index->endPosition < position) {
		return -1;
	}

	low = 0;
	high = index->numPositions-1;
	while(low < high) {
		mid = (low + high)/2;

		if(position == index->positions[mid]) {
			return (Bytes32 == index->bytesType)?(index->bytes_32[mid]):(index->bytes_64[mid]);
		}
		else if(position < index->positions[mid]) {
			high = mid - 1;
		}
		else {
			low = mid + 1;
		}
	}

	return -1;
}
