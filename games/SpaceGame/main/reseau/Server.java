package reseau;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server extends Thread{

	private ServerSocket server=null;
	public BufferedReader reader=null;
	public BufferedWriter writer=null;
	private Socket client=null;
	public ArrayList<String> toRead=new ArrayList<String>();
	public Lock mutex=new ReentrantLock(true);

	public Server(int port) throws Exception{

		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	public Server(Socket s){
		client=s;
	}
	public void run(){
		try{
			if(Reseau.serverBoo){
				System.out.println("en attente de connexion ...");
				client = server.accept();
				System.out.println("connecté !");
			}

			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
			Reseau.connected=true;
			String line;
			while((line = reader.readLine()) != null){
				mutex.lock();
				toRead.add(line);
				mutex.unlock();
			}
		}
		catch(Exception e){
			e.printStackTrace();
			kill();
		}
	}

	public void kill(){
		Reseau.connected=false;
		System.out.println("deconnexion");
		try{
			if (reader != null)
				reader.close();
			if(writer != null)
				writer.close();
			if (client != null)
				client.close();
			if(server !=null)
				server.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}
}
