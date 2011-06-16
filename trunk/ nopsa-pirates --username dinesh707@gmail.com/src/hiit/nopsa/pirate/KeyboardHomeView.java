package hiit.nopsa.pirate;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import hiit.nopsa.pirate.GameHomeView.ViewControllerThread;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.SlidingDrawer;
import android.widget.Toast;

public class KeyboardHomeView extends SurfaceView implements SurfaceHolder.Callback {

	private Activity keyboardHomeActivity;
	private final String TAG = "NOPSA-P";
	private ViewControllerThread _thread;
	private Bitmap keyboard;
	private int selectedKey = -1;
	private String selectedKey_String = "";
	private char[] letters = null ;
	private String tags = "";
	private URL img_url = null;
	private Bitmap image = null;
	private Bitmap icons;
	
	public KeyboardHomeView(Context context, Activity activity) {
		super(context);
		keyboardHomeActivity = activity;
		getHolder().addCallback(this);
		_thread = new ViewControllerThread(getHolder(), this);
		setFocusable(true);
		System.gc();
		keyboard = BitmapFactory.decodeResource(getResources(), R.drawable.key_board);
		try {
			img_url = new URL(keyboardHomeActivity.getIntent().getExtras().getString("img_url").replace("square", "photo"));
			Log.d(TAG,"IMAGE URL >>"+img_url.toString());
			HttpURLConnection connection  = (HttpURLConnection) img_url.openConnection();
			InputStream is = connection.getInputStream();
			image = BitmapFactory.decodeStream(is);  		
			image = resizeBitmap(image);
		} catch (Exception e) {
			Log.d(TAG,"URL Object Creation or Bitmap Loading Failed");
			e.printStackTrace();
		}
	}
	
