package hiit.nopsa.pirate;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class MarketHome extends Activity{
	
	private MarketHomeView marketHomeView;
	private final String TAG = "NOPSA-P";

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
	
}
