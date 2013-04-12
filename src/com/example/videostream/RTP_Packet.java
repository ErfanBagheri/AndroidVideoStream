package com.example.videostream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

//

public class RTP_Packet {
	//Size of the packet header (bytes)
	static int header_size = 12;
	
	//Fields in the packet header
	public int v; //version - 2 bits
	public int p; //padding - 1 bit
	public int x; //extension - 1 bit
	public int cc; //CSRC count - 4 bits
	public int m; //marker - 1 bit
	public int pt; //payload type - 7 bits
	public int seq_num; //sequence number - 16 bits
	public int time; //timestamp - 32 bits
	public int ssrc; //synchronization source - 32 bits
	
	//Bitstream of the RTP header
	public byte[] header;
	
	//Size of payload
	public int payload_size = 0;
	
	//Bitstream of the RTP payload
	public byte[] payload;
	Bitmap bmp;
	
	public RTP_Packet(byte[] packet, int size) {
		//get header bitstream
		//header = new byte[header_size];
		//for(int i = 0; i < header_size; i++) {
		//	header[i] = packet[i];
		//}
		
		//get payload bitstream
		//payload_size = size - header_size;
		//payload = new byte[size];
		//for(int i = header_size; i < size; i++) {
		//	payload[i - header_size] = packet[i];
		//}
		bmp = BitmapFactory.decodeByteArray(packet, 12, packet.length - 12);
	}
	/* Returns length of packet
	 * header size + payload size
	 * Returns integer value
	 */
	public int getLength() {
		return header_size + payload_size;
	}
	
	public int getPayloadLength() {
		return payload_size;
	}
	/* Returns entire packet
	 * Returns byte[]
	 */
	public byte[] getPacket() {
		byte[] packet = new byte[header_size + payload_size];
		for(int i = 0; i < header_size; i++) {
			packet[i] = header[i];
		}
		for(int i = 0; i < payload_size; i++) {
			packet[i + header_size] = payload[i];
		}
		
		return packet;
	}
	
	public void printPacket() {
		Log.d("Packet", header.toString() + payload.toString());
	}
}
