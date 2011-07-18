package hiit.nopsa.pirate;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends Activity {
    
	private HomeView homeView;
	private final String TAG = "NOPSA-P";
	private MediaPlayer mPlayer = null;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"MainActivity Started");
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        homeView = new HomeView(this,this);
        setContentView(homeView);        
	}
	
	private void playSound(){
		new Thread(new Runnable() {
			public void run() {
				try{
					mPlayer = MediaPlayer.create(MainActivity.this, R.raw.carribian_theam);
					mPlayer.setLooping(true);
					mPlayer.start();
					while(mPlayer.isPlaying()){
						android.os.SystemClock.sleep(100);
					}
				}catch(Exception e){
					Log.d(TAG,"ERROR PLAYING");
					e.printStackTrace();
				}
			}}).start();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		playSound();
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		if (mPlayer!=null){
			mPlayer.stop();
			mPlayer = null;
		}
		super.onDestroy();
	}
	
	@Override
	protected void onPause() {
		if (mPlayer!=null){
			mPlayer.stop();
			mPlayer = null;
		}
		super.onPause();
	}
	
	
	
	
	
}