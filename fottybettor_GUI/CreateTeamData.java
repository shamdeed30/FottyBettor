import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;
import java.sql.*;
import java.util.ArrayList;

public class CreateTeamData {

    //variables
    private static Connection conn = null;
    private static PreparedStatement statement = null;
    public static ArrayList<TeamStat> teamList = new ArrayList<>();
    //static String file;

    public static void addTeamStatFromFile(String filename) throws IOException {

        //file = filename;
        teamList.clear();
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line = br.readLine();

        while (line != null){
            String[] tLine = line.split(",");
            String tName = tLine[0];
            int gWon = Integer.parseInt(tLine[1]);
            int gLost = Integer.parseInt(tLine[2]);
            int gTied = Integer.parseInt(tLine[3]);;
            TeamStat tStat = new TeamStat(tName, gWon, gLost, gTied);
            addTeamStatToList(tStat);
            line = br.readLine();
        }
        br.close();
    }

    public static void addTeamStatToList(TeamStat teamStat){
        teamList.add(teamStat);
    }

    public static void makeTeamStatToDBConn() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mysql://localhost:8889/mysql", "root", "root");
    }

    public static void makeSQLTeamStatTable(String filename) throws SQLException, IOException {
        String q = "CREATE TABLE TEAMSTAT(team varchar(25), won int(4), lost int(4), tied int(4))";
        statement = conn.prepareStatement(q);
        statement.executeUpdate();

        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line = br.readLine();

        while(line != null){
            String[] tline = line.split(",");
            String team  = tline[0];
            int won = Integer.parseInt(tline[1]);
            int lost = Integer.parseInt(tline[2]);
            int drawn = Integer.parseInt(tline[3]);

            addDataToTeamStat(team, won, lost, drawn);

            line = br.readLine();
        }
        br.close();
    }

    private static void addDataToTeamStat(String team, int w, int l, int d) throws SQLException {
        String q = "INSERT into TEAMSTAT VALUES (?,?,?,?)";
        statement = conn.prepareStatement(q);
        statement.setString(1, team);
        statement.setInt(2, w);
        statement.setInt(3, l);
        statement.setInt(4, d);
        statement.executeUpdate();
    }

    public static void addSQLToTeamList() throws SQLException {
        Statement st = conn.createStatement();
        String q = "SELECT * from TEAMSTAT";

        ResultSet rs_team = st.executeQuery(q);
        String[] team_arr = new String[20];
        int team_i = 0;
        while(rs_team.next()){
            team_arr[team_i] = rs_team.getString("team");
            team_i++;
        }

        ResultSet rs_win = st.executeQuery(q);
        int[] win_arr = new int[20];
        int win_i = 0;
        while (rs_win.next()){
            win_arr[win_i] = rs_win.getInt("won");
            win_i++;
        }

        ResultSet rs_loss = st.executeQuery(q);
        int[] loss_arr = new int[20];
        int loss_i = 0;
        while(rs_loss.next()){
            loss_arr[loss_i] = rs_loss.getInt("lost");
            loss_i++;
        }

        ResultSet rs_draw = st.executeQuery(q);
        int[] draw_arr = new int[20];
        int draw_i = 0;
        while (rs_draw.next()){
            draw_arr[draw_i] = rs_draw.getInt("tied");
            draw_i++;
        }

        String teamName;
        int winNum, lossNum, drawNum;

        teamList.clear();

        for(int i = 0; i < 20; i++){
            teamName = team_arr[i];
            winNum = win_arr[i];
            lossNum = loss_arr[i];
            drawNum = draw_arr[i];

            TeamStat ts = new TeamStat(teamName, winNum, lossNum, drawNum);
            teamList.add(ts);
        }
    }

    public static void showTeamList(){
        for(int i = 0; i < teamList.size(); i++){
            System.out.println(teamList.get(i));
        }
    }

    public static void showTeamStatFromSQL() throws SQLException {
        String query = "SELECT * from TEAMSTAT";
        statement = conn.prepareStatement(query);
        ResultSet set = statement.executeQuery();

        while (set.next()){
            String team = set.getString("team");
            int w = set.getInt("won");
            int l = set.getInt("lost");
            int d = set.getInt("tied");
            System.out.format("%s, %d, %d, %d\n", team, w, l , d);
        }
    }

}