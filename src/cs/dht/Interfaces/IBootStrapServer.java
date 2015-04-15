package cs.dht.Interfaces;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

import cs.dht.Client.Zone;

public interface IBootStrapServer extends Remote{

	
	public String returnBootStrapNode(String ip) throws RemoteException,NotBoundException;
	public void RemoveBootStrapNode(String ip) throws RemoteException,NotBoundException, MalformedURLException;
	
}
