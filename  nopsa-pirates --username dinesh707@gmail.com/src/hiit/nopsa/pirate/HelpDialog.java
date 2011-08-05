package hiit.nopsa.pirate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;

import android.widget.TextView;
import android.view.Window;
import android.view.accessibility.AccessibilityEvent;

public class HelpDialog {
	
	private AlertDialog.Builder builder;
	private AlertDialog alertDialog;
	private final String TAG = "NOPSA-P";
	
	public void popInstructionsDialog(final Activity activity, final int text_line){
		if (text_line==5)
			return;
		
		String title = "Yo Captain "+GameStatus.getGameStatusObject().getUser_name()+" !";
  		String[] text = {
  				"Welcome to NOPSA Sea, You are loaded with "+GameStatus.getGameStatusObject().getNum_crew()+" crew members " +
  				" and enough food to survive for some time. Hoist the sails to rule the sea.(1/5)", 
  				"Train animals and make them worth to sell. Train slaves and turn them to crew or sell them for higher price.(2/5)", 
  				"You also need to pile more food to feed your hungry crew, slaves and animals.(3/5)", 
  				"Once your time in the sea is up your ship will reach an island. There you can capture " +
  				"more animals, slaves and food.(4/5)",
  				"To upgrade your ship you need money and crew. To get money you must sell trained " +
  				"animals and slaves. To get crew you can buy them or convert your trained slaves to crew members..."};
		
  		Log.d(TAG,"Text Length"+text.length);
		final Context mContext = activity;
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View layout = inflater.inflate(R.layout.instructions, (ViewGroup) activity.findViewById(R.id.layout_root));
		layout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.d(TAG,"Hello Tocuhed !!!");
				HelpDialog hd = new HelpDialog();
	      	    hd.popInstructionsDialog(activity,text_line+1);
	      	    dissmissAlert();
				return false;
			}
		});
		
		Typeface font = Typeface.createFromAsset(activity.getAssets(),"fonts/pirates.ttf" ); 
		TextView text2 = (TextView) layout.findViewById(R.id.title);
		text2.setTypeface(font);
		text2.setTextSize(45);
		text2.setText(title);

		font = Typeface.createFromAsset(activity.getAssets(),"fonts/piecesofeight.ttf" ); 
		TextView text1 = (TextView) layout.findViewById(R.id.text);
		text1.setTypeface(font);
		text1.setTextSize(30);
		text1.setText(text[text_line]);

		ImageView image = (ImageView) layout.findViewById(R.id.image);
		image.setImageResource(R.drawable.captain);

		//alertDialog.dismiss();
		//GameStatus.getGameStatusObject().setInstructions(isChecked);
		
		builder = new AlertDialog.Builder(mContext);
		builder.setView(layout);
		alertDialog = builder.create();
		alertDialog.getWindow().setGravity(Gravity.BOTTOM);
		alertDialog.setCanceledOnTouchOutside(true);
		alertDialog.show();
	}
	
	public void dissmissAlert(){
		alertDialog.dismiss();
	}
	
}
