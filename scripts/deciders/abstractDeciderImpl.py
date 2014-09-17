#! /usr/bin/env python3
import argparse
from abc import ABCMeta, abstractmethod
from argparse import PARSER

#############################################################
## This abstract class defines what methods are customizable 
## by decider developers. 
#############################################################


class AbstractDecider( metaclass=ABCMeta ):
    @abstractmethod
    def init (self, PARSER):
        """ initialize any required instance variables and setup additional parser arguments """
        pass
    @abstractmethod
    def provideTargetQuery (self):
        """ provides a SQL query that handles filtering and grouping, should include a column 'group_col' that is the same for all elements of the same group """
        pass
    @abstractmethod
    def checkFileDetails (self, dict):
        """ returns whether or not this file should make it into the group for scheduling """
        pass
    @abstractmethod
    def doFinalCheck(self, workflowRunGroup):
        """ returns whether or not this workflow run group should be scheduled """
        pass
    @abstractmethod
    def modifyIniFile(self, dict, workflowRunGroup):
        """ given a dictionary and a workflowRunGroup, the dictionary can be modified to produce a custom ini file """
        pass
    

