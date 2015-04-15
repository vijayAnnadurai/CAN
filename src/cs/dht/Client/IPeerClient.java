
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;


public interface IPeerClient extends Remote{

	
	public String returnBootStrapNode(String ipAddress) throws RemoteException,NotBoundException;
	public void join(String ip,int x,int y) throws RemoteException, NotBoundException;
	public void updateNeighborRemotely(Zone z,String a) throws RemoteException, NotBoundException;
	
	public void updatingToNewNeighbour(HashMap<String,File> newWords,ArrayList<Zone> newNeighbour,Zone z) throws RemoteException, NotBoundException;

	public void remoteRemoveFromNeighbors(String IPAddress)throws RemoteException, NotBoundException;
	public void remoteUpdateBootStrapServer(String ipAddress)throws RemoteException,NotBoundException;
	public ArrayList<String> searchZoneForFileInsertion(String fileName,ArrayList<String> foundZoneforFile,File newFile) throws RemoteException, NotBoundException;
	public void searchFileInNetwork(String name) throws RemoteException, NotBoundException, FileNotFoundException, IOException;
	public void transferFile(String name,byte[] data,int len) throws IOException;


}
	

