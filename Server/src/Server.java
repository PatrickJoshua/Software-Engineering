
import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.UIManager;
import javax.swing.text.DefaultCaret;

import net.proteanit.sql.DbUtils;

/**
 *
 * @author patrickjoshua
 */
class Global
{
     public static boolean nextCommand=false;
     public static String toDisplay;


     public static boolean newITStudent=false;
     public static String toIT;
     public static boolean newITStudentDisp=false;
     public static int itlength=0;

     public static String [] itQueueListData = new String[99];

     public static String passType;
     public static int max = 5;

     public static boolean mac = false;

     public static String itStudentPending;
     public static String itConcernPending;
     public static boolean pendingIT = false;

     public static int getITlength() throws FileNotFoundException, IOException
     {
         BufferedReader br = new BufferedReader(new FileReader(ServerTest.queue));
         String current;
         for(itlength=0;(current = br.readLine())!=null;itlength++);
         br.close();
         return itlength+1;
     }
}

class cutoff extends Thread
{
    private ServerSocket serverSocket;
    static DataOutputStream out;
    int port;

    public cutoff(int port)
    {
        this.port = port;
    }

    @Override
    public void run()
    {
        try
        {
            serverSocket = new ServerSocket(port);
            Server.logg.append("Waiting for client on port " + serverSocket.getLocalPort() + "...\n");
            Socket server = serverSocket.accept();          //wait for connection
            out = new DataOutputStream(server.getOutputStream());  //server output stream
            Server.logg.append("Kiosk Event Listener at " + server.getRemoteSocketAddress() + " successfully connected.\n");
            Server.eventCon.setText("Online - " + server.getRemoteSocketAddress());
            Server.eventCon.setForeground(Color.green);
        }
        catch (IOException ioe)
        {
            Server.logg.append("IOException thrown by kiosk event listener\n");
        }
    }

    public static void cutoffInput()
    {
        try {
            out.writeUTF("exit");
        } catch (IOException ex) {
            Server.logg.append("Error: Cannot shut down the Input Kiosk.\n");
        }
    }

    public static void lunchBreak()
    {
        try
        {
            out.writeUTF("lunch");
            Global.toDisplay = "lunch";
            Global.nextCommand = true;
        }
        catch (IOException ioe)
        {
            Server.logg.append("Information: Lunch break message cannot be displayed on the input kiosk\n");
        }
    }

    public static void resumeFromLunch()
    {
        try
        {
            out.writeUTF("resume");
            Global.toDisplay = "resume";
            Global.nextCommand = true;
        }
        catch (IOException ioe)
        {
            Server.logg.append("Information: Cannot modify input kiosk status. Operations can still continue.\n");
        }
    }
}

class ServerTest extends Thread
{
   private ServerSocket serverSocket;
   public static File queue = new File("Queue.txt");
   static Date date = new Date();
   static DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd");
   public static File folder = new File("Records");
   public static File itFolder = new File("Records");
   public static File itQueueRecord = new File("Records/" + dateFormat.format(date) + ".csv");
   Connection con = null;

   public ServerTest(int port, Connection conn) throws IOException
   {
      serverSocket = new ServerSocket(port);
      //serverSocket.setSoTimeout(60000);
      con = conn;
   }

   @SuppressWarnings("finally")
public boolean scanForDuplicate(String studNum)
   {
       boolean duplicate = false;
       String txt;
       BufferedReader br = null;
       try
       {
            br = new BufferedReader(new FileReader(queue));
            String[] record;

           while((txt = br.readLine()) != null)
           {
               record = txt.split("\\|");
               if(record[0].trim().equalsIgnoreCase(studNum.trim()))
               {
                   duplicate = true;
                   break;
               }
           }
           br.close();
       }
       catch (FileNotFoundException fnfe)
       {
           Server.logg.append("Duplicate scanning skipped. Queue file not yet created.\n");
       }
       catch (IOException ioe)
       {
           Server.logg.append("IOException at Duplicate Scanning\n");
       }
       catch (Exception e)
       {
           Server.logg.append("Exception at Duplicate Scanning\n");
           e.printStackTrace();
       }
       finally
       {
               return duplicate;
       }
   }

   @SuppressWarnings("finally")
public boolean maxLimiter(String department, String studentNumber)
   {
       boolean abuse = false;
       String txt;
       BufferedReader br = null;
       int count = 0;
       try
       {
            br = new BufferedReader(new FileReader(itQueueRecord));
            String[] record;

           while((txt = br.readLine()) != null)
           {
               record = txt.split("|");
               if(txt.charAt(7) == ',')
               {
                   if(txt.substring(8, 18).equals(studentNumber))
                   {
                       count++;
                       if(count>=Global.max)
                       {
                            abuse = true;
                            break;
                       }
                   }
               }
               else
               {
                   if(txt.substring(9, 19).equals(studentNumber))
                   {
                       count++;
                       if(count>=Global.max)
                       {
                            abuse = true;
                            break;
                       }
                   }
               }
           }
           br.close();
       }
       catch (FileNotFoundException fnfe)
       {
           Server.logg.append("Max limiter skipped. Queue history file not yet created.\n");
       }
       catch (IOException ioe)
       {
           Server.logg.append("IOException at Max Limiter\n");
       }
       catch (Exception e)
       {
           Server.logg.append("Exception at Max Limiter\n");
           e.printStackTrace();
       }
       finally
       {
               return abuse;
       }
   }

   public static void updateITQueueList()
   {
       try
        {
           Global.itQueueListData = new String[99];
           if(!queue.exists())
               queue.createNewFile();
            BufferedReader br = new BufferedReader(new FileReader(queue));
            String currentLine;
            for(int i=0;(currentLine = br.readLine()) != null; i++) {
                Global.itQueueListData[i] = currentLine.split("\\|")[0];
            }
            Server.itUpcomingList.setListData(Global.itQueueListData);
            Server.itQueueList.setListData(Global.itQueueListData);
            br.close();
        }
        catch (IOException ioe)
        {
            Server.logg.append("Failed to update Upcoming Queue List\n");
        }

   }

