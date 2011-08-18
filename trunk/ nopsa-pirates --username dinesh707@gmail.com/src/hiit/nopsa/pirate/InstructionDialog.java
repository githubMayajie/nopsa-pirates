package hiit.nopsa.pirate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Inflates a dialog in each screen of the game if "Text" option is ON. 
 * It will tell player what is their task in every game page.
 * 
 * @author Dinesh Wijekoon
 */
public class InstructionDialog {
	
	private AlertDialog.Builder builder;
	private AlertDialog alertDialog;
	private final String TAG = "NOPSA-P";
	
	public void popInstructionsDialog(String title, String text, Activity activity){
		Log.d(TAG,"Instruction Dialog Called, But game status is ::"+GameStatus.getGameStatusObject().getInstructions());
		if (!GameStatus.getGameStatusObject().getInstructions())
			return;
		Context mContext = activity;
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View layout = inflater.inflate(R.layout.instructions, (ViewGroup) activity.findViewById(R.id.layout_root));
		layout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.d(TAG,"Hello Tocuhed !!!");
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
		text1.setText(text);

		ImageView image = (ImageView) layout.findViewById(R.id.image);
		image.setImageResource(R.drawable.captain);

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
