@echo on
setlocal

cd ..\..\..\bin

set SSL_PARAMS=-ssl -ssl-trace -ssl-verify-host -ssl-trusted-certs ..\samples\ssl\certs\server_root.cert.pem 

@call .\EMSQueueSender.bat -server ssl://localhost:7443 -user admin -password "" -queue q1 -infile EMSAdmin.bat %SSL_PARAMS%

endlocal
pause

