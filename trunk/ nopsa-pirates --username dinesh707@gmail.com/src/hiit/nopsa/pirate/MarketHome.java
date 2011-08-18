package hiit.nopsa.pirate;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

/**
 * IMPORTANT : "MarketHome.java" and "MarketHomeView.java" needs to be re modeled and simplified.
 * 
 * @author Dinesh Wijekoon
 */
public class MarketHome extends Activity{
	
	private MarketHomeView marketHomeView;
	private final String TAG = "NOPSA-P";
	private MediaPlayer mPlayer = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    marketHomeView = new MarketHomeView(this,this);
	    setContentView(marketHomeView);
	}
	
	private void playSound(){
		if (GameStatus.getGameStatusObject().isSounds()){
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
	}
	
	@Override
	protected void onResume() {
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
