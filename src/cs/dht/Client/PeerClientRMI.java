package cs.dht.Client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeSet;

import cs.dht.Interfaces.IBootStrapServer;
import cs.dht.Interfaces.IPeerClient;

public class PeerClientRMI extends UnicastRemoteObject implements IPeerClient {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int lowerX;
	int lowerY;
	int upperX;
	int upperY;
	String IPAddress;
static boolean clientActive = false; 
	ArrayList<Zone> neighbourList = new ArrayList<Zone>();
	HashMap<String, File> wordsMap = new HashMap<String, File>();
	Zone zone;

	public PeerClientRMI() throws RemoteException, AlreadyBoundException {

	}

	public void initialize() throws UnknownHostException {
		lowerX = 0;
		lowerY = 0;
		upperX = 100;
		upperY = 100;
		IPAddress = InetAddress.getLocalHost().getHostAddress();

	}

	public void newJoinRequest(String BSIp) throws RemoteException,
			NotBoundException, UnknownHostException {
		Registry reg = LocateRegistry.getRegistry(BSIp, 2000);
		IBootStrapServer bsObj = (IBootStrapServer) reg.lookup("RmiServer");
		String receivedMsg = bsObj.returnBootStrapNode(InetAddress
				.getLocalHost().getHostAddress());
		if (receivedMsg.equals("Node1")) {
			initialize();
			this.zone = new Zone();
			this.zone.setIp(InetAddress.getLocalHost().getHostAddress());
			this.zone.setLowerX(lowerX);
			this.zone.setLowerY(lowerY);
			this.zone.setUpperX(upperX);
			this.zone.setUpperY(upperY);

			System.out.println(this.zone.getUpperX());

		} else {

			System.out.println("receivedIp is : " + receivedMsg);
			Random rand = new Random();
			int coodX = rand.nextInt(100);
			int coodY = rand.nextInt(100);
			System.out
					.println("Random coordinates are: " + coodX + "," + coodY);
			Registry clientReg = LocateRegistry.getRegistry(receivedMsg, 4000);
			IPeerClient clientObj = (IPeerClient) clientReg
					.lookup("PeerClient");
			// System.out.println("upperX is " + zone.getUpperX());

			clientObj.join(InetAddress.getLocalHost().getHostAddress(), coodX,
					coodY);

		}

	}

	public void join(String ip, int x, int y) throws RemoteException,
			NotBoundException {
		Zone z;

		System.out.println(this.zone.getUpperX());
		System.out.println(this.zone.getUpperY());
		System.out.println(this.zone.getLowerX());
		System.out.println(this.zone.getLowerY());

		if (CheckPointsLieoonZone(this.zone, x, y)) {
			if (isZoneSquare()) {
				if (y <= (this.zone.upperY / 2)) {
					z = new Zone();
					z.setLowerX(this.zone.lowerX);
					z.setUpperX(this.zone.upperX);
					z.setLowerY(this.zone.lowerY);
					z.setUpperY(this.zone.upperY / 2);
					z.setIp(ip);

					this.zone.lowerY = this.zone.upperY / 2;
				} else {

					z = new Zone();
					z.setLowerX(this.zone.lowerX);
					z.setUpperX(this.zone.upperX);
					z.setLowerY(this.zone.upperY / 2);
					z.setUpperY(this.zone.upperY);
					z.setIp(ip);

					this.zone.upperY = this.zone.upperY / 2;

				}
			} else {
				if (x <= (this.zone.upperX / 2)) {

					z = new Zone();
					z.setLowerX(this.zone.lowerX);
					z.setUpperX(this.zone.upperX / 2);
					z.setLowerY(this.zone.lowerY);
					z.setUpperY(this.zone.upperY);
					z.setIp(ip);

					this.zone.lowerX = this.zone.upperX / 2;
				} else {

					z = new Zone();
					z.setLowerX(this.zone.upperX / 2);
					z.setUpperX(this.zone.upperX);
					z.setLowerY(this.zone.lowerY);
					z.setUpperY(this.zone.upperY);
					z.setIp(ip);

					this.zone.upperX = this.zone.upperX / 2;
				}
			}
			ArrayList<Zone> newNeighbor = neighbourPeerUpdate(z, "Join");
			this.neighbourList.add(z);
			System.out.println("Neighbour zone is " + z.getIp());
			newNeighbor.add(this.zone);
			// Swap hash tables
			HashMap<String, File> newPeerKeywords = TransferKeywordsTonewZone(z);

			System.out.println("Ip address of joining zone is : " + z.getIp());
			Registry clientReg = LocateRegistry.getRegistry(z.getIp(), 2000);
			IPeerClient clientObj = (IPeerClient) clientReg
					.lookup("PeerClient");

			clientObj.updatingToNewNeighbour(newPeerKeywords, newNeighbor, z);// ------------------->

		} else {
			// redirecting
			String IP = this.calculateMinDistanceNeighbour(x, y);
			Registry clientReg = LocateRegistry.getRegistry(IP, 2000);
			IPeerClient clientObj = (IPeerClient) clientReg
					.lookup("PeerClient");
			clientObj.join(ip, x, y);

		}
	}

