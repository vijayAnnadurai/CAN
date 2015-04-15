package cs.dht.Interfaces;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

import cs.dht.Client.Zone;
import cs.dht.Client.getInfo;

public interface IPeerClient extends Remote{

	
	public void join(String ip,int x,int y) throws RemoteException, NotBoundException;
	public void updateNeighborRemotely(Zone z,String a) throws RemoteException, NotBoundException;
	
	public void updatingToNewNeighbour(HashMap<String,File> newWords,ArrayList<Zone> newNeighbour,Zone z) throws RemoteException, NotBoundException;

	public ArrayList<String> searchZoneForFileInsertion(String fileName,ArrayList<String> foundZoneforFile,File newFile) throws RemoteException, NotBoundException;
	public void searchFileInNetwork(String name,String ip) throws RemoteException, NotBoundException, FileNotFoundException, IOException;
	public void transferFile(String name,byte[] data,int len, String ip) throws IOException;

	public void updateLeaveFinally(Zone z, ArrayList<Zone> newList,
			HashMap<String, File> newWords) throws RemoteException, NotBoundException;
	public void deleteClientRemotely(String ip) throws RemoteException, NotBoundException;
		public void displayFileInsertMessage(String msg) throws RemoteException, NotBoundException;;

	public void getInformation(String incomingIp) throws RemoteException,NotBoundException;
	public void displayClientParameters(getInfo getObj) throws RemoteException,NotBoundException;
	public void getIpAddress(ArrayList<String> ipList,int count) throws RemoteException, NotBoundException;
	public void displayAllClientsInfo(ArrayList<String> ipList) throws RemoteException, NotBoundException;

}
	

