package com.example.videostream;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.StringTokenizer;

import android.os.AsyncTask;
import android.util.Log;

//Establishes a TCP connection with the server to exchange RTSP messages
//Also send RTSP requests and helps the controller parse responses

public class RTSP_Model {
	InetAddress serverIPAddr;
	int serverRTSPPort, clientRTPPort, RTSPSeqNum;
	int state; //The state can be INIT (0), READY (1), or PLAYING(1)
	int RTSPid = 0; //ID of the RTSP session
	String fileName; //Name of video file to be requested
	int MJPEG_TYPE = 26; //RTP payload type for MJPEG video
	Socket RTSPsocket; //Used to send and receive RTSP messages
	DatagramPacket rcvdp; //UDP packet received from the server
	DatagramSocket RTPsocket; //socket to be used to send and receive UDP packets
	BufferedReader RTSPBufferedReader;
	BufferedWriter RTSPBufferedWriter;
	byte buf[];
	
	public RTSP_Model(InetAddress ipAddr, int portS, int portC, String name) {
		Log.d("Startup", "RTSP - Setting up");
		serverIPAddr = ipAddr;
		Log.d("IPAddress", serverIPAddr.toString());
		serverRTSPPort = portS;
		Log.d("Server Port", "" + serverRTSPPort);
		clientRTPPort = portC;
		Log.d("Client Port", "" + clientRTPPort);
		fileName = name;
		Log.d("File Name", fileName);
		state = 0;
		Log.d("STATE", "INIT");
		buf = new byte[15000];
	}
	
	public void setupTCP() {
		Log.d("Startup", "RTSP - creating socket");
		//Set up a TCP connection to exchange RTSP messages
		try {
			RTSPsocket = new Socket(/*serverIPAddr*/"10.0.2.2", serverRTSPPort);
		} catch (IOException e) {
			Log.d("ERROR", "RTSP - creating socket");
		}
		
		Log.d("Startup", "RTSP - Creating Buffered Reader/Writers");
		try {
			RTSPBufferedReader = new BufferedReader(new InputStreamReader(RTSPsocket.getInputStream()));
		} catch (IOException e) {
			
		}
		try {
			RTSPBufferedWriter = new BufferedWriter(new OutputStreamWriter(RTSPsocket.getOutputStream()));
		} catch (IOException e) {
			
		}
	}
	
	public int parseResponse() throws IOException {
		int replyCode = 0;
		String StatusLine = RTSPBufferedReader.readLine();
		Log.d("Response", "" + StatusLine);
		StringTokenizer tokens = new StringTokenizer(StatusLine);
	    tokens.nextToken(); //skip over the RTSP version
	    replyCode = Integer.parseInt(tokens.nextToken());
	    
	    if(replyCode == 200) {
	    	String SeqNumLine = RTSPBufferedReader.readLine();
	    	Log.d("Response", SeqNumLine);

	  	  	String SessionLine = RTSPBufferedReader.readLine();
	  	  	Log.d("Response", SessionLine);
	  	  	
	  	  	tokens = new StringTokenizer(SessionLine);
	  	  	tokens.nextToken(); //skip over the Session:
	  	  	RTSPid = Integer.parseInt(tokens.nextToken());
	    }
		return replyCode;
	}
	
	public void playRequest() throws IOException {
		if(state == 1) { //If the state is READY
			RTSPSeqNum++;
			//Send PLAY request
			RTSPBufferedWriter.write("PLAY "+ fileName + " RTSP/1.0\r\n");
			Log.d("Sending", "PLAY "+ fileName + " RTSP/1.0\r\n");
			RTSPBufferedWriter.write("CSeq: " + RTSPSeqNum + "\r\n");
			RTSPBufferedWriter.write("Session: "+ RTSPid + "\r\n");
			RTSPBufferedWriter.flush();
			if(parseResponse() == 200) {
				//good response
				state = 2; //set state to PLAYING
				Log.d("STATE", "PLAYING");
				//start timer?
			}
		}
	}
	
	public void pauseRequest() throws IOException {
		if(state == 2) { //If the state is PLAYING
			RTSPSeqNum++;
			//Send PAUSE request
			RTSPBufferedWriter.write("PAUSE "+ fileName + " RTSP/1.0\r\n");
			Log.d("Sending", "PAUSE "+ fileName + " RTSP/1.0\r\n");
			RTSPBufferedWriter.write("CSeq: " + RTSPSeqNum + "\r\n");
			RTSPBufferedWriter.write("Session: "+ RTSPid + "\r\n");
			RTSPBufferedWriter.flush();
			
			if(parseResponse() == 200) {
				//good response
				state = 1; //set state to READY
				Log.d("STATE", "READY");
				//stop timer?
			}
		}
	}

	public void setupRequest() throws IOException {
		if(state == 0) { //If the state is not READY or PLAYING
			RTPsocket = new DatagramSocket(clientRTPPort);
			RTPsocket.setSoTimeout(5);
			RTSPSeqNum = 1;
			//Send SETUP request
			RTSPBufferedWriter.write("SETUP "+ fileName + " RTSP/1.0\r\n");
			Log.d("Sending", "SETUP "+ fileName + " RTSP/1.0\r\n");
			RTSPBufferedWriter.write("CSeq: " + RTSPSeqNum + "\r\n");
			RTSPBufferedWriter.write("Transport: RTP/UDP; client_port= "+ clientRTPPort + "\r\n");
			RTSPBufferedWriter.flush();
			
			if(parseResponse() == 200) {
				//good response
				state = 1; //set state to READY
				Log.d("STATE", "READY");
			}
		}
	}

	public void teardownRequest() throws IOException {
		RTSPSeqNum++;
		//Send TEARDOWN request
		RTSPBufferedWriter.write("TEARDOWN "+ fileName + " RTSP/1.0\r\n");
		Log.d("Sending", "TEARDOWN "+ fileName + " RTSP/1.0\r\n");
		RTSPBufferedWriter.write("CSeq: " + RTSPSeqNum + "\r\n");
		RTSPBufferedWriter.write("Session: "+RTSPid + "\r\n");
		RTSPBufferedWriter.flush();
		
		if(parseResponse() == 200) {
			//good response
			state = 0; //set state to INIT
			Log.d("STATE", "INIT");
		}
	}

	public RTP_Model timerTick() {
		rcvdp = new DatagramPacket(buf, buf.length);
		try {
			RTPsocket.receive(rcvdp);
			RTP_Model rtp_packet = new RTP_Model(rcvdp.getData(), rcvdp.getLength());
			return rtp_packet;
		} catch (IOException e) {
		}
		return null;
	}
}
