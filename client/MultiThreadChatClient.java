import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class MultiThreadChatClient implements Runnable {

  // The client side socket
  private static Socket clientSocket = null;
  // The output stream
  private static PrintStream os = null;
  // The input stream
  private static DataInputStream is = null;

  private static BufferedReader inputLine = null;
  private static boolean closed = false;
  
  public static void main(String[] args) {

    // Port number to be used
    int portNumber = 2222;
    
    // Ask for the Server IP
    System.out.print("Please enter Server IP address: ");
   	Scanner scanner = new Scanner(System.in);
    String host = scanner.next();

    if (args.length < 2) {
      System.out
          .println("Usage: java MultiThreadChatClient <host> <portNumber>\n"
              + "Looking for Server: " + host + ", on the port number: " + portNumber);
    } else {
      host = args[0];
      portNumber = Integer.valueOf(args[1]).intValue();
    }

    /*Open a socket on a given host and port.After that Open input and output streams.*/
    try {
      clientSocket = new Socket(host, portNumber);
      inputLine = new BufferedReader(new InputStreamReader(System.in));
      os = new PrintStream(clientSocket.getOutputStream());
      is = new DataInputStream(clientSocket.getInputStream());
    } catch (UnknownHostException e) {
      System.err.println("Don't know about server: " + host);
    } catch (IOException e) {
      System.err.println("Couldn't get I/O for the connection to the host "
          + host);
    }

    
     // Start writing data on the opened socket
          
    if (clientSocket != null && os != null && is != null) {
      try {

        //Create a thread to read from the server. 
        new Thread(new MultiThreadChatClient()).start();
        while (!closed) {
          os.println(inputLine.readLine().trim());
        }
        //Close the output stream, input stream and the socket.
        os.close();
        is.close();
        clientSocket.close();
      } catch (IOException e) {
        System.err.println("IOException:  " + e);
      }
    }
  }

  /*Create a thread to read from the server.*/
  public void run() {
    /* Keep on reading from the socket till receive "Bye" from the
     * server. Once we received "Bye" then break. */
    String responseLine;
    try {
      while ((responseLine = is.readLine()) != null) {
        System.out.println(responseLine);
        if (responseLine.indexOf("*** Bye") != -1)
          break;
      }
      closed = true;
    } catch (IOException e) {
      System.err.println("IOException:  " + e);
    }
  }
}
