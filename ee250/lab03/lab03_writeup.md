Answers to tcp/udp testing in terminal:

Question 1:
Some numbers simply didn't show up in the receiving side terminal. This is because the increased packet loss caused data transmission errors, but UDP does nothing to correct it.

Question 2:
The reliability did not change with TCP, as even if there was a packet loss, the message would continue to be sent until the receiving side received the message correctly.

Question 3:
The speed greatly decreased. (There was a delay in when I'd send a message/number to the server side and when it would actually display.) This is because of the packet drops and the need to resend the same data (as well as timeouts).

Answers to server c code questions:

1)
argc is the number of arguments passed to main (when the program is executed in terminal, you can add additional space-separated strings after the program name to send main arguments)
argv is an array of strings storing these arguments passed to main

2)
A UNIX file descriptor is a nonnegative integer that identifies a file that can be accessed by a program.
A file descriptor table is a table containing locational/permission information about the files corresponding to the file descriptors

3)
A struct is a grouping of different datatypes into a single object.

The sockaddr_in is defined by the following code:

struct sockaddr_in {
    short            sin_family;   // e.g. AF_INET
    unsigned short   sin_port;     // e.g. htons(3490)
    struct in_addr   sin_addr;     // see struct in_addr, below
    char             sin_zero[8];  // zero this if you want to
};

struct in_addr {
    unsigned long s_addr;  // load with inet_aton()
};

it has a sin_family field, sin_port field (contains the port number), a in_addr structure sin_addr (contains the ip address), which contains just a single unsigned long s_addr, and sockaddr_in contains a char array of size 8.

4)
socket() takes a communication domain (e.g. IPv4 or IPv6), a communication type (e.g. TCP or UDP), and a protocol value for internet protocol, and it returns a socket descriptor (an int)

5)
bind() takes a socket descriptor, an address in the form of a sockaddr structure pointer, and the length of the sockaddr structure in memory
listen() takes a socket descriptor and an integer specifying the backlog allowed, or the maximum length of pending connections from clients allowed.

6)
The while(1) loop allows for multiple successive connections without having to rerun the program each after a client finishes connecting.
If there are multiple connections to handle, the program will have to handle one at a time, and the clients will just have to "wait for their turn" before their request can be processed. Also, the backlog could become filled if too many clients connect at the same time.

7)
fork() allows for multiple processes to be created, which would allow for multiple clients to be addressed at the same time, rather in the order their requests were received.

8)
A system call is a request that is sent to the kernel to perform an operation, such as input/output.
