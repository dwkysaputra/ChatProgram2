package ChatProgram2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class ChatServerUDP {
    private static final int PORT = 9876;
    private static List<ClientInfo> connectedClients = new ArrayList<>();

    public static void main(String[] args) {
        try {
            DatagramSocket serverSocket = new DatagramSocket(PORT);
            System.out.println("Server is listening on port " + PORT);

            while (true) {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                // Menerima pesan dari klien
                serverSocket.receive(receivePacket);

                // Mendapatkan informasi pesan yang diterima
                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Received message from " + receivePacket.getAddress() + ": " + message);

                // Menambahkan informasi klien yang terhubung
                ClientInfo clientInfo = new ClientInfo(receivePacket.getAddress().getHostAddress(), receivePacket.getPort());
                connectedClients.add(clientInfo);

                // Menyebarkan pesan ke semua klien yang terhubung
                broadcastMessage(serverSocket, message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void broadcastMessage(DatagramSocket serverSocket, String message) {
        byte[] sendData = message.getBytes();
        for (ClientInfo client : connectedClients) {
            try {
                // Membuat DatagramPacket baru untuk setiap klien dan mengirimnya
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(client.getAddress()), client.getPort());
                serverSocket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Kelas untuk menyimpan informasi klien yang terhubung
    static class ClientInfo {
        private final String address;
        private final int port;

        public ClientInfo(String address, int port) {
            this.address = address;
            this.port = port;
        }

        public String getAddress() {
            return address;
        }

        public int getPort() {
            return port;
        }
    }
}

