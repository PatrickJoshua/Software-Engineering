
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;
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
class cutoffListener extends Thread
{

    @Override
    public void run()
    {
        try
        {
            Socket client = new Socket(Input.ServerIP, 6074);
            DataInputStream inFromServer = new DataInputStream(client.getInputStream());
            String serverCommand;
            while(true)
            {
                serverCommand = inFromServer.readUTF();
                if(serverCommand.equalsIgnoreCase("exit"))
                {
                    /*boolean connected = false;
                    Input.StudentNumber.setEnabled(connected);
                    concern.setEnabled(connected);
                    Send.setEnabled(connected);
                    clear.setEnabled(connected);
                    deptIT.setEnabled(connected);
                    deptCS.setEnabled(connected);
                    deptIS.setEnabled(connected);
                    backspace.setEnabled(connected);
                    b0.setEnabled(connected);
                    b1.setEnabled(connected);
                    b2.setEnabled(connected);
                    b3.setEnabled(connected);
                    b4.setEnabled(connected);
                    b5.setEnabled(connected);
                    b6.setEnabled(connected);
                    b7.setEnabled(connected);
                    b8.setEnabled(connected);
                    b9.setEnabled(connected);
                    C.setEnabled(connected);*/
                    JOptionPane.showMessageDialog(null, "St. Jude College Queuing System is now shutting down.\nFor unfinished business, please come again tomorrow.", "End of Day Cut-off", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                }
                else if(serverCommand.equalsIgnoreCase("lunch"))
                    Input.ConnectToServerBT.setText("Status: Lunch Break");
                else if(serverCommand.equalsIgnoreCase("resume"))
                    Input.ConnectToServerBT.setText("Status: System is Online");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (SocketException se) {
            JOptionPane.showMessageDialog(null, "The system has detected that the server is down.\nNow shutting down this unit", "Connection Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        } catch (IOException ex) {
            Logger.getLogger(cutoffListener.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }
}

public class Input extends javax.swing.JFrame {

    Preferences prefs = Preferences.userRoot();
    
    public Input() {
        initComponents();
//        BufferedImage myPicture;
//        try {
//            myPicture = ImageIO.read(Input.class.getResourceAsStream("resources/mainlogo.png"));
//            //myPicture = ImageIO.read(new File("mainlogo.gif"));
//            //picLabel = new javax.swing.JLabel(new ImageIcon(myPicture));
//            int width = (myPicture.getWidth()*logoLabel.getHeight())/myPicture.getHeight();
//            Image resized = myPicture.getScaledInstance(width, logoLabel.getHeight(), Image.SCALE_SMOOTH);
//            logoLabel.setIcon(new ImageIcon(resized));
//            logoLabel.setText("");
//        } catch (IOException ex) {
//            Logger.getLogger(Input.class.getName()).log(Level.SEVERE, null, ex);
//        }
        if(prefs.get("SERVERIP", "").isEmpty()) {
            ConnectToServerBT.requestFocus();
            progress.setVisible(false);
            connectToServer.setLocationRelativeTo(null);
            connectToServer.setVisible(true);
        } else {
            ConnectNowActionPerformed(new java.awt.event.ActionEvent(new String(),0, new String()));
        }
    }
    
    public static String ServerIP;
    int port;
    boolean connected = false;
    Socket client;
    DataOutputStream outToServer;
    DataInputStream inFromServer;

    Date date = new Date();
    DateFormat dateFormat = new SimpleDateFormat("E, MMMMM d, yyyy");

    public void sendToServer(String sent)
    {
        String position;
        try
        {
            outToServer.writeUTF(sent);
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(rootPane, "Sorry, your student number cannot be added right now. Please inform the system administrator.", "Sending Failed", JOptionPane.ERROR_MESSAGE);
        }

        try
        {
            position = inFromServer.readUTF();
            if(position.equalsIgnoreCase("duplicate"))
                JOptionPane.showMessageDialog(rootPane, "Student Number " + StudentNumber.getText() + " has already been added to the queue", "Duplicate Detected", JOptionPane.ERROR_MESSAGE);
            //else if(position.equalsIgnoreCase("abuse"))
            //    JOptionPane.showMessageDialog(rootPane, "Student Number " + StudentNumber.getText() + " has already reached its maximum limit per day", "Exceeded Maximum Transaction", JOptionPane.ERROR_MESSAGE);
            else
            {
                if(position.equals("1"))
                    positionLabel.setText(position + "st");
                else if(position.equals("2"))
                    positionLabel.setText(position + "nd");
                else if(position.equals("3"))
                    positionLabel.setText(position + "rd");
                else
                    positionLabel.setText(position + "th");
                
                Success.setLocationRelativeTo(null);
                Success.setVisible(true);
                StudentNumber.setText(null);
                concern.setSelectedIndex(0);
            }
        }
        catch (IOException ioe){
            JOptionPane.showMessageDialog(rootPane, "Can't Retreive your position.\nOperations can still continue.", "Retreive Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

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
        jLabel6 = new javax.swing.JLabel();
        positionLabel = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        cancel = new javax.swing.JButton();
        ConnectToServerBT = new javax.swing.JButton();
        logoLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        Send = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        clear = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        StudentNumber = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        concern = new javax.swing.JComboBox();

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

        portTextField.setText("6066");
        portTextField.setToolTipText("Leave Blank to use default port (6066)");
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
                .addGroup(connectToServerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(connectToServerLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(connectToServerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(connectToServerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ServerIPTextField)
                            .addComponent(portTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)))
                    .addGroup(connectToServerLayout.createSequentialGroup()
                        .addGap(99, 99, 99)
                        .addComponent(ConnectNow))
                    .addGroup(connectToServerLayout.createSequentialGroup()
                        .addGap(78, 78, 78)
                        .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ConnectNow)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        Success.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        Success.setTitle("You have been added to the queue");
        Success.setAlwaysOnTop(true);
        Success.setMinimumSize(new java.awt.Dimension(412, 248));

        jLabel4.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Thank You!");
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel5.setText("You are the");

        okButton.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        jLabel6.setText("Please wait for your student number to flash on the screen.");

        positionLabel.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        positionLabel.setText("nth");

        jLabel8.setText("person in the queue.");

        cancel.setText("Cancel Request");
        cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout SuccessLayout = new javax.swing.GroupLayout(Success.getContentPane());
        Success.getContentPane().setLayout(SuccessLayout);
        SuccessLayout.setHorizontalGroup(
            SuccessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
            .addGroup(SuccessLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(SuccessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(SuccessLayout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(positionLabel)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel8))
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(SuccessLayout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(55, Short.MAX_VALUE))
        );
        SuccessLayout.setVerticalGroup(
            SuccessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SuccessLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(SuccessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(positionLabel)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(SuccessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(okButton, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                    .addComponent(cancel, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE))
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("St. Jude College Queuing System Student Input Kiosk - " + dateFormat.format(date));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        ConnectToServerBT.setText("Status: Not Connected");
        ConnectToServerBT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ConnectToServerBTActionPerformed(evt);
            }
        });

        logoLabel.setFont(new java.awt.Font("Impact", 0, 36)); // NOI18N
        logoLabel.setForeground(new java.awt.Color(158, 0, 0));
        logoLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logoLabel.setText("St. Jude College");
        logoLabel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                logoLabelComponentResized(evt);
            }
        });

        Send.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        Send.setText("Enter");
        Send.setEnabled(false);
        Send.setPreferredSize(new java.awt.Dimension(75, 30));
        Send.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SendActionPerformed(evt);
            }
        });

        jLabel7.setText("Concern:");

        clear.setText("Clear");
        clear.setEnabled(false);
        clear.setPreferredSize(new java.awt.Dimension(75, 30));
        clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel3.setText("Enter your Name");

        StudentNumber.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        StudentNumber.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        StudentNumber.setEnabled(false);
        StudentNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StudentNumberActionPerformed(evt);
            }
        });

        jLabel9.setText("Format: Lastname, F");

        concern.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Request for TRANSCRIPT", "Request for DIPLOMA", "Request for COPY OF GRADES", "Request for CTC/HD", "Request for CAV", "Request for CERTIFIED TRUE COPY", "Request for LICENSURE/INTERNSHIP", "Certificate of GRADUATION", "Certificate of GRADES", "Certificate of ENROLLMENT", "Certificate of G.W.A.", "Certificate of UNITS EARNED", "Certificate of COURSE DESCRIPTION" }));
        concern.setEnabled(false);
        concern.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                concernActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel3)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel9)))
                        .addGap(157, 157, 157))
                    .addComponent(concern, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(StudentNumber)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(Send, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(30, 30, 30)
                        .addComponent(clear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addGap(18, 18, 18)
                .addComponent(StudentNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(concern, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Send, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(clear, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(logoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(ConnectToServerBT)
                                .addGap(0, 169, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(logoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(ConnectToServerBT)
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
            port = 6066;
        else
        {
            try
            {
                port = Integer.parseInt(portTextField.getText());
            }
            catch (NumberFormatException ex) 
            {
                System.err.println("Not a valid port number. Using the default port 6066.");
                port = 6066;
            }
        }
        ServerIP = ServerIPTextField.getText();
        if(ServerIP.isEmpty())
            ServerIP = "localhost";
        progress.setVisible(true);
        progress.setIndeterminate(true);
        //connectingStatus.setText("Connecting to " + ServerIP + " at port " + port);
        try
        {
            client = new Socket(ServerIP, port);
            connected=true;
            inFromServer = new DataInputStream(client.getInputStream());
            outToServer = new DataOutputStream(client.getOutputStream());     //client to server output stream
            outToServer.writeUTF("client");
            //connectingStatus.setText("Connected to " + client.getRemoteSocketAddress());
            //statusTxt.setText("Connected to " + client.getRemoteSocketAddress());
            ConnectToServerBT.setText("Status: System is Online");
            connectToServer.hide();
            StudentNumber.setEnabled(connected);
            concern.setEnabled(connected);
            Send.setEnabled(connected);
            clear.setEnabled(connected);
            Thread cutoffThread = new cutoffListener();
            cutoffThread.start();
            prefs.put("SERVERIP", ServerIP);
        }
        catch (Exception uhe)
        {
            progress.setIndeterminate(false);
            JOptionPane.showMessageDialog(rootPane, "Error connecting to " + ServerIP + ". Please ensure that the server is running and the IP Address/Port is correct.", "Connection Failed", JOptionPane.ERROR_MESSAGE);
             progress.setVisible(false);
        }
    }//GEN-LAST:event_ConnectNowActionPerformed

    private void portTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_portTextFieldActionPerformed
        ConnectNowActionPerformed(evt);
    }//GEN-LAST:event_portTextFieldActionPerformed

    private void ServerIPTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ServerIPTextFieldActionPerformed
        ConnectNowActionPerformed(evt);
    }//GEN-LAST:event_ServerIPTextFieldActionPerformed

    private void StudentNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StudentNumberActionPerformed
        SendActionPerformed(evt);
    }//GEN-LAST:event_StudentNumberActionPerformed

    private void SendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SendActionPerformed
        String sent = null;
/*            if(department.getSelectedItem().equals("Information Technology"))
                sent = "IT/" + StudentNumber.getText();
            else if(department.getSelectedItem().equals("Information Systems"))
                sent = "IS/" + StudentNumber.getText();
            else
                sent = "CS/" + StudentNumber.getText();*/

        //verification
        String student=StudentNumber.getText();
        try
        {
            String[] name = student.trim().split(",");
            if(StudentNumber.getText().isEmpty())
                JOptionPane.showMessageDialog(rootPane, "Please enter your Last Name and your Initials\n"
                        + "Example: Dela Cruz, J. P.", "Blank Name", JOptionPane.INFORMATION_MESSAGE);
            else if(name.length < 2)
                JOptionPane.showMessageDialog(rootPane, "Please enter your name in this format:\n"
                        + "Lastname, F. M.\n\n"
                        + "Example: Dela Cruz, J. P.", "Invalid Number Format", JOptionPane.WARNING_MESSAGE);
            else
            {
                String[] initials = name[1].trim().split("[^a-zA-Z0-9]");
                int countOfInitials = 0;
                String[] finalInitials = new String[2];
                for(String splitted : initials) {
                    if(!splitted.isEmpty()) {
                        finalInitials[countOfInitials++] = splitted;
                        System.out.println("Parsed " + countOfInitials + ": " + splitted);
                    }
                    if(countOfInitials >= 2)
                        break;
                }
                switch(countOfInitials) {
                    //initials[0] = initials[0].replace('.', ' ').trim().substring(0,1).toUpperCase();
                    case 1: sent = name[0].substring(0,1).toUpperCase() + name[0].substring(1).trim()
                            + ", " + finalInitials[0].substring(0,1).toUpperCase()
                            + ".|" + concern.getSelectedItem();
                            sendToServer(sent);
                            System.out.println(sent);
                            break;
                    case 2: sent = name[0].substring(0, 1).toUpperCase() + name[0].substring(1).trim()
                            + ", " + finalInitials[0].substring(0,1).toUpperCase() + ". "
                            + finalInitials[1].substring(0,1).toUpperCase() + ". |" + concern.getSelectedItem();
                            sendToServer(sent);
                            System.out.println(sent);
                            break;
                    default: JOptionPane.showMessageDialog(rootPane, "Error occured on parsing the name", "Parsing Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        catch (Exception e)
        {
                JOptionPane.showMessageDialog(rootPane, "Error occured", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
        }
    }//GEN-LAST:event_SendActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        try
        {
            outToServer.writeUTF("");
        }
        catch (IOException ioe)
        {
                JOptionPane.showMessageDialog(rootPane, "Failed to add your student number", "Connection Error", JOptionPane.WARNING_MESSAGE);
                ioe.printStackTrace();
        }
        Success.dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    private void clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearActionPerformed
        StudentNumber.setText("");
        concern.setSelectedIndex(0);
    }//GEN-LAST:event_clearActionPerformed

    private void cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelActionPerformed
        try
        {
            outToServer.writeUTF("cancel");
        }
        catch (IOException ioe)
        {
                JOptionPane.showMessageDialog(rootPane, "Failed to cancel your request", "Connection Error", JOptionPane.WARNING_MESSAGE);
                ioe.printStackTrace();
        }
        Success.dispose();
    }//GEN-LAST:event_cancelActionPerformed

    private void logoLabelComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_logoLabelComponentResized
        // TODO add your handling code here:
    }//GEN-LAST:event_logoLabelComponentResized

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
//        BufferedImage myPicture;
//        try {
//            myPicture = ImageIO.read(Input.class.getResourceAsStream("resources/mainlogo.png"));
//            //myPicture = ImageIO.read(new File("mainlogo.gif"));
//            //picLabel = new javax.swing.JLabel(new ImageIcon(myPicture));
//            int width = (myPicture.getWidth()*logoLabel.getHeight())/myPicture.getHeight();
//            int height = (myPicture.getHeight() * logoLabel.getWidth()) / myPicture.getWidth();
//            if(logoLabel.getWidth() < width) {
//                width = myPicture.getWidth();
//                height = (myPicture.getHeight() * logoLabel.getWidth()) / myPicture.getWidth();
//            } else if(logoLabel.getHeight() < height) {
//                height = myPicture.getHeight();
//                width = (myPicture.getWidth()*logoLabel.getHeight())/myPicture.getHeight();
//            } else if(logoLabel.getWidth() < myPicture.getWidth() & logoLabel.getHeight() < myPicture.getHeight()) {
//                width = myPicture.getWidth();
//                height = myPicture.getHeight();
//            }
//            Image resized = myPicture.getScaledInstance(width, height, Image.SCALE_SMOOTH);
//            logoLabel.setIcon(new ImageIcon(resized));
//            logoLabel.setText("");
//        } catch (IOException ex) {
//            Logger.getLogger(Input.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }//GEN-LAST:event_formComponentResized

    private void concernActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_concernActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_concernActionPerformed

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
            java.util.logging.Logger.getLogger(Input.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Input.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Input.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Input.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Input().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ConnectNow;
    public static javax.swing.JButton ConnectToServerBT;
    private javax.swing.JButton Send;
    private javax.swing.JTextField ServerIPTextField;
    private javax.swing.JTextField StudentNumber;
    private javax.swing.JDialog Success;
    private javax.swing.JButton cancel;
    private javax.swing.JButton clear;
    private javax.swing.JComboBox concern;
    private javax.swing.JDialog connectToServer;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    public static javax.swing.JLabel logoLabel;
    private javax.swing.JButton okButton;
    private javax.swing.JTextField portTextField;
    private javax.swing.JLabel positionLabel;
    private javax.swing.JProgressBar progress;
    // End of variables declaration//GEN-END:variables
}
