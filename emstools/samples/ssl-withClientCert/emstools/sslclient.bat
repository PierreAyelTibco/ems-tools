@echo on
setlocal

cd ..\..\..\bin

set SSL_PARAMS=-ssl -ssl-trace -ssl-verify-host -ssl-trusted-certs ..\samples\ssl\certs\server_root.cert.pem -ssl-identity ..\samples\ssl\certs\client_identity.p12 -ssl-password password 

@call .\EMSQueueSender.bat -server ssl://localhost:7443 -user client -password "password" -queue q1 -infile EMSAdmin.bat %SSL_PARAMS%

endlocal
pause
