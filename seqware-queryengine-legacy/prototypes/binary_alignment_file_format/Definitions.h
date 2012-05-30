#ifndef DEFINITIONS_H_
#define DEFINITIONS_H_

#include <stdlib.h>
#include <stdint.h>

// Maximum size of each read
//const int MAX_FILE_READ_SIZE = 10000;

enum{Bytes32, Bytes64};

// Storage for a single read
typedef struct {
  int8_t pair; // 1, 2, 3, etc
  char orientation; // F or R // FIXME: should be 1bit bool like value
  char * read; // Sequencer read
  char * ref;  // Reference read
  char contig;  // Chromosome
  uint32_t position; // Position in genome
  uint32_t offset;   // Offset into the file
} Read;

typedef struct {
	/* Meta data */
	int32_t packageVersionLength;
	int8_t *packageVersion;
	int32_t contig;
	int64_t numBytes;
	int32_t bytesType;
	uint32_t startPosition;
	uint32_t endPosition;
	/* Index */
	uint32_t numPositions;
	uint32_t *positions;
	uint32_t *bytes_32;
	int64_t *bytes_64;
        Read *Reads;
	/* Reads */
	char **reads;
} ReadsIndex;

#endif
