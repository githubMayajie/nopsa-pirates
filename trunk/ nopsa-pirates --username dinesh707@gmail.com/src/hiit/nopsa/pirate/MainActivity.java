package hiit.nopsa.pirate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {
    
	private HomeView homeView;
	private final String TAG = "NOPSA-P";
	private MediaPlayer mPlayer = null;
	private String name_enterd="";
	private MainActivity mainActivity;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"MainActivity Started");
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        GameStatus.getGameStatusObject().loadGameData(this);
        if (GameStatus.getGameStatusObject().getUser_id()==0)
        	popInputScreenToGetName();
        homeView = new HomeView(this,this);
        setContentView(homeView);
        mainActivity = this;
	}
	
	public void  popInputScreenToGetName(){
		Log.d(TAG,"ENTER NAME and update Game Data with it --->");
		AlertDialog.Builder alert = new AlertDialog.Builder(this);                 
		alert.setMessage("What will be your Pirate Name Cap'n?");                

		// Set an EditText view to get user input   
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Sail", new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int whichButton) {  
				name_enterd = input.getText().toString();
				Log.d(TAG, "ööööööööööööööööööööööööööööööö--- "+name_enterd.length());
				if (name_enterd.length()<1){
					mainActivity.popInputScreenToGetName();
				}
				else
					return;                  
			}  
		});  
		alert.show();
		GameStatus.getGameStatusObject().setUser_name(name_enterd);
		//TODO get a user_id from server -- send user_name to server and get the user_id
		//     then save the user_id into GameStatus as well
	}
	
	private void playSound(){
		new Thread(new Runnable() {
			public void run() {
				try{
					mPlayer = MediaPlayer.create(MainActivity.this, R.raw.carribian_theam);
					mPlayer.setLooping(true);
					mPlayer.start();
					while(mPlayer.isPlaying()){
						android.os.SystemClock.sleep(100);
					}
				}catch(Exception e){
					Log.d(TAG,"ERROR PLAYING");
					e.printStackTrace();
				}
			}}).start();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		playSound();
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		if (mPlayer!=null){
			mPlayer.stop();
			mPlayer = null;
		}
		super.onDestroy();
	}
	
	@Override
	protected void onPause() {
		if (mPlayer!=null){
			mPlayer.stop();
			mPlayer = null;
		}
		super.onPause();
	}
	
	
	
	
	
}