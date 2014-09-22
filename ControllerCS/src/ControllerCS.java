
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
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

class Globalvars
{
    public static String ServerIP;
    public static int port;
    public static String [] upcomingCS = new String[99];
    public static int lastIndex = 0;
    public static BufferedWriter csWrite;

    public static void writeCS() throws IOException
    {
        csWrite = new BufferedWriter(new FileWriter("CSqueue.txt"));
        for(int i=0; i<lastIndex; i++)
        {
            csWrite.append(upcomingCS[i]);
            csWrite.newLine();
        }
        csWrite.close();
    }
}

class ReceiverThread extends Thread
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
            File csQueue = new File("CSqueue.txt");
            if(!csQueue.exists())
                csQueue.createNewFile();
            else
            {
                BufferedReader csReader = new BufferedReader(new FileReader(csQueue));
                String currentLine;
                int i;
                for(i=0;(currentLine = csReader.readLine())!=null; i++)
                    Globalvars.upcomingCS[i] = currentLine;
                csReader.close();
                Globalvars.lastIndex = i;
                ControllerCS.upcomingList.setListData(Globalvars.upcomingCS);
            }
        }
        catch (IOException ioe)
        {
            JOptionPane.showMessageDialog(null, "Cannot display recovered upcoming queue. Operations can still continue", "Recovery Failed", JOptionPane.INFORMATION_MESSAGE);
        }

        try
        {
            receiver = new Socket(Globalvars.ServerIP, 6068);
            outToServer = new DataOutputStream(receiver.getOutputStream());     //client to server output stream
            outToServer.writeUTF("CSreceiver");
            inFromServer = new DataInputStream(receiver.getInputStream());       //server to client input stream
            String received;
            while(true)
            {
                received = inFromServer.readUTF();
                Globalvars.upcomingCS[Globalvars.lastIndex] = received;
                Globalvars.lastIndex++;
                ControllerCS.upcomingList.setListData(Globalvars.upcomingCS);
                Globalvars.writeCS();
            }
        }
        catch (IOException ioe)
        {
            JOptionPane.showMessageDialog(null, "Error retreiving data from server.\nReal-time upcoming clients cannot be updated\nas of the moment. Basic Operation can still continue.", "Retreive Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}

public class ControllerCS extends javax.swing.JFrame {

    /**
     * Creates new form Input
     */
    public ControllerCS() {
        initComponents();
        ConnectToServerBT.requestFocus();
        progress.setVisible(false);
        connectToServer.setLocationRelativeTo(null);
        connectToServer.setVisible(true);
    }
    
    boolean connected = false;
    Socket client;
    DataOutputStream outToServer;
    DataInputStream inFromServer;

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
        ConnectToServerBT = new javax.swing.JButton();
        next = new javax.swing.JButton();
        nowServing = new javax.swing.JLabel();
        nextNum = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        upcomingList = new javax.swing.JList();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        concernTxt = new javax.swing.JLabel();
        callCSAgain = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        remarks = new javax.swing.JTextField();

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

        portTextField.setText("6067");
        portTextField.setToolTipText("Leave Blank to use default port (6067)");
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
            .addGroup(connectToServerLayout.createSequentialGroup()
                .addGap(82, 82, 82)
                .addComponent(ConnectNow)
                .addContainerGap(124, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, connectToServerLayout.createSequentialGroup()
                .addContainerGap(96, Short.MAX_VALUE)
                .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ConnectNow)
                .addGap(10, 10, 10)
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("C.S. Queue Controller");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        ConnectToServerBT.setText("Connect to Server");
        ConnectToServerBT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ConnectToServerBTActionPerformed(evt);
            }
        });

        next.setText("Next");
        next.setEnabled(false);
        next.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextActionPerformed(evt);
            }
        });

        nowServing.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        nowServing.setForeground(new java.awt.Color(0, 51, 204));
        nowServing.setText("None");

        nextNum.setForeground(new java.awt.Color(102, 102, 102));
        nextNum.setText("Next: ");

        jScrollPane1.setViewportView(upcomingList);

        jLabel3.setText("Upcoming clients:");

        jLabel7.setText("Now Serving:");

        concernTxt.setText("Concern: ");

        callCSAgain.setText("Call Again");
        callCSAgain.setEnabled(false);
        callCSAgain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                callCSAgainActionPerformed(evt);
            }
        });

        jLabel8.setText("Remarks:");

        remarks.setEnabled(false);
        remarks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                remarksActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(nowServing)
                            .addComponent(concernTxt)
                            .addComponent(nextNum)
                            .addComponent(jLabel8)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addComponent(next)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(callCSAgain))
                            .addComponent(remarks, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, 0, 0, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(41, 41, 41))))
                    .addComponent(ConnectToServerBT))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nowServing)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(concernTxt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(remarks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                        .addComponent(nextNum)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(next)
                            .addComponent(callCSAgain))
                        .addGap(18, 18, 18)
                        .addComponent(ConnectToServerBT))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                        .addGap(42, 42, 42))))
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
        if(portTextField.getText().isEmpty())
            Globalvars.port = 6067;
        else
        {
            try
            {
                Globalvars.port = Integer.parseInt(portTextField.getText());
            }
            catch (NumberFormatException ex) 
            {
                System.err.println("Not a valid port number. Using the default port 6067.");
                Globalvars.port = 6067;
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
            outToServer.writeUTF("CS");
            inFromServer = new DataInputStream(client.getInputStream());       //server to client input stream
            //connectingStatus.setText("Connected to " + client.getRemoteSocketAddress());
            //statusTxt.setText("Connected to " + client.getRemoteSocketAddress());
            ConnectToServerBT.setText("Connected to " + client.getRemoteSocketAddress());
            connectToServer.hide();
            next.setEnabled(connected);
            callCSAgain.setEnabled(connected);
            Thread receive = new ReceiverThread();
            receive.start();
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

    private void nextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextActionPerformed
        try
        {
            remarks.setEnabled(true);
            remarks.requestFocus();
            outToServer.writeUTF(remarks.getText());
            nowServing.setText(inFromServer.readUTF());
            nextNum.setText(inFromServer.readUTF());
            concernTxt.setText(inFromServer.readUTF());
            if(nowServing.getText().equalsIgnoreCase("None"))
                JOptionPane.showMessageDialog(rootPane, "No one is currently on queue.", "Empty Queue", JOptionPane.INFORMATION_MESSAGE);
            //removehead
            for(int i=0;i<Globalvars.lastIndex;i++)
                Globalvars.upcomingCS[i] = Globalvars.upcomingCS[i+1];
            if(Globalvars.lastIndex!=0)
                Globalvars.lastIndex--;
            upcomingList.setListData(Globalvars.upcomingCS);
            Globalvars.writeCS();
            remarks.setText("");
            if(nowServing.getText().equalsIgnoreCase("None"))
                remarks.setEnabled(false);
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(rootPane, "There was a problem calling the next in queue. Please try again in a while.", "Error calling the next", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_nextActionPerformed

    private void callCSAgainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_callCSAgainActionPerformed
        try
        {
            outToServer.writeUTF("CallAgain");
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(rootPane, "There was a problem calling agin. Please try again in a while.", "Error calling the next", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_callCSAgainActionPerformed

    private void remarksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_remarksActionPerformed
        nextActionPerformed(evt);
    }//GEN-LAST:event_remarksActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if((JOptionPane.showConfirmDialog(rootPane, "Are you sure you want to exit?","Confirm Exit",JOptionPane.YES_NO_OPTION))==JOptionPane.YES_OPTION)
        {
            System.exit(0);
        }
    }//GEN-LAST:event_formWindowClosing

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
            java.util.logging.Logger.getLogger(ControllerCS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ControllerCS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ControllerCS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ControllerCS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ControllerCS().setVisible(true);
                }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ConnectNow;
    private javax.swing.JButton ConnectToServerBT;
    private javax.swing.JTextField ServerIPTextField;
    private javax.swing.JDialog Success;
    private javax.swing.JButton callCSAgain;
    private javax.swing.JButton cancel;
    private javax.swing.JLabel concernTxt;
    private javax.swing.JDialog connectToServer;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton next;
    private javax.swing.JLabel nextNum;
    private javax.swing.JLabel nowServing;
    private javax.swing.JButton okButton;
    private javax.swing.JTextField portTextField;
    private javax.swing.JProgressBar progress;
    private javax.swing.JTextField remarks;
    public static javax.swing.JList upcomingList;
    // End of variables declaration//GEN-END:variables
}