   @Override
   public void run()
   {
       String identity = "";
      while(true)
      {
         try
         {
            Server.logg.append("Waiting for client on port " + serverSocket.getLocalPort() + "...\n");
            Socket server = serverSocket.accept();          //wait for connection
            DataInputStream in = new DataInputStream(server.getInputStream());  //server input stream
            DataOutputStream out = new DataOutputStream(server.getOutputStream());  //server output stream
            identity = in.readUTF();
            if(identity.equals("client"))
            {
                Server.logg.append("Input Unit at " + server.getRemoteSocketAddress() + " successfully connected.\n");
                Server.inputCon.setText("Online - " + server.getRemoteSocketAddress());
                Server.inputCon.setForeground(Color.green);
                String received;
                while(true)//!(in.readUTF().equals("exit")))
                {
                    received = in.readUTF();
                    if(received.equals("exit"))
                        break;
                    else
                    {
                        boolean canceled = false;
                        String[] receivedArray = received.split("\\|");
                        //String department = receivedArray[0];
                        String studentNumber = receivedArray[0];
                        String concern = receivedArray[1];
                        BufferedWriter bw,recordWriter;
                        boolean duplicate = scanForDuplicate(studentNumber);
                        //boolean abuse = maxLimiter(department,studentNumber);
                        
                        if(duplicate)
                            out.writeUTF("duplicate");
                        //else if(abuse)
                        //    out.writeUTF("abuse");
                        else
                        {
                                bw = new BufferedWriter(new FileWriter(queue, true));
                                //recordWriter = new BufferedWriter(new FileWriter(itQueueRecord,true));
                                out.writeUTF(Global.getITlength() + "");
                                if(in.readUTF().equalsIgnoreCase("cancel"))
                                    canceled = true;
                                else
                                {
                                    Global.toIT = studentNumber;
                                    Global.newITStudent = true;
                                    Global.newITStudentDisp = true;
                                }
                            if(!canceled)
                            {
                                Server.logg.append("New student in queue: " + studentNumber + "\n");
                                bw.append(studentNumber + "|" + concern);
                                bw.newLine();
                                bw.close();
                                updateITQueueList();
                            }
                        }
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                        Server.logg.append("Thread interrupted.\n");
                    }
                }
                //out.writeUTF("Thank you for connecting to " + server.getLocalSocketAddress() + "\nGoodbye!");

                if(queue.exists())     //delete queueDB.txt if it exist
                {
                    boolean deleted = queue.delete();
                    if(!deleted)
                        Server.logg.append("Error deleting current queue database.\n");
                }
                server.close();
                Server.logg.append("Client at " + server.getRemoteSocketAddress() + " disconnected.\n");
            }
            else if(identity.equals("display"))
            {
                Server.displayCon.setText("Online - " + server.getRemoteSocketAddress());
                Server.displayCon.setForeground(Color.green);
                Server.logg.append("Display at " + server.getRemoteSocketAddress() + " successfully connected.\n");
                while(true)
                {
                    if(Global.nextCommand)
                    {
                        out.writeUTF(Global.toDisplay);
                        //if mac, announce name
                        if(Global.mac && (!Global.toDisplay.contains("None")) && (!Global.toDisplay.equalsIgnoreCase("resume")) && (!Global.toDisplay.equalsIgnoreCase("lunch")))
                             Runtime.getRuntime().exec("say " + Global.toDisplay);
                        Global.nextCommand=false;
                        Global.toDisplay=null;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Server.logg.append("Thread interrupted.\n");
                    }
                }
            }
            else if(identity.equals("dispITreceiver"))
            {
                Server.displayitCon.setText("Online - " + server.getRemoteSocketAddress());
                Server.displayitCon.setForeground(Color.green);
                Server.logg.append("Display IT Upcoming Receiver at " + server.getRemoteSocketAddress() + " successfully connected.\n");
                while(true)
                {
                    if(Global.newITStudentDisp)
                    {
                        out.writeUTF(Global.toIT);
                        Global.newITStudentDisp=false;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Server.logg.append("Thread interrupted.\n");
                    }
                }
            }
            else
            {
                 Server.logg.append("Warning: Unidentified client tried to connect.\nConnection has been terminated.\n");
                 server.close();
            }

         }

         catch(SocketTimeoutException s)
         {
            Server.logg.append("Socket timed out!\n");
            break;
         }
         catch(EOFException eof)
         {
             if(identity.equalsIgnoreCase("client"))
                 Server.logg.append("Input Kiosk terminated the connection.\n");
             else if(identity.equalsIgnoreCase("display"))
                 Server.logg.append("Display unit terminated the connection.\n");
             else if(identity.equalsIgnoreCase("IT"))
                 Server.logg.append("Controller terminated the connection.\n");
             else
                 Server.logg.append("A remote client terminated the connection abnormally.\n");
         }
         catch(IOException e)
         {
            e.printStackTrace();
            break;
         }
      }
   }

   public static void nextIT(Connection con)
   {
       try
       {
           Server.remarks.setEnabled(true);
           Server.remarks.requestFocus();
           if(Global.pendingIT)
            {
                Date date = new Date();
                DateFormat df = new SimpleDateFormat("HH:mm:ss");
                BufferedWriter br = new BufferedWriter(new FileWriter(itQueueRecord, true));
                br.write(df.format(date) + ",\"" + Global.itStudentPending + "\",\"" + Global.itConcernPending + "\",\"" + Server.remarks.getText() + "\"");
                br.newLine();
                br.close();
                //write to database
                if(con != null) {
                    try {
                        PreparedStatement ps = con.prepareStatement("insert into HISTORY values (" + Integer.parseInt(Global.itStudentPending) + ",'IT','" + Global.itConcernPending + "','" + Server.remarks.getText() + "',?,?)");
                        ps.setDate(1, new java.sql.Date(new java.util.Date().getTime()));
                        ps.setTime(2, new java.sql.Time(new java.util.Date().getTime()));
                        ps.executeUpdate();
                        ps.close();
                    } catch (SQLException ex) {
                        Server.logg.append("Database error. " + ex.getMessage());
                    }
                }
            }
            BufferedReader br = new BufferedReader(new FileReader(queue));
            String getFromFile, upcoming;
            getFromFile = br.readLine();
            if(getFromFile == null)
            {
                Server.logg.append("Queue is now empty\n");
                Server.itNowServingTxt.setText("None");
                Server.itNowServing.setText("None");
                Server.nextTxt.setText("");
                Server.itConcernTxt.setText("Concern: ");
                //JOptionPane.showMessageDialog(null, "No one is currently on queue", "Queue is Empty", JOptionPane.INFORMATION_MESSAGE);
                Global.toDisplay="None";
                Global.nextCommand=true;
                Global.pendingIT = false;
                br.close();
                br = null;
            }
            else    //begin processing next in queue
            {
                String[] splitted = getFromFile.split("\\|");
                Global.toDisplay = splitted[0];
                Global.nextCommand=true;
                Server.itNowServingTxt.setText(splitted[0]);
                Server.itNowServing.setText(splitted[0]);
                Server.itConcernTxt.setText("Concern: " + splitted[1]);
                Global.itStudentPending = splitted[0];
                //test csv
                //for(String x : csv)
                //    System.out.println("CSVTEST: " + x);
                Server.logg.append("Now Serving " + splitted[0] + "\n");
                String[] splittedUpcoming = new String[2];
                try //sen
                {
                    if((upcoming = br.readLine()) != null) {  //output to display client
                        splittedUpcoming = upcoming.split("\\|");
                            Server.nextTxt.setText("Next: " + splittedUpcoming[0]);
                    }
                    else {
                        Server.nextTxt.setText(""); 
                        splittedUpcoming[0] = "";
                        splittedUpcoming[1] = "";
                    }
                }
                catch (IOException ioe)
                {
                    Server.logg.append("Information: Failed to retreive next client.\nQueue file might have been deleted\n");
                }
                br.close();
                br = null;
                //Server.itConcernTxt.setText("Concern: " + splittedUpcoming[1]);
                Global.itConcernPending = splittedUpcoming[1];
                Global.pendingIT = true;

                //file trim
                File temp = new File("temp.txt");

                BufferedReader reader = new BufferedReader(new FileReader(queue));
                BufferedWriter writer = new BufferedWriter(new FileWriter(temp,true));

                String currentLine;
                boolean first=true;
                while((currentLine = reader.readLine()) != null)
                {
                    if(!first)
                    {
                        writer.append(currentLine);
                        writer.newLine();
                    }
                    else
                        first=false;
                }
                writer.flush();
                writer.close();
                writer = null;
                reader.close();
                reader = null;
                
                System.gc();

                if(!queue.canWrite() || !temp.canWrite())
                {
                    Server.logg.append("Access Denied: Can't modify the Queue Database. Attempting to gain permission...\n");
                    if(!queue.setWritable(true) || !temp.setWritable(true))
                        Server.logg.append("Error gaining write access to the file.\n");
                    else
                        Server.logg.append("Permission granted.\n");
                }
                else
                {
                    if(!queue.delete())
                        Server.logg.append("Cannot delete untrimmed queue file\n");
                    if(queue.exists())
                    {
                        System.gc();
                        if(!queue.delete())
                            Server.logg.append("Cannot delete untrimmed queue file\n");
                    }
                    boolean successful = temp.renameTo(queue);
                    if(!successful)
                        Server.logg.append("Error dequeuing " + getFromFile + "\n");
                }
            }

            Server.remarks.setText("");
            if(Server.itNowServingTxt.getText().equalsIgnoreCase("None"))
                Server.remarks.setEnabled(false);
            updateITQueueList();
       }
       catch (FileNotFoundException fnfe)
       {
            JOptionPane.showMessageDialog(null, "Error calling the next in queue.\nQueue file not found. Check if files aren't deleted.", "Next Command Failed", JOptionPane.ERROR_MESSAGE);
       }
       catch (IOException ioe)
       {
            JOptionPane.showMessageDialog(null, "There's a problem reading or writing on Queue file.", "File Access Error", JOptionPane.ERROR_MESSAGE);
       }
       finally
       {
            System.gc();
       }
   }
}

