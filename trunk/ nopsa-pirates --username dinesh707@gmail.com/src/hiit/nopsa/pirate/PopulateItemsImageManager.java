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

public class PopulateItemsImageManager {
	
	private final String TAG = "NOPSA-P";
	private ArrayList<Bitmap> imgOnBuffer = null;
	private ArrayList<URL> urlOnBuffer = null;	
	private Collectable collectable;
	private View parentView;
	private Bitmap photo_bitmap;
	private int begining=0;
	
	public PopulateItemsImageManager(Collectable ctb, View pv){
		collectable = ctb;
		parentView = pv;
		imgOnBuffer = new ArrayList<Bitmap>();
		urlOnBuffer = new ArrayList<URL>();
		begining = 1;
		loadImagesToBuffer(1);
		Log.d(TAG,"PopulateItemsImageManager OBJECT CREATED");
	}
		
	private void loadImagesToBuffer(int stPoint){
	    if ((begining+4)>stPoint){
	    	// DO Nothing
	    	// Already Images are on the buffer
	    }else{
	    	int temp_i = stPoint-(begining+4);
	    	for (int i=0;i<temp_i;i++){
	    		imgOnBuffer.remove(0);
	    		imgOnBuffer.remove(0);
	    		begining = begining+1;
	    	}
	    }
		while (imgOnBuffer.size()<=5){
			URL url = null;
			String url_str = "http://128.214.112.107/pmg/index.php/api/search?" +
			"apikey=1A4ECEAF1A942425&tags="+URLEncoder.encode(collectable.getTag())+"&order_attr=rank&mode=any" +
			"&per_page=1&page="+(stPoint)+"&encoding=html&output_type=xml&yt1=Query";
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
				Matrix matrix = new Matrix();
				matrix.postScale(scaleWidth, scaleHeight);
				photo_bitmap = Bitmap.createBitmap(photo_bitmap,0,0,width,height,matrix,true); 
				//========== End of Image resizing
				stPoint=stPoint+1;
				Log.d(TAG,"<<<<<"+stPoint);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (photo_bitmap!=null){
				urlOnBuffer.add(url);
				imgOnBuffer.add(photo_bitmap);
				Log.d(TAG,"Image Added to System");
			}
		}
	}
	
	public Bitmap getImagetoMarkBonderies(int stPoint){
		loadImagesToBuffer(stPoint);
		if ((stPoint<(begining+4)))
			return imgOnBuffer.get(stPoint);
		else 
			return imgOnBuffer.get(5);
	}
	
}	
	


