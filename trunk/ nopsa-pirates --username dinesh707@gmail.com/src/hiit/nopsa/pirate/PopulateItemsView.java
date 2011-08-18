package hiit.nopsa.pirate;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import com.senseg.effect.EffectManager;
import com.senseg.effect.FeelableSurface;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 
 * @author Dinesh Wijekoon
 */
public class PopulateItemsView extends SurfaceView implements SurfaceHolder.Callback{

	private Activity populateItemsActivity;
	private final String TAG = "NOPSA-P";
	private ViewControllerThread _thread; 
	private int collectableType; //0-animal, 1-slave, 2-food
	private PopulateItemsImageManager popImageManager = null;
	private Collectable selectedColectable = null;
	private int selectedCollectableXPosition = 0;
	private Bitmap background,trash_icon,back_icon,plus1;
	private Bitmap bitmap=null;
	private boolean imageDragging = false;
	private int imgDrag_x = 0;
	private Paint squarePaint = null;
	private boolean boundaryMarkingOn = false;
	private ArrayList<int[]> boundary = null; 
	private String imageId = null;
	private float scaledVal;
	private GameStatus gameStatus = null; 
	private String boundary_str = "";
	private boolean anmatePlus1 = false;
	private double plus1_x=100,plus1_y=100;
	private EffectManager manager;
	private FeelableSurface mSurface_dragImg;
	
	public PopulateItemsView(Context context, Activity activity) {
		super(context);
		gameStatus = GameStatus.getGameStatusObject();
		imgDrag_x = 0;
		populateItemsActivity = activity;
		getHolder().addCallback(this);
		_thread = new ViewControllerThread(getHolder(), this);
		collectableType = activity.getIntent().getExtras().getInt("type");
		infoDialog();
	}
	
