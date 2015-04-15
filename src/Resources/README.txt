working scenario of the CAN


1.compile whole project using the command "javac *.java"

2.start BootStrapServer using the command "java BootStrapServerRMI"
   * server ip address is displayed and "server ready" message is dispalyed

3.start client 1 by running the command "java PeerClientRMI" in one of the different server

4. Enter value "1" to join the client to the network

5.Success message will be displayed 
 
6.connect client2 to the network by running it in different server 

7.coordinates of the client2 and success message will be displayed

8.connect client3 to the network by running it in different server (JOIN)

9.coordinates of the client3 and success message will be displayed

10. insert a text file by entering option "2" (INSERT)

11. Enter the file name as "client1"  .

12.success message and client ip in which the file is stored is displayed.

13.insert another text file by entering option "2" (INSERT)

14. Enter the file name as "test1"  

15.success message and client ip in which the file is stored is displayed.

16.Enter option "3" and enter file name as either "client1" or "test1" (SEARCH)

17.If same client contains the file ,it will display the message as "File is located in the same client.So file is not downloaded"

18.If file located in different client,it will display its ip address and file will be downloaded automatically to this client.
and success message will be displayed.

19.Enter option "4" and enter ip address of any client (VIEW)
 * it will display all the parameters of the client

20.Enter option "4" and enter "all" 
* it will display all the parameters of all active clients

21.Enter option "5" for leaving the network   (LEAVE)
* Success message will be displayed

22.enter option "4" and enter "all"

It will display all the parameters of all active clients
It will display ipaddress as "null" and coordinates as "0" for clients who left the network.

NOTE:

Coordinates start and end positions are 
lowerX =0
lowerY =0
UpperX =400
upperY =400







