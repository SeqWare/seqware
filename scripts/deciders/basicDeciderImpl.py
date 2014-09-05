#! /usr/bin/env python3
from abstractDeciderImpl import AbstractDecider

####################################################
## This is an implementation of a barebones decider
## usable for running simple workflows
#################################################### 

class Decider (AbstractDecider):
    def init (self, PARSER):
        print("oogly")
    def provideTargetQuery (self):
        pass
    def checkFileDetails (self):
        pass
    def doFinalCheck(self):
        pass
