Project Description:

A distributed file service consists of a server and client processes. Each client process will connect to the server over a socket. The server should be able to handle all the clients concurrently.
In this project, Clients will designate a directory on their system to serve as their shared directory. Any file placed into that directory will automatically be uploaded to the server. Once the server receives the new file, it will send that file to the remaining clients. Clients will place the received file into their shared directory.
All clients can add files to their shared directories.



Steps to run:
    Open Server.java,GUI.java
    Change File path to suitable one on line 103 of client.java,this decides where the client sharede
        directories are created..
    Run the Server class first-saved as Server.java
    Run 3 instances of client class-saved as GUI.Java.
        >on entering the client name,click the button as enter key is not enabled.Then click on
        the OK button for the alert message.
        >On entering duplicate client name,as socket closes,it leads to a run time exception on
        console.But on changing the name and
        clicking the "connect " button. Connection is re-established
    Now, go to the location where the 3 client folders are created.They are created in the name of
        the 3 unique clients.
    drag and drop a text file(trial1,trial2 and trial3.txt are there) into any of the clients.
    The file is created in the other two client folders in the name-Tested.txt, the contents are a but
        faulty and showing the client name instead.