	public void updatingToNewNeighbour(HashMap<String, File> newWords,
			ArrayList<Zone> newNeighbour, Zone z) {

		this.wordsMap = newWords;
		this.neighbourList = newNeighbour;
		this.zone = z;

	}

	ArrayList<Zone> neighbourPeerUpdate(Zone newClient, String a)
			throws RemoteException, NotBoundException {
		ArrayList<Zone> newNeighborPeer = new ArrayList<Zone>();

		for (int i = 0; i < neighbourList.size(); i++) {
			Zone currentZone = this.neighbourList.get(i);
			Registry clientReg = LocateRegistry.getRegistry(
					currentZone.getIp(), 2000);
			IPeerClient obj = (IPeerClient) clientReg.lookup("PeerClient");
			if (isNeighbour(newClient, currentZone)) {
				if (a.equals("Join")) {
					
					obj.updateNeighborRemotely(newClient, "Add");
					newNeighborPeer.add((currentZone));
				}
				if (!newClient.getIp().equals((currentZone.getIp()))) {
					obj.updateNeighborRemotely(newClient, "Leave");
					newNeighborPeer.add((currentZone));
				}
			}
			if (isNeighbour(this.zone, currentZone))
				obj.updateNeighborRemotely(this.zone, "Update");
			else
				obj.updateNeighborRemotely(this.zone, "Remove");

		}
		return newNeighborPeer;
	}

	// Remote method to update the neighbors of the remote object
	public void updateNeighborRemotely(Zone z, String a)
			throws RemoteException, NotBoundException {
		if(a.equals("Add"))
		{
			//If a new node joins
			this.neighbourList.add(z);
			return;
		}
        int i;
		for ( i = 0; i < this.neighbourList.size(); i++) {
			if (this.neighbourList.get(i).getIp().equals(z.getIp()))
				break;
		}
		if (a.equals("Leave")) {
			// Updating the neighbors when the node leaves
			if (i == this.neighbourList.size()) {
				this.neighbourList.add(z);
			} else
				this.neighbourList.remove(i);
		}
		if (a.equals("Remove")) {
			// deleting the node from the neighbor list
			this.neighbourList.remove(i);

		}
		if (a.equals("Update"))
			this.neighbourList.remove(i);
		if (z.getLowerX() != z.getUpperX() && z.getLowerY() != z.getUpperY())
			this.neighbourList.add(z);
	
	}
	public boolean isNeighbour(Zone zone1, Zone zone2) {

		if ((zone1.getLowerX() == zone2.getLowerX())
				&& (zone1.getLowerY() == zone2.getLowerY())
				&& (zone1.getUpperX() == zone2.getUpperX())
				&& (zone1.getUpperY() == zone2.getUpperY()))
			return false;

		int breadth = Math.abs(zone2.getLowerY() - zone2.getUpperY())
				+ Math.abs(zone1.getLowerY() - zone1.getUpperY());
		int length = Math.abs(zone2.getLowerX() - zone2.getUpperX())
				+ Math.abs(zone1.getLowerX() - zone1.getUpperX());
		if (zone1.getLowerX() == zone1.getUpperX()
				|| zone1.getLowerY() == zone1.getUpperY())
			return false;
		if (Math.abs(zone1.getLowerY() - zone2.getUpperY()) > breadth
				|| Math.abs(zone1.getUpperY() - zone2.getLowerY()) > breadth)
			return false;
		if (Math.abs(zone1.getLowerX() - zone2.getUpperX()) > length
				|| Math.abs(zone1.getUpperX() - zone2.getLowerX()) > length)

			return false;

		return true;

	}

