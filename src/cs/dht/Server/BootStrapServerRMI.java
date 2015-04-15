package cs.dht.Server;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Enumeration;

import cs.dht.Interfaces.IBootStrapServer;
import cs.dht.Interfaces.IPeerClient;

public class BootStrapServerRMI extends UnicastRemoteObject implements
		IBootStrapServer {

	ArrayList<String> nodes = new ArrayList<String>();

	public BootStrapServerRMI() throws RemoteException {
		super();
		
		  
		 

	}

	public String returnBootStrapNode(String ip) throws RemoteException,
			NotBoundException {
		if (nodes.isEmpty()) {
			nodes.add(ip);
			System.out.println("First ip added " + ip);
			return "Node1";
		} else
		// if (nodes.contains(ip))
		{
			System.out.println("Ip returned is " + nodes.get(0));
			return nodes.get(0);
		}

	}

	public void RemoveBootStrapNode(String ip) throws RemoteException,
			NotBoundException, MalformedURLException {
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.contains(ip))
				nodes.remove(ip);
		}
	}
	String getIPAddress()
	{
		String ip = "";
        try 
        {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            
            while (interfaces.hasMoreElements()) 
            {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp())
                {
                    continue;
                }

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while(addresses.hasMoreElements()) 
                {
                    InetAddress addr = addresses.nextElement();
                    if(!addr.getHostAddress().contains(":"))
                    {
                    	ip = addr.getHostAddress();
                    }
                }
            }
            return ip;
        } 
        catch (Exception e) 
        {
            throw new RuntimeException(e);
        }
       
	}
	

	public static void main(String[] args) throws RemoteException {
		// System.setSecurityManager(new RMISecurityManager());

		

		try {
			IBootStrapServer obj = new BootStrapServerRMI();
			Registry reg = LocateRegistry.createRegistry(2000);
			reg.rebind("RmiServer", obj);
			
			
			
			
System.out.println(((BootStrapServerRMI) obj).getIPAddress());
			System.out.println("Server ready");
		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}
	}

}
