import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.sql.*;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class Main {
    private static String foldername;
    private static User user = null;
    private static Connection conn = null;
    private static PreparedStatement statement = null;

    public static void checkAllTables() throws SQLException, ClassNotFoundException, IOException {

        if(!userAccountExists()){
            User.madeUserToDBConn();
            User.makeSQLUserTable();
        } else{}

        if(!teamStatExists()){
            CreateTeamData.makeTeamStatToDBConn();
            CreateTeamData.makeSQLTeamStatTable("src/pastSeason.txt");
            //CreateTeamData.addTeamStatFromFile("src/pastSeason.txt");
            CreateTeamData.addSQLToTeamList();
        } else{}

        if(!gameTrackExists()){
            TeamChoice.makeGameTrackToDBConn();
            TeamChoice.makeSQLGameTrackTable();
        } else {}

        if(!fixtureExists()){
            CreateFixture.makeFixToDBConn();
            CreateFixture.makeSQLFixtureTable("src/pySoccer.txt");
            //CreateFixture.addFixtureFromFile("src/pySoccer.txt");
            CreateFixture.addSQLToFixtureList();
        } else{}

//        File file = null;
//        try {
//            file = new File(path);
//            if (!file.exists()) {
//                return file.mkdirs();
//            }else {
//                return false;
//            }
//        }catch(Exception e) {
//        }return false;
    }

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        //foldername = "FootyBettor";
        GameManager game = new GameManager();
        checkAllTables();


//        if(createfolder(foldername)){
//            System.out.println("Folder created!");
//        } else {
//            System.out.println("Folder exists.");
//        }
        JFrame frame = new JFrame();
        frame.setTitle("FootyBettor");
        frame.setSize(500, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.getContentPane().setBackground(Color.WHITE);
        frame.setLayout(new FlowLayout());
        //frame.add(new JLabel(new ImageIcon("src/Logo.png")));
        JLabel label2 = new JLabel("Welcome to FootyBettor", JLabel.CENTER);
        label2.setFont(new Font("Verdana", Font.BOLD, 20));

        JButton logIn = new JButton("Log in");
        logIn.setBounds(800,800, 800, 800);
        logIn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame testFrame = new JFrame("LOG IN");
                testFrame.setSize(400, 150);
                testFrame.setResizable(false);
                testFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                testFrame.setLocationRelativeTo(null);
                testFrame.setLayout(new FlowLayout());

                JButton enter = new JButton("Enter");
                JLabel text = new JLabel("Enter username:");
                JTextField textBook = new JTextField(20);
                testFrame.add(text);
                testFrame.add(textBook);
                JLabel text2 = new JLabel("Enter password:");
                JTextField textBook2 = new JTextField(20);
                testFrame.add(text2);
                testFrame.add(textBook2);
                testFrame.add(enter);
                enter.addActionListener(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String command = e.getActionCommand();
                        if ("Enter".equals(command)){

                            try {
                                user = game.login(textBook, textBook2);
                            } catch (SQLException ex) {
                                throw new RuntimeException(ex);
                            } catch (ClassNotFoundException ex) {
                                throw new RuntimeException(ex);
                            }
                            if(user.balance.loadBalance() != -10 && user.balance.loadBalance() != -20){
                                System.out.println("Access Granted!");

                                testFrame.dispose();
                                frame.dispose();

                                //////start a new frame to display dashboard
                                JFrame frame = new JFrame();
                                frame.setTitle("Sport Betting Platform");
                                frame.setSize(500, 300);
                                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                                frame.setResizable(false);
                                frame.getContentPane().setBackground(Color.WHITE);
                                // the box layout will be from top to bottom
                                frame.setLayout(new FlowLayout());
                                JLabel label = new JLabel("Welcome to Sport Betting Platform", JLabel.CENTER);
                                // set font style and size
                                label.setFont(new Font("Verdana", Font.BOLD, 20));

                                JButton show_team_stats = new JButton("View Team Stats");
                                show_team_stats.setBounds(800,800, 800, 800);
                                show_team_stats.addActionListener(new AbstractAction() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        try {
                                            game.displayStat();
                                        } catch (IOException ex) {
                                            throw new RuntimeException(ex);
                                        } catch (SQLException ex) {
                                            throw new RuntimeException(ex);
                                        } catch (ClassNotFoundException ex) {
                                            throw new RuntimeException(ex);
                                        }
                                    }
                                });

                                JButton bet1 = new JButton("Start to bet with game number");
                                bet1.setBounds(800,800, 800, 800);
                                User finalUser = user;
                                User finalUser1 = user;
                                bet1.addActionListener(new AbstractAction() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        try {
                                            game.startBet1(user);
                                        } catch (IOException ex) {
                                            throw new RuntimeException(ex);
                                        } catch (SQLException ex) {
                                            throw new RuntimeException(ex);
                                        } catch (ClassNotFoundException ex) {
                                            throw new RuntimeException(ex);
                                        }
                                    }
                                });

                                JButton bet2 = new JButton("Start to bet with team names");
                                bet2.setBounds(800,800, 800, 800);
                                bet2.addActionListener(new AbstractAction() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        try {
                                            game.startBet2(user);
                                        } catch (IOException ex) {
                                            throw new RuntimeException(ex);
                                        } catch (SQLException ex) {
                                            throw new RuntimeException(ex);
                                        } catch (ClassNotFoundException ex) {
                                            throw new RuntimeException(ex);
                                        }
                                    }
                                });


                                JButton check_balance = new JButton("Check Balance/ Deposit");
                                check_balance.setBounds(800,800, 800, 800);
                                check_balance.addActionListener(new AbstractAction() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        game.showBalance(user);
                                    }
                                });

                                JButton exit = new JButton("Exit Program");
                                exit.addActionListener(new AbstractAction() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        //confirm before leave or exit the program
                                        String command = e.getActionCommand();
                                        UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("Verdana", Font.PLAIN, 13)));
                                        UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("Verdana", Font.PLAIN, 13)));
                                        if ("Exit Program".equals(command)) {
                                            Object[] options = { "Confirm", "Cancel" };
                                            int response = JOptionPane.showOptionDialog(frame, "Do you want to exit?", "", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                                            if (response == 0) {
                                                System.exit(0);
                                            }
                                        }
                                    }
                                });
                                frame.add(label);
                                frame.add(check_balance);
                                frame.add(show_team_stats);
                                frame.add(bet1);
                                frame.add(bet2);
                                frame.add(exit);
                                frame.setVisible(true);

                       
                            } else {
                          
                                testFrame.dispose();
                                JFrame newFrame = new JFrame();
                                newFrame.setSize(400, 150);
                                newFrame.setTitle("Result");
                                newFrame.setResizable(false);
                                newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                newFrame.setLayout(new FlowLayout());
                                newFrame.setLocationRelativeTo(null);
                                JLabel label = new JLabel("Invalid username or password. Failed to log in.");
                                newFrame.add(label);
                                newFrame.setVisible(true);
                                JButton back = new JButton("Return");
                                newFrame.add(back);
                                back.addActionListener(new AbstractAction() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        String command2 = e.getActionCommand();
                                        if ("Return".equals(command2)){
                                            newFrame.dispose();
                                        }
                                    }
                                });
                            }
                        }

                    }

                });
                testFrame.setVisible(true);
            }
        });
        JButton signUp = new JButton("Sign Up");
        frame.add(signUp);
        signUp.setBounds(800,800, 800, 800);
        signUp.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame testFrame = new JFrame("SIGN UP");
                testFrame.setSize(400, 150);
                testFrame.setResizable(false);
                testFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                testFrame.setLocationRelativeTo(null);
                testFrame.setLayout(new FlowLayout());

                JButton enter = new JButton("Enter");
                JLabel text = new JLabel("Enter username:");
                JTextField textBook = new JTextField(20);
                testFrame.add(text);
                testFrame.add(textBook);
                JLabel text2 = new JLabel("Enter password:");
                JTextField textBook2 = new JTextField(20);
                testFrame.add(text2);
                testFrame.add(textBook2);
                testFrame.add(enter);
                enter.addActionListener(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String command = e.getActionCommand();
                        if ("Enter".equals(command)){
                            try {
                                game.signUp(textBook, textBook2, testFrame);
                            } catch (SQLException ex) {
                                throw new RuntimeException(ex);
                            } catch (ClassNotFoundException ex) {
                                throw new RuntimeException(ex);
                            }

                        }
                    }
                });
                testFrame.setVisible(true);
            }
        });
        frame.add(label2);
        frame.add(logIn);
        frame.add(signUp);
        frame.setVisible(true);
    }
    private static boolean fixtureExists() throws SQLException, ClassNotFoundException {
        boolean bool;
        makeDBMainConn();
        DatabaseMetaData dbm = conn.getMetaData();
        ResultSet rs = dbm.getTables(null, null, "fixture", null);
        if(rs.next()){
            bool = TRUE;
        } else {bool = FALSE; }
        return bool;
    }
    private static boolean teamStatExists() throws SQLException, ClassNotFoundException {
        boolean b;
        makeDBMainConn();
        DatabaseMetaData dbm = conn.getMetaData();
        //String q = "SELECT IF( EXISTS( SELECT * from INFORMATION_SCHEMA.TABLES where TABLE_NAME = 'teamstat'), 1, 0)";
        //statement = conn.prepareStatement(q);
        ResultSet rs = dbm.getTables(null,null,"teamstat", null);
        if(rs.next()){
            b = TRUE;
        } else { b = FALSE; }
        return b;
    }
    private static boolean userAccountExists() throws SQLException, ClassNotFoundException {
        boolean b;
        makeDBMainConn();
        DatabaseMetaData dbm = conn.getMetaData();
        //String q = "SELECT IF( EXISTS( SELECT * from INFORMATION_SCHEMA.TABLES where TABLE_NAME = 'useraccount'), 1, 0)";
        //statement = conn.prepareStatement(q);
        ResultSet rs = dbm.getTables(null,null,"useraccount", null);
        if(rs.next()){
            b = TRUE;
        } else { b = FALSE; }
        return b;
    }
    private static boolean gameTrackExists() throws SQLException, ClassNotFoundException {
        boolean b;
        makeDBMainConn();
        DatabaseMetaData dbm = conn.getMetaData();
        //String q = "SELECT IF( EXISTS( SELECT * from INFORMATION_SCHEMA.TABLES where TABLE_NAME = 'gametrack'), 1, 0)";
        //statement = conn.prepareStatement(q);
        ResultSet rs = dbm.getTables(null,null,"gametrack", null);
        if(rs.next()){
            b = TRUE;
        } else { b = FALSE; }
        return b;
    }
    private static void makeDBMainConn() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mysql://localhost:8889/mysql", "root", "root");
    }
}