public class Server extends javax.swing.JFrame {

    /** Creates new form Server */
    public Server() {
            initComponents();
            DefaultCaret caret = (DefaultCaret)logg.getCaret();
            caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
              try
              {
                  if(!ServerTest.folder.exists())
                      if(!ServerTest.folder.mkdir())
                          Server.logg.append("Cannot create folder on the current directory\n");
                  if(!ServerTest.itFolder.exists())
                      if(!ServerTest.itFolder.mkdir())
                          Server.logg.append("Cannot create IT folder on the Records directory\n");
              }
              catch (SecurityException se)
              {
                  Server.logg.append("Security Exception while creating folders\n");
              }

            try
            {
                if(!ServerTest.itQueueRecord.exists())
                {
                    if(!ServerTest.itQueueRecord.createNewFile())
                        Server.logg.append("Cannot create Queue Record");
                    else
                    {
                        BufferedWriter br = new BufferedWriter(new FileWriter(ServerTest.itQueueRecord));
                        br.write("Time,Student Name,Concern,Remarks");
                        br.newLine();
                        br.close();
                    }
                }
            }
            catch (IOException ioe)
            {
                        Server.logg.append("Failed to initialize Queue Records\n");
            }
        //Connect to Databse    
        /*try {
            Server.logg.append("Attempting to connect to the database...\n");
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            con = DriverManager.getConnection("jdbc:derby://localhost:1527/QueueDB", "dbadmin", "dba");
            Server.logg.append("Database connection acquired.\n");
        } catch (SQLException ex) {
            Server.logg.append("Failed to connect to database. Records output will be redirected to excel files.\n");
        } catch (ClassNotFoundException x) {
            Server.logg.append("Error loading java database drivers.\n");
        }*/
    }
    
    public static Connection con = null;
    static FileOutputStream fileOut;
    static ObjectOutputStream pwout;
    static FileInputStream fileIn;
    static ObjectInputStream pwin;


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        passwordDialog = new javax.swing.JDialog();
        passField = new javax.swing.JPasswordField();
        confirm = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        adminMode = new javax.swing.JDialog();
        changePW = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        lunch = new javax.swing.JToggleButton();
        jButton1 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        lock = new javax.swing.JToggleButton();
        changePWDiag = new javax.swing.JDialog();
        jLabel4 = new javax.swing.JLabel();
        oldPW = new javax.swing.JPasswordField();
        jLabel5 = new javax.swing.JLabel();
        newPW = new javax.swing.JPasswordField();
        jLabel6 = new javax.swing.JLabel();
        confirmPW = new javax.swing.JPasswordField();
        jButton2 = new javax.swing.JButton();
        viewQueue = new javax.swing.JDialog();
        jPanel1 = new javax.swing.JPanel();
        csQueueListScroll2 = new javax.swing.JScrollPane();
        itQueueList = new javax.swing.JList();
        itNowServing = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        csQueueListScroll = new javax.swing.JScrollPane();
        csQueueList = new javax.swing.JList();
        csNowServing = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        csQueueListScroll1 = new javax.swing.JScrollPane();
        isQueueList = new javax.swing.JList();
        isNowServing = new javax.swing.JLabel();
        setLimit = new javax.swing.JDialog();
        jLabel8 = new javax.swing.JLabel();
        maxSpinner = new javax.swing.JSpinner();
        setLimitBT = new javax.swing.JButton();
        connectionStatusDiag = new javax.swing.JDialog();
        jPanel7 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        inputCon = new javax.swing.JLabel();
        eventCon = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        displayCon = new javax.swing.JLabel();
        displayitCon = new javax.swing.JLabel();
        about = new javax.swing.JDialog();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        changePortsDialog = new javax.swing.JDialog();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        jLabel35 = new javax.swing.JLabel();
        jSpinner2 = new javax.swing.JSpinner();
        jSpinner3 = new javax.swing.JSpinner();
        jSpinner5 = new javax.swing.JSpinner();
        jSpinner6 = new javax.swing.JSpinner();
        jLabel36 = new javax.swing.JLabel();
        jSpinner7 = new javax.swing.JSpinner();
        jSpinner8 = new javax.swing.JSpinner();
        jSpinner9 = new javax.swing.JSpinner();
        jSpinner10 = new javax.swing.JSpinner();
        jSpinner11 = new javax.swing.JSpinner();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        changePortsBT = new javax.swing.JButton();
        cancelChangePort = new javax.swing.JButton();
        viewDatabase = new javax.swing.JFrame();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        sqlTF = new javax.swing.JTextField();
        sqlBT = new javax.swing.JButton();
        logPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        logg = new javax.swing.JTextArea();
        jLabel21 = new javax.swing.JLabel();
        queueControlPanel = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        itNowServingTxt = new javax.swing.JLabel();
        itConcernTxt = new javax.swing.JLabel();
        nextTxt = new javax.swing.JLabel();
        nextBT = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        itUpcomingList = new javax.swing.JList();
        callAgain = new javax.swing.JButton();
        jLabel44 = new javax.swing.JLabel();
        remarks = new javax.swing.JTextField();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        adminMenu = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem3 = new javax.swing.JMenuItem();
        viewISfile = new javax.swing.JMenu();
        viewITrecord = new javax.swing.JMenuItem();
        allIT = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem5 = new javax.swing.JMenuItem();

        passwordDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        passwordDialog.setTitle("Restricted Access");
        passwordDialog.setMinimumSize(new java.awt.Dimension(257, 160));
        passwordDialog.setModal(true);

