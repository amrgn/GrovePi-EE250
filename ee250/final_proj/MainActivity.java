package com.example.ee250project;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    MqttClient client;
    String motion_detect  = "Motion detected!";
    String hello_msg      = "Hello!";
    String record_msg     = "Recording!";
    String not_record_msg = "Stopped recording!";
    String goodbye_msg    = "Goodbye!";
    int qos               = 2;
    String broker         = "tcp://broker.emqx.io:1883";
    String clientId       = "xm_phone";
    TextView status;
    String disconnected   = "Status: Disconnected";
    String connected      = "Status: Connected";

    int currently_recording = 0;

    float light_val = 0;
    double THRESHOLD     = 3.0;

    private Sensor accel = null;
    private SensorManager sensorManager = null;

    private Sensor light = null;
    private SensorManager lightManager = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        status = findViewById(R.id.status);
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            client = new MqttClient(broker, clientId, persistence);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        if(accel == null){
            Snackbar popup = Snackbar.make(findViewById(R.id.ConstraintLayout),
                    "Accelerometer error ", BaseTransientBottomBar.LENGTH_SHORT);
            popup.show();
        }
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);

        lightManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        light = lightManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if(light == null){
            Snackbar popup = Snackbar.make(findViewById(R.id.ConstraintLayout),
                    "Light sensor error ", BaseTransientBottomBar.LENGTH_SHORT);
            popup.show();
        }
        lightManager.registerListener(this, light, lightManager.SENSOR_DELAY_NORMAL);

    }

    public void connect(View view){
        try {
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Snackbar popup = Snackbar.make(findViewById(R.id.ConstraintLayout),
                            "Connection Lost", BaseTransientBottomBar.LENGTH_LONG);
                    popup.show();
                    status.setText(disconnected);
                    //Log.i(TAG, "connection lost");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    if(topic.equals("xm_pi/poll")){
                        try{
                            String pub_topic = "xm_phone/light";
                            MqttMessage msg = new MqttMessage(String.valueOf(light_val).getBytes());
                            message.setQos(qos);
                            message.setRetained(false);
                            client.publish(pub_topic, msg);
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        String num_claps = new String(message.getPayload(), StandardCharsets.UTF_8);
                        Snackbar show_claps = Snackbar.make(findViewById(R.id.ConstraintLayout),
                                "Number of claps detected: " + num_claps, BaseTransientBottomBar.LENGTH_SHORT);
                        show_claps.show();
                    }

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                    //Log.i(TAG, "msg delivered");
                }
            });
            client.connect(connOpts);
            String topic1 = "xm_pi/poll";
            String topic2 = "xm_vm/num_claps";
            client.subscribe(topic1, 2);
            client.subscribe(topic2, 2);
            status.setText(connected);
            Snackbar popup = Snackbar.make(findViewById(R.id.ConstraintLayout),
                    "Connected!", BaseTransientBottomBar.LENGTH_SHORT);
            popup.show();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public void disconnect(View view) {
        try {
            client.disconnect();
            status.setText(disconnected);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public void get_sound_data(View v) {
        try {
            String topic = "xm_phone/get_sound";
            MqttMessage message = new MqttMessage("get_sound".getBytes());
            message.setQos(qos);
            message.setRetained(false);
            client.publish(topic, message);
            Snackbar popup = Snackbar.make(findViewById(R.id.ConstraintLayout),
                    "Get sound request delivered!", BaseTransientBottomBar.LENGTH_SHORT);
            popup.show();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public void hello(View view){
        try {
            String topic = "xm_phone/hello";
            MqttMessage message = new MqttMessage(hello_msg.getBytes());
            message.setQos(qos);
            message.setRetained(false);
            client.publish(topic, message);
            Snackbar popup = Snackbar.make(findViewById(R.id.ConstraintLayout),
                    "Message delivered!", BaseTransientBottomBar.LENGTH_SHORT);
            popup.show();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public void goodbye(View view){
        try {
            String topic = "xm_phone/hello";
            MqttMessage message = new MqttMessage(goodbye_msg.getBytes());
            message.setQos(qos);
            message.setRetained(false);
            client.publish(topic, message);
            Snackbar popup = Snackbar.make(findViewById(R.id.ConstraintLayout),
                    "Message delivered!", BaseTransientBottomBar.LENGTH_SHORT);
            popup.show();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            if(event.values[0]*event.values[0] + event.values[1]*event.values[1] + event.values[2]*event.values[2] > THRESHOLD){
                try {
                    String topic = "xm_phone/motion";
                    MqttMessage message = new MqttMessage(motion_detect.getBytes());
                    message.setQos(qos);
                    message.setRetained(false);
                    client.publish(topic, message);
                    Snackbar popup = Snackbar.make(findViewById(R.id.ConstraintLayout),
                            "Motion detected!", BaseTransientBottomBar.LENGTH_SHORT);
                    popup.show();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }else{
            light_val = event.values[0];
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}