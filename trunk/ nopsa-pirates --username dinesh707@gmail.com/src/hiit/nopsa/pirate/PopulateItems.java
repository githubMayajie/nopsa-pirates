package hiit.nopsa.pirate;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

/**
 * 
 * @author Dinesh Wijekoon
 */
public class PopulateItems extends Activity {
	
	private PopulateItemsView populateItemsView;
	private final String TAG = "NOPSA-P";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    populateItemsView = new PopulateItemsView(this,this);
	    setContentView(populateItemsView);
	}

}