	protected void onDraw(Canvas canvas){
		//==========Draw the background
		Paint background = new Paint();
		background.setColor(Color.BLACK);
		canvas.drawRect(0, 0, getWidth(), getHeight(), background);
		
		//==========Draw Keyboard
		Paint back_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		if (letters!=null)
			back_paint.setAlpha(50);
		canvas.drawBitmap(keyboard, 472, 90, back_paint);// 420*420
		
		//==========Draw OK and Back
		Paint icon_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		icon_paint.setStyle(Style.FILL);
		icons = BitmapFactory.decodeResource(getResources(), R.drawable.back_icon);
		canvas.drawBitmap(icons, 904, 20, icon_paint);
		icons = BitmapFactory.decodeResource(getResources(), R.drawable.ok_icon);
		canvas.drawBitmap(icons, 904, 480, icon_paint);
		
		//=========Draw Image
		back_paint.setAlpha(255);
		if (image!= null)
			canvas.drawBitmap(image, 50, 50, back_paint);
		
		//==========Currently Selected KEY
		Paint text_paint = new Paint();
		text_paint.setColor(Color.RED);
		text_paint.setStyle(Style.FILL);
		text_paint.setAntiAlias(true);
		text_paint.setTextSize(40);
		text_paint.setTypeface(Typeface.SANS_SERIF);
		canvas.drawText(selectedKey_String, 560, 120, text_paint);
		
		//==========Draw Selected Circles
		Paint glow = new Paint(Paint.ANTI_ALIAS_FLAG);
		glow.setColor(Color.WHITE);
		glow.setAlpha(75);
		if (selectedKey==80)
			canvas.drawCircle(640, 303, 30, glow);		
		if (selectedKey==90)
			canvas.drawCircle(724, 303, 30, glow);
		
		
		//=========Draw New Center and Letters
		text_paint.setColor(Color.WHITE);
		if (letters!=null){
			canvas.drawText(Character.toString(letters[0]), 550, 250, text_paint);
			canvas.drawText(Character.toString(letters[1]), 672, 185, text_paint);
			canvas.drawText(Character.toString(letters[2]), 793, 245, text_paint);
			canvas.drawText(Character.toString(letters[3]), 798, 380, text_paint);
			canvas.drawText(Character.toString(letters[4]), 678, 450, text_paint);
			canvas.drawText(Character.toString(letters[5]), 557, 375, text_paint);
			canvas.drawCircle(682, 300, 30, glow);
		}

		//=========Draw Tag
		text_paint.setTextSize(15);
		canvas.drawText("Tags: "+tags, 50, 520, text_paint);
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent me) {
		if (me.getAction() == MotionEvent.ACTION_DOWN) {
			if (cartDist(682, 300, (int)me.getX(), (int)me.getY()) < 30){
				selectedKey = 100;
			}
			if ((904<me.getX())&&(me.getX()<1004)&&(20<me.getY())&&(me.getY()<120)){
				keyboardHomeActivity.finish();
			}
			if ((904<me.getX())&&(me.getX()<1004)&&(480<me.getY())&&(me.getY()<580)){
				if(tags.length()>0){
					Collectable c  = new Collectable();
					c.setIcon_url(img_url.toString().replace("photo", "square")); 		
					c.setTag((" "+tags).replace(" ", "+"));
					Log.d(TAG,"SAVED TAG >>>"+c.getTag());
					c.setScore(0);
					c.setLast_img_marked(1);
					GameStatus.getGameStatusObject().addCollectableFromId(
							keyboardHomeActivity.getIntent().getExtras().getInt("type"), c);
					keyboardHomeActivity.finish();
					
				}
				else{
					Toast.makeText(keyboardHomeActivity,"Enter a Tag first !",Toast.LENGTH_SHORT).show();
				}
			}
		}
		if (me.getAction() == MotionEvent.ACTION_MOVE) {
			if (((cartDist(682, 300, (int)me.getX(), (int)me.getY()) > 30))&&(letters==null)){
				int angle = getAngle(682, 300, (int)me.getX(), (int)me.getY());
				if ((-60<angle)&&(angle<-10)){
					selectedKey = 20;
					char[] l = {'g','h','i','j','k','l'};
					letters = l;
				}
				else if ((-120<angle)&&(angle<-60)){
					selectedKey = 10;
					char[] l = {'a','b','c','d','e','f'};
					letters = l;
				}
				else if ((-170<angle)&&(angle<-120)){
					selectedKey = 60;
					char[] l = {'4','5','6','7','8','9'};
					letters = l;
				}
				else if ((10<angle)&&(angle<60)){
					selectedKey = 30;
					char[] l = {'m','n','o','p','q','r'};
					letters = l;
				}	
				else if ((60<angle)&&(angle<120)){
					selectedKey = 40;
					char[] l = {'s','t','u','v','w','x'};
					letters = l;
				}
				else if ((120<angle)&&(angle<170)){
					selectedKey = 50;
					char[] l = {'y','z','0','1','2','3'};
					letters = l;
				}
				else if (((-179<angle)&&(angle<-170))||((170<angle)&&(angle<179))){
					selectedKey = 80;
					selectedKey_String = "<";
				}
				else if (((0<angle)&&(angle<10))||((angle<0)&&(-10<angle))){
					selectedKey = 90;
					selectedKey_String = ">";
				}
			}
			if (((cartDist(682, 300, (int)me.getX(), (int)me.getY()) > 30))&&(letters!=null)){
				int angle = getAngle(682, 300, (int)me.getX(), (int)me.getY());
				selectedKey = (int) ((Math.floor(selectedKey/10))*10);
				if ((-60<angle)&&(angle<0))
					selectedKey = selectedKey+3;
				else if ((-120<angle)&&(angle<-60))
					selectedKey = selectedKey+2;
				else if ((-180<angle)&&(angle<-120))
					selectedKey = selectedKey+1;
				else if ((0<angle)&&(angle<60))
					selectedKey = selectedKey+4;
				else if ((60<angle)&&(angle<120))
					selectedKey = selectedKey+5; 
				else if ((120<angle)&&(angle<180))
					selectedKey = selectedKey+6;
				
				char c = (char) (96 + 6*((Math.floor(selectedKey/10))-1) + (selectedKey%10));
				if (c>122)
					c = (char)(c-75);
				selectedKey_String = Character.toString(c);
			}
			if (((cartDist(682, 300, (int)me.getX(), (int)me.getY()) < 30))){
				letters = null;
				selectedKey = -1;
				selectedKey_String = "";
			}
		}

		if (me.getAction() == MotionEvent.ACTION_UP){
			if (selectedKey_String==">")
				tags = tags+ " ";
			else if (selectedKey_String=="<")
				tags = tags.substring(0, tags.length()-1);
			else
				tags = tags+ selectedKey_String;
			selectedKey_String = "";
			selectedKey = -1;
			letters = null;
		}
		return true;
	}
	

	private int getAngle(int x1, int y1, int x2, int y2){
		double theta = Math.atan2((y2-y1),(x2-x1));
		theta = theta*57.2957795;
		return (int) theta;
	}
	private int cartDist(int x1, int y1, int x2, int y2){
		 return (int) Math.sqrt((Math.pow(x1-x2, 2)+Math.pow(y1-y2, 2)));
	}
	
	private Bitmap resizeBitmap(Bitmap b){
		int width = b.getWidth();  
		int height = b.getHeight();  
		int newWidth = 200;        
		float scale = ((float) newWidth) / width;  
		Log.d(TAG,"SCALE<<>>>>>"+scale);
		Matrix matrix = new Matrix();  
		matrix.postScale(scale, scale);  

		Bitmap resizedBitmap = Bitmap.createBitmap(b,0,0,width,height, matrix, true);   
	    BitmapDrawable bmd = new BitmapDrawable(resizedBitmap);  
	    return bmd.getBitmap();
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		_thread.setRunning(true);
		_thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
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
        private KeyboardHomeView _keyboardHomeView;
        private boolean _run = false;
     
        public ViewControllerThread(SurfaceHolder sh, KeyboardHomeView khv) {
            _surfaceHolder = sh;
            _keyboardHomeView = khv;
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
    	                _keyboardHomeView.onDraw(c);
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
