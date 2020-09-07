"""
Server IP is 52.88.20.156, ports are 5000-5008, socket is UNIX TCP 
Server receiver buffer is char[256]
If correct, the server will send a message back to you saying "I got your message"
Write your socket client code here in python
Establish a socket connection -> send a short message -> get a message back -> ternimate
"""
import socket

def main():
    
    IP = "34.209.114.30"
    PORT = 5001
    # TODO: Create a socket and connect it to the server at the designated IP and port
    s = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
    s.connect((IP,PORT))
    # TODO: Get user input and send it to the server using your TCP socket
    msg = input("Enter msg to send\n")
    s.sendall(msg.encode('utf-8'))

    # TODO: Receive a response from the server and close the TCP connection
    recvmsg = s.recv(1024)
    print(recvmsg)
    s.close()

if __name__ == '__main__':
    main()
