package hiit.nopsa.pirate;

import hiit.nopsa.pirate.KeyboardHomeView.ViewControllerThread;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.senseg.effect.EffectManager;
import com.senseg.effect.FeelableSurface;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.YuvImage;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class TagSelectorView extends SurfaceView implements SurfaceHolder.Callback {

	private final String TAG = "NOPSA-P";
	private ViewControllerThread _thread;
	private Bitmap image;
	private Activity keyboardHomeActivity;
	private ArrayList<ImageTags> tags;
	private int showfactor = 0;
	private int selectedTag = -1;
	private Bitmap icons;
	private String tag_string="";
	private int btn_state=0;
	private int btn_lastY;
	private URL url;
	private boolean imageGlow = false;
	private EffectManager manager;
	private FeelableSurface mSurface_dots,mSurface_sticky;

	
	public TagSelectorView(Context context, Activity activity) {
		super(context);
		keyboardHomeActivity = activity;
		getHolder().addCallback(this);
		loadImageandTags();
		loadEffects();
		_thread = new ViewControllerThread(getHolder(), this);
		setFocusable(true);
		System.gc();
	}
	
	private void loadEffects(){
		manager = (EffectManager) keyboardHomeActivity.getSystemService(keyboardHomeActivity.EFFECT_SERVICE);
		mSurface_dots = new FeelableSurface(this.getContext(), manager, R.xml.eve_bumps_eachdot_slide4);
		mSurface_sticky = new FeelableSurface(this.getContext(), manager, R.xml.eveidea_stickysurface_slide4);
	}
	
	private void loadImageandTags(){
		int fileId = keyboardHomeActivity.getIntent().getExtras().getInt("img_fileid");
		String url_str = "http://nopsa.hiit.fi/pmg/index.php/api/fileById?apikey=1A4ECEAF1A942425&fileId="+fileId+"&yt0=XML";
		try {
			//========================= Loading Tags ================================
			url = new URL(url_str);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			InputStream stream = connection.getInputStream();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(stream);
			doc.getDocumentElement().normalize();
			NodeList tagsList = doc.getElementsByTagName("Tag");
			tags = new ArrayList<TagSelectorView.ImageTags>();
			ImageTags it;
			int _x=0,_y=0,_r=0;
			for (int i=0;i<tagsList.getLength();i++){
				_x = (int)((Math.random()*350)+600)-5;
				_y = (int)(Math.random()*450)+50-10;
				_r = 50;
				it = new ImageTags(tagsList.item(i).getAttributes().getNamedItem("keyword").getTextContent(),_x, _y, _r);
				tags.add(it);
			}
			System.gc();
			//========================= Loading Image ===============================
			Node node = doc.getElementsByTagName("File").item(0);
			NamedNodeMap nnm = node.getAttributes();
			node = nnm.getNamedItem("baseName");
			url = new URL("http://128.214.112.107/pmg/viewer/images/"+node.getTextContent());
			connection  = (HttpURLConnection) url.openConnection();
			InputStream is = connection.getInputStream();
			image = BitmapFactory.decodeStream(is);  		
			image = resizeBitmap(image);
		} catch (Exception e) {
			Log.d(TAG,"Image Loading or Tags Loading Failed!");
		}
	}
	
	protected void onDraw(Canvas canvas){
		//==========Draw the background
		Paint background = new Paint();
		background.setColor(Color.BLACK);
		canvas.drawRect(0, 0, getWidth(), getHeight(), background);
		
		//==========Draw Image
		Paint img_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		img_paint.setStyle(Style.FILL);
		Paint imGlow = new Paint();
		imGlow.setStyle(Style.FILL);
		if (imageGlow){
			imGlow.setColor(Color.GREEN);
			canvas.drawRect(45,45,image.getWidth()+55,image.getHeight()+55, imGlow);
		}
		else if (selectedTag>=0){
			imGlow.setColor(Color.YELLOW);
			canvas.drawRect(45,45,image.getWidth()+55,image.getHeight()+55, imGlow);
		}
		canvas.drawBitmap(image, 50, 50, img_paint);
		
		//==========Draw Tag Bar
		Bitmap tag_bar = BitmapFactory.decodeResource(getResources(), R.drawable.tag_bar2);
		Paint tag_bar_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		canvas.drawBitmap(tag_bar, 570,530, tag_bar_paint);
		
		//==========Draw Final Tags
		Paint tags_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		tags_paint.setColor(Color.WHITE);
		tags_paint.setTextSize(25);
		Bitmap tag_icon = BitmapFactory.decodeResource(getResources(), R.drawable.tags_icon);
		canvas.drawBitmap(tag_icon, 50, 15, tag_bar_paint);
		canvas.drawText(tag_string.replace("+", "  "), 73, 40, tags_paint);
		
		
		//==========Draw Tags
		Paint text_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		text_paint.setColor(Color.BLACK);
		text_paint.setTextSize(20);
		
		Paint sq = new Paint();
		sq.setStyle(Style.FILL);
		sq.setColor(Color.WHITE);

		Paint cir = new Paint();
		cir.setStyle(Style.FILL);
		cir.setColor(Color.RED);
		
		if (showfactor<0){
			//Show Ok and Back Button
			Paint icon_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			icons = BitmapFactory.decodeResource(getResources(), R.drawable.ok_icon);
			canvas.drawBitmap(icons, 904, 20, icon_paint);
			icons = BitmapFactory.decodeResource(getResources(), R.drawable.back_icon);
			canvas.drawBitmap(icons, 904, 140, icon_paint);
			if (btn_state>0){
				icons = BitmapFactory.decodeResource(getResources(), R.drawable.glow_ok);
				canvas.drawBitmap(icons, 894, 10, icon_paint);
			}
		}else{
			int j = 1, i=0, k = 0;
			if (showfactor==0){
				j = 1;
				k = 0;
			}else{
				k = showfactor-1;
				j = 10;
			}
			for (i=k;i<tags.size();i=i+j){
				sq.setAlpha(150);
				canvas.drawRect( tags.get(i).x, tags.get(i).y-10, tags.get(i).x+(tags.get(i).tag.length()*15),tags.get(i).y+15, sq);
				canvas.drawText(tags.get(i).tag, tags.get(i).x+5, tags.get(i).y+10, text_paint);
				sq.setAlpha(250);
				canvas.drawRect( tags.get(i).x-10, tags.get(i).y-10,tags.get(i).x,tags.get(i).y+15, sq);
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent me) {
		// Controling Number of Tags shown in screen
		if ((me.getAction() == MotionEvent.ACTION_MOVE)||(me.getAction() == MotionEvent.ACTION_DOWN)){
			if ((me.getX()>570)&&(me.getY()>530)){
				mSurface_dots.setActive(true);
			    mSurface_dots.onTouchEvent(me);
				showfactor = ((int) (me.getX()-570)/34)+1;
				if (me.getX()>920)
					showfactor = 0;
				if (me.getX()>980)
					showfactor = -1;
			}
		}
		//TODO - Selecting the Dragging Tag
		if(me.getAction() == MotionEvent.ACTION_DOWN){
			if ((me.getX()>550)&&(me.getY()<530)&&(showfactor>=0)){
				int j = 1, i=0, k = 0;
				if (showfactor==0){
					j = 1;
					k = 0;
				}else{
					k = showfactor-1;
					j = 10;
				}
				for (i=k;i<tags.size();i=i+j){
					if (cartDist((int) me.getX(), (int) me.getY(), tags.get(i).x, tags.get(i).y)<20){
						selectedTag = i;
						break;
					}
				}
			}
			if (showfactor<0){
				if ((904<me.getX())&&(me.getX()<1024)&&(20<me.getY())&&(me.getY()<120)){
					// OK Button Clicked
					if (btn_state==0){
						btn_state = 1;
						btn_lastY = (int) me.getY();
					}
				}
				if ((904<me.getX())&&(me.getX()<1024)&&(140<me.getY())&&(me.getY()<240)){
					// Back Button Clicked
					keyboardHomeActivity.finish();
				}
			}
			
		}
		//TODO - Moving the Selected Tag
		if(me.getAction() == MotionEvent.ACTION_MOVE){
			if (selectedTag>=0){
				tags.get(selectedTag).x = (int) me.getX();
				tags.get(selectedTag).y = Math.min(520,(int) me.getY());
				if (me.getX()<550){
					imageGlow = true;
					//TODO ADD EFFECTS
					mSurface_sticky.setActive(true);
					mSurface_sticky.onTouchEvent(me);
				}
			}
			if (showfactor<0){
				if ((btn_state==1)&&((int) me.getY()>btn_lastY)){
					btn_state = 2;
					btn_lastY = (int) me.getY();
				}
				if ((btn_state==2)&&((int) me.getY()<btn_lastY)){
					Log.d(TAG,"OK Clicked");
					btn_state = 0;
					if(tag_string.length()>0){
						Collectable c  = new Collectable();
						c.setIcon_url(url.toString().replace("photo", "square")); 		
						c.setTag(tag_string);
						c.setScore(0);
						c.setLast_img_marked(1);
						GameStatus.getGameStatusObject().addCollectableFromId(keyboardHomeActivity.getIntent().getExtras().getInt("type"), c);
						keyboardHomeActivity.finish();
					}
					else{
						Toast.makeText(keyboardHomeActivity,"No Tags Selected !",Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
		
		if(me.getAction() == MotionEvent.ACTION_UP){
			if ((selectedTag>=0)&&(me.getX()<550)){
				tag_string = tag_string + "+" + tags.remove(selectedTag).tag;
				// Auto Close the window when person selects a tag
				Collectable c  = new Collectable();
				c.setIcon_url(url.toString().replace("photo", "square")); 		
				c.setTag(tag_string);
				c.setScore(0);
				c.setLast_img_marked(1);
				GameStatus.getGameStatusObject().addCollectableFromId(keyboardHomeActivity.getIntent().getExtras().getInt("type"), c);
				keyboardHomeActivity.finish();
			}
			selectedTag = -1;
			btn_state = 0;
			imageGlow = false;
		}
		
		return true;
	}
	
	// ===================== Other Control & Support "Methods & Classes"===========================
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
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
		int newWidth;
		if (width>500)
			newWidth = 500;
		else
			newWidth = width;
		int newHeight;
		if (height>500)
			newHeight = 500;
		else
			newHeight = height;
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		if (scaleWidth<scaleHeight)
			scaleHeight = scaleWidth;
		else 
			scaleWidth = scaleHeight;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		b = Bitmap.createBitmap(b,0,0,width,height,matrix,true); 
		return b;
	}

	
	class ViewControllerThread extends Thread {
        private SurfaceHolder _surfaceHolder;
        private TagSelectorView _tagSelectorView;
        private boolean _run = false;
     
        public ViewControllerThread(SurfaceHolder sh, TagSelectorView tsv) {
            _surfaceHolder = sh;
            _tagSelectorView = tsv;
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
    	                _tagSelectorView.onDraw(c);
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
	
	class ImageTags{
		String tag;
		int x;
		int y;
		int r;
		public ImageTags(String t, int x, int y, int r){
			this.tag = t;
			this.x = x;
			this.y = y;
			this.r = r;
		}
	}

}
