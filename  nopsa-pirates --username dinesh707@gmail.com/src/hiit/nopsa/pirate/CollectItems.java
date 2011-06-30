package hiit.nopsa.pirate;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class CollectItems extends Activity {
	
	private CollectItemsView collectItemsView;
	private int type; //0-animal, 1-slave, 2-food
	private final String TAG = "NOPSA-P";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
	    //Remove title bar
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    //Remove notification bar
	    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    
	    type = getIntent().getExtras().getInt("type");
	    Log.d(TAG,"CollectItems Selected Type:"+type);
	    
	    collectItemsView = new CollectItemsView(this,this);
	    setContentView(collectItemsView);
	}

}