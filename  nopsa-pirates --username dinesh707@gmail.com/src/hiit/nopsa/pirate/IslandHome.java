package hiit.nopsa.pirate;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class IslandHome extends Activity{
	
	private IslandHomeView islandHomeView;
	private final String TAG = "NOPSA-P";
	private MediaPlayer mPlayer = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
	    //Remove title bar
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    //Remove notification bar
	    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    islandHomeView = new IslandHomeView(this,this);
	    setContentView(islandHomeView);
	}
	
	private void playSound(){
		new Thread(new Runnable() {
			public void run() {
				try{
					mPlayer = MediaPlayer.create(IslandHome.this, R.raw.island);
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
	public void onPause()
	{
		if (mPlayer!=null){
			mPlayer.stop();
			mPlayer = null;
		}
	    super.onPause();
	}

	@Override
	public void onResume()
	{
		playSound();
	    super.onResume();
	}
	
}
