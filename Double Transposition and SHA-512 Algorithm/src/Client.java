import crypto.Checksum;
import crypto.Crypto;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.Arrays;

public class Client {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;

    public Client(String serverAddress, int port) {
        try {
            socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            createGUI();
            new Thread(this::receiveMessages).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createGUI() {
        frame = new JFrame("Chat App");
        chatArea = new JTextArea(20, 40);
        chatArea.setEditable(false);
        messageField = new JTextField(30);
        sendButton = new JButton("Send");

        JPanel panel = new JPanel();
        panel.add(messageField);
        panel.add(sendButton);

        frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        frame.add(panel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            chatArea.append("Sent: " + message + "\n");

            /**
             * calculate checksum and encrypt message
             * send encrypted message and checksum to server
             */
            // ciphertext
            Crypto crypto = new Crypto("hack", "me");
            String cipherText = crypto.encrypt(message);

            // hashvalue
            Checksum hashChecksum = new Checksum();
            String cipherHashValue = null;
            try {
                String hashValue = hashChecksum.calculateHash(message);
                cipherHashValue = crypto.encrypt(hashValue);
            }catch (Exception e){
                System.out.println(e);
            }
            /**
             * sending encrypted message and checksum to server
             */
            out.println(cipherText + ":" + cipherHashValue);
            System.out.println("sent cipher text and hash: "  + cipherText + ":" + cipherHashValue);
            messageField.setText("");
        }
    }

    private void receiveMessages() {
        try {
            String receivedMessage;
            while ((receivedMessage = in.readLine()) != null) {
                /**
                 * print encrypted message and hash value
                 */
                String[] parts = receivedMessage.split(":", 2);
                System.out.println("Receipt cipher text and hash value: " + Arrays.toString(parts));

                System.out.println("recept: " + parts[0]);
                /**
                 * decrypt the message and check checksum status
                 */
                // decrypt
                Crypto crypto = new Crypto("hack", "me");
                String decryptedMsg =  crypto.decrypt(parts[0]); // we can modify message

                Checksum hashChecksum = new Checksum();
                // checksum checking
                String finalMessage = null;
                try{
                    String decryptedHash = crypto.decrypt(parts[1]);
                    Boolean  isChecksumOk  =  hashChecksum.verifyHash(decryptedMsg, decryptedHash);

                    if(isChecksumOk){
                        finalMessage = decryptedMsg;
                        System.out.println("Receipt Original message: " + decryptedMsg);
                        System.out.println("Receipt checksum status: " + isChecksumOk);
                    }else{
                        finalMessage = "modified: " + decryptedMsg;
                        System.out.println("Receipt broken message: " + decryptedMsg);
                        System.out.println("Receipt checksum status: " + isChecksumOk);
                    }
                }catch (Exception e){
                    System.out.println("Error while verify checksum value: " + e);
                }
                chatArea.append("Received: " + finalMessage + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Client("localhost", 12345));
    }
}
