package hiit.nopsa.pirate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;

import android.widget.TextView;

public class InstructionDialog {
	
	private AlertDialog.Builder builder;
	private AlertDialog alertDialog;
	
	public void popInstructionsDialog(String title, String text, String buttonText, Activity activity){
		
		Context mContext = activity;
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View layout = inflater.inflate(R.layout.instructions, (ViewGroup) activity.findViewById(R.id.layout_root));

		TextView text2 = (TextView) layout.findViewById(R.id.title);
		text2.setTextSize(20);
		text2.setText(title);

		TextView text1 = (TextView) layout.findViewById(R.id.text);
		text1.setTextSize(12);
		text1.setText(text);
		
		CheckBox checkBox = (CheckBox) layout.findViewById(R.id.checkBox);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				GameStatus.getGameStatusObject().setInstructions(isChecked);
			}
		});
		
		ImageView image = (ImageView) layout.findViewById(R.id.image);
		image.setImageResource(R.drawable.icon);

		Button btn = (Button) layout.findViewById(R.id.ok_btn);
		btn.setTextSize(12);
		btn.setText(buttonText);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alertDialog.dismiss();
			}
		});
		
		builder = new AlertDialog.Builder(mContext);
		builder.setView(layout);
		alertDialog = builder.create();
		
		alertDialog.show();
	}
	
	public void dissmissAlert(){
		alertDialog.dismiss();
	}
}
