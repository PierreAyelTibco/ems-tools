@echo off
setlocal
rem #########################################################################
rem #
rem # Release 1_2_0
rem # - First Release
rem #
rem #########################################################################

set EMSTOOLS_LIB=%0\..\..\lib

set CP=%EMSTOOLS_LIB%\emstools.jar
set CP=%CP%;%EMSTOOLS_LIB%\jms.jar
set CP=%CP%;%EMSTOOLS_LIB%\tibjms.jar
set CP=%CP%;%EMSTOOLS_LIB%\tibjmsufo.jar
set CP=%CP%;%EMSTOOLS_LIB%\tibjmsadmin.jar
set CP=%CP%;%EMSTOOLS_LIB%\tibcrypt.jar

set CP=%CP%;%EMSTOOLS_LIB%\log4j-1.2.15.jar
set CP=%CP%;%EMSTOOLS_LIB%\slf4j-api-1.5.2.jar
set CP=%CP%;%EMSTOOLS_LIB%\slf4j-log4j12-1.5.2.jar
set CP=%CP%;%EMSTOOLS_LIB%\slf4j-simple-1.5.2.jar

rem #########################################################################

java -cp %CP% com.tibco.tools.EMSTestQueueConnection %*

:end
endlocal

rem #########################################################################
rem ###  END OF FILE  #######################################################
rem #########################################################################


