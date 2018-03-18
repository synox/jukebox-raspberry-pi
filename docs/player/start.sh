#!/bin/bash

# create signal file
mkfifo pipe_to_java
# motion -c configfile 

# run parser
java -jar player.jar pipe_to_java 5

