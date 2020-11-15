"""EE 250L Lab 04 Starter Code

Run rpi_pub_and_sub.py on your Raspberry Pi."""

import sys

sys.path.append('../../Software/Python/')
# This append is to support importing the LCD library.
sys.path.append('../../Software/Python/grove_rgb_lcd')

import grovepi
import grove_rgb_lcd

import paho.mqtt.client as mqtt
import time

HOSTNAME = "xm_pi/"
buzzer = 2
led    = 5

def hello_msg(client, userdata, message):
    msg = str(message.payload,"utf-8")
    if msg == "Hello!":
        print("Received hello message from phone! Printing to LCD...")
        grovepi
        lcd_fail = 5
        while lcd_fail > 0:
                try:
                    grove_rgb_lcd.setText(msg)
                    grove_rgb_lcd.setRGB(50, 100, 100)
                    lcd_fail = 0
                except:
                    grove_rgb_lcd.textCommand(0x01)
                    time.sleep(0.2)
                    print("lcd write error, retrying...")
                    lcd_fail -= 1
    elif msg == "Goodbye!":
        print("Received goodbye message from phone! Printing to LCD...")
        lcd_fail = 5
        while lcd_fail > 0:
                try:
                    grove_rgb_lcd.setText(msg)
                    grove_rgb_lcd.setRGB(0, 0, 0)
                    lcd_fail = 0
                except:
                    grove_rgb_lcd.textCommand(0x01)
                    time.sleep(0.2)
                    print("lcd write error, retrying...")
                    lcd_fail -= 1
    else:
        print("Invalid message")

def motion_msg(client, userdata, message):
    msg = str(message.payload,"utf-8")
    if msg== "Motion detected!":
        grovepi.digitalWrite(buzzer,1)
        time.sleep(0.5)
        grovepi.digitalWrite(buzzer,0)

    else:
        print("Invalid motion msg")
        print(msg)

def led_msg(client, userdata, message):
    msg = float(str(message.payload,"utf-8"))
    print(msg)
    if msg < 30:
        grovepi.digitalWrite(led, 1)
    else:
        grovepi.digitalWrite(led, 0)

def on_connect(client, userdata, flags, rc):
    print("Connected to server (i.e., broker) with result code "+str(rc))

    #subscribe to topics of interest here
    client.subscribe("xm_phone/hello", qos = 2)
    client.message_callback_add("xm_phone/hello", hello_msg)
    client.subscribe("xm_phone/motion", qos = 2)
    client.message_callback_add("xm_phone/motion", motion_msg)
    client.subscribe("xm_phone/light", qos = 2)
    client.message_callback_add("xm_phone/light", led_msg)

#Default message callback. Please use custom callbacks.
def on_message(client, userdata, msg):
    print("on_message: " + msg.topic + " " + str(msg.payload, "utf-8"))

if __name__ == '__main__':
    client = mqtt.Client()
    client.on_message = on_message
    client.on_connect = on_connect
    client.connect(host="broker.emqx.io", port=1883, keepalive=60)
    client.loop_start()

    grovepi.pinMode(buzzer, "OUTPUT")
    grovepi.pinMode(led, "OUTPUT")

    while True:
        try:
            client.publish(HOSTNAME + "poll", "request for light level")
            time.sleep(0.2)

        except KeyboardInterrupt:
            # Gracefully shutdown on Ctrl-C
            grove_rgb_lcd.setText('')
            grove_rgb_lcd.setRGB(0, 0, 0)

            # Turn buzzer and led off just in case
            grovepi.digitalWrite(buzzer, 0)
            grovepi.digitalWrite(led, 0)

            break


