package hiit.nopsa.pirate;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.view.View;

/**
 * 
 * @author Dinesh Wijekoon
 */
public class PopulateItemsImageManager {
	
	private final String TAG = "NOPSA-P";
	private ArrayList<Bitmap> imgOnBuffer = null;
	private ArrayList<URL> urlOnBuffer = null;	
	private ArrayList<String> fileIdOnBuffer = null;
	private ArrayList<Float> scaledValues = null;
	private Collectable collectable;
	private Bitmap photo_bitmap;
	private int lastStartPoint = 0;
	private int lastendPoint = 1;
	private String fileId = "";
	private float scaledValue;
	
	
	public PopulateItemsImageManager(Collectable ctb, View pv){
		collectable = ctb;
		imgOnBuffer = new ArrayList<Bitmap>();
		urlOnBuffer = new ArrayList<URL>();
		fileIdOnBuffer = new ArrayList<String>();
		scaledValues = new ArrayList<Float>();
		lastendPoint = ctb.getLast_img_marked();
		lastStartPoint = lastendPoint;
		loadImagesToBuffer(1);
		Log.d(TAG,"PopulateItemsImageManager OBJECT CREATED");
	}
		
	private void loadImagesToBuffer(int stPoint){
		synchronized (this) {
		new Thread(new Runnable() {
			public void run() {
		//====
		while (imgOnBuffer.size()<=5){
			URL url = null;
			Log.d(TAG,"Last ENDDDDDDDDDDDDDDDDDD:::::"+lastendPoint);
			String url_str = "http://128.214.112.107/pmg/index.php/api/search?" +
			"apikey=1A4ECEAF1A942425&tags="+URLEncoder.encode(collectable.getTag())+"&order_attr=rank&mode=any" +
			"&per_page=1&page="+(lastendPoint)+"&encoding=html&output_type=xml&yt1=Query";
			try {
				url = new URL(url_str);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.connect();
				InputStream stream = connection.getInputStream();
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(stream);
				doc.getDocumentElement().normalize();
				fileId = doc.getElementsByTagName("fileId").item(0).getTextContent();
				Node node = doc.getElementsByTagName("baseName").item(0);
				url = new URL("http://128.214.112.107/pmg/viewer/images/"+node.getTextContent());
				Log.d(TAG,"Loaded URL"+url.toString());
				connection  = (HttpURLConnection) url.openConnection();
				InputStream is = connection.getInputStream();
				photo_bitmap = BitmapFactory.decodeStream(is); 
				//================resize photo_bitmap to match the size on screen
				int width = photo_bitmap.getWidth();
				int height = photo_bitmap.getHeight();
				int newWidth;
				if (width>700)
					newWidth = 700;
				else
					newWidth = width;
				int newHeight;
				if (height>400)
					newHeight = 400;
				else
					newHeight = height;
				float scaleWidth = ((float) newWidth) / width;
				float scaleHeight = ((float) newHeight) / height;
				Log.d(TAG,"SCALED Width:"+scaleWidth+" Height:"+scaleHeight);
				
				if (scaleWidth<scaleHeight)
					scaleHeight = scaleWidth;
				else 
					scaleWidth = scaleHeight;
				scaledValue = scaleWidth;
				Matrix matrix = new Matrix();
				matrix.postScale(scaleWidth, scaleHeight);
				photo_bitmap = Bitmap.createBitmap(photo_bitmap,0,0,width,height,matrix,true); 
				//========== End of Image resizing
				lastendPoint=lastendPoint+1;
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (photo_bitmap!=null){
				urlOnBuffer.add(url);
				imgOnBuffer.add(photo_bitmap);
				fileIdOnBuffer.add(fileId);
				scaledValues.add(new Float(scaledValue));
				Log.d(TAG,"Image Added to System");
			}
		}
				//===
		    }}).start();
		}
	}
	
	public Bitmap getImagetoMarkBonderies(int stPoint){
		try{
		if (lastStartPoint!=stPoint){
			for (int i=0;i<stPoint-lastStartPoint;i++){
				imgOnBuffer.remove(0);
				urlOnBuffer.remove(0);
				fileIdOnBuffer.remove(0);
				scaledValues.remove(0);
			}
			lastStartPoint = stPoint;
			loadImagesToBuffer(stPoint);
		}	
		}catch(Exception e){}
		
		try{
			return imgOnBuffer.get(0);
		}catch (Exception e) {
			return null;
		}
	}
	
	public String getFileIdOfImageToMarkBonderies(int stPoint){
		if (fileIdOnBuffer.size()>0)
			return fileIdOnBuffer.get(0);
		else
			return null;
	}
	
	public float getScaleFactorOfImageToMarkBonderies(int stPoint){
		if (scaledValues.size()>0)
			return scaledValues.get(0);
		else
			return 0;
	}
}	
	


