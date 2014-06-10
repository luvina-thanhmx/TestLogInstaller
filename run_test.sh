#!/bin/sh
if [ "$#" -eq 4 ]; then
  groovy "run_test.groovy" "$@"
else
 echo           Incorrect parameters!
 echo			Format:
 echo			run_test.sh -p [/path/to/wiperdog/] -c [Folder_Test_Case]
 echo			Example:
 echo			run_test.sh -p /home/mrtit/WiperdogHome/ -c Case1
fi
