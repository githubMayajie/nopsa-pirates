package hiit.nopsa.pirate;

import java.util.Date;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

/**
 * IMPORTANT : "MarketHome.java" and "MarketHomeView.java" needs to be re modeled and simplified.
 * 
 * @author Dinesh Wijekoon
 */
public class MarketHomeView extends SurfaceView implements SurfaceHolder.Callback{
	
	private final String TAG = "NOPSA-P";
	private Activity marketHomeActivity;
	private ViewControllerThread _thread;
	private Bitmap sea = null, coin = null, animal = null, slave= null;
	private Bitmap coin_glow = null, animal_glow=null, slave_glow=null;
	private Bitmap old_paper = null, animal_shop_glow=null, slave_shop_glow=null;
	private GameStatus gameStatus = null;
	private float glowValue = 0;
	private boolean activityIsOnTop = false;
	private boolean animal_btn_clicked = false;
	private boolean slave_btn_clicked = false;
	private int dragfinger_animal_x = 0;
	private int dragfinger_slave_x = 0;
	private int dragging_animal_id = -1;
	private int dragging_slave_id = -1;
	private boolean dragCoin = false;
	private int drag_x=0, drag_y=0;
	private Bitmap dragImage = null;
	
	
	public MarketHomeView(Context context, Activity activity) {
		super(context);
		marketHomeActivity = activity;
		gameStatus = GameStatus.getGameStatusObject();
		glowValue = 250; //Make this Dynamically Changing from a Thread
		getHolder().addCallback(this);
		_thread = new ViewControllerThread(getHolder(), this);
		setFocusable(true);
		//InfoDialog();
	}
	
