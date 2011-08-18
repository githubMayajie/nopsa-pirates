package hiit.nopsa.pirate;
/**
 * Structure of the Whole Application
 * ==================================

 *                                 MainActivity.java 
 *                                  (HomeView.java)   
 *                                        |
 *                                        |
 *                                  GameHome.java
 *                                (GameHomeView.java)                               
 *   PopulateItemsManager.java     /               \
 *           	\			      /              	\
 *               PopulateItems.java  			IslandHome.java
 *             (PopulateItemsView.java)		  (IslandHomeView.java)
 *       										    	\
 *      	    									     \
 *      						                  CollectItems.java
 *      										(CollectItemsView.java)
 * 															\
 * 															 \
 *  												KeyboardHome.java
 * 												   (TagSelectorView.java)
 * 
 * @author Dinesh Wijekoon
 */


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Inflates a dialog when player clicks "about" in start screen
 * This will show up the product and developer details
 * 
 * @author Dinesh Wijekoon
 */
public class About {
	
	private AlertDialog.Builder builder;
	private AlertDialog aboutDialog;
	private final String TAG = "NOPSA-P";
	
	public void popAboutDialog(Activity activity){
		Context mContext = activity;	
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.instructions, (ViewGroup) activity.findViewById(R.id.layout_root));
		layout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
	      	    dissmissAlert();
				return false;
			}
		});
		
		String text = "Nopsa Pirate is a project of Google Summer of Code 2011 program. This project was " +
				"proposed and mentored by Helsinki Institute for Information Technology (HIIT) and Senseg. " +
				"The goal of Nopsa Pirate is to utilize haptic implemented by Senseg and to improve the " +
				"Nopsa image database.<br><br> " +
				"Project NOPSA : http://nopsa.hiit.fi/ <br>" +
				"HIIT : http://www.hiit.fi/<br>" +
				"Senseg : http://senseg.com/<br> " +
				"Google Summer of Code : http://socghop.appspot.com";
		
		TextView text2 = (TextView) layout.findViewById(R.id.title);
		text2.setTextSize(35);
		text2.setText("About");

		TextView text1 = (TextView) layout.findViewById(R.id.text);
		text1.setTextSize(16);
		text1.setText(Html.fromHtml(text));

		ImageView image = (ImageView) layout.findViewById(R.id.image);
		image.setImageResource(R.drawable.captain);

		builder = new AlertDialog.Builder(mContext);
		builder.setView(layout);
		aboutDialog = builder.create();
		aboutDialog.getWindow().setGravity(Gravity.BOTTOM);
		aboutDialog.setCanceledOnTouchOutside(true);
		aboutDialog.show();
	}
	
	public void dissmissAlert(){
		aboutDialog.dismiss();
	}
}
