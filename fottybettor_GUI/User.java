
import java.io.*;
import java.sql.*;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class User {
    public String userName;
    public String password;
    public static Balance balance;
    private static Connection conn;
    private static PreparedStatement statement;

    public User(String userName, String password, float balance){
        this.userName = userName;
        this.password = password;
        this.balance = new Balance(balance);
    }

    //todo DBConn
    public static void madeUserToDBConn() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mysql://localhost:8889/mysql", "root", "root");
    }
    // todo create table
    public static void makeSQLUserTable() throws SQLException {
        String q = "CREATE TABLE USERACCOUNT(username varchar(64), password varchar(64), balance int(8))";
        statement = conn.prepareStatement(q);
        statement.executeUpdate();
    }
    // todo add new user to table
    public static void addUserToUserAccountTable(User user) throws SQLException {
        String q = "INSERT into USERACCOUNT VALUES (?,?,?)";
        String name = user.userName;
        String pass = user.password;
        //float bal = 00;
        float bal = user.balance.loadBalance();
        statement = conn.prepareStatement(q);
        statement.setString(1, name);
        statement.setString(2, pass);
        statement.setFloat(3, bal);
        statement.executeUpdate();
    }

    //todo update balance in userAccout
    public static float updateBalanceInUserAccount(float bal, String s) throws SQLException {
        balance.changeBalance(bal);
        String q = "UPDATE USERACCOUNT SET BALANCE = " + balance.loadBalance() + " WHERE USERNAME = " + "\"" + s + "\"";
        statement  = conn.prepareStatement(q);
        statement.executeUpdate();
        return balance.loadBalance();
    }

    //todo return User object from useraccount SQL
    public static User retrunUserObjectFromSQL(String username) throws SQLException {
        String q = "SELECT * FROM USERACCOUNT WHERE username = '" + username + "'";

        //statement = conn.prepareStatement(q);
        Statement st = conn.createStatement();
        ResultSet rs_userName = st.executeQuery(q);
        String name = null;
        while (rs_userName.next()){
            name = rs_userName.getString("username");
        }

        ResultSet rs_pass = st.executeQuery(q);
        String pass = null;
        while (rs_pass.next()){
            pass = rs_pass.getString("password");
        }

        ResultSet rs_bal = st.executeQuery(q);
        float bal = 0;
        while (rs_bal.next()){
            bal = rs_bal.getFloat("balance");
        }

        User loginUserInfo = new User(name, pass, bal);
        return loginUserInfo;
    }


    //todo check if user already exists
    public static boolean checkUserExists(String user) throws SQLException {
        boolean bool;
        String q = "SELECT * FROM useraccount WHERE username = '" + user + "'";
        statement = conn.prepareStatement(q);
        ResultSet rs = statement.executeQuery(q);
        if(rs.next()){
            bool = TRUE;
            System.out.println("exists");
        } else {
            bool = FALSE;
            System.out.println("dosen't exists");
        }
        return bool;
    }

//    protected float addBalance(float money){
//        balance.changeBalance(money);
//        try {
//            FileWriter writer = new FileWriter("FootyBettor/"+userName+".txt");
//            writer.write("Username:" + userName + ",");
//            writer.write("\nPassword: "+ password + ",");
//            writer.write("\nBalance" + "," + balance.loadBalance() + ",");
//            writer.flush();
//            writer.close();
//
//        }catch(IOException ex) {
//            System.out.println("Error.addBalance");
//            ex.printStackTrace();
//        }
//        return balance.loadBalance();
//    }


}