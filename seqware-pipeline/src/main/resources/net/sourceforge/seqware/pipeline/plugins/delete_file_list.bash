#!/bin/bash
while read -r filename; do
  if [ -f $filename ] 
  then
    rm -v "$filename"
  fi
  if [ -f $filename.bak ] 
  then
    rm -v "$filename.bak"
  fi
done <$1
