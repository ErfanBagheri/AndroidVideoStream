package com.example.videostream;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


//Renders all the video player operations into Android activity to facilitate interaction

public class MainActivity extends Activity {
	ClientController client;
	ImageView image;
	//Timer timer = new Timer();
	private Handler handler = new Handler();
	
	private Runnable runnable = new Runnable() {
		   @Override
		   public void run() {
		      /* do what you need to do */
			  image.setImageBitmap(client.timerEvent());
		      /* and here comes the "trick" */
		      handler.postDelayed(this, 10);
		   }
		};
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setup();
        handler.postDelayed(runnable, 20);
        Log.d("Startup", "Finished");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void setup() {
    	Log.d("Startup", "Application Loading");
        client = new ClientController();
    	Log.d("Startup", "Client created");
    	client.playButton = (Button)findViewById(R.id.play);
    	client.pauseButton = (Button)findViewById(R.id.pause);
    	client.setupButton = (Button)findViewById(R.id.setup);
    	client.teardownButton = (Button)findViewById(R.id.teardown);
    	client.textField = (EditText)findViewById(R.id.address);
    	image = (ImageView)findViewById(R.id.imageView1);
    	Log.d("Startup", "Client setup starting");
    	client.setup();
    	//timer.scheduleAtFixedRate(new TimerTask() {
			//public void run() {
				//called each time when the timer parameter is met
    	
			//}
		//},
		//Set how long before to start calling the TimerTask (in ms)
		//0,
		//Set the amount of time between each exection (in ms)
		//20);
    	Log.d("Startup", "Client setup complete");
    }
    
}
