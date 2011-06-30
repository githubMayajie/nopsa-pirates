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

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class TagSelectorView extends SurfaceView implements SurfaceHolder.Callback {

	private final String TAG = "NOPSA-P";
	private ViewControllerThread _thread;
	private Bitmap image;
	private Activity keyboardHomeActivity;
	private ArrayList<ImageTags> tags;
	private int showfactor = 0;

	
	public TagSelectorView(Context context, Activity activity) {
		super(context);
		keyboardHomeActivity = activity;
		getHolder().addCallback(this);
		loadImageandTags();
		_thread = new ViewControllerThread(getHolder(), this);
		setFocusable(true);
		System.gc();
	}
	
	private void loadImageandTags(){
		int fileId = keyboardHomeActivity.getIntent().getExtras().getInt("img_fileid");
		String url_str = "http://nopsa.hiit.fi/pmg/index.php/api/fileById?apikey=1A4ECEAF1A942425&fileId="+fileId+"&yt0=XML";
		URL url;
		Log.d(TAG,">>>>>>>>>>>>>A");
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
			Log.d(TAG,">>>>>>>>>>>>>B");
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
			Log.d(TAG,">>>>>>>>>>>>>C");
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
		canvas.drawBitmap(image, 50, 50, img_paint);
		
		//==========Draw Tag Bar
		Bitmap tag_bar = BitmapFactory.decodeResource(getResources(), R.drawable.tag_bar);
		Paint tag_bar_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		canvas.drawBitmap(tag_bar, 570,530, tag_bar_paint);
		
		//==========Draw Tags
		Paint text_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		text_paint.setColor(Color.BLACK);
		text_paint.setTextSize(20);
		
		Paint sq = new Paint();
		sq.setStyle(Style.FILL);
		sq.setColor(Color.WHITE);
		sq.setAlpha(150);
		
		Paint cir = new Paint();
		cir.setStyle(Style.FILL);
		cir.setColor(Color.RED);
		
		int j = 1, i=0, k = 0;
		if (showfactor==0){
			j = 1;
			k = 0;
		}else{
			k = showfactor-1;
			j = 10;
		}
		for (i=k;i<tags.size();i=i+j){
			canvas.drawRect( tags.get(i).x, tags.get(i).y-10, tags.get(i).x+(tags.get(i).tag.length()*15),tags.get(i).y+15, sq);
			canvas.drawText(tags.get(i).tag, tags.get(i).x+5, tags.get(i).y+10, text_paint);
			canvas.drawCircle(tags.get(i).x, tags.get(i).y, 5, cir);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent me) {
		if ((me.getAction() == MotionEvent.ACTION_MOVE)||(me.getAction() == MotionEvent.ACTION_DOWN)){
			if ((me.getX()>570)&&(me.getY()>530)){
				showfactor = ((int) (me.getX()-570)/34)+1;
				if (me.getX()>920)
					showfactor = 0;
				Log.d(TAG,"Show Factor:"+showfactor);
			}
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