	protected void onDraw(Canvas canvas){
		if (background == null){
			loadBitmaps();
		}
		//==========Draw the background
		Paint back = new Paint();
		back.setColor(Color.GREEN);
		canvas.drawRect(0, 0, getWidth(), getHeight(), back);
		
		//==========Draw Background Image
		Paint back_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		back_paint.setStyle(Style.FILL);
		canvas.drawBitmap(background, 0, 0, back_paint);
		
		
		//==========Draw Collected Items Ribbon
		Paint text_paint = new Paint();
		text_paint.setColor(Color.WHITE);
		text_paint.setStyle(Style.FILL);
		text_paint.setAntiAlias(true);
		text_paint.setTextSize(15);
		text_paint.setTypeface(Typeface.SANS_SERIF);
		
		Paint glow_paint = new Paint();
		glow_paint.setColor(Color.WHITE);
		glow_paint.setAlpha(100);
		int count=0;
		for (Collectable collectable: gameStatus.getCollectableFromId(collectableType)){
				canvas.drawRect(15+(count*90), 485, 100+(count*90), 570, glow_paint);
				canvas.drawBitmap(collectable.getIcon_bitmap(), 20+(count*90), 490 , back_paint);
				canvas.drawText(""+collectable.getScore(),45+(count*90), 585, text_paint);
				count = count+1;
				if (count>8)
					break;
	    }
		//==========Draw Image to mark boundaries
		if (popImageManager!=null){
			bitmap = popImageManager.getImagetoMarkBonderies(selectedColectable.getLast_img_marked());
			imageId = popImageManager.getFileIdOfImageToMarkBonderies(selectedColectable.getLast_img_marked());
			scaledVal = popImageManager.getScaleFactorOfImageToMarkBonderies(selectedColectable.getLast_img_marked());
			if ((bitmap!=null)&&(!imageDragging)){
				canvas.drawRect(bitmap.getWidth()+50, 50, bitmap.getWidth()+100, bitmap.getHeight()+50, glow_paint);
				canvas.drawBitmap(bitmap, 50,50, back_paint);
			}
			if (imageDragging){
				if (squarePaint!=null){
					canvas.drawRect(imgDrag_x+20,20,imgDrag_x+bitmap.getWidth()+80,80+bitmap.getHeight(),squarePaint);
				}
				canvas.drawBitmap(bitmap,50+imgDrag_x,50, back_paint);
			}
			//=====Draw Next Image Icon
			canvas.drawCircle(850, 320, 70, glow_paint);
			canvas.drawBitmap(trash_icon, 800, 270, back_paint);	
			
			text_paint.setTextSize(30);
			text_paint.setColor(Color.BLACK);
			canvas.drawText(selectedColectable.getTag().replace("+", " "), 50, 40, text_paint);
		}		
		canvas.drawBitmap(back_icon, 904, 480, back_paint);
		
		//==========Draw Boundary
		try{
		Paint boandary_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		boandary_paint.setStyle(Style.FILL);
		boandary_paint.setColor(Color.GREEN);
		if ((boundary!=null)&&(imgDrag_x==0)){
			for(int i=0;i<boundary.size();i++){
				canvas.drawCircle(boundary.get(i)[0], boundary.get(i)[1], 5, boandary_paint);
			}
			boandary_paint.setColor(Color.RED);
			boandary_paint.setStrokeWidth(5);
			if (!anmatePlus1)
				canvas.drawCircle(boundary.get(0)[0], boundary.get(0)[1], 25, boandary_paint);
		}
		}catch(Exception e){
			Log.d(TAG,"EXCEPTION: Boundray drawing failed");
		}
		//Plus 1 animater
		if (anmatePlus1)
			canvas.drawBitmap(plus1, (float)plus1_x, (float)plus1_y, back_paint);
		
		
		//============Draw Game Status BOX========
		text_paint.setTextSize(15);
		int temp_t = gameStatus.getTimeOfNextIsland();
		text_paint.setColor(Color.BLACK);
		//canvas.drawRect(600,480,880,580, glow_paint);
		Bitmap backPaper = BitmapFactory.decodeResource(getResources(), R.drawable.game_status_back);
		canvas.drawBitmap(backPaper, 680, 5, back_paint);
		glow_paint.setColor(Color.YELLOW);//glow_paint.setColor(Color.rgb(255-(temp_t/600)*255, (temp_t/600)*255, 0));
		canvas.drawRect(705,35,705+(temp_t*270/600),60, glow_paint);
		canvas.drawText("Time On Sea", 710, 55, text_paint);
		int food_t;
		if ((gameStatus.getNum_animals()+gameStatus.getNum_slaves()*2+gameStatus.getNum_crew()*5)==0)
			food_t = 25;
		else{
			food_t = gameStatus.getTotal_food_score() / (gameStatus.getNum_animals()+gameStatus.getNum_slaves()*2+gameStatus.getNum_crew()*5);
			food_t = Math.min(food_t, 25);
		}
		glow_paint.setColor(Color.rgb(255-(food_t*255/25), (food_t*255/25), 0));
		canvas.drawRect(705,60,705+(food_t*270/25),85, glow_paint);
		canvas.drawText("Food",710,80,text_paint);
		text_paint.setTextSize(25);
		Bitmap small = BitmapFactory.decodeResource(getResources(), R.drawable.crew_small);
		canvas.drawBitmap(small, 710, 95 , back_paint);
		canvas.drawText(""+gameStatus.getNum_crew(),740,120,text_paint);
		small = BitmapFactory.decodeResource(getResources(), R.drawable.slave_small);
		canvas.drawBitmap(small, 790, 95 , back_paint);
		canvas.drawText(""+gameStatus.getNum_slaves(),820,120,text_paint);
		small = BitmapFactory.decodeResource(getResources(), R.drawable.animal_small);
		canvas.drawBitmap(small, 870, 95 , back_paint);
		canvas.drawText(""+gameStatus.getNum_animals(),900,120,text_paint);
		
	}//======================================================== END OF ON_DRAW()
	
	private void infoDialog(){
		InstructionDialog id = new InstructionDialog();
		String title = "Arrr !";
		String text = "Need More Food? Need to make your slaves Intelligent? OR You want to make animals worth?.." +
				"Dont Wait.. start marking them .. if you cant find any of them throw it to green Ghost. P.S: If you " +
				"cant find any items to mark, wait till you get into an Island. There you can capture them ..";
		id.popInstructionsDialog(title, text, populateItemsActivity);
	}
		
	private void animatePlusOne(){
		//selectedCollectableXPosition
		anmatePlus1 = true;
		plus1_x = boundary.get(0)[0];
		plus1_y = boundary.get(0)[1];
				while (plus1_y<550){
					double ang = 0;
					if (boundary!=null)
						ang = Math.atan2(boundary.get(0)[1]-550, boundary.get(0)[0]-selectedCollectableXPosition);
					plus1_x = plus1_x-5*Math.cos(ang);
					plus1_y = plus1_y-5*Math.sin(ang);
					android.os.SystemClock.sleep(10);
				}
				anmatePlus1 = false;
	}
	
