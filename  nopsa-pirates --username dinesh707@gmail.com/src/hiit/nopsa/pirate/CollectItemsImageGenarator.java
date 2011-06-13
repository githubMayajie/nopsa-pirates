package hiit.nopsa.pirate;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;

public class CollectItemsImageGenarator {

	public ArrayList<Bitmap> images;
	public ArrayList<URL> image_urls;
	private int currentLocation;
	private int oldLoaction = -1;
	private final String TAG = "NOPSA-P";
	private String search_tags;
	
	public CollectItemsImageGenarator(View parentView, int type){
		images = new ArrayList<Bitmap>();
		image_urls = new ArrayList<URL>();
		Bitmap no_img = BitmapFactory.decodeResource(parentView.getResources(), R.drawable.no_img);
		for (int i=0;i<24;i++){
			images.add(no_img);
			image_urls.add(null);
		}
		switch (type) {
		case 0:
			search_tags = "%2Banimal%2fish%2Bmonkey%2Bparrot";
			break;
		case 1:
			search_tags = "%2Bpeople%2Bperson%2Bman%2Bwoman%2Bboy%2Bgirl";
			break;
		case 2:
			search_tags = "%2Bfood%2Bdrink%2Bfruit";
			break;
		default:
			break;
		}
	}
	// This set the current location of first image shows in the screen
	// Screen shows 24 images starting from currentLocation
	public void setCurrentLocation(int i){
		currentLocation = i;
		loadImagesToArray();
	}
	
	public Bitmap getImageById(int id){
		return images.get(id);
	}
	
	public URL getImageUrlById(int id) {
		return image_urls.get(id);
	}
	
	
	private void loadImagesToArray(){
		if (oldLoaction<0){
			// Code Runs for FIrst time
			new Thread(new Runnable() {
				public void run() {
					Bitmap icon_bitmap = null;
					URL url = null;
					for (int i=0;i<24;i++){
						String url_str = "http://128.214.112.107/pmg/index.php/api/search?" +
							"apikey=1A4ECEAF1A942425&tags="+search_tags+"&order_attr=rank&mode=any" +
							"&per_page=1&page="+(i+1)+"&encoding=html&output_type=xml&yt1=Query";
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
							url = new URL("http://128.214.112.107/pmg/viewer/images/"+node.getTextContent().replace("photo", "square"));
							connection  = (HttpURLConnection) url.openConnection();
							InputStream is = connection.getInputStream();
							icon_bitmap = BitmapFactory.decodeStream(is);  			
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (icon_bitmap!=null){
							images.add(icon_bitmap);
							images.remove(0);
							image_urls.add(url);
							image_urls.remove(0);
						}
					}
				}
			}
			).start();
			oldLoaction = 0;
		}// End of IF 
	}
}
