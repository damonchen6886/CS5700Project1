## Class project for cs5700 network class

### This project is to implement a client program which communicates with a server using sockets




#### how to run this program from scratch:
run  
` Make `

It will automatically build the Client.Class
then build the installCert class to create a certificate. run

`javac InstallCert.java`

once finished, run

`java InstallCert cs5700fa20.ccs.neu.edu:27996`

press `1` once the command line message prompt, it will generate a jssecacerts file which is the certificate that SSL socket needs. 
Please note that this java class is from the offcial Orcle doc:
https://github.com/escline/InstallCert/blob/master/InstallCert.java
then run 

`java Client <-p port> <-s> [hostname] [NEU ID]` 

where content in `< >` is optional

**Alternatively, you can ignore the above setps and directly run** 

`./client <-p port> <-s> [hostname] [NEU ID]`

#### High-Level approach:
1. Read the command line input in the main method
2. Initialize a Client class 
3. Check if the command argument contains a specific portal or SSL enabled 
4. Create a Client object and set given value to attributes of the object, run to create connection method
5. Process the response from the server and send back the math result until the response message contains BYE

#### Challenges:
The hardest part is to create an SSL socket. Despite the fact that I have created the SSLsocket in my code, I kept getting the SSLException while sending the first message to the server. The printed call stackTrce indicates the error is "Unable to find valid certification path to requested target." Looking it up, I found an official document I mentioned above that can solve the issue. Once follow the step and update the system.setProperty, the code works fine and successfully generated a new secret for the SSL socket.
