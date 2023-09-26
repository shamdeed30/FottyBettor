import java.sql.*;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class BetCalc {

    // takes in the users prediction, their wager, the odds and the
    // actual result and the odds to calculate their return.

    private int prediction ;
    private float wager;
    private int actualResult;
    private float[] odds;
    private static PreparedStatement statement = null;
    private static Connection conn = null;

    BetCalc(int prediction,float wager,int actualResult,float[] odds){
        this.prediction = prediction;
        this.wager = wager;
        this.actualResult = actualResult;
        this.odds = odds;
    }

    //todo DB Conn
    public static void updateGameTrackDBConn() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mysql://localhost:8889/mysql", "root", "root");
    }

    //todo update gametrack table for the user
    public static void updateGameTrack(int id, String username) throws SQLException {
        String q = "INSERT into GAMETRACK VALUES(?,?)";
        statement = conn.prepareStatement(q);
        statement.setInt(1, id);
        statement.setString(2, username);
        statement.executeUpdate();
    }

    //todo check if gameTrack already exists for user
    public static boolean checkGameTrackExists(int id, String name) throws SQLException {
        boolean bool;
        String q = "SELECT * FROM gametrack WHERE fixid = " + id + " AND username = '" + name + "'";
        statement = conn.prepareStatement(q);
        ResultSet rs = statement.executeQuery(q);
        if(rs.next()){
            bool = TRUE;
        } else {
            bool = FALSE;
        }
        return bool;
    }

    public float calcReturn(){

        float userReturn;

        if (prediction == actualResult){

            userReturn = wager * (100 / odds[prediction - 1]);

        }

        else {
            userReturn = -wager;

            //or 0 depending on how we want to calculate the change in balance
            // ie if the wager is automatically taken out of account when the user bets vs not

        }
        return userReturn;
    }

}