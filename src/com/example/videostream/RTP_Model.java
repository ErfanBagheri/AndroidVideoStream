package com.example.videostream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

//Gets the information from the RTP packet header
//Strips off header then gets the RTP packet payload to be converted to an image

public class RTP_Model {
	RTP_Packet packet;
	public RTP_Model(byte[] rec, int length) {
		packet = new RTP_Packet(rec, length);
	}
	
	public int getPayloadLength() {
		return packet.getPayloadLength();
	}
	
	public int getPayload(byte[] data) {
		for (int i=0; i < packet.payload_size; i++)
		      data[i] = packet.payload[i];

		    return(packet.payload_size);
	}
	
	public Bitmap getImage() {
		
		return packet.bmp;
	}
}

//get seq num

//get time stamp

//get payload type

//get data

//get length
