package hiit.nopsa.pirate;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public class GameHomeView extends View{

	private Bitmap sea1 = null;
	private Bitmap ship, icons;
	private Activity gameHomeActivity;
	private final String TAG = "NOPSA-P";
	private GameStatus gameStatus;
	private boolean gameOn = false;
	private Intent islandHome;
	private Intent populateItems;
	private InstructionDialog id;

	
	public GameHomeView(Context context, Activity activity) {
		super(context);
		gameHomeActivity = activity;
		gameStatus = GameStatus.getGameStatusObject();
	}

	protected void onDraw(Canvas canvas){
		if (sea1 == null){
			loadBitmaps();
			if (gameStatus.getInstructions()){		
				id = new InstructionDialog();
				id.popInstructionsDialog("Welcome to the Sea", 
						"Your Task is to be the best pirate in whole world. In order to do that you have to " +
						"capture slaves and animals and make them intelligent. Make crew. Mean while you are sailing you need " +
						"food to keep your animals, slaves and crew alive. Dont forget to collect food...", 
						"Go Sailing !!",
						gameHomeActivity);
			}
			
		}
		//==========Draw the background
		Paint background = new Paint();
		background.setColor(Color.BLACK);
		canvas.drawRect(0, 0, getWidth(), getHeight(), background);
		
		//==========Draw Sea & Sky & Draw Ship
		Paint sea_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		sea_paint.setStyle(Style.FILL);
		canvas.drawBitmap(sea1, 0, 0, sea_paint);
		//Paint ship_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		//ship_paint.setStyle(Style.FILL);
		canvas.drawBitmap(ship, 0, 0, sea_paint);
	
		//==========Draw Animal, Slaves, and Food Icons & BACK icon
		Paint icon_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		icon_paint.setStyle(Style.FILL);
		icons = BitmapFactory.decodeResource(getResources(), R.drawable.back_icon);
		canvas.drawBitmap(icons, 904, 480, icon_paint);
		icons = BitmapFactory.decodeResource(getResources(), R.drawable.animal_icon);
		canvas.drawBitmap(icons, 20, 20, icon_paint);
		icons = BitmapFactory.decodeResource(getResources(), R.drawable.slave_icon);
		canvas.drawBitmap(icons, 20, 140, icon_paint);
		icons = BitmapFactory.decodeResource(getResources(), R.drawable.food_icon);
		canvas.drawBitmap(icons, 20, 260, icon_paint);
		
		//=========Write(Draw) Text (Ship Class & etc..)
		Paint text_paint = new Paint();
		text_paint.setColor(Color.BLACK);
		text_paint.setStyle(Style.FILL);
		text_paint.setAntiAlias(true);
		text_paint.setTextSize(20);
		text_paint.setTypeface(Typeface.SANS_SERIF);
		canvas.drawText("Ship Class :"+gameStatus.getShip_class(),800, 50, text_paint);
		canvas.drawText("Weapon Class :"+gameStatus.getWeapon_class(),800, 75, text_paint);
		canvas.drawText("Sails Class :"+gameStatus.getSails_class(),800, 100, text_paint);
		
		canvas.drawText("Crew Size :"+gameStatus.getNum_crew(),800, 140, text_paint);
		canvas.drawText("Coins :"+gameStatus.getCoins(),800, 165, text_paint);
		
		canvas.drawText("Time on Sea :"+(gameStatus.getTimeOfNextIsland()/60)+":"+(gameStatus.getTimeOfNextIsland()%60), 800, 205, text_paint);
		canvas.drawText("Food Left :"+gameStatus.getTotal_food_score(),800,230, text_paint);
	
		if (gameOn==false){
			gameOn = true;
			startGameTimeElapseThread();	
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent me) {
		if (me.getAction() == MotionEvent.ACTION_DOWN) {
			// Touched the screen
			if ( (480<me.getY())&&(580>me.getY())&&(1004>me.getX())&&(904<me.getX())){
				// Go Back to HOME Screen
				Log.d(TAG, "Back to Home Screen");
				//Save the state in a file
				GameStatus.getGameStatusObject().saveGameData(gameHomeActivity);
				gameOn = false;
				//Clearing Memory
				sea1 = null;
				ship = null;
				System.gc();
				gameHomeActivity.finish();
			}
			
			populateItems = new Intent(gameHomeActivity,PopulateItems.class);
			if ((20<me.getY())&&(120>me.getY())&&(120>me.getX())&&(20<me.getX())){
				// TODO Catch Animals Implementation
				populateItems.putExtra("type", 0);
	    		gameHomeActivity.startActivity(populateItems);
			}
			if ((140<me.getY())&&(240>me.getY())&&(120>me.getX())&&(20<me.getX())){
				// TODO Catch Slaves
				populateItems.putExtra("type", 1);
	    		gameHomeActivity.startActivity(populateItems);
			}
			if ((260<me.getY())&&(360>me.getY())&&(120>me.getX())&&(20<me.getX())){
				// TODO Collect Food
				populateItems.putExtra("type", 2);
	    		gameHomeActivity.startActivity(populateItems);
			}
			
		}
		return true;
	}
	
	private void startGameTimeElapseThread(){
		if (gameStatus.getTimeOfNextIsland() < 1){
			gameOn = false;
			// dismiss the alert box
			try{
				id.dissmissAlert();
				Log.d(TAG,"Alert Dissmissed");
			}
			catch (NullPointerException ne){
				Log.d(TAG,"No Alert To Dissmiss");
			}
			// Clear memory
			sea1 = null;
			ship = null;
			System.gc();
			// Screen Change in to Island Mode
			islandHome = new Intent(gameHomeActivity,IslandHome.class);
    		gameHomeActivity.startActivityForResult(islandHome,231);
		}
		if (gameOn){
			new Thread(new Runnable() {
				Date d = new Date();
				public void run() {
						gameStatus.setTimeOfNextIsland(gameStatus.getTimeOfNextIsland()-(int)(((d.getTime()-gameStatus.getLastTimeUpdated()))/1000));
						gameStatus.setLastTimeUpdated(d.getTime());
						Log.d(TAG,""+d.getTime()+"ms  "+d.getTime()/1000+"sec");
						postInvalidate(795, 150, 1024, 300);
						android.os.SystemClock.sleep(1000); 
						startGameTimeElapseThread();
			    }
			}).start();
		}
	}
	
	private void loadBitmaps(){
		System.gc();
		sea1 = BitmapFactory.decodeResource(getResources(), R.drawable.day_sea);
		//sea2 = BitmapFactory.decodeResource(getResources(), R.drawable.sea_attack);
		//sea3 = BitmapFactory.decodeResource(getResources(), R.drawable.sea_island);
		ship = BitmapFactory.decodeResource(getResources(), R.drawable.ship_look);
	}
	
	public void gameResume(){
		Date d = new Date();
		gameStatus.setLastTimeUpdated(d.getTime());
		//TODO gameStatus.setTimeOfNextIsland(x)
		// x <- Needs to be random between 2 min to 5 min 
		gameStatus.setTimeOfNextIsland(30);
		invalidate();
	}
	
}