        passField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passFieldActionPerformed(evt);
            }
        });

        confirm.setText("Confirm");
        confirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmActionPerformed(evt);
            }
        });

        jLabel1.setText("Enter your administrator password");

        org.jdesktop.layout.GroupLayout passwordDialogLayout = new org.jdesktop.layout.GroupLayout(passwordDialog.getContentPane());
        passwordDialog.getContentPane().setLayout(passwordDialogLayout);
        passwordDialogLayout.setHorizontalGroup(
            passwordDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(passwordDialogLayout.createSequentialGroup()
                .add(passwordDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(passwordDialogLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel1))
                    .add(passwordDialogLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(passField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE))
                    .add(passwordDialogLayout.createSequentialGroup()
                        .add(79, 79, 79)
                        .add(confirm)))
                .addContainerGap())
        );
        passwordDialogLayout.setVerticalGroup(
            passwordDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(passwordDialogLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .add(18, 18, 18)
                .add(passField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(confirm)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        adminMode.setTitle("Administrator Panel");
        adminMode.setMinimumSize(new java.awt.Dimension(304, 280));
        adminMode.setModal(true);

        changePW.setText("Modify Password");
        changePW.setToolTipText("Changes your administrator Password");
        changePW.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changePWActionPerformed(evt);
            }
        });

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Remote Controls"));

        jButton3.setText("Shut Down Display");
        jButton3.setToolTipText("Sends a shut down command to the remote display unit");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        lunch.setText("Activate Lunch Break");
        lunch.setToolTipText("Sends a lunch break message to Input and Display Unit");
        lunch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lunchActionPerformed(evt);
            }
        });

        jButton1.setText("End of Day Cut-Off");
        jButton1.setToolTipText("Sends a shuts down command to the input kiosk");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jButton3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, lunch, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jButton1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(lunch)
                .add(18, 18, 18)
                .add(jButton1)
                .add(18, 18, 18)
                .add(jButton3))
        );

        jButton4.setText("Close");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        lock.setText("Lock");
        lock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lockActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout adminModeLayout = new org.jdesktop.layout.GroupLayout(adminMode.getContentPane());
        adminMode.getContentPane().setLayout(adminModeLayout);
        adminModeLayout.setHorizontalGroup(
            adminModeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(adminModeLayout.createSequentialGroup()
                .addContainerGap()
                .add(adminModeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jButton4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
                    .add(jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(adminModeLayout.createSequentialGroup()
                        .add(lock, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(changePW, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 142, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        adminModeLayout.setVerticalGroup(
            adminModeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(adminModeLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(adminModeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lock)
                    .add(changePW))
                .add(18, 18, 18)
                .add(jButton4)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        changePWDiag.setTitle("Change Admin Password");
        changePWDiag.setMinimumSize(new java.awt.Dimension(186, 290));
        changePWDiag.setModal(true);

        jLabel4.setText("Old Password:");

        jLabel5.setText("New Password:");

        jLabel6.setText("Confirm Password:");

        jButton2.setText("Update");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout changePWDiagLayout = new org.jdesktop.layout.GroupLayout(changePWDiag.getContentPane());
        changePWDiag.getContentPane().setLayout(changePWDiagLayout);
        changePWDiagLayout.setHorizontalGroup(
            changePWDiagLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(changePWDiagLayout.createSequentialGroup()
                .add(changePWDiagLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(changePWDiagLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(changePWDiagLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(oldPW, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                            .add(jLabel4)
                            .add(jLabel5)
                            .add(newPW)
                            .add(jLabel6)
                            .add(confirmPW)))
                    .add(changePWDiagLayout.createSequentialGroup()
                        .add(42, 42, 42)
                        .add(jButton2)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        changePWDiagLayout.setVerticalGroup(
            changePWDiagLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(changePWDiagLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel4)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(oldPW, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(jLabel5)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(newPW, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jLabel6)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(confirmPW, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(jButton2)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        viewQueue.setTitle("IICS Queues");
        viewQueue.setMinimumSize(new java.awt.Dimension(595, 320));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "I.T.", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(51, 153, 0))); // NOI18N

        csQueueListScroll2.setViewportView(itQueueList);

        itNowServing.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        itNowServing.setText("IT");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(csQueueListScroll2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 121, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(itNowServing))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(itNowServing)
                .add(4, 4, 4)
                .add(csQueueListScroll2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 186, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "C.S.", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(255, 102, 0))); // NOI18N

        csQueueListScroll.setViewportView(csQueueList);

        csNowServing.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        csNowServing.setText("CS");

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(csQueueListScroll, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 121, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(csNowServing))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(csNowServing)
                .add(4, 4, 4)
                .add(csQueueListScroll, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 186, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "I.S.", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 0, 153))); // NOI18N

        csQueueListScroll1.setViewportView(isQueueList);

        isNowServing.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        isNowServing.setText("IS");

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(isNowServing)
                    .add(csQueueListScroll1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 121, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .add(isNowServing)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(csQueueListScroll1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 186, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout viewQueueLayout = new org.jdesktop.layout.GroupLayout(viewQueue.getContentPane());
        viewQueue.getContentPane().setLayout(viewQueueLayout);
        viewQueueLayout.setHorizontalGroup(
            viewQueueLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(viewQueueLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        viewQueueLayout.setVerticalGroup(
            viewQueueLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(viewQueueLayout.createSequentialGroup()
                .addContainerGap()
                .add(viewQueueLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel4, 0, 260, Short.MAX_VALUE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setLimit.setTitle("Maximum Limit");
        setLimit.setMinimumSize(new java.awt.Dimension(361, 115));

        jLabel8.setText("Maximum transactions per student per day:");

        setLimitBT.setText("OK");
        setLimitBT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setLimitBTActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout setLimitLayout = new org.jdesktop.layout.GroupLayout(setLimit.getContentPane());
        setLimit.getContentPane().setLayout(setLimitLayout);
        setLimitLayout.setHorizontalGroup(
            setLimitLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(setLimitLayout.createSequentialGroup()
                .add(setLimitLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(setLimitLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel8)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(maxSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 49, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(setLimitLayout.createSequentialGroup()
                        .add(139, 139, 139)
                        .add(setLimitBT)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        setLimitLayout.setVerticalGroup(
            setLimitLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(setLimitLayout.createSequentialGroup()
                .addContainerGap()
                .add(setLimitLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                    .add(maxSpinner))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(setLimitBT)
                .addContainerGap())
        );

        connectionStatusDiag.setTitle("System Status");
        connectionStatusDiag.setMinimumSize(new java.awt.Dimension(400, 320));
        connectionStatusDiag.setResizable(false);

        jLabel11.setText("Display");

        jLabel10.setText("Input Kiosk Event Listener");

        jLabel12.setText("Display IT Receiver");

        inputCon.setForeground(new java.awt.Color(204, 0, 0));
        inputCon.setText("Offline");

        eventCon.setForeground(new java.awt.Color(204, 0, 0));
        eventCon.setText("Offline");

        jLabel9.setText("Input Kiosk");

        displayCon.setForeground(new java.awt.Color(204, 0, 0));
        displayCon.setText("Offline");

        displayitCon.setForeground(new java.awt.Color(204, 0, 0));
        displayitCon.setText("Offline");

        org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel9)
                    .add(jLabel10)
                    .add(jLabel11)
                    .add(jLabel12))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(displayitCon)
                    .add(displayCon)
                    .add(inputCon)
                    .add(eventCon))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel9)
                    .add(inputCon))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel10)
                    .add(eventCon))
                .add(18, 18, 18)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel11)
                    .add(displayCon))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel12)
                    .add(displayitCon))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout connectionStatusDiagLayout = new org.jdesktop.layout.GroupLayout(connectionStatusDiag.getContentPane());
        connectionStatusDiag.getContentPane().setLayout(connectionStatusDiagLayout);
        connectionStatusDiagLayout.setHorizontalGroup(
            connectionStatusDiagLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(connectionStatusDiagLayout.createSequentialGroup()
                .add(20, 20, 20)
                .add(jPanel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(20, 20, 20))
        );
        connectionStatusDiagLayout.setVerticalGroup(
            connectionStatusDiagLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(connectionStatusDiagLayout.createSequentialGroup()
                .add(20, 20, 20)
                .add(jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(10, 10, 10))
        );

        about.setTitle("Credits");
        about.setMinimumSize(new java.awt.Dimension(525, 496));

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("UST IICS Queuing Management System");

        jLabel3.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Jasmine Eve Utzabia / Patrick Joshua Saguinsin");

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Project Manager, System Programmer, GUI Designer");

        jLabel15.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("Maidy Precious Paula Santos");

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("Assistant Project Manager, Documentation");

        jLabel23.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText("Jasmine Eve Urzabia");

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText("GUI Planner/Designer, System Planning, Documentation");

        jLabel25.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setText("Justine Rianne Diza");

        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setText("Documentation Lead, Quality Assurance");

        jLabel27.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setText("Xavier Girard Fajardo");

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setText("Research and Documentation");

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setText("Special Thanks to:");

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setText("Karlo Antonio Espiritu");

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setText("Version 2.5");

        org.jdesktop.layout.GroupLayout aboutLayout = new org.jdesktop.layout.GroupLayout(about.getContentPane());
        about.getContentPane().setLayout(aboutLayout);
        aboutLayout.setHorizontalGroup(
            aboutLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(aboutLayout.createSequentialGroup()
                .addContainerGap()
                .add(aboutLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
                    .add(jLabel31, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
                    .add(jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
                    .add(jLabel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel15, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
                    .add(jLabel16, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
                    .add(jLabel23, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
                    .add(jLabel24, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
                    .add(jLabel25, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel26, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel27, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
                    .add(jLabel28, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel29, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
                    .add(jLabel30, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE))
                .addContainerGap())
        );
        aboutLayout.setVerticalGroup(
            aboutLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(aboutLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel31)
                .add(30, 30, 30)
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel7)
                .add(18, 18, 18)
                .add(jLabel15)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel16)
                .add(18, 18, 18)
                .add(jLabel23)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel24)
                .add(18, 18, 18)
                .add(jLabel25)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel26)
                .add(18, 18, 18)
                .add(jLabel27)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel28)
                .add(35, 35, 35)
                .add(jLabel29)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel30)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        changePortsDialog.setTitle("Customize Server Ports");
        changePortsDialog.setMinimumSize(new java.awt.Dimension(402, 485));

        jLabel32.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel32.setText("Subsystem:");

        jLabel33.setText("Input Kiosk");

        jLabel34.setText("CS Controller");

        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(6066, null, null, 1));

        jLabel35.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel35.setText("Port:");

        jSpinner2.setModel(new javax.swing.SpinnerNumberModel(6067, null, null, 1));

        jSpinner3.setModel(new javax.swing.SpinnerNumberModel(6068, null, null, 1));

        jSpinner5.setModel(new javax.swing.SpinnerNumberModel(6069, null, null, 1));

        jSpinner6.setModel(new javax.swing.SpinnerNumberModel(6070, null, null, 1));

        jLabel36.setText("CS Controller Receiver");

        jSpinner7.setModel(new javax.swing.SpinnerNumberModel(6072, null, null, 1));

        jSpinner8.setModel(new javax.swing.SpinnerNumberModel(6073, null, null, 1));

        jSpinner9.setModel(new javax.swing.SpinnerNumberModel(6071, null, null, 1));

        jSpinner10.setModel(new javax.swing.SpinnerNumberModel(6074, null, null, 1));

        jSpinner11.setModel(new javax.swing.SpinnerNumberModel(6075, null, null, 1));

        jLabel37.setText("Display Main");

        jLabel38.setText("Display CS Receiver");

        jLabel39.setText("IS Controller");

        jLabel40.setText("IS Controller Receiver");

        jLabel41.setText("IS Display Receiver");

        jLabel42.setText("Input Kiosk Event Listener");

        jLabel43.setText("IT Display Receiver");

        changePortsBT.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        changePortsBT.setText("Assign Ports");
        changePortsBT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changePortsBTActionPerformed(evt);
            }
        });

        cancelChangePort.setText("Cancel");
        cancelChangePort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelChangePortActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout changePortsDialogLayout = new org.jdesktop.layout.GroupLayout(changePortsDialog.getContentPane());
        changePortsDialog.getContentPane().setLayout(changePortsDialogLayout);
        changePortsDialogLayout.setHorizontalGroup(
            changePortsDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(changePortsDialogLayout.createSequentialGroup()
                .addContainerGap()
                .add(changePortsDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel33)
                    .add(jLabel32)
                    .add(jLabel34)
                    .add(jLabel36)
                    .add(jLabel37)
                    .add(jLabel38)
                    .add(jLabel39)
                    .add(jLabel40)
                    .add(jLabel41)
                    .add(jLabel42)
                    .add(jLabel43))
                .add(99, 99, 99)
                .add(changePortsDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jSpinner8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 99, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jSpinner9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 99, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jSpinner7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 99, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jSpinner6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 99, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jSpinner5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 99, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jSpinner3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 99, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel35)
                    .add(jSpinner1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 99, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jSpinner2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 99, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jSpinner10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 99, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jSpinner11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 99, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(25, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, changePortsDialogLayout.createSequentialGroup()
                .addContainerGap(90, Short.MAX_VALUE)
                .add(changePortsBT)
                .add(18, 18, 18)
                .add(cancelChangePort)
                .add(85, 85, 85))
        );
        changePortsDialogLayout.setVerticalGroup(
            changePortsDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(changePortsDialogLayout.createSequentialGroup()
                .addContainerGap()
                .add(changePortsDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel32)
                    .add(jLabel35))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(changePortsDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel33)
                    .add(jSpinner1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(changePortsDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel34)
                    .add(jSpinner2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(changePortsDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jSpinner3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel36))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(changePortsDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jSpinner5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel37))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(changePortsDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jSpinner6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel38))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(changePortsDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jSpinner9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel39))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(changePortsDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jSpinner7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel40))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(changePortsDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jSpinner8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel41))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(changePortsDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel42)
                    .add(jSpinner10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(changePortsDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel43)
                    .add(jSpinner11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 27, Short.MAX_VALUE)
                .add(changePortsDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(changePortsBT)
                    .add(cancelChangePort))
                .addContainerGap())
        );

        viewDatabase.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        viewDatabase.setTitle("View Database");

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(table);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("SQL Statement"));

        sqlBT.setText("Execute SQL");
        sqlBT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sqlBTActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .add(sqlTF)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(sqlBT)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(sqlTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(sqlBT))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout viewDatabaseLayout = new org.jdesktop.layout.GroupLayout(viewDatabase.getContentPane());
        viewDatabase.getContentPane().setLayout(viewDatabaseLayout);
        viewDatabaseLayout.setHorizontalGroup(
            viewDatabaseLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(viewDatabaseLayout.createSequentialGroup()
                .addContainerGap()
                .add(viewDatabaseLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                    .add(jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        viewDatabaseLayout.setVerticalGroup(
            viewDatabaseLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(viewDatabaseLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("St. Jude College Queuing System Server");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        logg.setEditable(false);
        logg.setColumns(20);
        logg.setRows(5);
        logg.setFocusable(false);
        logg.setRequestFocusEnabled(false);
        jScrollPane2.setViewportView(logg);

        jLabel21.setText("System Log:");

        org.jdesktop.layout.GroupLayout logPanelLayout = new org.jdesktop.layout.GroupLayout(logPanel);
        logPanel.setLayout(logPanelLayout);
        logPanelLayout.setHorizontalGroup(
            logPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(logPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(logPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
                    .add(jLabel21))
                .addContainerGap())
        );
        logPanelLayout.setVerticalGroup(
            logPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(logPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel21)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
                .addContainerGap())
        );

        queueControlPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Queue Controller"));

        jLabel22.setText("Now Serving:");

        itNowServingTxt.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        itNowServingTxt.setForeground(new java.awt.Color(51, 102, 0));
        itNowServingTxt.setText("None");

        itConcernTxt.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        itConcernTxt.setText("Concern: ");

        nextTxt.setForeground(new java.awt.Color(102, 102, 102));
        nextTxt.setText("Next:");

        nextBT.setText("Next");
        nextBT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextBTActionPerformed(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Queue List"));

        itUpcomingList.setMinimumSize(new java.awt.Dimension(82, 17));
        itUpcomingList.setPreferredSize(new java.awt.Dimension(82, 17));
        jScrollPane3.setViewportView(itUpcomingList);

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 130, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
        );

        callAgain.setText("Call Again");
        callAgain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                callAgainActionPerformed(evt);
            }
        });

        jLabel44.setText("Remarks:");

        remarks.setEnabled(false);
        remarks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                remarksActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout queueControlPanelLayout = new org.jdesktop.layout.GroupLayout(queueControlPanel);
        queueControlPanel.setLayout(queueControlPanelLayout);
        queueControlPanelLayout.setHorizontalGroup(
            queueControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(queueControlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(queueControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(remarks)
                    .add(queueControlPanelLayout.createSequentialGroup()
                        .add(queueControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(queueControlPanelLayout.createSequentialGroup()
                                .add(nextBT)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(callAgain))
                            .add(jLabel22)
                            .add(itNowServingTxt)
                            .add(itConcernTxt)
                            .add(nextTxt)
                            .add(jLabel44))
                        .add(0, 220, Short.MAX_VALUE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        queueControlPanelLayout.setVerticalGroup(
            queueControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(queueControlPanelLayout.createSequentialGroup()
                .add(queueControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(queueControlPanelLayout.createSequentialGroup()
                        .add(jLabel22)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(itNowServingTxt)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(itConcernTxt)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel44)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(remarks, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(nextTxt)
                        .add(18, 18, 18)
                        .add(queueControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(nextBT)
                            .add(callAgain)))
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jLabel45.setFont(new java.awt.Font("Impact", 0, 24)); // NOI18N
        jLabel45.setForeground(new java.awt.Color(158, 0, 0));
        jLabel45.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel45.setText("ST. JUDE COLLEGE");

        jLabel46.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel46.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel46.setText("Queuing System");

        jMenu1.setText("Menu");
        jMenu1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu1ActionPerformed(evt);
            }
        });

        adminMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        adminMenu.setText("Administrator Mode");
        adminMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adminMenuActionPerformed(evt);
            }
        });
        jMenu1.add(adminMenu);
        jMenu1.add(jSeparator1);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem3.setText("Exit");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuBar1.add(jMenu1);

        viewISfile.setText("View");
        viewISfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewISfileActionPerformed(evt);
            }
        });

        viewITrecord.setText("View Generated Queue Record");
        viewITrecord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewITrecordActionPerformed(evt);
            }
        });
        viewISfile.add(viewITrecord);

        allIT.setText("Records Directory");
        allIT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allITActionPerformed(evt);
            }
        });
        viewISfile.add(allIT);
        viewISfile.add(jSeparator2);

        jMenuItem5.setText("Connection Status");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        viewISfile.add(jMenuItem5);

        jMenuBar1.add(viewISfile);

        setJMenuBar(jMenuBar1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(logPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(queueControlPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jLabel45, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jLabel46, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jLabel45)
                .add(1, 1, 1)
                .add(jLabel46, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(queueControlPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(logPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        cutoff.cutoffInput();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void confirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmActionPerformed
        try 
        {
            File file = new File(System.getProperty("user.home") + System.getProperty("file.separator") + ".pw.ser");
            if(!file.exists())
            {
                file.createNewFile();
                Runtime.getRuntime().exec("attrib +H " + System.getProperty("user.home") + System.getProperty("file.separator") + ".pw.ser");
                fileOut = new FileOutputStream(file);
                pwout = new ObjectOutputStream(fileOut);
                String e = "";
                pwout.writeObject(e);
                pwout.close();
                fileOut.close();
            }
            fileIn = new FileInputStream(file);
            pwin = new ObjectInputStream(fileIn);
            String pw = (String) pwin.readObject();
            if (!passField.getText().equals(pw))
            {
                JOptionPane.showMessageDialog(rootPane, "Incorrect Administrator Password", "Access Denied", JOptionPane.ERROR_MESSAGE);
            } 
            else
            {
                passField.setText("");
                passwordDialog.dispose();
                if (Global.passType.equalsIgnoreCase("adminmode"))
                {
                    adminMode.setLocationRelativeTo(null);
                    adminMode.setVisible(true);
                } 
                else if (Global.passType.equalsIgnoreCase("exit"))
                    System.exit(0);
                else if(Global.passType.equalsIgnoreCase("setLimit"))
                {
                    maxSpinner.setValue(Global.max);
                    setLimit.setLocationRelativeTo(null);
                    setLimit.setVisible(true);
                }
                else if(Global.passType.equalsIgnoreCase("unlock"))
                {
                    lock.setText("Lock");
                    boolean enabled = true;
                    lunch.setEnabled(enabled);
                    jButton1.setEnabled(enabled);
                    jButton3.setEnabled(enabled);
                    changePW.setEnabled(enabled);
                    jButton4.setEnabled(enabled);
                    remarks.setEnabled(enabled);
                    nextBT.setEnabled(enabled);
                    callAgain.setEnabled(enabled);
                    viewISfile.setEnabled(enabled);
                    //jMenu3.setEnabled(enabled);
                    //jMenuItem2.setEnabled(enabled);
                    jMenuItem3.setEnabled(enabled);
                    itUpcomingList.setEnabled(enabled);
                }
            }
            pwin.close();
            fileIn.close();
        } catch (IOException ex) {
            Server.logg.append("IOException while retreiving password from file\n");
        } catch (ClassNotFoundException ex) {
            Server.logg.append("Password file cannot be deserialized:\nClassNotFoundException\n");
        }
    }//GEN-LAST:event_confirmActionPerformed

    private void jMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu1ActionPerformed
        
    }//GEN-LAST:event_jMenu1ActionPerformed

    private void adminMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adminMenuActionPerformed
        Global.passType = "adminmode";
        passwordDialog.setTitle("Restricted Access");
        passwordDialog.setLocationRelativeTo(null);
        passwordDialog.setVisible(true);
    }//GEN-LAST:event_adminMenuActionPerformed

    private void passFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passFieldActionPerformed
        confirmActionPerformed(evt);
    }//GEN-LAST:event_passFieldActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        Global.passType = "exit";
        passwordDialog.setTitle("Shutdown Protection");
        passwordDialog.setLocationRelativeTo(null);
        passwordDialog.setVisible(true);
    }//GEN-LAST:event_formWindowClosing

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        Global.passType = "exit";
        passwordDialog.setTitle("Shutdown Protection");
        passwordDialog.setLocationRelativeTo(null);
        passwordDialog.setVisible(true);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void changePWActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changePWActionPerformed
        changePWDiag.setLocationRelativeTo(null);
        changePWDiag.setVisible(true);
    }//GEN-LAST:event_changePWActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // old passwords match
        try
        {
            fileIn = new FileInputStream(System.getProperty("user.home") + System.getProperty("file.separator") + ".pw.ser");
            pwin = new ObjectInputStream(fileIn);
            String pw = (String) pwin.readObject();
            pwin.close();
            fileIn.close();
            if (!oldPW.getText().equals(pw))
                JOptionPane.showMessageDialog(rootPane, "Incorrect Old Administrator Password", "Old Password doesn't match", JOptionPane.ERROR_MESSAGE);
            else if(!newPW.getText().equals(confirmPW.getText()))
                JOptionPane.showMessageDialog(rootPane, "The new password you've entered doesn't match", "New Password doesn't match", JOptionPane.ERROR_MESSAGE);
            else
            {
                File pwFile = new File(System.getProperty("user.home") + System.getProperty("file.separator") + ".pw.ser");
                pwFile.delete();
//                pwFile.setReadable(true);
//                pwFile.setWritable(true);
//                pwFile.setExecutable(true);
//                Server.logg.append("" + pwFile.canRead() + pwFile.canWrite() + pwFile.canExecute());
//                fileOut = new FileOutputStream(pwFile);
                pwout = new ObjectOutputStream(new FileOutputStream(System.getProperty("user.home") + System.getProperty("file.separator") + ".pw.ser"));
                String e = newPW.getText();
                pwout.writeObject(e);
                pwout.close();
                //fileOut.close();
                if(System.getProperty("os.name").contains("Windows"))
                    Runtime.getRuntime().exec("attrib +H " + System.getProperty("user.home") + System.getProperty("file.separator") + ".pw.ser");
                changePWDiag.dispose();
            }
        } catch (IOException ex) {
            Server.logg.append("Error: Password file not found\n");
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            Server.logg.append("Error: Password file cannot be deserialized:\nClassNotFoundException\n");
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        Global.toDisplay = "exit";
        Global.nextCommand = true;
    }//GEN-LAST:event_jButton3ActionPerformed

    private void lunchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lunchActionPerformed
        if(lunch.isSelected())
        {
            cutoff.lunchBreak();
            lunch.setText("Deactivate Lunch Break");
        }
        else
        {
            cutoff.resumeFromLunch();
            lunch.setText("Activate Lunch Break");
        }
    }//GEN-LAST:event_lunchActionPerformed

    private void setLimitBTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setLimitBTActionPerformed
        Global.max = Integer.parseInt(maxSpinner.getValue().toString());
        setLimit.hide();
    }//GEN-LAST:event_setLimitBTActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        connectionStatusDiag.setLocationRelativeTo(null);
        connectionStatusDiag.setVisible(true);
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void nextBTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextBTActionPerformed
        ServerTest.nextIT(con);
    }//GEN-LAST:event_nextBTActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        adminMode.hide();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void cancelChangePortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelChangePortActionPerformed
        changePortsDialog.dispose();
    }//GEN-LAST:event_cancelChangePortActionPerformed

    private void callAgainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_callAgainActionPerformed
        Global.toDisplay="callITAgain";
        Global.nextCommand=true;
    }//GEN-LAST:event_callAgainActionPerformed

    private void changePortsBTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changePortsBTActionPerformed
        
    }//GEN-LAST:event_changePortsBTActionPerformed

    private void viewISfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewISfileActionPerformed
        
    }//GEN-LAST:event_viewISfileActionPerformed

    private void viewITrecordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewITrecordActionPerformed
        try
        {
            if(Global.mac)
                Runtime.getRuntime().exec("open Records/" + ServerTest.itQueueRecord.getName());
            else if(System.getProperty("os.name").contains("Windows"))
            {
    //            Runtime.getRuntime().exec("start excel Records\\IT\\" + ServerTest.itQueueRecord.getName());
                //Runtime.getRuntime().exec("start QueueIT.txt");
                Runtime runtime = Runtime.getRuntime();
                Process process = runtime.exec("cmd /c start Records\\" + ServerTest.itQueueRecord.getName());
            }
            else if(System.getProperty("os.name").contains("Linux"))
                Runtime.getRuntime().exec("gnome-open Records/" + ServerTest.itQueueRecord.getName());
            else
                JOptionPane.showMessageDialog(rootPane, "You can open the file at Records\\" + ServerTest.itQueueRecord.getName(), "Unsupported Operating System", JOptionPane.ERROR_MESSAGE);
        }
        catch (IOException ioe)
        {
            JOptionPane.showMessageDialog(rootPane, "Cannot find " + ServerTest.itQueueRecord.getName() + "\n or Microsoft Excel isn't installed in your system", "File not found/Failed to launch Microsoft Excel", JOptionPane.ERROR_MESSAGE);
            ioe.printStackTrace();
        }
    }//GEN-LAST:event_viewITrecordActionPerformed

    private void lockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lockActionPerformed
        if(lock.isSelected())
        {
            boolean enabled = false;
            lock.setText("Locked");
            lunch.setEnabled(enabled);
            jButton1.setEnabled(enabled);
            jButton3.setEnabled(enabled);
            changePW.setEnabled(enabled);
            jButton4.setEnabled(enabled);
            remarks.setEnabled(enabled);
            nextBT.setEnabled(enabled);
            callAgain.setEnabled(enabled);
            jMenuBar1.setEnabled(enabled);
            viewISfile.setEnabled(enabled);
            //jMenu3.setEnabled(enabled);
            //jMenuItem2.setEnabled(enabled);
            jMenuItem3.setEnabled(enabled);
            itUpcomingList.setEnabled(enabled);

        }
        else
        {
            Global.passType = "unlock";
            passwordDialog.setTitle("Confirm Password");
            passwordDialog.setLocationRelativeTo(null);
            passwordDialog.setVisible(true);
        }
    }//GEN-LAST:event_lockActionPerformed

    private void remarksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_remarksActionPerformed
        nextBTActionPerformed(evt);
    }//GEN-LAST:event_remarksActionPerformed

    private void allITActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allITActionPerformed
        try
        {
            if(Global.mac)
                Runtime.getRuntime().exec("open Records/");
            else if(System.getProperty("os.name").contains("Windows"))
                Runtime.getRuntime().exec("cmd /c explorer.exe Records\\");
            else if(System.getProperty("os.name").contains("Linux"))
                Runtime.getRuntime().exec("gnome-open Records/");
            else
                JOptionPane.showMessageDialog(rootPane, "You can open the folder located at Records/IT", "Unsupported Operating System", JOptionPane.ERROR_MESSAGE);
        }
        catch (IOException ioe)
        {
            JOptionPane.showMessageDialog(rootPane, "Failed to launch file browser", "Launching Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_allITActionPerformed

    private void sqlBTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sqlBTActionPerformed
        executeSQL("HISTORY");
    }//GEN-LAST:event_sqlBTActionPerformed

    void executeSQL(String DBtable) {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs;
            if(sqlTF.getText().startsWith("select"))
                rs = stmt.executeQuery(sqlTF.getText());
            else {
                JOptionPane.showMessageDialog(null, stmt.executeUpdate(sqlTF.getText()) + " row(s) affected", "SQL Result", JOptionPane.INFORMATION_MESSAGE);
                rs = stmt.executeQuery("select * from " + DBtable);
            }
            table.setModel(DbUtils.resultSetToTableModel(rs));
            rs.close();
            stmt.close();
        } catch (SQLException sqle) {
            JOptionPane.showMessageDialog(null, "Error executing statement: " + sqlTF.getText() + "\n" + sqle.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
    * @param args the command line arguments
    */

    public static void initJSpinners()
    {
        jSpinner1.setEditor(new JSpinner.NumberEditor(jSpinner1,"#"));
        jSpinner2.setEditor(new JSpinner.NumberEditor(jSpinner2,"#"));
        jSpinner3.setEditor(new JSpinner.NumberEditor(jSpinner3,"#"));
        jSpinner5.setEditor(new JSpinner.NumberEditor(jSpinner5,"#"));
        jSpinner6.setEditor(new JSpinner.NumberEditor(jSpinner6,"#"));
        jSpinner7.setEditor(new JSpinner.NumberEditor(jSpinner7,"#"));
        jSpinner8.setEditor(new JSpinner.NumberEditor(jSpinner8,"#"));
        jSpinner9.setEditor(new JSpinner.NumberEditor(jSpinner9,"#"));
        jSpinner10.setEditor(new JSpinner.NumberEditor(jSpinner10,"#"));
        jSpinner11.setEditor(new JSpinner.NumberEditor(jSpinner11,"#"));
        /*jSpinner1.setValue("6066");
        jSpinner2.setValue("6067");
        jSpinner3.setValue("6068");
        jSpinner5.setValue("6069");
        jSpinner6.setValue("6070");
        jSpinner7.setValue("6071");
        jSpinner8.setValue("6072");
        jSpinner9.setValue("6073");
        jSpinner10.setValue("6074");
        jSpinner11.setValue("6075");*/
    }

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
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Server().setVisible(true);
                if(System.getProperty("os.name").contains("Mac OS X"))
                    Global.mac = true;
                initJSpinners();
                logg.append("Welcome to St. Jude College Queuing System!\n");
                  int port1, port2, port3, port4, port5, port6, port7, port8, port9, port10;
                      port1=Integer.parseInt(jSpinner1.getValue().toString());   //input
                      port2=Integer.parseInt(jSpinner2.getValue().toString());   //Controller CS
                      port3=Integer.parseInt(jSpinner3.getValue().toString());   //Controller CS Upcoming
                      port4=Integer.parseInt(jSpinner5.getValue().toString());   //Display
                      port5=Integer.parseInt(jSpinner6.getValue().toString());   //CS Upcoming Display
                      port6=Integer.parseInt(jSpinner7.getValue().toString());   //Controller IS
                      port7=Integer.parseInt(jSpinner8.getValue().toString());   //Controller IS Upcoming
                      port8=Integer.parseInt(jSpinner9.getValue().toString());   //IS Display Upcoming
                      port9=Integer.parseInt(jSpinner10.getValue().toString());   //cutoff listener
                      port10=Integer.parseInt(jSpinner11.getValue().toString());  //IT Display Upcoming
                      try
                      {
                      logg.append("This server's IP Addresses:\n");
                     String ip;
                     Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                     while (interfaces.hasMoreElements()) {
                        NetworkInterface iface = interfaces.nextElement();
                        // filters out 127.0.0.1 and inactive interfaces
                        if (iface.isLoopback() || !iface.isUp())
                            continue;

                        Enumeration<InetAddress> addresses = iface.getInetAddresses();
                        if(!addresses.hasMoreElements())
                            logg.append("No Network Interface has been detected.\nUse localhost or 127.0.0.1");
                        else
                            while(addresses.hasMoreElements()) {
                                InetAddress addr = addresses.nextElement();
                                ip = addr.getHostAddress();
                                //if(!ip.substring(0, 3).equals("192"))
                                //    continue;
                                //Server.logg.append("This server's IP Addresses: " + ip + " at " + iface.getDisplayName() + " interface");
                                logg.append(" => " + ip + " at " + iface.getDisplayName() + " interface\n");                            logg.append(" => " + ip + " at " + iface.getDisplayName() + " interface\n");
                                //System.out.println(" => " + ip + " at " + iface.getDisplayName() + " interface\n");
                            }
                    }
                      logg.append("Connect the clients through the IP Addresses\nlisted above and the port numbers below.\n");
                     Thread inputThread = new ServerTest(port1,null);
                     inputThread.start();
                     Thread displayThread = new ServerTest(port4,null);
                     displayThread.start();
                     Thread cutoffListenerThread = new cutoff(port9);
                     cutoffListenerThread.start();

                     Thread displayITupcomingThread = new ServerTest(port10,null);
                     displayITupcomingThread.start();

                     ServerTest.updateITQueueList();
                  } catch (BindException be) {
                	  logg.append("ERROR: Ports are already in use. Check if another server instance is running.\n");
                  }
                  catch(Exception ee)
                  {
                      logg.append("ERROR: Unable to detect current IP Address.\n");
                      ee.printStackTrace();
                  }
            }
        });
    }

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog about;
    private javax.swing.JMenuItem adminMenu;
    private javax.swing.JDialog adminMode;
    private javax.swing.JMenuItem allIT;
    private javax.swing.JButton callAgain;
    private javax.swing.JButton cancelChangePort;
    private javax.swing.JButton changePW;
    private javax.swing.JDialog changePWDiag;
    private javax.swing.JButton changePortsBT;
    private javax.swing.JDialog changePortsDialog;
    private javax.swing.JButton confirm;
    private javax.swing.JPasswordField confirmPW;
    private javax.swing.JDialog connectionStatusDiag;
    public static javax.swing.JLabel csNowServing;
    private javax.swing.JList csQueueList;
    public static javax.swing.JScrollPane csQueueListScroll;
    public static javax.swing.JScrollPane csQueueListScroll1;
    public static javax.swing.JScrollPane csQueueListScroll2;
    public static javax.swing.JLabel displayCon;
    public static javax.swing.JLabel displayitCon;
    public static javax.swing.JLabel eventCon;
    public static javax.swing.JLabel inputCon;
    public static javax.swing.JLabel isNowServing;
    private javax.swing.JList isQueueList;
    public static javax.swing.JLabel itConcernTxt;
    public static javax.swing.JLabel itNowServing;
    public static javax.swing.JLabel itNowServingTxt;
    public static javax.swing.JList itQueueList;
    public static javax.swing.JList itUpcomingList;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    public static javax.swing.JLabel jLabel32;
    public static javax.swing.JLabel jLabel33;
    public static javax.swing.JLabel jLabel34;
    public static javax.swing.JLabel jLabel35;
    public static javax.swing.JLabel jLabel36;
    public static javax.swing.JLabel jLabel37;
    public static javax.swing.JLabel jLabel38;
    public static javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    public static javax.swing.JLabel jLabel40;
    public static javax.swing.JLabel jLabel41;
    public static javax.swing.JLabel jLabel42;
    public static javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    public static javax.swing.JSpinner jSpinner1;
    public static javax.swing.JSpinner jSpinner10;
    public static javax.swing.JSpinner jSpinner11;
    public static javax.swing.JSpinner jSpinner2;
    public static javax.swing.JSpinner jSpinner3;
    public static javax.swing.JSpinner jSpinner5;
    public static javax.swing.JSpinner jSpinner6;
    public static javax.swing.JSpinner jSpinner7;
    public static javax.swing.JSpinner jSpinner8;
    public static javax.swing.JSpinner jSpinner9;
    private javax.swing.JToggleButton lock;
    private javax.swing.JPanel logPanel;
    public static javax.swing.JTextArea logg;
    private javax.swing.JToggleButton lunch;
    private javax.swing.JSpinner maxSpinner;
    private javax.swing.JPasswordField newPW;
    private javax.swing.JButton nextBT;
    public static javax.swing.JLabel nextTxt;
    private javax.swing.JPasswordField oldPW;
    private javax.swing.JPasswordField passField;
    public static javax.swing.JDialog passwordDialog;
    private javax.swing.JPanel queueControlPanel;
    public static javax.swing.JTextField remarks;
    private javax.swing.JDialog setLimit;
    private javax.swing.JButton setLimitBT;
    private javax.swing.JButton sqlBT;
    private javax.swing.JTextField sqlTF;
    public javax.swing.JTable table;
    public static javax.swing.JFrame viewDatabase;
    private javax.swing.JMenu viewISfile;
    private javax.swing.JMenuItem viewITrecord;
    private javax.swing.JDialog viewQueue;
    // End of variables declaration//GEN-END:variables

}
