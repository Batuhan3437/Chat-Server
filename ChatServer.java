import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {

    private static final int PORT = 8080; // Port numarasını uygun bir değerle değiştirin
    private static final List<PrintWriter> clients = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Chat Server başlatıldı. Port: " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Yeni bağlantı: " + clientSocket.getInetAddress());

                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                clients.add(out);

                Thread clientThread = new Thread(new ClientHandler(clientSocket, out));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {

        private final Socket clientSocket;
        private final PrintWriter out;

        public ClientHandler(Socket clientSocket, PrintWriter out) {
            this.clientSocket = clientSocket;
            this.out = out;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Gelen mesaj: " + message);

                    // Gelen mesajı diğer client'lara gönder
                    for (PrintWriter client : clients) {
                        if (client != out) {
                            client.println(message);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
