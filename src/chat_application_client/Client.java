/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat_application_client;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/**
 *
 * @author Suneth
 */
public class Client extends JFrame{
    
    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String message = "";
    private String serverIP;
    private Socket connection;
    final String secretKey = "ssshhhhhhhhhhh!!!!";
    AESencryption aes = new AESencryption();
		
    //constructor
    public Client(String host){
        super("WhatsChat Instant Messenger");
        try{
            serverIP = host;
            userText = new JTextField();
            userText.setEditable(false);
            userText.addActionListener(
                new ActionListener(){
                    public void actionPerformed(ActionEvent event){
                        sendMessage(event.getActionCommand());
                        userText.setText("");
                    }
                }
            );
            add(userText, BorderLayout.NORTH);
            chatWindow = new JTextArea();
            add(new JScrollPane(chatWindow), BorderLayout.CENTER);
            setSize(300,150);
            setVisible(true);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null,"Error occured","Error",JOptionPane.ERROR_MESSAGE);
            System.out.println("Error occured: " + ex.toString());
        }
    }

    //set up and run the server
    public void startRunning(){
        try{
            try{
                    connectToServer();
                    setupStreams();
                    whileChatting();
               }catch(Exception ex){
                    JOptionPane.showMessageDialog(null,"Error occured","Error",JOptionPane.ERROR_MESSAGE);
                    System.out.println("Error occured: " + ex.toString());
               }finally{
                    closeCrap();
               }
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null,"Error occured","Error",JOptionPane.ERROR_MESSAGE);
            System.out.println("Error occured: " + ex.toString());
        }
    }
    
    //connect to server
    private void connectToServer(){
        try{
            showMessage("Attempting connection... \n");
            connection = new Socket(InetAddress.getByName(serverIP), 6789);
            showMessage("Connected to: " + connection.getInetAddress().getHostName());
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null,"Error occured","Error",JOptionPane.ERROR_MESSAGE);
            System.out.println("Error occured: " + ex.toString());
        }
    }
    
    //set up streams to send and receive messages
    private void setupStreams(){
        try{
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            showMessage("\n The streams are now setup! \n");
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null,"Error occured","Error",JOptionPane.ERROR_MESSAGE);
            System.out.println("Error occured: " + ex.toString());
        }
    }
    
    //while chatting with server
    private void whileChatting(){
        try{
            ableToType(true);
            do{
                try{
                        message = (String) input.readObject();
                        //AES encryption
                        String encryptedString = message;
                        System.out.println(encryptedString);
                        String decryptedString = aes.decrypt(encryptedString, secretKey) ;
                        System.out.println(decryptedString);  
                        showMessage("\n" + decryptedString);
                   }catch(ClassNotFoundException ex){
                        JOptionPane.showMessageDialog(null,"Unable to send the message","Error",JOptionPane.ERROR_MESSAGE);
                        System.out.println("Error occured: " + ex.toString());
                   }
            }while(!message.equals("SERVER - END"));
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null,"Unable to send the message","Error",JOptionPane.ERROR_MESSAGE);
            System.out.println("Error occured: " + ex.toString());
        }
    }
    
    //close the streams and sockets
    private void closeCrap(){
        try{
            showMessage("\n Closing background tasks down... ");
            ableToType(false);
            try{
                    output.close();
                    input.close();
                    connection.close();
               }catch(IOException ex){
                    JOptionPane.showMessageDialog(null,"Error occured","Error",JOptionPane.ERROR_MESSAGE);
                    System.out.println("Error occured: " + ex.toString());
               }
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null,"Error occured","Error",JOptionPane.ERROR_MESSAGE);
            System.out.println("Error occured: " + ex.toString());
        }
    }
    
    //input validation
    public boolean inputValidation(String m) throws Exception{
        String pattern= "^[a-zA-Z0-9\\t\\n ./<>?;:\"'`!@#$%^&*()\\[\\]{}_+=|\\\\-]+$";
        return m.matches(pattern);
    }
    
    //send messages to server
    private void sendMessage(String message){
        try{
                if(inputValidation(message) == true)
                {
                    System.out.println("CLIENT - " + message);
                    String encryptedString = aes.encrypt("CLIENT - " + message, secretKey) ;
                    System.out.println(encryptedString);
                    output.writeObject(encryptedString);
                    output.flush();
                    showMessage("\nCLIENT - " + message);
                }
                else
                {
                    JOptionPane.showMessageDialog(null,"Unable to send the message","Error",JOptionPane.ERROR_MESSAGE);
                }
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null,"Unable to send the message","Error",JOptionPane.ERROR_MESSAGE);
            System.out.println("Error occured: " + ex.toString());
        }
    }
    
    //change/update chatWindow
    private void showMessage(final String m){
        try{
            SwingUtilities.invokeLater(
                    new Runnable(){
                        public void run(){
                            chatWindow.append(m);
                        }
                    }
            );
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null,"Unable to send the message","Error",JOptionPane.ERROR_MESSAGE);
            System.out.println("Error occured: " + ex.toString());
        }
    }
    
    //gives user permission to type message into the text box
    private void ableToType(final boolean tof){
        try{
            SwingUtilities.invokeLater(
            new Runnable(){
                public void run(){
                    userText.setEditable(tof);
                }
            });
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null,"Error occured","Error",JOptionPane.ERROR_MESSAGE);
            System.out.println("Error occured: " + ex.toString());
        }
    }
}
