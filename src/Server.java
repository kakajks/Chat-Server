import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Server {

	ServerSocket server;
	ArrayList<PrintWriter> list_clientWriter;

	HashMap<Socket, String> logged = new HashMap<>();
	final int LEVEL_ERROR = 1;
	final int LEVEL_NORMAL = 0;

	public static void main(String[] args) {
		MySQL.connect();
		Server s = new Server();
		if (s.runServer()) {
			s.Clientjoin();
		}

	}

	public class ClientHandler implements Runnable {
		Socket client;
		BufferedReader reader;

		public ClientHandler(Socket client) {
			try {
				this.client = client;

				reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			String nachricht;
			try {
				
				while((nachricht = reader.readLine()) != null) {
					if(logged.containsKey(client)) {
						if(!nachricht.startsWith("login;")){
							if(nachricht.startsWith("@")) {
								for(Socket clients : logged.keySet()) {
									String clientname =nachricht.split(" ")[0].substring(1);
									if(logged.get(clients).equalsIgnoreCase(clientname)) {
										sendToClient(nachricht, client, clients);								
									}
		
								}
							}else {
								sendToAllClient(nachricht,client);
							}
							
						}
					}else {
						if(nachricht.startsWith("login;")) {
							String msg = nachricht.substring(6, nachricht.length());
							String[] log = msg.split(":");

							if(MySQLPoints.getCode(log[0].toLowerCase()).equals(log[1])) {

								PrintWriter writer = new PrintWriter(client.getOutputStream());
								writer.println("true");
								writer.flush();
								logged.put(client,log[0]);
								list_clientWriter.add(writer);
								appendTextToConsole(log[0]+ " joined the Chat!", LEVEL_ERROR);
								sendToAllClient(log[0]+" joined the Chat!");
								sendToAllOnline();
						}else {
								PrintWriter writer = new PrintWriter(client.getOutputStream());
								writer.println("false");
								writer.flush();
							}
						}else {
						PrintWriter writer = new PrintWriter(client.getOutputStream());
						writer.println("Du bist nicht eingeloggt!");
						writer.flush();
						}
					}
				}
				
			}catch(

		IOException e)
		{

				appendTextToConsole(logged.get(client)+ " left the Chat!", LEVEL_ERROR);
				sendToAllClient(logged.get(client)+" left the Chat!");
				logged.remove(client);
				sendToAllOnline();

				
		}

	}

	}

	public void Clientjoin() {
		while (true) {
			try {

				Socket client = server.accept();
				Thread clientThread = new Thread(new ClientHandler(client));
				clientThread.start();

			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

	}

	public void sendToAllOnline() {
		String online = "Online;";
		
		for (Socket clients : logged.keySet()) {
			online += logged.get(clients) + ",";
		}
		
		sendToAllClient(online);
	}

	public void sendToAllClient(String nachricht) {
		@SuppressWarnings("rawtypes")
		Iterator it = list_clientWriter.iterator();

		while (it.hasNext()) {
			PrintWriter writer = (PrintWriter) it.next();
			writer.println(nachricht);
			writer.flush();
		}
	}


	public void sendToClient(String nachricht, Socket client, Socket To) {
		
		
		String msg = "";

		if (logged.containsKey(client)) {
			msg += "["+logged.get(client) + "->"+logged.get(To)+"] ";
		}
		msg += nachricht.substring(1);
			PrintWriter writer;
			try {
				writer = new PrintWriter(To.getOutputStream());
				writer.println(msg);
				writer.flush();

				writer = new PrintWriter(client.getOutputStream());
				writer.println(msg);
				writer.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		

	}
	public void sendToAllClient(String nachricht, Socket client) {
		@SuppressWarnings("rawtypes")
		Iterator it = list_clientWriter.iterator();
		String msg = "";

		if (logged.containsKey(client)) {
			msg += logged.get(client) + ": ";
		}
		msg += nachricht;
		while (it.hasNext()) {
			PrintWriter writer = (PrintWriter) it.next();
			writer.println(msg);
			writer.flush();
		}

	}

	public boolean runServer() {
		try {
			server = new ServerSocket(333);
			appendTextToConsole("Server wurde gestartet!", LEVEL_ERROR);

			list_clientWriter = new ArrayList<PrintWriter>();
			return true;
		} catch (IOException e) {
			// TODO: handle exception

			appendTextToConsole("Server konnte nicht gestartet werden!", LEVEL_ERROR);

			e.printStackTrace();
			return false;
		}

	}

	public void appendTextToConsole(String message, int level) {
		if (level == LEVEL_ERROR) {
			System.err.println(message + "\n");

		} else {
			System.out.println(message + "\n");
		}

	}

	public enum ClientState {
		Login, Chat;
	}

}
