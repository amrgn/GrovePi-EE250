
import paho.mqtt.client as mqtt
import time

import matplotlib.pyplot as plt

import json

NUM_SECONDS = 3
SAMPLES_PER_SECOND = 20
NUM_SAMPLES = NUM_SECONDS*SAMPLES_PER_SECOND

THRESHOLD = -200

visualize = "n"

def disp_sound_data(client, userdata, message):
    msg = str(message.payload,"utf-8")
    data = json.loads(msg)

    #compute second time deriv (up to constant of proportionality)

    cnt = find_num_max(data)
    print("Detected {} claps".format(cnt))
    client.publish("xm_vm/num_claps", str(cnt), qos = 2)
    if(visualize.lower() == "y"):
        plt.figure(figsize=(20,10))
        plt.plot(data["time"],data["amplitude"])
        plt.xlabel("time")
        plt.ylabel("amplitude")
        plt.title("Sound data")
        plt.show()


def find_num_max(data):
    dt1 = []
    dt2 = []
    islocalmax = []
    for i in range(NUM_SAMPLES - 1):
        dt1.append(data["amplitude"][i+1] - data["amplitude"][i])
    for i in range(NUM_SAMPLES - 2):
        dt2.append(dt1[i+1] - dt1[i])
    for i in range(NUM_SAMPLES - 2):
        if data["amplitude"][i + 2] <= data["amplitude"][i + 1] and data["amplitude"][i] <= data["amplitude"][i + 1]:
            islocalmax.append(1)
        else:
            islocalmax.append(0)
    cnt = 0     
    for i in range(NUM_SAMPLES - 2):
        if dt2[i] < THRESHOLD and islocalmax[i] == 1:
            cnt += 1
    return cnt


def on_connect(client, userdata, flags, rc):
    print("Connected to server (i.e., broker) with result code "+str(rc))

    client.subscribe("xm_pi/sound_data", qos = 2)
    client.message_callback_add("xm_pi/sound_data", disp_sound_data)

def on_message(client, userdata, msg):
    print("on_message: " + msg.topic + " " + str(msg.payload, "utf-8"))

if __name__ == '__main__':
    visualize = input("Visualize data? Y/y for yes\n")
    client = mqtt.Client()
    client.on_message = on_message
    client.on_connect = on_connect
    client.connect(host="broker.emqx.io", port=1883, keepalive=60)
    client.loop_start()

    while True:
        try:
            time.sleep(1)
        except KeyboardInterrupt:
            break

        