	public boolean onTouchEvent(MotionEvent me) {
		if (me.getAction() == MotionEvent.ACTION_DOWN){
			if (bitmap!=null){
				if (((bitmap.getWidth()+50)<me.getX())&&
						(me.getX()<(bitmap.getWidth()+100))&&
						(50<me.getY())&&
						(me.getY()<(bitmap.getHeight()+50))){
						//Start Moving the Image when player is dragging towards "chest" or "bin"
						Log.d(TAG,"Clicked on Correct Position !!!");
						imageDragging = true;
				}
			}
			if (cartDist((int)me.getX(), (int)me.getY(), 904, 580)<75){
				populateItemsActivity.finish();
			}
			//boundry marking START
			if (bitmap!=null)
				if ((50<me.getX())&&(me.getX()<(50+bitmap.getWidth()))&&(50<me.getY())&&(me.getY()<(50+bitmap.getWidth()))){
					boundary = new ArrayList<int[]>();
					boundaryMarkingOn = true;
					Log.d(TAG,"Image Boundary Marking Started");
				}
		}
		if (me.getAction() == MotionEvent.ACTION_MOVE){
			if (imageDragging){
				//TODO ADD HAPTIC
				if (gameStatus.isHaptics()){
					mSurface_dragImg.setActive(true);
					mSurface_dragImg.onTouchEvent(me);
				}
				imgDrag_x = (int)me.getX()-bitmap.getWidth()-50;
				if (cartDist((int)me.getX(), (int)me.getY(), 850, 320)<75){
					squarePaint = new Paint();
					squarePaint.setColor(Color.RED);
				}
				else 
					squarePaint = null;
			}
			if (boundaryMarkingOn){
				//Bounadry Marking
				int[] a = new int[2];
				a[0] = (int) me.getX();
				a[1] = (int) me.getY();				
				if (a[0]<50)
					a[0] = 50;
				if (a[0]>(50+bitmap.getWidth()))
					a[0] = 50+bitmap.getWidth();
				if (a[1]<50)
					a[1] = 50;
				if (a[1]>(50+bitmap.getHeight()))
					a[1] = 50+bitmap.getHeight();
				
				if (boundary.size()==0)
					boundary.add(a);
				else if (cartDist(boundary.get(boundary.size()-1)[0], boundary.get(boundary.size()-1)[1], a[0], a[1])>5)
					boundary.add(a);
				
				if (boundary.size()>4)
					if (cartDist(boundary.get(0)[0], boundary.get(0)[1], a[0], a[1])<25){
					// When boundary reachers back to the red starting dot
						animatePlusOne();
						Vibrator v = (Vibrator) populateItemsActivity.getSystemService(Context.VIBRATOR_SERVICE);
						long[] pattern = {100,2};
						v.vibrate(pattern, 4);
						
						// When ever player reachers to close the boundary as a circle system auto uploads data and present the
						// next image
						selectedColectable.setLast_img_marked(selectedColectable.getLast_img_marked()+1);
						Log.d(TAG,"Image Droped on GREEN "+selectedColectable.getLast_img_marked());
						//==== Update Boundaries to Server=====================
						for (int i=0;i<boundary.size();i++){
							boundary_str = boundary_str + (boundary.get(i)[0]/scaledVal)+","+(boundary.get(i)[1]/scaledVal)+";";
						}
						String url_str = "http://ec2-107-20-212-167.compute-1.amazonaws.com/nopsa_game/boundary_update.php";
						//POSTING THE DATA===========================================
						try{
							String urlParameters =
							 	"id=" + URLEncoder.encode(imageId, "UTF-8") +
							 	"&boundary="+ URLEncoder.encode(boundary_str, "UTF-8");
							this.excutePost(url_str, urlParameters);
							Log.d(TAG,"POST WORKED !! ================= ");
							selectedColectable.setScore(selectedColectable.getScore()+1); 
							// Add a point to Total food score
							if (collectableType == 2)
								gameStatus.setTotal_food_score(gameStatus.getTotal_food_score()+1);
						}catch(Exception e){
							Log.d(TAG,"POST did not work ");
						}
						boundaryMarkingOn = false;
						boundary = null;
					}
			}
		}
		if (me.getAction() == MotionEvent.ACTION_UP) {
			if (me.getX()<900)
				if ((485<me.getY())&&(me.getY()<570)){
					if (((int)((me.getX()-15)/90)) < gameStatus.getCollectableFromId(collectableType).size()){
						selectedCollectableXPosition = (int) me.getX();
						selectedColectable = gameStatus.getCollectableFromId(collectableType).get((int) ((me.getX()-15)/90));
						popImageManager = new PopulateItemsImageManager(
								//GameStatus.getGameStatusObject().getCollectableFromId(collectableType).get((int) ((me.getX()-15)/90)), 
								selectedColectable,
								this);
					}
				}
			if (squarePaint!=null){
				if (squarePaint.getColor() == Color.RED){
					//TODO -- Mark Image as Not useful
					selectedColectable.setLast_img_marked(selectedColectable.getLast_img_marked()+1);
					Log.d(TAG,"Image Droped on RED");
					//== Update MINUS for server
					String url_str = "http://ec2-107-20-212-167.compute-1.amazonaws.com/nopsa_game/relavancy.php?"+"id="+imageId+"&tag="+
						selectedColectable.getTag()+"&plus=0";
					try {
						URL url = new URL(url_str);
						Log.d(TAG,"URL "+url_str);
						HttpURLConnection connection = (HttpURLConnection) url.openConnection();
						connection.setRequestMethod("GET");
						connection.connect();
						InputStream stream = connection.getInputStream();
						Log.d(TAG,"===============UPDATE SUCESSFULL");
						Log.d(TAG,stream.toString());
						// Add a point to item
						selectedColectable.setScore(selectedColectable.getScore()+1); 
						// Add a point to Total food score
						if (collectableType == 2)
							gameStatus.setTotal_food_score(gameStatus.getTotal_food_score()+1);
					}catch(Exception e){
						Log.d(TAG,"Data Updating into Border DB Failed!");
						e.printStackTrace();
					}
					boundary = null;
					//===End of UPDATE MINUS
				}
			}
			imgDrag_x = 0;
			imageDragging = false;
			squarePaint = null;
			boundaryMarkingOn = false;
		}
		return true;
	}
	
