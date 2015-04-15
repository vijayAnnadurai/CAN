package cs.dht.Client;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class getInfo implements Serializable{
	private static final long serialVersionUID = 1L;

	public getInfo() {
		// TODO Auto-generated constructor stub
	}
		
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getLowerX() {
		return lowerX;
	}
	public void setLowerX(int lowerX) {
		this.lowerX = lowerX;
	}
	public int getUpperX() {
		return upperX;
	}
	public void setUpperX(int upperX) {
		this.upperX = upperX;
	}
	public int getLowerY() {
		return lowerY;
	}
	public void setLowerY(int lowerY) {
		this.lowerY = lowerY;
	}
	public int getUpperY() {
		return upperY;
	}
	public void setUpperY(int upperY) {
		this.upperY = upperY;
	}
	public HashMap<String, File> getWordsList() {
		return wordsList;
	}
	public void setWordsList(HashMap<String, File> wordsList) {
		this.wordsList = wordsList;
	}
	public ArrayList<Zone> getNeighbourList() {
		return neighbourList;
	}
	public void setNeighbourList(ArrayList<Zone> neighbourList) {
		this.neighbourList = neighbourList;
	}
      boolean clientActive;
	public boolean isClientActive() {
	return clientActive;
}

public void setClientActive(boolean clientActive) {
	this.clientActive = clientActive;
}
	String ip;
	int lowerX;
	int upperX;
	int lowerY;
	int upperY;
	HashMap<String,File> wordsList = new HashMap<String,File>();
	ArrayList<Zone> neighbourList = new ArrayList<Zone>();

}
