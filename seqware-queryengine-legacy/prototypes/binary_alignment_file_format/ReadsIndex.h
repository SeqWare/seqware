#ifndef READSINDEX_H_
#define READSINDEX_H_

#include "Definitions.h"
#include <string.h>
#include <stdio.h>

void ReadsIndexCreate(ReadsIndex*, FILE* fp);
void ReadsIndexDelete(ReadsIndex*);
void ReadsIndexInitialize(ReadsIndex*);
void ReadsIndexRead(ReadsIndex*, FILE*);
void ReadsIndexWrite(ReadsIndex*, FILE*);
int64_t ReadsIndexGetStartByte(ReadsIndex*, uint32_t position);
int GetReadsFromSLO(FILE *fp, ReadsIndex *index);

#endif
