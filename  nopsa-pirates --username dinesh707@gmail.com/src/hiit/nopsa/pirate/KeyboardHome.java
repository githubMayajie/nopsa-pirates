package hiit.nopsa.pirate;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

/**
 * 
 * 
 * @author Dinesh Wijekoon
 */
public class KeyboardHome extends Activity {

	private final String TAG = "NOPSA-P";
	private TagSelectorView tagSelectorView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    tagSelectorView = new TagSelectorView(this, this);
	    setContentView(tagSelectorView);
	}
}
