package hiit.nopsa.pirate;

public class Collectable {
	
	private String icon_url; 		// http://nopsa.hiit.fi/viewer/images/square_2675942790_32794abcf1_t.jpg
	private String tag;				// wine
	private int score;				// 12  -  When player marks the bonderies he gets a score for item
	private int last_img_marked;	// 16  -  Will be used to find what image was marked last
	
	public String getIcon_url() {
		return icon_url;
	}
	public void setIcon_url(String icon_url) {
		this.icon_url = icon_url;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public int getLast_img_marked() {
		return last_img_marked;
	}
	public void setLast_img_marked(int last_img_marked) {
		this.last_img_marked = last_img_marked;
	}	
}
