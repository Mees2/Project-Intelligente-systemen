package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClientTicTacToe implements Runnable {

    private String hostName = "127.0.0.1"; // localhost address
    private int portNumber = 7789; // port nummer van de server

    private Socket client; // aanmaken van de socket
    private BufferedReader in; // aanmaken van input reader
    private PrintWriter out; // aanmaken van output writer
    private boolean done; // boolean voor het bewaren of we klaar zijn (voor disconnect)

    boolean placed = false;
    private boolean connected = false; // Add this field

    private List<Integer> gohitthese;

    /**
     * Connect to the server
     */
    public boolean connectToServer() {
        try {
            System.out.println("Attempting to connect to server at " + hostName + ":" + portNumber);
            
            client = new Socket(hostName, portNumber);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            
            connected = true;
            System.out.println("Successfully connected to server!");
            return true;
            
        } catch (IOException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
            System.err.println("Make sure the server is running on " + hostName + ":" + portNumber);
            connected = false;
            return false;
        }
    }

    /**
     * Check if connected to server
     */
    public boolean isConnected() {
        return connected && client != null && !client.isClosed();
    }

	@Override
	public void run()
	{
		System.out.print("starting game");

		try {
			client = new Socket(hostName, portNumber); // we maken een nieuwe Socket genaamd client op de hostname en portname van de server later moet de hostname en port uit void main args[0] en args[1] gepakt worden zodat we kunnen verbinden met de toeirnooi server
			out = new PrintWriter(client.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));

			InputHandler inputHandler = new InputHandler(); // we maken een InputHander aan, deze is hier beneden gemaakt staat niet in aparte classe
			Thread thread = new Thread(inputHandler); // we zetten de inputhandler op een apparte thread
			thread.start(); //we starten de thread, gebruik nooit run(); dat start geen apparte thread

			out.println("login "+ "iemand"+ LocalDateTime.now().getSecond()+LocalDateTime.now().getNano());

			out.println("get gamelist");

			out.println("subscribe tic-tac-toe");

			String inputMessage; // we maken een input message aan

			while((inputMessage = in.readLine()) != null) { // het moet in een loop zodat we input kunnen blijven verwerken
				System.out.println(inputMessage);

				if(inputMessage.contains("YOURTURN")){
					System.out.println("YOURTURN");

					//out.println("move 0"); // een eerste kruisje/nulletje komt op plek 0, eeruste vakje links bovenaan

				}

			}
		} catch (IOException e) {
			//TODO handle
		}
	}

	/**
	 * Deze methode verbreekt de verbinding met de server
	 */
	public void shutdown()
	{
		done = true;
		try {
			in.close();
			out.close();
			if(!client.isClosed()) {
				client.close();
			}
		} catch (IOException e) {
			//we negeren een exception omdat we toch de verbinding verbreken
		}
	}

	/**
	 * Deze methode logt in met de opgegeven gebruikersnaam parameter
	 * @param name
	 */
	public void login(String name)
	{
		System.out.println("Logging in as " + name);
		out.println("login " + name);
	}
	class InputHandler implements Runnable
	{
		@Override
		public void run() {
			try {
				BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
				while (!done) {
					String message = inputReader.readLine();

					if(message.equalsIgnoreCase("/quit")) { // we kijken eerst of de gebruiker de disconnect command gebruikt
						inputReader.close();
						shutdown();
					}
					if(message.startsWith("/login")) { // daarna kijken we of de gebruiker de login command gebruikt
						String[] i = message.split(" ", 2);
						String name = i[1];
						login(name);
						//out.println("login " + name);
					} else { // anders printen we het message
						out.println(message);
					}

				}
			} catch (IOException e) { // als er een exception is verbreken we gewoon de verbinding
				shutdown();
			}

		}

	}
	public static void main(String[] args) {
		ClientTicTacToe client = new ClientTicTacToe();

		client.run();
	}

}