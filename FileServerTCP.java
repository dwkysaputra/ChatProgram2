package ChatProgram2;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServerTCP {
    public static void main(String[] args) {
        final int PORT = 12345;

        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server is listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                // Membuat thread baru untuk melayani client
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread handlerThread = new Thread(clientHandler);
                handlerThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                InputStream inputStream = clientSocket.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

                String fileName = objectInputStream.readUTF();
                long fileSize = objectInputStream.readLong();
                System.out.println("Receiving file: " + fileName + " (" + fileSize + " bytes)");

                FileOutputStream fileOutputStream = new FileOutputStream("server_" + fileName);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    bufferedOutputStream.write(buffer, 0, bytesRead);
                }

                bufferedOutputStream.close();
                clientSocket.close();

                System.out.println("File received successfully.");

                // Membaca dan menampilkan isi file yang diterima
                FileInputStream fileInputStream = new FileInputStream("server_" + fileName);
                BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));

                System.out.println("File content:");
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
