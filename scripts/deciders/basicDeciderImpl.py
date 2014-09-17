#! /usr/bin/env python3
from abstractDeciderImpl import AbstractDecider

####################################################
## This is an implementation of a barebones decider
## usable for running simple workflows
#################################################### 

class Decider (AbstractDecider):
    def init (self, PARSER):
        print("Basic Decider does nothing for init")
    def provideTargetQuery (self):
        return "select *, file_swid as group_col from file_report"
    def checkFileDetails (self, dict):
        return True
    def doFinalCheck(self, workflowRunGroup):
        return True
    def modifyIniFile(self, dict, workflowRunGroup):
        return dict
