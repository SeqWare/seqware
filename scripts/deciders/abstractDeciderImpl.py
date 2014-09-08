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
        pass
    @abstractmethod
    def provideTargetQuery (self):
        pass
    @abstractmethod
    def checkFileDetails (self):
        pass
    @abstractmethod
    def doFinalCheck(self):
        pass
    

