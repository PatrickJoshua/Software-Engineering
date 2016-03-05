
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.UIManager;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author macbookpro
 */

class Globalvars
{
    public static String ServerIP;
    public static int port;
    public static String [] upcomingIT = new String[99];
    public static int ITlastIndex = 0;
    public static BufferedWriter itWrite;
    
    public static boolean closing = false;
    public static boolean mac = false;

    public static void writeIT() throws IOException
    {
        itWrite = new BufferedWriter(new FileWriter("queue.txt"));
        for(int i=0; i<ITlastIndex; i++)
        {
            itWrite.append(upcomingIT[i]);
            itWrite.newLine();
        }
        itWrite.close();
    }
}

class ITReceiverThread extends Thread
{
    Socket receiver;
    DataOutputStream outToServer;
    DataInputStream inFromServer;

    @Override
    public void run()
    {
        try
        {
            //recover
            File itQueue = new File("queue.txt");
            if(!itQueue.exists())
                itQueue.createNewFile();
            else
            {
                BufferedReader itReader = new BufferedReader(new FileReader(itQueue));
                String currentLine;
                int i;
                for(i=0;(currentLine = itReader.readLine())!=null; i++)
                    Globalvars.upcomingIT[i] = currentLine;
                itReader.close();
                Globalvars.ITlastIndex = i;
                Display.ITupcoming.setListData(Globalvars.upcomingIT);
            }
        }
        catch (IOException ioe)
        {
            JOptionPane.showMessageDialog(null, "Cannot display recovered upcoming queue (IT). Operations can still continue", "Recovery Failed", JOptionPane.INFORMATION_MESSAGE);
        }

        try
        {
            //start connecting to server
            receiver = new Socket(Globalvars.ServerIP, 6075);
            outToServer = new DataOutputStream(receiver.getOutputStream());     //client to server output stream
            outToServer.writeUTF("dispITreceiver");
            inFromServer = new DataInputStream(receiver.getInputStream());       //server to client input stream
            String received;
            while(true)
            {
                received = inFromServer.readUTF();
                Globalvars.upcomingIT[Globalvars.ITlastIndex] = received;
                Globalvars.ITlastIndex++;
                Display.ITupcoming.setListData(Globalvars.upcomingIT);
                Globalvars.writeIT();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        catch (IOException ioe)
        {
            if(!Globalvars.closing)
                JOptionPane.showMessageDialog(null, "Error retreiving upcoming data from server.\nReal-time upcoming clients cannot be updated\nas of the moment. Basic operations can still continue.", "Retreive Failed", JOptionPane.ERROR_MESSAGE);
            else {
                try {
                    Display.client.close();
                    Display.cutoffDialog.setFocusable(false);
                    Display.cutoffDialog.setLocationRelativeTo(null);
                    Display.cutoffDialog.setVisible(true);
                    Globalvars.closing = true;
                    Thread.sleep(30000);
                    System.exit(0);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ITReceiverThread.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ITReceiverThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}

class Wait4Next extends Thread
{
    @Override
    public void run()
    {
        //proper code
        String received,department = null,studentNumber = null;
        /*String tone = "tone.mp3";
        Media hit = new Media(tone);
        MediaPlayer mediaPlayer = new MediaPlayer(hit);*/
        try
        {
            while(true)
             {
                 received = Display.inFromServer.readUTF();
                 System.out.println(received);
                 if(received.equals("exit"))
                 {
                     Display.client.close();
                     Display.cutoffDialog.setFocusable(false);
                     Display.cutoffDialog.setLocationRelativeTo(null);
                     Display.cutoffDialog.setVisible(true);
                     Globalvars.closing = true;
                     Thread.sleep(30000);
                     System.exit(0);
                 }
                 else if(received.equals("lunch"))
                     Display.ConnectToServerBT.setText("Status: Lunch Break");
                 else if(received.equalsIgnoreCase("resume"))
                     Display.ConnectToServerBT.setText("Status: System is Online");
                 else if(received.equalsIgnoreCase("callITAgain"))
                 {
                     if(!Display.ITserving.getText().contains("None"))
                     {
                         //AudioInputStream ais = AudioSystem.getAudioInputStream(new File("tone.wav"));
                         AudioInputStream ais = AudioSystem.getAudioInputStream(Display.class.getResourceAsStream("resources/tone.wav"));
                         Clip tone = AudioSystem.getClip();
                         tone.open(ais);
                         tone.start();
                         if(Globalvars.mac)
                         {
                             //String tts = "Now serving " + studentNumber.substring(0, 4) + " " + studentNumber.substring(4) + " on " + department.substring(0, 1) + " " + department.substring(1) + " department";
                             Runtime.getRuntime().exec("say " + "Now serving " + studentNumber);
                         }
                         Display.blinkIT();
                     }
                 }
                 else
                 {
                     studentNumber = received;
                     //play notification
                     if(!studentNumber.equalsIgnoreCase("none"))
                     {
                         //AudioInputStream ais = AudioSystem.getAudioInputStream(new File("tone.wav"));
                         AudioInputStream ais = AudioSystem.getAudioInputStream(Display.class.getResourceAsStream("resources/tone.wav"));
                         Clip tone = AudioSystem.getClip();
                         tone.open(ais);
                         tone.start();
                         if(Globalvars.mac)
                         {
                             String tts = "Now serving " + studentNumber.substring(0, 4) + " " + studentNumber.substring(4) + " on " + department.substring(0, 1) + " " + department.substring(1) + " department";
                             Runtime.getRuntime().exec("say " + "Now serving " + studentNumber);
                         }
                     }
                     
                    Display.ITserving.setText(studentNumber);
                    if(Globalvars.ITlastIndex>0)
                    {
                        for(int i=0;i<Globalvars.ITlastIndex;i++)
                           Globalvars.upcomingIT[i] = Globalvars.upcomingIT[i+1];
                        if(Globalvars.ITlastIndex!=0)
                            Globalvars.ITlastIndex--;
                        Display.ITupcoming.setListData(Globalvars.upcomingIT);
                        Globalvars.writeIT();
                        Display.blinkIT();
                    }
                    else
                    {
                        Globalvars.upcomingIT[0] = "";
                        Display.ITupcoming.setListData(Globalvars.upcomingIT);
                        Globalvars.writeIT();
                    }
                 }
                 try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
             }
        } catch (SocketException se) {
            JOptionPane.showMessageDialog(null, "The system has detected that the server is down.\nNow shutting down this unit", "Connection Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        catch (IOException ioe)
        {
              JOptionPane.showMessageDialog(null, "Error occured. Please call the administrator.", "ALERT!", JOptionPane.ERROR_MESSAGE);
              ioe.printStackTrace();
              System.out.println(ioe.getMessage());
        }
        catch (UnsupportedAudioFileException uafe)
        {
              JOptionPane.showMessageDialog(null, "Notification tone cannot be played", "File not found", JOptionPane.ERROR_MESSAGE);
        }
        catch (Exception e)
        {
             e.printStackTrace();
        }
    }
}

public class Display extends javax.swing.JFrame {

    Preferences prefs = Preferences.userRoot();

    public Display() {
        initComponents();
        BufferedImage myPicture;
        try {
            myPicture = ImageIO.read(Display.class.getResourceAsStream("resources/mainlogo.png"));
            //myPicture = ImageIO.read(new File("mainlogo.gif"));
            //picLabel = new javax.swing.JLabel(new ImageIcon(myPicture));
            picLabel.setIcon(new ImageIcon(myPicture));
            picLabel.setText("");
        } catch (IOException ex) {
            Logger.getLogger(Display.class.getName()).log(Level.SEVERE, null, ex);
        }
        ActionListener updateClockAction = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clockLabel.setText(new SimpleDateFormat("EEE, MMM dd, yyyy - hh:mm:ss a").format(new Date())); 
            }
        };
        Timer t = new Timer(1000, updateClockAction);
        t.start();
        if(prefs.get("SERVERIP", "").isEmpty()) {
            ConnectToServerBT.requestFocus();
            progress.setVisible(false);
            connectToServer.setLocationRelativeTo(null);
            connectToServer.setVisible(true);
        } else {
            ConnectNowActionPerformed(new java.awt.event.ActionEvent(this, WIDTH, null));
        }
    }
    
    public static void blinkIT()
    {
        try
        {
            Color green = new Color(0,153,0);
            ITserving.setForeground(Color.GREEN);
            Thread.sleep(1000);
            ITserving.setForeground(green);
            Thread.sleep(1000);
            ITserving.setForeground(Color.GREEN);
            Thread.sleep(1000);
            ITserving.setForeground(green);
            Thread.sleep(1000);
            ITserving.setForeground(Color.GREEN);
            Thread.sleep(1000);
            ITserving.setForeground(green);
        }
        catch (InterruptedException ie)
        {
            JOptionPane.showMessageDialog(null, "Label cannot blink", "Interrupted Exception", JOptionPane.ERROR_MESSAGE);
        }
    }

    boolean connected = false;
    public static Socket client;
    public static DataOutputStream outToServer;
    public static DataInputStream inFromServer;

    Date date = new Date();
    DateFormat dateFormat = new SimpleDateFormat("E, MMMMM d, yyyy");

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        connectToServer = new javax.swing.JDialog();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        ServerIPTextField = new javax.swing.JTextField();
        portTextField = new javax.swing.JTextField();
        ConnectNow = new javax.swing.JButton();
        progress = new javax.swing.JProgressBar();
        Success = new javax.swing.JDialog();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        okButton = new javax.swing.JButton();
        cancel = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        cutoffDialog = new javax.swing.JDialog();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        ConnectToServerBT = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        ITupcoming = new javax.swing.JList();
        ITserving = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        clockLabel = new javax.swing.JLabel();
        picLabel = new javax.swing.JLabel();

        connectToServer.setTitle("Connect to Server");
        connectToServer.setMinimumSize(new java.awt.Dimension(270, 165));
        connectToServer.setModal(true);
        connectToServer.setName("connectToServer"); // NOI18N

        jLabel1.setText("Server IP Address:");

        jLabel2.setText("Port:");

        ServerIPTextField.setToolTipText("IPv4 and IPv6 are accepted");
        ServerIPTextField.setName("serverIP"); // NOI18N
        ServerIPTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ServerIPTextFieldActionPerformed(evt);
            }
        });

        portTextField.setText("6069");
        portTextField.setToolTipText("Leave Blank to use default port (6069)");
        portTextField.setName("port"); // NOI18N
        portTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                portTextFieldActionPerformed(evt);
            }
        });

