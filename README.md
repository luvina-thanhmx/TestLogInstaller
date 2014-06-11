Source test for create wiperdog installation log file (#411).
---------------------------------------------
A. How to test?  
- Step1: run ./run_test.sh with format  
	*Run each case: ./run_test.sh -p /home/mrtit/Wiperdog/1205Wiperdog/ -c Case1  
	*Run all case: ./run_test.sh -p /home/mrtit/Wiperdog/1205Wiperdog/ -c all  
- Step2: Check result  

B. Case test  

 1. Test write log when install with value of parameters without default (Case1)  
  - Input: Value of parameters without default (ex: jetty port is 123456, mongo host is 10.0.0.2).  
  - Expected: in the log file will show value of parameters corresponding to set.  
  
 2. Test write log when install have error (Case2)  
  - Input: Value of jetty port to set is not number.  
  - Expected: in the log file will show the message about error.  

 3. Test write log when install with value of parameters is default (Case3)  
  - Input: Value of parameters is default.  
  - Expected: in the log file will show value of parameters is default.  