	boolean isZoneSquare() {
		if (Math.abs(this.zone.lowerX - this.zone.upperX)
				- Math.abs(this.zone.lowerY - this.zone.upperY) != 0) {
			return false;
		} else {
			return true;
		}
	}

	public boolean CheckPointsLieoonZone(Zone z, int x, int y) {
		if ((x >= z.getLowerX()) && (x <= z.getUpperX())
				&& (y >= z.getLowerY()) && (y <= z.getUpperY()))
			return true;
		else
			return false;
	}

	public int calculateHashX(String word) {
		int hashX = 17;
		for (int i = 0; i < word.length(); i = i + 2)
			hashX = hashX * 3 + word.charAt(i);
		while (hashX > 400) {
			hashX = hashX / 10;
		}
		return hashX;

	}

	public int calculateHashY(String word) {
		int hashY = 5;
		for (int i = 1; i < word.length(); i = i + 2)
			hashY = hashY * 7 + word.charAt(i);
		while (hashY > 400) {
			hashY = hashY / 10;
		}
		return hashY;

	}

	// transferring keyword to the newly joined client
	HashMap<String, File> TransferKeywordsTonewZone(Zone z) {
		HashMap<String, File> newWords = new HashMap<String, File>();
		Iterator<Entry<String, File>> itr = this.wordsMap.entrySet().iterator();
		while (itr.hasNext()) {
			HashMap.Entry<String, File> newMap = (HashMap.Entry<String, File>) itr
					.next();
			if (CheckPointsLieoonZone(z, calculateHashX(newMap.getKey()),
					calculateHashY(newMap.getKey()))) {
				this.wordsMap.remove(newMap.getKey());
				newWords.put(newMap.getKey(), newMap.getValue());
			}
		}
		return newWords;

	}

	String calculateMinDistanceNeighbour(int x, int y) throws RemoteException,
			NotBoundException {
		int currentD = 0;
		int dis = 400;
		int minI = 0;

		int size = this.neighbourList.size();
		for (int i = 0; i < size; i++) {
			Zone currentNeighbour = this.neighbourList.get(i);
			currentD = (int) Math.abs((Math.pow(
					(x - ((currentNeighbour.getLowerX() + currentNeighbour
							.getUpperX()) / 2)), 2)))
					+ (int) Math.abs((Math
							.pow((y - ((currentNeighbour.getLowerY() + currentNeighbour
									.getUpperY()) / 2)), 2)));
			if (currentD < dis) {
				dis = currentD;
				minI = i;
			}
		}
		return this.neighbourList.get(minI).getIp();

	}