        ConnectNow.setText("Connect");
        ConnectNow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ConnectNowActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout connectToServerLayout = new javax.swing.GroupLayout(connectToServer.getContentPane());
        connectToServer.getContentPane().setLayout(connectToServerLayout);
        connectToServerLayout.setHorizontalGroup(
            connectToServerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(connectToServerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(connectToServerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(connectToServerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ServerIPTextField)
                    .addComponent(portTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, connectToServerLayout.createSequentialGroup()
                .addContainerGap(96, Short.MAX_VALUE)
                .addGroup(connectToServerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ConnectNow)
                    .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(60, 60, 60))
        );
        connectToServerLayout.setVerticalGroup(
            connectToServerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(connectToServerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(connectToServerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(ServerIPTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(connectToServerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(portTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ConnectNow)
                .addGap(8, 8, 8)
                .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        Success.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        Success.setTitle("You have been added to the queue");
        Success.setAlwaysOnTop(true);
        Success.setMinimumSize(new java.awt.Dimension(400, 200));

        jLabel4.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Thank You!");
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel5.setText("You are the                  person in the queue.");

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancel.setText("Cancel Request");

        jLabel6.setText("Please wait for your student number to flash on the screen.");

        javax.swing.GroupLayout SuccessLayout = new javax.swing.GroupLayout(Success.getContentPane());
        Success.getContentPane().setLayout(SuccessLayout);
        SuccessLayout.setHorizontalGroup(
            SuccessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SuccessLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(okButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancel)
                .addGap(83, 83, 83))
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(SuccessLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(SuccessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE))
                .addContainerGap())
        );
        SuccessLayout.setVerticalGroup(
            SuccessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SuccessLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addGroup(SuccessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(cancel))
                .addContainerGap())
        );

        cutoffDialog.setTitle("End of Day Cut-off");
        cutoffDialog.setAlwaysOnTop(true);
        cutoffDialog.setAutoRequestFocus(false);
        cutoffDialog.setFocusable(false);
        cutoffDialog.setFocusableWindowState(false);
        cutoffDialog.setMinimumSize(new java.awt.Dimension(600, 200));

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("St. Jude College Queuing System");

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Thank you");

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("is about to shut down in a while.");

        javax.swing.GroupLayout cutoffDialogLayout = new javax.swing.GroupLayout(cutoffDialog.getContentPane());
        cutoffDialog.getContentPane().setLayout(cutoffDialogLayout);
        cutoffDialogLayout.setHorizontalGroup(
            cutoffDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cutoffDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cutoffDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 835, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 835, Short.MAX_VALUE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 835, Short.MAX_VALUE))
                .addContainerGap())
        );
        cutoffDialogLayout.setVerticalGroup(
            cutoffDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cutoffDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                .addGap(31, 31, 31))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("St. Jude College Queuing System - " + dateFormat.format(date));

        ConnectToServerBT.setFont(new java.awt.Font("Lucida Grande", 1, 24)); // NOI18N
        ConnectToServerBT.setText("Status: Not Connected");
        ConnectToServerBT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ConnectToServerBTActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Queue", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 18), new java.awt.Color(0, 102, 0))); // NOI18N

        ITupcoming.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        jScrollPane1.setViewportView(ITupcoming);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );

