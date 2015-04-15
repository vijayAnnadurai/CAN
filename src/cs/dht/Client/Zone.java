package cs.dht.Client;

import java.io.Serializable;
import java.net.InetAddress;

public class Zone implements Serializable{
	private static final long serialVersionUID = 1L;

	String ip;
	int lowerX;
	int lowerY;
	int upperX;
	int upperY;
int centerX;
String BSIp;



public String getBSIp() {
	return BSIp;
}
public void setBSIp(String bSIp2) {
	BSIp = bSIp2;
}
public int getDistance(int cX, int cY)
{
	return (int)Math.sqrt(Math.pow(this.centerX-cX, 2)+(Math.pow(this.centerY-cY, 2)));
}
public int getCenterX() {
	return centerX;
}

public void setCenterX(int lowX,int uppX) {
	this.centerX = (lowX+uppX)/2;
}

public int getCenterY() {
	return centerY;
}

public void setCenterY(int lowY,int uppY) {
	this.centerY = (lowY+uppY)/2;
}

int centerY;
	public String getIp() {
		return ip;
	}

	public void setIp(String inetAddress) {
		this.ip = inetAddress;
	}

	public int getLowerX() {
		return lowerX;
	}

	public void setLowerX(int lowerX) {
		this.lowerX = lowerX;
	}

	public int getLowerY() {
		return lowerY;
	}

	public void setLowerY(int lowerY) {
		this.lowerY = lowerY;
	}

	public int getUpperX() {
		return upperX;
	}

	public void setUpperX(int upperX) {
		this.upperX = upperX;
	}

	public int getUpperY() {
		return upperY;
	}

	public void setUpperY(int upperY) {
		this.upperY = upperY;
	}

	public Zone split() {
		Zone createdZone;
		if (this.isZoneSquare())
			createdZone = this.splitZoneHorizontally();
		else
			createdZone = this.splitZoneVertically();

		return createdZone;
	}

	public int length() {
		return Math.abs((upperX - lowerX));
	}

	public int Breadth() {
		return Math.abs(upperY - lowerY);
	}

	public boolean isZoneSquare() {
		if (length() == Breadth()) {
			return true;
		}
		return false;
	}

	public Zone splitZoneVertically() {
		Zone z = new Zone();

		z.lowerX = this.lowerX + (this.length() / 2);
		z.upperX = this.upperX;
		z.lowerY = this.lowerY;
		z.upperY = this.upperY;

		this.upperX = this.lowerX + (this.length() / 2);

		return z;

	}

	public Zone splitZoneHorizontally() {
		Zone z = new Zone();

		z.lowerY = this.lowerY + (this.Breadth() / 2);
		z.upperX = this.upperX;
		z.lowerX = this.lowerX;
		z.upperY = this.upperY;

		this.upperY = this.upperY - (this.Breadth() / 2);

		return z;

	}

}
