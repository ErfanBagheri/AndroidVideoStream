AndroidVideoStream
==================

Video streaming app for Android

------------------
How To:
Run the VideoStream code on an emulated or dev Android device
Open the VideoStreaming.exe server found in /SERVER
In terminal, telnet into the virtual android device (telnet localhost 5554)
Route UDP traffice through port 25000 (redir add udp:25000:25000)
Using the Videostream software, you can request SETUP, PLAY, PAUSE, and TEARDOWN commands from the server
