import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TeamChoice {
    protected TeamStat t1;
    protected TeamStat t2;
    private static Connection conn = null;
    private static PreparedStatement statement = null;

    public TeamChoice(int team1, int team2){
        this.t1 = CreateTeamData.teamList.get(team1);
        this.t2 = CreateTeamData.teamList.get(team2);
    }

    //todo make gametrack DB Conn
    public static void makeGameTrackToDBConn() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mysql://localhost:8889/mysql", "root", "root");
    }

    //todo make GameTrack table
    public static void makeSQLGameTrackTable() throws SQLException {
        String q = "CREATE TABLE GAMETRACK(fixid int(4), username varchar(25))";
        statement = conn.prepareStatement(q);
        statement.executeUpdate();
    }

    //public static void addFix

}
