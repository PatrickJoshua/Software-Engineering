
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
                    JOptionPane.showMessageDialog(null, "Thank you for using UST IICS Queuing System.\nFor unfinished business, please come again tomorrow.", "End of Day Cut-off", JOptionPane.INFORMATION_MESSAGE);
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
        if(prefs.get("SERVERIP", "").isEmpty()) {
            ConnectToServerBT.requestFocus();
            progress.setVisible(false);
            connectToServer.setLocationRelativeTo(null);
            connectToServer.setVisible(true);
        } else {
            ConnectNowActionPerformed(new java.awt.event.ActionEvent(C, port, ServerIP));
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
            else if(position.equalsIgnoreCase("abuse"))
                JOptionPane.showMessageDialog(rootPane, "Student Number " + StudentNumber.getText() + " has already reached its maximum limit per day", "Exceeded Maximum Transaction", JOptionPane.ERROR_MESSAGE);
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
                deptIT.setSelected(false);
                deptIS.setSelected(false);
                deptCS.setSelected(false);
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
        StudentNumber = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        Send = new javax.swing.JButton();
        deptIT = new javax.swing.JRadioButton();
        clear = new javax.swing.JButton();
        deptCS = new javax.swing.JRadioButton();
        deptIS = new javax.swing.JRadioButton();
        jLabel7 = new javax.swing.JLabel();
        concern = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        b7 = new javax.swing.JButton();
        b8 = new javax.swing.JButton();
        b9 = new javax.swing.JButton();
        b4 = new javax.swing.JButton();
        b5 = new javax.swing.JButton();
        b6 = new javax.swing.JButton();
        b1 = new javax.swing.JButton();
        b2 = new javax.swing.JButton();
        b3 = new javax.swing.JButton();
        C = new javax.swing.JButton();
        b0 = new javax.swing.JButton();
        backspace = new javax.swing.JButton();

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
        setTitle("IICS Queuing System Student Input Kiosk - " + dateFormat.format(date));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        ConnectToServerBT.setText("Status: Not Connected");
        ConnectToServerBT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ConnectToServerBTActionPerformed(evt);
            }
        });

        StudentNumber.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        StudentNumber.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        StudentNumber.setEnabled(false);
        StudentNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StudentNumberActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel3.setText("Enter your Student Number");

        Send.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        Send.setText("Enter");
        Send.setEnabled(false);
        Send.setPreferredSize(new java.awt.Dimension(75, 30));
        Send.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SendActionPerformed(evt);
            }
        });

        deptIT.setForeground(new java.awt.Color(0, 153, 0));
        deptIT.setText("Information Technology");
        deptIT.setEnabled(false);
        deptIT.setName("deptIT"); // NOI18N
        deptIT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deptITActionPerformed(evt);
            }
        });

        clear.setText("Clear");
        clear.setEnabled(false);
        clear.setPreferredSize(new java.awt.Dimension(75, 30));
        clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearActionPerformed(evt);
            }
        });

        deptCS.setForeground(new java.awt.Color(0, 51, 204));
        deptCS.setText("Computer Science");
        deptCS.setEnabled(false);
        deptCS.setName("dept"); // NOI18N
        deptCS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deptCSActionPerformed(evt);
            }
        });

        deptIS.setForeground(new java.awt.Color(255, 102, 0));
        deptIS.setText("Information Systems");
        deptIS.setEnabled(false);
        deptIS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deptISActionPerformed(evt);
            }
        });

        jLabel7.setText("Concern:");

        concern.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Course Advising", "Add/Drop Subjects", "Conditional Enrollment", "Application for Waiver", "Returnees", "Request for Overload", "Request for Petition", "Request for Crediting Subjects", "Cross Enrollment", "Department Chair Consultation" }));
        concern.setEnabled(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Input Panel"));
        jPanel1.setLayout(new java.awt.GridLayout(4, 3, 5, 5));

        b7.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        b7.setText("7");
        b7.setEnabled(false);
        b7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b7ActionPerformed(evt);
            }
        });
        jPanel1.add(b7);

        b8.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        b8.setText("8");
        b8.setEnabled(false);
        b8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b8ActionPerformed(evt);
            }
        });
        jPanel1.add(b8);

        b9.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        b9.setText("9");
        b9.setEnabled(false);
        b9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b9ActionPerformed(evt);
            }
        });
        jPanel1.add(b9);

        b4.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        b4.setText("4");
        b4.setEnabled(false);
        b4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b4ActionPerformed(evt);
            }
        });
        jPanel1.add(b4);

        b5.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        b5.setText("5");
        b5.setEnabled(false);
        b5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b5ActionPerformed(evt);
            }
        });
        jPanel1.add(b5);

        b6.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        b6.setText("6");
        b6.setEnabled(false);
        b6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b6ActionPerformed(evt);
            }
        });
        jPanel1.add(b6);

        b1.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        b1.setText("1");
        b1.setEnabled(false);
        b1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b1ActionPerformed(evt);
            }
        });
        jPanel1.add(b1);

        b2.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        b2.setText("2");
        b2.setEnabled(false);
        b2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b2ActionPerformed(evt);
            }
        });
        jPanel1.add(b2);

        b3.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        b3.setText("3");
        b3.setEnabled(false);
        b3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b3ActionPerformed(evt);
            }
        });
        jPanel1.add(b3);

        C.setText("C");
        C.setEnabled(false);
        C.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CActionPerformed(evt);
            }
        });
        jPanel1.add(C);

        b0.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        b0.setText("0");
        b0.setEnabled(false);
        b0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b0ActionPerformed(evt);
            }
        });
        jPanel1.add(b0);

        backspace.setText("<-");
        backspace.setEnabled(false);
        backspace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backspaceActionPerformed(evt);
            }
        });
        jPanel1.add(backspace);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel7)
                                        .addComponent(StudentNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                            .addGap(21, 21, 21)
                                            .addComponent(concern, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(deptIS)
                                        .addComponent(deptCS)
                                        .addComponent(deptIT)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(Send, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(18, 18, 18)
                                            .addComponent(clear, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel3)))
                        .addGap(48, 48, 48)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE))
                    .addComponent(ConnectToServerBT))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(StudentNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(deptIT)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(deptCS)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(deptIS)
                        .addGap(31, 31, 31)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(concern, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(clear, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Send, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                        .addComponent(ConnectToServerBT)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                        .addGap(103, 103, 103))))
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
            C.setEnabled(connected);
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

    private void deptITActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deptITActionPerformed
        deptIT.setSelected(true);
        deptCS.setSelected(false);
        deptIS.setSelected(false);
    }//GEN-LAST:event_deptITActionPerformed

    private void SendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SendActionPerformed
        String sent;
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
            int studnum = Integer.parseInt(student);
            if(StudentNumber.getText().isEmpty())
                JOptionPane.showMessageDialog(rootPane, "Please enter your student number", "Blank Student Number", JOptionPane.INFORMATION_MESSAGE);
            else if(student.length() != 10 || !student.substring(0, 2).equals("20"))
                JOptionPane.showMessageDialog(rootPane, student + " is not a valid student number.", "Invalid Student Number", JOptionPane.WARNING_MESSAGE);
            else
            {
                if(deptIT.isSelected())
                {
                    sent = "IT/" + StudentNumber.getText() + "/" + concern.getSelectedItem();
                    sendToServer(sent);
                }
                else if(deptIS.isSelected())
                {
                    sent = "IS/" + StudentNumber.getText() + "/" + concern.getSelectedItem();
                    sendToServer(sent);
                }
                else if(deptCS.isSelected())
                {
                    sent = "CS/" + StudentNumber.getText() + "/" + concern.getSelectedItem();
                    sendToServer(sent);
                }
                else
                    JOptionPane.showMessageDialog(rootPane, "Please select a department", "No Department Selected", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        catch (NumberFormatException nfe)
        {
                JOptionPane.showMessageDialog(rootPane, student + " is not a valid student number.\nError: Exceeded 10 digits or letter detected.", "Invalid Student Number", JOptionPane.WARNING_MESSAGE);
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
        }
        Success.dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    private void clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearActionPerformed
        StudentNumber.setText("");
        deptIT.setSelected(false);
        deptIS.setSelected(false);
        deptCS.setSelected(false);
        concern.setSelectedIndex(0);
    }//GEN-LAST:event_clearActionPerformed

    private void deptCSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deptCSActionPerformed
        deptCS.setSelected(true);
        deptIS.setSelected(false);
        deptIT.setSelected(false);
    }//GEN-LAST:event_deptCSActionPerformed

    private void deptISActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deptISActionPerformed
        // TODO add your handling code here:
        deptCS.setSelected(false);
        deptIS.setSelected(true);
        deptIT.setSelected(false);
    }//GEN-LAST:event_deptISActionPerformed

    private void b1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b1ActionPerformed
        StudentNumber.setText(StudentNumber.getText() + 1);
    }//GEN-LAST:event_b1ActionPerformed

    private void b2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b2ActionPerformed
        StudentNumber.setText(StudentNumber.getText() + 2);
    }//GEN-LAST:event_b2ActionPerformed

    private void b3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b3ActionPerformed
        StudentNumber.setText(StudentNumber.getText() + 3);
    }//GEN-LAST:event_b3ActionPerformed

    private void b4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b4ActionPerformed
        StudentNumber.setText(StudentNumber.getText() + 4);
    }//GEN-LAST:event_b4ActionPerformed

    private void b5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b5ActionPerformed
        StudentNumber.setText(StudentNumber.getText() + 5);
    }//GEN-LAST:event_b5ActionPerformed

    private void b6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b6ActionPerformed
        StudentNumber.setText(StudentNumber.getText() + 6);
    }//GEN-LAST:event_b6ActionPerformed

    private void b7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b7ActionPerformed
        StudentNumber.setText(StudentNumber.getText() + 7);
    }//GEN-LAST:event_b7ActionPerformed

    private void b8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b8ActionPerformed
        StudentNumber.setText(StudentNumber.getText() + 8);
    }//GEN-LAST:event_b8ActionPerformed

    private void b9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b9ActionPerformed
        StudentNumber.setText(StudentNumber.getText() + 9);
    }//GEN-LAST:event_b9ActionPerformed

    private void b0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b0ActionPerformed
        StudentNumber.setText(StudentNumber.getText() + 0);
    }//GEN-LAST:event_b0ActionPerformed

    private void backspaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backspaceActionPerformed
        StudentNumber.setText(StudentNumber.getText().substring(0, StudentNumber.getText().length()-1));
    }//GEN-LAST:event_backspaceActionPerformed

    private void CActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CActionPerformed
        StudentNumber.setText("");
    }//GEN-LAST:event_CActionPerformed

    private void cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelActionPerformed
        try
        {
            outToServer.writeUTF("cancel");
        }
        catch (IOException ioe)
        {
                JOptionPane.showMessageDialog(rootPane, "Failed to cancel your request", "Connection Error", JOptionPane.WARNING_MESSAGE);
        }
        Success.dispose();
    }//GEN-LAST:event_cancelActionPerformed

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
    private javax.swing.JButton C;
    private javax.swing.JButton ConnectNow;
    public static javax.swing.JButton ConnectToServerBT;
    private javax.swing.JButton Send;
    private javax.swing.JTextField ServerIPTextField;
    private javax.swing.JTextField StudentNumber;
    private javax.swing.JDialog Success;
    private javax.swing.JButton b0;
    private javax.swing.JButton b1;
    private javax.swing.JButton b2;
    private javax.swing.JButton b3;
    private javax.swing.JButton b4;
    private javax.swing.JButton b5;
    private javax.swing.JButton b6;
    private javax.swing.JButton b7;
    private javax.swing.JButton b8;
    private javax.swing.JButton b9;
    private javax.swing.JButton backspace;
    private javax.swing.JButton cancel;
    private javax.swing.JButton clear;
    private javax.swing.JComboBox concern;
    private javax.swing.JDialog connectToServer;
    private javax.swing.JRadioButton deptCS;
    private javax.swing.JRadioButton deptIS;
    private javax.swing.JRadioButton deptIT;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton okButton;
    private javax.swing.JTextField portTextField;
    private javax.swing.JLabel positionLabel;
    private javax.swing.JProgressBar progress;
    // End of variables declaration//GEN-END:variables
}
