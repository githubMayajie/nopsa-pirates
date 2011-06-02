package hiit.nopsa.pirate;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
    
	private HomeView homeView;
	private final String TAG = "NOPSA-P";

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
		
}