	protected void onDraw(Canvas canvas){
		if (sea == null){
			loadBitmaps();
		    startGameTimeElapseThread();
		}
		
		Paint icon_glow = new Paint();
		icon_glow.setColor(Color.WHITE);
		icon_glow.setAlpha((int) glowValue);
		
		//==========Draw the background
		Paint background = new Paint();
		background.setColor(Color.BLACK);
		canvas.drawRect(0, 0, getWidth(), getHeight(), background);
		
		//==========Draw Backgrund Image
		Paint back_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		back_paint.setStyle(Style.FILL);
		canvas.drawBitmap(sea, 0, 0, back_paint);
		
		//==========Draw Animals Ribbon
		if (dragfinger_animal_x>0){
			Paint text_paint2 = new Paint();
			text_paint2.setColor(Color.WHITE);
			text_paint2.setStyle(Style.FILL);
			text_paint2.setAntiAlias(true);
			text_paint2.setTextSize(15);
			text_paint2.setTypeface(Typeface.SANS_SERIF);
			Paint rectPaint = new Paint();
			rectPaint.setColor(Color.WHITE);
			rectPaint.setAlpha(40);
			int count=0;
			int num_items = ((dragfinger_animal_x-110)/75);
			try{
				for (Collectable collectable: GameStatus.getGameStatusObject().getCollectableFromId(0)){
					if (count == num_items)
						break;
					canvas.drawRect(110,362,dragfinger_animal_x+10,457, rectPaint);
					canvas.drawBitmap(collectable.getIcon_bitmap(),(float)(dragfinger_animal_x-155-(count*90)), (float) 372.5 , back_paint);
					canvas.drawText("("+collectable.getScore()+")",(float)(dragfinger_animal_x-130-(count*90)),470, text_paint2);
					count = count+1;
		    	}
			}catch (Exception e){
				Log.d(TAG,"Error: Collectable Array Size Changed on Remove");
			}
			if (dragfinger_animal_x>120){
				rectPaint.setAlpha((int) glowValue);
				canvas.drawCircle(970, 410, 10, rectPaint);
			}
		}
		
		//==========Draw Slaves Ribbon
		if (dragfinger_slave_x>0){
			Paint text_paint2 = new Paint();
			text_paint2.setColor(Color.WHITE);
			text_paint2.setStyle(Style.FILL);
			text_paint2.setAntiAlias(true);
			text_paint2.setTextSize(15);
			text_paint2.setTypeface(Typeface.SANS_SERIF);
			Paint rectPaint = new Paint();
			rectPaint.setColor(Color.WHITE);
			rectPaint.setAlpha(40);
			int count=0;
			int num_items = ((dragfinger_slave_x-110)/75);
			try{
				for (Collectable collectable: GameStatus.getGameStatusObject().getCollectableFromId(1)){
					if (count == num_items)
						break;
					canvas.drawRect(110,482,dragfinger_slave_x+10,577, rectPaint);
					canvas.drawBitmap(collectable.getIcon_bitmap(),(float)(dragfinger_slave_x-155-(count*90)), (float) 492.5 , back_paint);
					canvas.drawText("("+collectable.getScore()+")",(float)(dragfinger_slave_x-130-(count*90)), 590 , text_paint2);
					count = count+1;
		    	}
			}catch(Exception e){
				// Error in Loading Array
			}
			if (dragfinger_slave_x>120){
				rectPaint.setAlpha((int) glowValue);
				canvas.drawCircle(970, 530, 10, rectPaint);
			}
		}
		//==========Draw Dagging Image
		if (dragImage!=null)
			canvas.drawBitmap(dragImage, drag_x, drag_y, back_paint);
		
		
		//==========Draw Icons
		Paint icon_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		canvas.drawBitmap(coin, 800, 20, icon_paint);
		canvas.drawBitmap(coin_glow, 800, 20, icon_glow);
		canvas.drawBitmap(animal, 20, 360, icon_paint);
		canvas.drawBitmap(animal_glow, 20, 360, icon_glow);
		canvas.drawBitmap(slave, 20, 480, icon_paint);
		canvas.drawBitmap(slave_glow, 20, 480, icon_glow);
		canvas.drawBitmap(old_paper, 20, 20, icon_paint);
		if (dragCoin){
			canvas.drawBitmap(coin, drag_x, drag_y, icon_paint);
			canvas.drawRect(40, 90, 210, 140,icon_glow);
			canvas.drawRect(40, 170, 210, 220,icon_glow);
		}
		
		if (dragfinger_animal_x<120)
			if (animal_btn_clicked)
				canvas.drawCircle((float)(150+glowValue*1.6), 410, 10, icon_glow);
		if (dragfinger_slave_x<120)
			if (slave_btn_clicked)
				canvas.drawCircle((float)(150+glowValue*1.6), 530, 10, icon_glow);
		
		if ((dragfinger_animal_x==977)&&(animal_btn_clicked)){
			canvas.drawBitmap(animal_shop_glow,0,0, icon_glow);
		}
		if ((dragfinger_slave_x==977)&&(slave_btn_clicked)){
			canvas.drawBitmap(slave_shop_glow,0,0, icon_glow);
		}
		
		//==========Draw Text
		Paint text_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		text_paint.setColor(Color.WHITE);
		text_paint.setTextSize(20);
		canvas.drawText("Coins", 920, 60, text_paint);
		canvas.drawText(""+gameStatus.getCoins(), 920, 90, text_paint);
		canvas.drawText("Ship Class: "+gameStatus.getShip_class(), 830, 170, text_paint);
		canvas.drawText("Crew Size: "+gameStatus.getNum_crew(), 830, 200, text_paint);
		text_paint.setColor(Color.BLACK);
		canvas.drawText("Upgrade Ship to", 50, 110, text_paint);
		canvas.drawText("Class "+(gameStatus.getShip_class()+1)+" ("+gameStatus.getShip_class()*666+")", 50, 135, text_paint);
		canvas.drawText("Buy Next Crew", 50, 190, text_paint);
		canvas.drawText("Member ("+((gameStatus.getNum_crew()+1)*230)+")", 50, 215, text_paint);
		
		if (!(animal_btn_clicked||slave_btn_clicked)){
			Bitmap icons = BitmapFactory.decodeResource(getResources(), R.drawable.ok_icon);
			canvas.drawBitmap(icons, 904, 480, back_paint);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent me) {
		if (me.getAction() == MotionEvent.ACTION_DOWN) {
			//Animal Ribbon Movements===================
			if ((20<me.getX()&&(me.getX()<120)&&(360<me.getY())&&(me.getY()<460)))
					animal_btn_clicked = true;
			if (cartDist((int)me.getX(),(int)me.getY(), 970, 410)<40){
					dragfinger_animal_x = 0;
					animal_btn_clicked = false;
			}
			//Slave Ribbon Movements====================
			if ((20<me.getX()&&(me.getX()<120)&&(480<me.getY())&&(me.getY()<580)))
				slave_btn_clicked = true;
			if (cartDist((int)me.getX(),(int)me.getY(), 970, 530)<40){
				dragfinger_slave_x = 0;
				slave_btn_clicked = false;
			}
			//==========================================
			//Animal Drag and drop to shop===================
			if ((dragfinger_animal_x == 977)&&(360<me.getY())&&(me.getY()<460)){
				dragging_animal_id = (int) Math.floor(((897-me.getX())/90));
				if ((dragging_animal_id<0)||(8<dragging_animal_id))
					dragging_animal_id = -1;
			}
			//Slave Drag and drop to shop===================
			if ((dragfinger_slave_x == 977)&&(480<me.getY())&&(me.getY()<580)){
				dragging_slave_id = (int) Math.floor(((897-me.getX())/90));
				if ((dragging_slave_id<0)||(8<dragging_slave_id))
					dragging_slave_id = -1;
			}
			//==================================================
			//Click on COIN
			if (cartDist(850, 79, (int) me.getX(), (int) me.getY())<50){
				drag_x = (int) (me.getX()-50);
				drag_y = (int) (me.getY()-50);
				dragCoin = true;
			}
			//==================================================
			//Exit View
			if ((904<me.getX())&&(me.getX()<1004)&&(480<me.getY())&&(me.getY()<580))
				if (!(animal_btn_clicked||slave_btn_clicked))
					marketHomeActivity.finish();
			
		}
		if (me.getAction() == MotionEvent.ACTION_MOVE) {
			//Animal Ribbon Movements===================
			if (animal_btn_clicked)
				if (dragfinger_animal_x!=977)
					dragfinger_animal_x = (int) me.getX();
			//Slave Ribbon Movements====================
			if (slave_btn_clicked)
				if (dragfinger_slave_x!=977)
					dragfinger_slave_x = (int) me.getX();
			//==========================================
			//Animal Drag and drop to shop===================
			if (dragging_animal_id>=0){
				if ((dragImage==null)&&((GameStatus.getGameStatusObject().getCollectableFromId(0).size()-1)>=dragging_animal_id))
					dragImage = GameStatus.getGameStatusObject().getCollectableFromId(0).get(dragging_animal_id).getIcon_bitmap();
				drag_x = (int) (me.getX()-37.5);
				drag_y = (int) (me.getY()-37.5);	
			}
			//Slave Drag and drop to shop===================
			if (dragging_slave_id>=0){
				if ((dragImage==null)&&((GameStatus.getGameStatusObject().getCollectableFromId(1).size()-1)>=dragging_slave_id))
					dragImage = GameStatus.getGameStatusObject().getCollectableFromId(1).get(dragging_slave_id).getIcon_bitmap();
				drag_x = (int) (me.getX()-37.5);
				drag_y = (int) (me.getY()-37.5);	
			}
			//================================================
			//Coin Movement
			if (dragCoin){
				drag_x = (int) (me.getX()-50);
				drag_y = (int) (me.getY()-50);
			}
		}
		if (me.getAction() == MotionEvent.ACTION_UP) {
			//Animal Ribbon Movements===================
			if ((cartDist((int)me.getX(),(int)me.getY(), 970, 410)<50)&&(animal_btn_clicked)){
				dragfinger_animal_x = 977;
			}
			if (dragfinger_animal_x!=977){
				dragfinger_animal_x = 0;
				animal_btn_clicked = false;
			}
			//Slave Ribbon Movements====================
			if ((cartDist((int)me.getX(),(int)me.getY(), 970, 530)<50)&&(slave_btn_clicked)){
				dragfinger_slave_x = 977;
			}
			if (dragfinger_slave_x!=977){
				dragfinger_slave_x = 0;
				slave_btn_clicked = false;
			}
			//Animal Drop In Shop==========================================
			if ((dragImage!=null)&&(dragging_animal_id>=0)){
				if (cartDist(418, 269, (int)me.getX(), (int)me.getY())<100){
					Collectable c = GameStatus.getGameStatusObject().getCollectableFromId(0).get(dragging_animal_id);
					if (c.getScore()<75){
						Toast.makeText(this.getContext(), "Animals Should Trained to 75 minimum before sell", Toast.LENGTH_SHORT).show();
					}else{
						// Animals are good enough to sell
						gameStatus.setCoins(gameStatus.getCoins()+(c.getScore()*10));
						GameStatus.getGameStatusObject().removeItemFromTypeAndId(0, dragging_animal_id);
					}
				}
			}
			//Slave Drop In Shop or Convert House==========================================
			if ((dragImage!=null)&&(dragging_slave_id>=0)){
				//Selling Slaves on SHOP
				if (cartDist(379, 374, (int)me.getX(), (int)me.getY())<100){
					Collectable c = GameStatus.getGameStatusObject().getCollectableFromId(1).get(dragging_slave_id);
					if (c.getScore()<100){
						Toast.makeText(this.getContext(), "Slaves Should Trained to 100 minimum before sell", Toast.LENGTH_SHORT).show();
					}else{
						// Slaves are good enough to sell
						gameStatus.setCoins(gameStatus.getCoins()+(c.getScore()*15));
						GameStatus.getGameStatusObject().removeItemFromTypeAndId(1, dragging_slave_id);
					}
				}
				//Converting Slaves on Church
				if (cartDist(600, 106, (int)me.getX(), (int)me.getY())<160){
					Collectable c = GameStatus.getGameStatusObject().getCollectableFromId(1).get(dragging_slave_id);
					if (c.getScore()<175){
						Toast.makeText(this.getContext(), "Slaves Should Trained to 175 to be Crew", Toast.LENGTH_SHORT).show();
					}else{
						// Slaves are good enough to sell
						gameStatus.setNum_crew(gameStatus.getNum_crew()+1);
						GameStatus.getGameStatusObject().removeItemFromTypeAndId(1, dragging_slave_id);
					}
				}	
			}
			//Coins drop to buy "ShipUpgrades" or "Next Crew Member"=============================
			if (dragCoin){
				Log.d(TAG,"Coin Droped");
				if((40<me.getX())&&(me.getX()<210)&&(90<me.getY())&&(me.getY()<140)){
					//Upgrade Ship
					if ((gameStatus.getNum_crew()>gameStatus.getShip_class()*3)&&(gameStatus.getCoins()>666*gameStatus.getShip_class())){
						// You need 3xclass crew and 666xclass coins for an upgrade
						gameStatus.setCoins(gameStatus.getCoins()-(666*gameStatus.getShip_class()));
						gameStatus.setShip_class(gameStatus.getShip_class()+1);
					}else{
						Toast.makeText(this.getContext(), "You need "+gameStatus.getShip_class()*3 +"crew members, and " +
								666*gameStatus.getShip_class()+" coins to upgrade into next class" 
								, Toast.LENGTH_LONG).show();
					}
				}
				if((40<me.getX())&&(me.getX()<210)&&(170<me.getY())&&(me.getY()<220)){
					//Buy Next Crew
					if (gameStatus.getCoins()>((gameStatus.getNum_crew()+1)*230)){
						gameStatus.setCoins(gameStatus.getCoins()-((gameStatus.getNum_crew()+1)*230));
						gameStatus.setNum_crew(gameStatus.getNum_crew()+1);
					}else{
						Toast.makeText(this.getContext(), "You need "+((gameStatus.getNum_crew()+1)*230)+" coins to buy a crew member", 
								Toast.LENGTH_SHORT).show();
					}
				}
			}
			
			// Resetting all items
			dragCoin = false;
			dragImage = null;
			dragging_animal_id = -1;
			dragging_slave_id = -1;
		}
		return true;
	}
	
	
	
	//============================================================================================	
	//========Extended Methods to Work on Background==============================================
	//============================================================================================

	private void loadBitmaps(){
		System.gc();
		sea = BitmapFactory.decodeResource(getResources(), R.drawable.pirate_bay);
		coin = BitmapFactory.decodeResource(getResources(), R.drawable.coin);
		animal = BitmapFactory.decodeResource(getResources(), R.drawable.animal_icon);
		slave = BitmapFactory.decodeResource(getResources(), R.drawable.slave_icon);
		coin_glow = BitmapFactory.decodeResource(getResources(), R.drawable.coin_glow);
		animal_glow = BitmapFactory.decodeResource(getResources(), R.drawable.animal_glow);
		slave_glow = BitmapFactory.decodeResource(getResources(), R.drawable.slave_glow);
		old_paper = BitmapFactory.decodeResource(getResources(), R.drawable.old_paper);
		animal_shop_glow = BitmapFactory.decodeResource(getResources(), R.drawable.anilmal_shop_glow);
		slave_shop_glow = BitmapFactory.decodeResource(getResources(), R.drawable.slave_shop_glow);
	}
	
	private int cartDist(int x1, int y1, int x2, int y2){
		 return (int) Math.sqrt((Math.pow(x1-x2, 2)+Math.pow(y1-y2, 2)));
	}
	
	private synchronized void startGameTimeElapseThread(){
		if (activityIsOnTop){
			new Thread(new Runnable() {
				public void run() {
						for (int i=0;i<10;i++){
							Date d = new Date();
							glowValue = (((float) Math.sin((d.getTime()/10)*0.0174532925))*120)+120;
							android.os.SystemClock.sleep(100);
						}
						startGameTimeElapseThread();
			    }
			}).start();
		}
	}
	
	
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		_thread.setRunning(true);
		_thread.start();
		activityIsOnTop = true;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		activityIsOnTop = false;
		 boolean retry = true;
		    _thread.setRunning(false);
		    while (retry) {
		        try {
		            _thread.join();
		            retry = false;
		        } catch (InterruptedException e) {
		        }
		    }	
	}
	
	 class ViewControllerThread extends Thread {
	        private SurfaceHolder _surfaceHolder;
	        private MarketHomeView _marketHomeView;
	        private boolean _run = false;
	     
	        public ViewControllerThread(SurfaceHolder sh, MarketHomeView mhv) {
	            _surfaceHolder = sh;
	            _marketHomeView = mhv;
	        }
	     
	        public void setRunning(boolean run) {
	            _run = run;
	        }
	        
	        public SurfaceHolder getSurfaceHolder(){
	        	return _surfaceHolder;
	        }
	     
	        @Override
	        public void run() {
	    	   Canvas c;
	    	    while (_run) {
	    	        c = null;
	    	        try {
	    	            c = _surfaceHolder.lockCanvas(null);
	    	            synchronized (_surfaceHolder) {
	    	                _marketHomeView.onDraw(c);
	    	            }
	    	        } finally {
	    	            if (c != null) {
	    	                _surfaceHolder.unlockCanvasAndPost(c);
	    	            }
	    	        }
	    	    }     
	        }
	    }
	
	

}
