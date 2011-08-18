package hiit.nopsa.pirate;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * 
 * 
 * @author Dinesh Wijekoon
 */
public class MainActivity extends Activity {
    
	private HomeView homeView;
	private final String TAG = "NOPSA-P";
	public MediaPlayer mPlayer = null;
	private String name_enterd="";
	private MainActivity mainActivity;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"MainActivity Started");
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (!isOnline())
        	showExitOption();
        GameStatus.getGameStatusObject().loadGameData(this);
        if (GameStatus.getGameStatusObject().getUser_id()==0)
        	popInputScreenToGetName();
        homeView = new HomeView(this,this);
        setContentView(homeView);
        mainActivity = this;
	}
	
	private boolean isOnline() {
		 ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		 boolean output = false;
		 try{
			output = cm.getActiveNetworkInfo().isConnectedOrConnecting();
		 }catch(Exception e){}
		 return output;
	}
	
	private void showExitOption(){
		AlertDialog.Builder alert = new AlertDialog.Builder(this);                 
		alert.setMessage("Game needs Internet Connection, Please Check your Interent Connection first.");                
		alert.setPositiveButton("Exit", new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int whichButton) {  
				MainActivity.this.finish();
			}  
		});  
		alert.show();
	}
	
	public void  popInputScreenToGetName(){
		GameStatus.getGameStatusObject().setUser_name("temp_user");
		// At this version we are not saving data from user. This can be used later.
		/*
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
				GameStatus.getGameStatusObject().setUser_name(name_enterd);
				getUserIdFromServer();
			}  
		});  
		alert.show();
		*/
	}
	
	public void getUserIdFromServer(){
		//TODO get a user_id from server -- send user_name to server and get the user_id
		//     then save the user_id into GameStatus as well
		try {
			String url_str = "http://ec2-107-20-212-167.compute-1.amazonaws.com/nopsa_game/user.php?name="+name_enterd;
			Log.d(TAG,url_str);
			URL url = new URL(url_str);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			InputStream stream = connection.getInputStream();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(stream);
			doc.getDocumentElement().normalize();
			int user_id = Integer.parseInt(doc.getElementsByTagName("id").item(0).getTextContent());
			GameStatus.getGameStatusObject().setUser_id(user_id);
			Log.d(TAG,"User Name:"+GameStatus.getGameStatusObject().getUser_name()+" User Id:"+
									GameStatus.getGameStatusObject().getUser_id());
		}catch (Exception e){
			Log.d(TAG,"User Id retrival FAILED");
		}
	}
	
	private void playSound(){
		if (GameStatus.getGameStatusObject().isSounds()){
			new Thread(new Runnable() {
				public void run() {
					try{
						mPlayer = MediaPlayer.create(MainActivity.this, R.raw.screen1_intro);
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
	}
	
	@Override
	protected void onResume() {
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.options, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.sound:
	    	if (GameStatus.getGameStatusObject().isSounds()){
	    		GameStatus.getGameStatusObject().setSounds(false);
	    		Toast.makeText(this,"Sound OFF", Toast.LENGTH_SHORT).show();	
	    		if (mPlayer!=null){
	    			mPlayer.stop();
	    			mPlayer = null;
	    		}
	    	}else{
	    		GameStatus.getGameStatusObject().setSounds(true);
	    		Toast.makeText(this,"Sound ON", Toast.LENGTH_SHORT).show();	
	    		playSound();
	    	}
	        return true;
	    case R.id.help:
	    	if (GameStatus.getGameStatusObject().getInstructions()){
	    		GameStatus.getGameStatusObject().setInstructions(false);
	    		Toast.makeText(this,"Help Dialogs OFF", Toast.LENGTH_SHORT).show();	
	    	}else{
	    		GameStatus.getGameStatusObject().setInstructions(true);
	    		Toast.makeText(this,"Help Dialogs ON", Toast.LENGTH_SHORT).show();	
	    	}
	        return true;
	    case R.id.haptics:
	    	if (GameStatus.getGameStatusObject().isHaptics()){
	    		GameStatus.getGameStatusObject().setHaptics(false);
	    		Toast.makeText(this,"Haptics OFF", Toast.LENGTH_SHORT).show();	
	    	}else{
	    		GameStatus.getGameStatusObject().setHaptics(true);
	    		Toast.makeText(this,"Haptics ON", Toast.LENGTH_SHORT).show();	
	    	}
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

}