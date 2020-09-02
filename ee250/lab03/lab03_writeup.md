Question 1:
Some numbers simply didn't show up in the receiving side terminal. This is because the increased packet loss caused data transmission errors, but UDP does nothing to correct it.

Question 2:
The reliability did not change with TCP, as even if there was a packet loss, the message would continue to be sent until the receiving side received the message correctly.

Question 3:
The speed greatly decreased. (There was a delay in when I'd send a message/number to the server side and when it would actually display.) This is because of the packet drops and the need to resend the same data (as well as timeouts).