        ITserving.setFont(new java.awt.Font("Lucida Grande", 0, 60)); // NOI18N
        ITserving.setForeground(new java.awt.Color(0, 153, 0));
        ITserving.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ITserving.setText("None");

        jLabel3.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Now Serving");

        clockLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        clockLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        clockLabel.setText("Clock");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ITserving, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
                    .addComponent(clockLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(ITserving, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(clockLabel))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        picLabel.setFont(new java.awt.Font("Impact", 0, 60)); // NOI18N
        picLabel.setForeground(new java.awt.Color(158, 0, 0));
        picLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        picLabel.setText("ST. JUDE COLLEGE");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ConnectToServerBT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(picLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(picLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ConnectToServerBT, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void ConnectToServerBTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ConnectToServerBTActionPerformed
        progress.setVisible(false);
        connectToServer.setLocationRelativeTo(null);
        connectToServer.setVisible(true);
    }//GEN-LAST:event_ConnectToServerBTActionPerformed

    private void ConnectNowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ConnectNowActionPerformed
        if(ServerIPTextField.getText().isEmpty())
            ServerIPTextField.setText(prefs.get("SERVERIP", ""));
        if(portTextField.getText().isEmpty())
            Globalvars.port = 6069;
        else
        {
            try
            {
                Globalvars.port = Integer.parseInt(portTextField.getText());
            }
            catch (NumberFormatException ex) 
            {
                System.err.println("Not a valid port number. Using the default port 6069.");
                Globalvars.port = 6069;
            }
        }
        Globalvars.ServerIP = ServerIPTextField.getText();
        if(Globalvars.ServerIP.isEmpty())
            Globalvars.ServerIP = "localhost";
        progress.setVisible(true);
        progress.setIndeterminate(true);
        //connectingStatus.setText("Connecting to " + Globalvars.ServerIP + " at Globalvars.port " + Globalvars.port);
        try
        {
            client = new Socket(Globalvars.ServerIP, Globalvars.port);
            connected=true;
            outToServer = new DataOutputStream(client.getOutputStream());     //client to server output stream
            outToServer.writeUTF("display");
            inFromServer = new DataInputStream(client.getInputStream());       //server to client input stream
            //connectingStatus.setText("Connected to " + client.getRemoteSocketAddress());
            //statusTxt.setText("Connected to " + client.getRemoteSocketAddress());
            ConnectToServerBT.setText("Status: System is Online");
            connectToServer.hide();
            Thread wait4nextThread = new Wait4Next();
            wait4nextThread.start();
            Thread ITupcomingThread = new ITReceiverThread();
            ITupcomingThread.start();

            if(System.getProperty("os.name").contains("Mac OS X"))
                    Globalvars.mac = true;
            
            prefs.put("SERVERIP", Globalvars.ServerIP);
        }
        catch (Exception uhe)
        {
            progress.setIndeterminate(false);
            JOptionPane.showMessageDialog(rootPane, "Error connecting to " + Globalvars.ServerIP + ". Please ensure that the server is running and the IP Address/port is correct.", "Connection Failed", JOptionPane.ERROR_MESSAGE);
            progress.setVisible(false);
        }
    }//GEN-LAST:event_ConnectNowActionPerformed

    private void portTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_portTextFieldActionPerformed
        ConnectNowActionPerformed(evt);
    }//GEN-LAST:event_portTextFieldActionPerformed

    private void ServerIPTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ServerIPTextFieldActionPerformed
        ConnectNowActionPerformed(evt);
    }//GEN-LAST:event_ServerIPTextFieldActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        Success.dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Display.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Display.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Display.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Display.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Display().setVisible(true);
                }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ConnectNow;
    public static javax.swing.JButton ConnectToServerBT;
    public static javax.swing.JLabel ITserving;
    public static javax.swing.JList ITupcoming;
    private javax.swing.JTextField ServerIPTextField;
    private javax.swing.JDialog Success;
    private javax.swing.JButton cancel;
    public static javax.swing.JLabel clockLabel;
    private javax.swing.JDialog connectToServer;
    public static javax.swing.JDialog cutoffDialog;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton okButton;
    public static javax.swing.JLabel picLabel;
    private javax.swing.JTextField portTextField;
    private javax.swing.JProgressBar progress;
    // End of variables declaration//GEN-END:variables
}
