package hiit.nopsa.pirate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class GameHome extends Activity{
	
	private GameHomeView gameHomeView;
	private final String TAG = "NOPSA-P";
	private GameStatus gameStatus;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
	    //Remove title bar
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    //Remove notification bar
	    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    GameStatus.getGameStatusObject().loadGameData(this);
	    gameHomeView = new GameHomeView(this,this);
	    setContentView(gameHomeView);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG,"Retured from IslandScreen");
		if (requestCode==231){
			gameHomeView.gameResume();
		}
	}
	

}