	 public static String excutePost(String targetURL, String urlParameters){
	    URL url;
	    HttpURLConnection connection = null;  
	    try {
	      //Create connection
	      url = new URL(targetURL);
	      connection = (HttpURLConnection)url.openConnection();
	      connection.setRequestMethod("POST");
	      connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	      connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
	      connection.setRequestProperty("Content-Language", "en-US");  
	      connection.setUseCaches (false);
	      connection.setDoInput(true);
	      connection.setDoOutput(true);

	      //Send request
	      DataOutputStream wr = new DataOutputStream (
	                  connection.getOutputStream ());
	      wr.writeBytes (urlParameters);
	      wr.flush ();
	      wr.close ();

	      //Get Response	
	      InputStream is = connection.getInputStream();
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	      String line;
	      StringBuffer response = new StringBuffer(); 
	      while((line = rd.readLine()) != null) {
	        response.append(line);
	        response.append('\r');
	      }
	      rd.close();
	      return response.toString();
	    } catch (Exception e) {
	      e.printStackTrace();
	      return null;
	    } finally {
	      if(connection != null) {
	        connection.disconnect(); 
	      }
	    }
	  }
	
	
	
	private void loadBitmaps(){
		System.gc();
		background = BitmapFactory.decodeResource(getResources(), R.drawable.blackship_background);
		trash_icon = BitmapFactory.decodeResource(getResources(), R.drawable.trash_icon);
		back_icon = BitmapFactory.decodeResource(getResources(), R.drawable.back_icon);
		plus1 = BitmapFactory.decodeResource(getResources(), R.drawable.plus1);
		
		manager = (EffectManager) populateItemsActivity.getSystemService(populateItemsActivity.EFFECT_SERVICE);
		mSurface_dragImg = new FeelableSurface(this.getContext(), manager, R.xml.a_circular_menu_buttons_green_red);
	}
	
	private int cartDist(int x1, int y1, int x2, int y2){
		 return (int) Math.sqrt((Math.pow(x1-x2, 2)+Math.pow(y1-y2, 2)));
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		_thread = new ViewControllerThread(getHolder(), this);
		_thread.setRunning(true);
		_thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		 boolean retry = true;
		    _thread.setRunning(false);
		    while (retry) {
		        try {
		            _thread.join();
		            retry = false;
		        } catch (InterruptedException e) {
		            // we will try it again and again...
		        }
		    }	
	}
	
    class ViewControllerThread extends Thread {
        private SurfaceHolder _surfaceHolder;
        private PopulateItemsView _populateItemsView;
        private boolean _run = false;
     
        public ViewControllerThread(SurfaceHolder sh, PopulateItemsView piv) {
            _surfaceHolder = sh;
            _populateItemsView = piv;
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
    	                _populateItemsView.onDraw(c);
    	            }
    	        } finally {
    	            // do this in a finally so that if an exception is thrown
    	            // during the above, we don't leave the Surface in an
    	            // inconsistent state
    	            if (c != null) {
    	                _surfaceHolder.unlockCanvasAndPost(c);
    	            }
    	        }
    	    }     
        }
    }

}
