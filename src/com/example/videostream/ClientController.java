package com.example.videostream;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

//Responsible for all actions that need to be taken when
//any of the buttons in the client application are pressed

//Define application behavior, interprets user gestures and maps them to actions
//These actions are requests to the model to perform and signal to the view to
//update its information from the models

public class ClientController {
	Button playButton;
	Button pauseButton;
	Button setupButton;
	Button teardownButton;
	EditText textField;
	//ImageView image;
	String address = "10.0.2.2";
	String fileName = "/video3.mjpeg";
	int serverPort = 3000;
	int clientPort = 25000;
	InetAddress serverIPAddr;
	//Timer timer = new Timer();
	
	
	RTSP_Model rtsp;
	
	public ClientController() {
		Log.d("Startup", "CLIENT - setting up");
		
		try {
			serverIPAddr = InetAddress.getByName(address);
		} catch (UnknownHostException e) {
			Log.d("Error", "Creating IPAddr");
		}
		rtsp = new RTSP_Model(serverIPAddr, serverPort, clientPort, fileName);
		
		Log.d("Startup", "CLIENT - establishing TCP");
		rtsp.setupTCP();
		
		Log.d("Startup", "CLIENT - done");
	}
	
	public Bitmap timerEvent() {
		if(rtsp.state == 2) {
			RTP_Model tempPackage = rtsp.timerTick();
			if(tempPackage != null) {
				Log.d("Package","not null");
				return  handlePayload(tempPackage);
			}
		}
		return null;
	}
	
	public Bitmap handlePayload(RTP_Model temp) {
		Bitmap bmp = temp.getImage();
		Log.d("test", " "+ bmp.getHeight());
		//image.setImageBitmap(bmp);
		return bmp;
	}
	
	public void setup() {
		
		playButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				//send PLAY request using session ID
				//read server's response
				Log.d("Button Press", "PLAY");
				playPress();
			}
		});
		pauseButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				//send PAUSE request using session ID
				//read server's response
				Log.d("Button Press", "PAUSE");
				pausePress();
			}
		});
		setupButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				//create a socket for receiving RTP data
				//send setup request to server. specify port for socket
				//read replyfrom server and get session ID from response
				Log.d("Button Press", "SETUP");
				setupPress();
			}
		});
		teardownButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				//send TEARDOWN request to server using session ID
				//read server's response
				Log.d("Button Press", "TEARDOWN");
				teardownPress();
			}
		});
		textField.setText("" + serverIPAddr + ":" + serverPort);
	}
	
	public void playPress() {
		try {
			rtsp.playRequest();
		} catch (IOException e) {
			Log.d("Error", "Play request to RTSP");
		}
	}
	
	public void pausePress() {
		try {
			rtsp.pauseRequest();
		} catch (IOException e) {
			Log.d("Error", "Pause request to RTSP");
		}
	}
	
	public void setupPress() {
		try {
			rtsp.setupRequest();
		} catch (IOException e) {
			Log.d("Error", "Setup request to RTSP");
		}
	}
	
	public void teardownPress() {
		try {
			rtsp.teardownRequest();
		} catch (IOException e) {
			Log.d("Error", "Teardown request to RTSP");
		}
	}

	
}


