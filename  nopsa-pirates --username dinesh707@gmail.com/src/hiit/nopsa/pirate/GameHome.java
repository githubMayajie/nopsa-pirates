package hiit.nopsa.pirate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class GameHome extends Activity{
	
	private GameHomeView gameHomeView;
	private final String TAG = "NOPSA-P";
	private GameStatus gameStatus;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
	    //Remove title bar
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    //Remove notification bar
	    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    GameStatus.getGameStatusObject().loadGameData(this);
	    gameHomeView = new GameHomeView(this,this);
	    setContentView(gameHomeView);
	}
	
	/*private void loadGameData(){
		String[] gameData;
		StringBuffer strContent;
		try {
			FileInputStream fis = openFileInput("game.dat");
			Log.d(TAG,"FILE OPEN FOR READ");
			int ch;
		    strContent = new StringBuffer("");
		    try {
				while((ch=fis.read()) != -1)
				    strContent.append((char)ch);
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Log.d(TAG,strContent.toString());
			gameData = strContent.toString().split(";");
			// Load Data to GameStatus Object
			gameStatus = GameStatus.getGameStatusObject();
			gameStatus.setShip_class(Integer.parseInt(gameData[0].split(",")[1]));
			gameStatus.setWeapon_class(Integer.parseInt(gameData[1].split(",")[1]));
			gameStatus.setSails_class(Integer.parseInt(gameData[2].split(",")[1]));
			gameStatus.setNum_crew(Integer.parseInt(gameData[3].split(",")[1]));
			gameStatus.setCoins(Integer.parseInt(gameData[4].split(",")[1]));
			gameStatus.setNum_animals(Integer.parseInt(gameData[5].split(",")[1]));
			gameStatus.setNum_slaves(Integer.parseInt(gameData[6].split(",")[1]));
			gameStatus.setNum_food(Integer.parseInt(gameData[7].split(",")[1]));
			gameStatus.setTotal_food_score(Integer.parseInt(gameData[8].split(",")[1]));			
			if (Long.parseLong(gameData[9].split(",")[1])==0){
				Date d = new Date();
				gameStatus.setLastTimeUpdated(d.getTime());
			}
			gameStatus.setTimeOfNextIsland(Integer.parseInt(gameData[10].split(",")[1]));
			gameStatus.setInstructions(Boolean.parseBoolean(gameData[11].split(",")[1]));
			
		} catch (FileNotFoundException e) {
			try {
				FileOutputStream fos = openFileOutput("game.dat", MODE_WORLD_WRITEABLE);
				Log.d(TAG,"FILE NEWLY CREATED");
				String game_data = 
						"ship_class,1;weapon_class,1;sails_class,1;num_crew,3;coins,1000;" +
						"num_animals,0;num_slaves,0;num_food,0;total_food_score,0;" +
						"lastTimeUpdated,0;timeOfNextIsland,20;instructions,true";
				try {
					fos.write(game_data.getBytes());
					fos.flush();
					fos.close();
					this.loadGameData();	// Method is called again in order to load Data
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}*/
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG,"Retured from IslandScreen");
		if (requestCode==231){
			gameHomeView.gameResume();
		}
	}
	

}
