package hiit.nopsa.pirate;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class KeyboardHome extends Activity {

	private final String TAG = "NOPSA-P";
	private KeyboardHomeView keyboardHomeView;
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
	    //Remove title bar
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    //Remove notification bar
	    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    //Loading GameData from Files
	    //GameStatus.getGameStatusObject().loadGameData(this);
	    keyboardHomeView = new KeyboardHomeView(this, this);
	    setContentView(keyboardHomeView);
	}
}
