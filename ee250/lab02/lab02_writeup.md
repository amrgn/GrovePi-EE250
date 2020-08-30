4.1)
git clone git@github.com:my-name/my-imaginary-repo.git
touch my_second_file.py
git add my_second_file.py
git commit -m "Added my_second_file.py"
git push

4.2)
I edited the python file in my VM using sublime, then I performed a git add, commit, and push each time to send the code to my raspberry pi. I then pulled it on my raspberry pi and ran it there. I am definately considering using a text-based editor directly on my raspberry pi in the future. I am also considering just using microsoft rdp and accessing my raspberry pi via that format rather than ssh, which would also make editing there easier. (I have this set up.)

4.3)
The delay between each reading would be approximately 60 ms. (This is because time.sleep(.06) is called each time the ultrasonic sensor is read from.)
The raspberry pi uses the i2c protocol to communicate with the Atmega328P on the grovepi.