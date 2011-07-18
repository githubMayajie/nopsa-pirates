package hiit.nopsa.pirate;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class MarketHome extends Activity{
	
	private MarketHomeView marketHomeView;
	private final String TAG = "NOPSA-P";
	private MediaPlayer mPlayer = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
	    //Remove title bar
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    //Remove notification bar
	    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    //Loading GameData from Files
	    marketHomeView = new MarketHomeView(this,this);
	    setContentView(marketHomeView);
	}
	
	
	private void playSound(){
		new Thread(new Runnable() {
			public void run() {
				try{
					mPlayer = MediaPlayer.create(MarketHome.this, R.raw.traven);
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