	public void addingWordsToList(HashMap<String, File> wordsMap) {
		Iterator<Entry<String, File>> itr = wordsMap.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry<String, File> tempmap = (Entry<String, File>) itr.next();

			this.wordsMap.put(tempmap.getKey(), tempmap.getValue());
		}
	}

	public void insertNewFile(String fileName, File newFile)
			throws RemoteException, NotBoundException {
		ArrayList<String> foundZoneforFile = new ArrayList<String>();

		foundZoneforFile = searchZoneForFileInsertion(fileName,
				foundZoneforFile, newFile);

	}

	public ArrayList<String> searchZoneForFileInsertion(String fileName,
			ArrayList<String> foundZoneforFile, File newFile)
			throws RemoteException, NotBoundException {
		int x = calculateHashX(fileName);
		int y = calculateHashY(fileName);
		foundZoneforFile.add(this.zone.getIp());
		if (CheckPointsLieoonZone(this.zone, x, y)) {
			String msg = null;
			if (this.wordsMap.containsKey(fileName)) {
				msg = "Already file with same Keyword is there in the zone.So new file is not added";
			} else {
				this.wordsMap.put(fileName, newFile);
				msg = "File is added to the client " + this.zone.getIp();
			}
			Registry reg = LocateRegistry.getRegistry(foundZoneforFile.get(0),
					3300);
			IPeerClient clientObj = (IPeerClient) reg.lookup("PeerClient");
			clientObj.displayFileInsertMessage(msg);

		} else {
			String minDIP = calculateMinDistanceNeighbour(x, y);
			Registry reg = LocateRegistry.getRegistry(minDIP, 2000);
			IPeerClient minDClient = (IPeerClient) reg.lookup("PeerClient");
			foundZoneforFile = minDClient.searchZoneForFileInsertion(fileName,
					foundZoneforFile, newFile);

		}

		return foundZoneforFile;
	}

	public void displayFileInsertMessage(String msg) throws RemoteException,
			NotBoundException {
		System.out.println(msg);
	}

	public void searchKeyword(String name) throws NotBoundException,
			IOException {

		searchFileInNetwork(name, this.zone.getIp());
	}

	public void searchFileInNetwork(String name, String ip)
			throws NotBoundException, IOException {
		int x = calculateHashX(name);
		int y = calculateHashY(name);

		if (CheckPointsLieoonZone(this.zone, x, y)) {

			if (this.wordsMap.containsKey(name)) {

				File file = this.wordsMap.get(name);

				// transferring files
				if (ip.equals(this.zone.getIp())) {
					System.out
							.println("File is located in the same client.So file is not downloaded");
				} else {
					Registry clientreg = LocateRegistry.getRegistry(ip, 3300);
					IPeerClient obj = (IPeerClient) clientreg
							.lookup("PeerClient");
					FileInputStream in = new FileInputStream(file);
					byte[] mydata = new byte[1024 * 1024];
					int fileSize = in.read(mydata);
					while (fileSize > 0) {
						obj.transferFile(file.getName(), mydata, fileSize,
								this.zone.getIp());
						fileSize = in.read(mydata);
					}
				}

			} else
				System.err.println("Keyword is not found in the network");

		} else {
			String minDIP = calculateMinDistanceNeighbour(x, y);
			Registry reg = LocateRegistry.getRegistry(minDIP, 3300);
			IPeerClient minDClient = (IPeerClient) reg.lookup("PeerClient");
			minDClient.searchFileInNetwork(name, ip);
		}

	}

	public void transferFile(String name, byte[] data, int len, String ip)
			throws IOException {

		File file = new File(name);
		file.createNewFile();
		FileOutputStream out = new FileOutputStream(file, true);
		out.write(data, 0, len);
		out.flush();
		out.close();

		this.wordsMap.put(name, file);
		System.out.println("Keyword found in client " + ip);
		System.out.println("File transfer successfull");

	}

	public void displayClientInfo(String ip) throws RemoteException,
			NotBoundException, AlreadyBoundException {
		
		
		if (ip.equals(this.zone.getIp())) {
			System.out.println("IP address is: " + this.zone.getIp());
			System.out.println("LowerX is: " + this.zone.getLowerX());
			System.out.println("UpperX is: " + this.zone.getUpperX());
			System.out.println("LowerY is: " + this.zone.getLowerY());
			System.out.println("UpperY is: " + this.zone.getUpperY());

			Iterator<Entry<String, File>> itr = this.wordsMap.entrySet()
					.iterator();
			while (itr.hasNext()) {
				Map.Entry<String, File> tempmap = (Entry<String, File>) itr
						.next();

				System.out.println("Keyword name is: " + tempmap.getKey());
				System.out.println("file name is: " + tempmap.getValue());
			}
			System.out.println("Neighbouring zone parameters");
			for (int j = 0; j < this.neighbourList.size(); j++) {
				Zone currentZone = this.neighbourList.get(j);
				System.out.println("IP address is: " + currentZone.getIp());
				System.out.println("LowerX is: " + currentZone.getLowerX());
				System.out.println("UpperX is: " + currentZone.getUpperX());
				System.out.println("LowerY is: " + currentZone.getLowerY());
				System.out.println("UpperY is: " + currentZone.getUpperY());
			}
		} else if (ip.equals("all")) {
			ArrayList<String> ipList = new ArrayList<String>();
			ipList.add(this.zone.getIp());
			for (int i = 0; i < this.neighbourList.size(); i++) {
				ipList.add(this.neighbourList.get(i).getIp());
			}

			Registry reg = LocateRegistry.getRegistry(ipList.get(1), 3300);
			IPeerClient clientObj = (IPeerClient) reg.lookup("PeerClient");
			clientObj.getIpAddress(ipList, 1);

		} else {
			Registry reg = LocateRegistry.getRegistry(ip, 3300);
			IPeerClient clientObj = (IPeerClient) reg.lookup("PeerClient");
			clientObj.getInformation(this.zone.getIp());

		}
	}

	public void getIpAddress(ArrayList<String> ipList, int count)
			throws RemoteException, NotBoundException {
		if(clientActive)
		{
		for (int i = 0; i < this.neighbourList.size(); i++) {
			if (ipList.contains(this.neighbourList.get(i).getIp())) {
				continue;
			} else {
				ipList.add(this.neighbourList.get(i).getIp());
			}
		}}
		if (++count < ipList.size()) {
			Registry reg = LocateRegistry.getRegistry(ipList.get(count), 3300);
			IPeerClient clientObj = (IPeerClient) reg.lookup("PeerClient");
			clientObj.getIpAddress(ipList, count);
		} else {

			Registry reg = LocateRegistry.getRegistry(ipList.get(0), 3300);
			IPeerClient clientObj = (IPeerClient) reg.lookup("PeerClient");
			clientObj.displayAllClientsInfo(ipList);

		}
	}

	public void displayAllClientsInfo(ArrayList<String> ipList)
			throws RemoteException, NotBoundException {

		for (int i = 0; i < ipList.size(); i++) {
			Registry reg = LocateRegistry.getRegistry(ipList.get(i), 3300);
			IPeerClient clientObj = (IPeerClient) reg.lookup("PeerClient");
			clientObj.getInformation(this.zone.getIp());

		}

	}

	public void getInformation(String incomingIp) throws RemoteException,
			NotBoundException {
		getInfo get = new getInfo();
		if(clientActive)
		{
		
		get.setIp(this.zone.getIp());
		get.setLowerX(this.zone.getLowerX());
		get.setLowerY(this.zone.getLowerY());
		get.setUpperX(this.zone.getUpperX());
		get.setUpperY(this.zone.getUpperY());
		get.setNeighbourList(this.neighbourList);
		get.setWordsList(this.wordsMap);
		}
		Registry reg = LocateRegistry.getRegistry(incomingIp, 3300);
		IPeerClient clientObj = (IPeerClient) reg.lookup("PeerClient");
		clientObj.displayClientParameters(get);
		

	}

	public void displayClientParameters(getInfo getObj) throws RemoteException,
			NotBoundException {
		System.out.println("Zone information: ");
		System.out.println("IpAddr is: " + getObj.getIp());
		System.out.println("lowerX is: " + getObj.getLowerX());
		System.out.println("lowerY is: " + getObj.getLowerY());
		System.out.println("upperX is: " + getObj.getUpperX());
		System.out.println("upperY is:  " + getObj.getUpperY());
		System.out.println("Displaying neighbouring list parameters");
		for (int j = 0; j < getObj.getNeighbourList().size(); j++) {
			Zone currentZone = getObj.neighbourList.get(j);
			System.out.println("IP address is: " + currentZone.getIp());
			System.out.println("LowerX is: " + currentZone.getLowerX());
			System.out.println("UpperX is: " + currentZone.getUpperX());
			System.out.println("LowerY is: " + currentZone.getLowerY());
			System.out.println("UpperY is: " + currentZone.getUpperY());
		}

		System.out.println("Displaying words list");
		Iterator<Entry<String, File>> itr = getObj.getWordsList().entrySet()
				.iterator();
		while (itr.hasNext()) {
			Map.Entry<String, File> tempmap = (Entry<String, File>) itr.next();

			System.out.println("Keyword name is: " + tempmap.getKey());
			System.out.println("file name is: " + tempmap.getValue());

		}

	}

	public static void main(String[] args) throws NotBoundException,
			AlreadyBoundException, IOException {
		// System.out.println(InetAddress.getLocalHost().getHostAddress());

		PeerClientRMI Obj = new PeerClientRMI();
		Registry clientReg = LocateRegistry.createRegistry(4000);
		clientReg.rebind("PeerClient", Obj);
		String BootStrapIp = null;
		while (true) {
			System.out
					.println("Enter the numbers for corresponding operation to be performed");
			System.out.println(" 1. Join to the CAN");
			System.out.println(" 2. Insert new file to the existing zone ");
			System.out.println(" 3. Search for a file ");
			System.out.println(" 4. View information of client");
			System.out.println(" 5. Leave from CAN Network");
			Scanner getInput = new Scanner(System.in);
			switch (getInput.nextInt()) {
			case 1:
				System.out.println("Enter BootStrap ip address");

				Obj.newJoinRequest(getInput.next());
				clientActive = true;
				System.out.println("New client successfully joined");
				break;
			case 2:
				System.out.println("Enter file name");
				String name = getInput.next();
				File f = new File(name + ".txt");
				FileInputStream fstream = new FileInputStream(f);
				BufferedReader br = new BufferedReader(new InputStreamReader(
						fstream));
				String strLine;
				while ((strLine = br.readLine()) != null) {
					System.out.println(strLine);
				}
				Obj.insertNewFile(name, f);
				System.out.println("File successfully added to the point");
				break;
			case 3:
				System.out.println("Enter file name");
				String Filename = getInput.next();
				Obj.searchKeyword(Filename);
			case 4:
				System.out
						.println("Enter IP address of particular client to view information");
				System.out
						.println("Enter \"all\" to display all client information");
				String ip = getInput.next();
				Obj.displayClientInfo(ip);
			case 5:
				Obj.leaveClientfromNetwork(BootStrapIp);
				clientActive = false;
				System.out.println("Client successfully left the network");
			}
		}
	}

	public void deleteLeavingClientInBootStrapServer(String ip)
			throws RemoteException, NotBoundException, MalformedURLException {
		Registry reg = LocateRegistry.getRegistry(ip, 2000);
		IBootStrapServer btObj = (IBootStrapServer) reg.lookup("RmiServer");
		btObj.RemoveBootStrapNode(this.zone.getIp());
	}

	public void deleteLeavingClientInNeighbours() throws RemoteException,
			NotBoundException {
		for (int i = 0; i < this.neighbourList.size(); i++) {
			Registry reg = LocateRegistry.getRegistry(this.neighbourList.get(i)
					.getIp(), 3300);
			IPeerClient clientObj = (IPeerClient) reg.lookup("PeerClient");
			clientObj.deleteClientRemotely(this.zone.getIp());
		}
	}

	public void deleteClientRemotely(String ip) throws RemoteException,
			NotBoundException {
		for (int i = 0; i < this.neighbourList.size(); i++) {
			if (this.neighbourList.get(i).getIp().equals(ip)) {
				this.neighbourList.remove(i);
				break;
			}
		}
	}

	public void leaveClientfromNetwork(String BSIp) throws RemoteException,
			NotBoundException, MalformedURLException {

		for (int i = 0; i < this.neighbourList.size(); i++) {
			sendToNeighbours(this.neighbourList.get(i), this.zone);
		}

		deleteLeavingClientInNeighbours();
		deleteLeavingClientInBootStrapServer(BSIp);

	}

	public void sendToNeighbours(Zone Neighbour, Zone leavingZone)
			throws RemoteException, NotBoundException {

		if ((leavingZone.getLowerX() < Neighbour.getLowerX())
				&& (Neighbour.getLowerY() >= leavingZone.getLowerY())
				&& (Neighbour.getUpperY() <= leavingZone.getUpperY())) {
			if (Neighbour.getLowerY() == leavingZone.getLowerY()) {
				Neighbour = NeighbourUpdateWhileLeaving(
						leavingZone.getLowerX(), leavingZone.getUpperX(),
						Neighbour.getLowerY(), Neighbour.getUpperY(), Neighbour);
				NeighbourUpdateWhileLeaving(leavingZone.getLowerX(),
						leavingZone.getUpperX(), Neighbour.getLowerY(),
						Neighbour.getUpperY(), leavingZone);
			} else if (Neighbour.getUpperY() == leavingZone.getUpperY()) {
				Neighbour = NeighbourUpdateWhileLeaving(
						leavingZone.getLowerX(), leavingZone.getUpperX(),
						Neighbour.getLowerY(), Neighbour.getUpperY(), Neighbour);
				NeighbourUpdateWhileLeaving(leavingZone.getLowerX(),
						leavingZone.getUpperX(), leavingZone.getLowerY(),
						Neighbour.getUpperY(), leavingZone);

			}

			ArrayList<Zone> newNeighbourList = neighbourPeerUpdate(Neighbour,
					"Leave");
			HashMap<String, File> newWords = TransferKeywordsTonewZone(Neighbour);

			leaveUpdateToNeighbour(Neighbour, newNeighbourList, newWords);

		} else if ((leavingZone.getLowerX() > Neighbour.getLowerX())
				&& (Neighbour.getLowerY() >= leavingZone.getLowerY())
				&& (Neighbour.getUpperY() <= leavingZone.getUpperY())) {
			if (Neighbour.getLowerY() == leavingZone.getLowerY()) {
				Neighbour = NeighbourUpdateWhileLeaving(Neighbour.getLowerX(),
						leavingZone.getUpperX(), Neighbour.getLowerY(),
						Neighbour.getUpperY(), Neighbour);
				NeighbourUpdateWhileLeaving(leavingZone.getLowerX(),
						leavingZone.getUpperX(), Neighbour.getUpperY(),
						leavingZone.getUpperY(), leavingZone);
			} else if (Neighbour.getUpperY() == leavingZone.getUpperY()) {
				Neighbour = NeighbourUpdateWhileLeaving(Neighbour.getLowerX(),
						leavingZone.getUpperX(), Neighbour.getLowerY(),
						Neighbour.getUpperY(), Neighbour);
				NeighbourUpdateWhileLeaving(leavingZone.getLowerX(),
						leavingZone.getUpperX(), leavingZone.getLowerY(),
						Neighbour.getLowerY(), leavingZone);

			}

			ArrayList<Zone> newNeighbourList = neighbourPeerUpdate(Neighbour,
					"Leave");
			HashMap<String, File> newWords = TransferKeywordsTonewZone(Neighbour);

			leaveUpdateToNeighbour(Neighbour, newNeighbourList, newWords);
		} else if ((leavingZone.getLowerY() > Neighbour.getLowerY())
				&& (Neighbour.getLowerX() >= leavingZone.getLowerX())
				&& (Neighbour.getUpperX() <= leavingZone.getUpperX())) {
			if (Neighbour.getLowerX() == leavingZone.getLowerX()) {
				Neighbour = NeighbourUpdateWhileLeaving(Neighbour.getLowerX(),
						Neighbour.getUpperX(), Neighbour.getLowerY(),
						leavingZone.getUpperY(), Neighbour);
				NeighbourUpdateWhileLeaving(Neighbour.getUpperX(),
						leavingZone.getUpperX(), leavingZone.getLowerY(),
						leavingZone.getUpperY(), leavingZone);
			} else if (Neighbour.getUpperX() == leavingZone.getUpperX()) {
				Neighbour = NeighbourUpdateWhileLeaving(Neighbour.getLowerX(),
						Neighbour.getUpperX(), Neighbour.getLowerY(),
						leavingZone.getUpperY(), Neighbour);
				NeighbourUpdateWhileLeaving(leavingZone.getLowerX(),
						Neighbour.getLowerX(), leavingZone.getLowerY(),
						leavingZone.getUpperY(), leavingZone);

			}

			ArrayList<Zone> newNeighbourList = neighbourPeerUpdate(Neighbour,
					"Leave");
			HashMap<String, File> newWords = TransferKeywordsTonewZone(Neighbour);

			leaveUpdateToNeighbour(Neighbour, newNeighbourList, newWords);
		} else if ((leavingZone.getLowerY() < Neighbour.getLowerY())
				&& (Neighbour.getLowerX() >= leavingZone.getLowerX())
				&& (Neighbour.getUpperX() <= leavingZone.getUpperX())) {
			if (Neighbour.getLowerX() == leavingZone.getLowerX()) {
				Neighbour = NeighbourUpdateWhileLeaving(Neighbour.getLowerX(),
						Neighbour.getUpperX(), leavingZone.getLowerY(),
						Neighbour.getUpperY(), Neighbour);
				NeighbourUpdateWhileLeaving(Neighbour.getUpperX(),
						leavingZone.getUpperX(), leavingZone.getLowerY(),
						leavingZone.getUpperY(), leavingZone);
			} else if (Neighbour.getUpperX() == leavingZone.getUpperX()) {
				Neighbour = NeighbourUpdateWhileLeaving(Neighbour.getLowerX(),
						Neighbour.getUpperX(), leavingZone.getLowerY(),
						Neighbour.getUpperY(), Neighbour);
				NeighbourUpdateWhileLeaving(leavingZone.getLowerX(),
						leavingZone.getLowerX(), leavingZone.getLowerY(),
						Neighbour.getLowerY(), leavingZone);

			}

			ArrayList<Zone> newNeighbourList = neighbourPeerUpdate(Neighbour,
					"Leave");
			HashMap<String, File> newWords = TransferKeywordsTonewZone(Neighbour);

			leaveUpdateToNeighbour(Neighbour, newNeighbourList, newWords);
		}

	}

	public void leaveUpdateToNeighbour(Zone z, ArrayList<Zone> newList,
			HashMap<String, File> newWords) throws RemoteException,
			NotBoundException {
		Registry reg = LocateRegistry.getRegistry(z.getIp(), 3300);
		IPeerClient clientObj = (IPeerClient) reg.lookup("PeerClient");
		clientObj.updateLeaveFinally(z, newList, newWords);
	}

	public void updateLeaveFinally(Zone z, ArrayList<Zone> newList,
			HashMap<String, File> newWords) throws RemoteException,
			NotBoundException {
		this.zone.setLowerX(z.getLowerX());
		this.zone.setLowerY(z.getLowerY());
		this.zone.setUpperX(z.getUpperX());
		this.zone.setUpperY(z.getUpperY());
		addingWordsToList(newWords);
		leaveUpdate(newList);

	}

	public void leaveUpdate(ArrayList<Zone> newList) {
		boolean setflag;
		for (int i = 0; i < newList.size(); i++) {
			int j;

			setflag = false;

			for (j = 0; j < this.neighbourList.size(); j++) {
				if (this.neighbourList.get(j).getIp()
						.equals(newList.get(i).getIp())) {
					this.neighbourList.remove(j);
					this.neighbourList.add(j, newList.get(i));
					setflag = true;
					break;
				}
			}
			if (!setflag) {
				this.neighbourList.add(newList.get(j));
			}
		}

	}

	public Zone NeighbourUpdateWhileLeaving(int lowX, int UppX, int lowY,
			int UppY, Zone Neighbour) {
		Neighbour.setLowerX(lowX);
		Neighbour.setUpperX(UppX);
		Neighbour.setLowerY(lowY);
		Neighbour.setUpperY(UppY);
		return Neighbour;
	}

}
