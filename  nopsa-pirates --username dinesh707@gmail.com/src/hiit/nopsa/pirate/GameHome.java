package hiit.nopsa.pirate;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

/**
 * 
 * 
 * @author Dinesh Wijekoon
 */
public class GameHome extends Activity{
	
	private GameHomeView gameHomeView;
	private final String TAG = "NOPSA-P";
	private MediaPlayer mPlayer = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    gameHomeView = new GameHomeView(this,this);
	    setContentView(gameHomeView);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG,"Retured from IslandScreen");
		if (requestCode==231){//Returning form ISLAND
			gameHomeView.gameResumeFromIsland();
		}
		if (requestCode==233){//Returning from PORT
			gameHomeView.gameResumeFromPort();
		}
	}
	
	private void playSound(){
		if (GameStatus.getGameStatusObject().isSounds()){
			new Thread(new Runnable() {
				public void run() {
					try{
						mPlayer = MediaPlayer.create(GameHome.this, R.raw.buy_sell_trade_anybg);
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
	public void onPause()
	{
		if (mPlayer!=null){
			mPlayer.stop();
			mPlayer = null;
		}
		Log.d(TAG,"GameHome:Activity:onPause() CALLED");
	    super.onPause();
	}

	@Override
	public void onResume()
	{
		playSound();
		gameHomeView.gameResumeFromPopulateItems();
		Log.d(TAG,"GameHome:Activity:onResume() CALLED");
	    super.onResume();
	}	
}
