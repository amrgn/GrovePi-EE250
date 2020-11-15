"""EE 250L Lab 04 Starter Code

Run vm_subscriber.py in a separate terminal on your VM."""

import paho.mqtt.client as mqtt
import time

import matplotlib.pyplot as plt

import json

NUM_SECONDS = 3
SAMPLES_PER_SECOND = 20
NUM_SAMPLES = NUM_SECONDS*SAMPLES_PER_SECOND

def disp_sound_data(client, userdata, message):
    msg = str(message.payload,"utf-8")
    data = json.loads(msg)
    plt.figure(figsize=(20,10))
    plt.plot(data["time"],data["amplitude"])
    plt.xlabel("time")
    plt.ylabel("amplitude")
    plt.title("Sound data")
    plt.show()

def on_connect(client, userdata, flags, rc):
    print("Connected to server (i.e., broker) with result code "+str(rc))

    client.subscribe("xm_pi/sound_data", qos = 2)
    client.message_callback_add("xm_pi/sound_data", disp_sound_data)

def on_message(client, userdata, msg):
    print("on_message: " + msg.topic + " " + str(msg.payload, "utf-8"))

if __name__ == '__main__':
    client = mqtt.Client()
    client.on_message = on_message
    client.on_connect = on_connect
    client.connect(host="broker.emqx.io", port=1883, keepalive=60)
    client.loop_start()

    while True:
        time.sleep(1)
            

