package ChatProgram2;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class ChatClientUDP {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 9876;

    public static void main(String[] args) {
        try {
            DatagramSocket clientSocket = new DatagramSocket();

            // Memasukkan nama klien
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter your name: ");
            String name = scanner.nextLine();

            // Menggunakan thread untuk menerima pesan dari server
            Thread receiveThread = new Thread(() -> receiveMessages(clientSocket));
            receiveThread.start();

            // Mengirim pesan ke server
            sendMessage(clientSocket, name);

            // Menutup socket setelah mengirim pesan
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendMessage(DatagramSocket clientSocket, String name) throws IOException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter your message (or type 'exit' to quit): ");
            String message = scanner.nextLine();

            if ("exit".equalsIgnoreCase(message)) {
                break;
            }

            message = name + ": " + message;

            byte[] sendData = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(SERVER_IP), SERVER_PORT);
            clientSocket.send(sendPacket);
        }
    }

    private static void receiveMessages(DatagramSocket clientSocket) {
        try {
            byte[] receiveData = new byte[1024];

            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);

                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Received message : " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

