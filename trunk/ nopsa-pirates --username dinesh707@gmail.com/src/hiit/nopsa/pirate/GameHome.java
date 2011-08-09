package hiit.nopsa.pirate;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class GameHome extends Activity{
	
	private GameHomeView gameHomeView;
	private final String TAG = "NOPSA-P";
	private MediaPlayer mPlayer = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
	    //Remove title bar
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    //Remove notification bar
	    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    //Loading GameData from Files
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
		new Thread(new Runnable() {
			public void run() {
				try{
					mPlayer = MediaPlayer.create(GameHome.this, R.raw.sailing_sound);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.options, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.sound:
	        //TODO
	        return true;
	    case R.id.help:
	    	if (GameStatus.getGameStatusObject().getInstructions()){
	    		GameStatus.getGameStatusObject().setInstructions(false);
	    		Toast.makeText(this,"Info Dialogs Disabled", Toast.LENGTH_SHORT).show();	
	    	}else{
	    		GameStatus.getGameStatusObject().setInstructions(true);
	    		Toast.makeText(this,"Info Dialogs Enabled", Toast.LENGTH_SHORT).show();	
	    	}
	        return true;
	    case R.id.haptics:
	    	//TODO
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	

}
