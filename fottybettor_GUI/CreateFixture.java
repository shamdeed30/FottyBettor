import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class CreateFixture {

    private static Connection conn = null;
    private static PreparedStatement statement = null;
    public static ArrayList<Fixture> fixtureList = new ArrayList<>();
    //static String fixtureFile;

    public static void addFixtureFromFile(String filename) throws IOException {
        //fixtureFile = filename;
        fixtureList.clear();
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String Line = br.readLine();

        while(Line != null){
            String[] tLine = Line.split(",");
            //int id = Integer.parseInt(tLine[0]);
            String hName = tLine[2];
            String aName = tLine[3];
            String win = tLine[4];

            Fixture tFix = new Fixture(hName, aName, win);
            addFixtureToList(tFix);
            Line = br.readLine();
        }
        br.close();
    }
    public static void addFixtureToList(Fixture fix){
        fixtureList.add(fix);
    }

    public static void makeFixToDBConn() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mysql://localhost:8889/mysql", "root", "root");
    }

    public static void makeSQLFixtureTable(String filename) throws SQLException, IOException {
        String query = "CREATE TABLE FIXTURE(fixid int(4), hometeam varchar(25), awayteam varchar(25), result varchar(8))";
        statement = conn.prepareStatement(query);
        statement.executeUpdate();
        //System.out.println("table made");

        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line = br.readLine();

        while(line != null){
            String[] tline = line.split(",");
            int fixtureID = Integer.parseInt(tline[0]);
            String hName = tline[2];
            String aName = tline[3];
            String win = tline[4];

            addDataToFixture(fixtureID, hName, aName, win);

            line = br.readLine();
        }
        br.close();
        //System.out.println("success: txt to sql");
    }

    private static void addDataToFixture(int id, String ht, String at, String win) throws SQLException {
        String insertionQuery = "INSERT into FIXTURE VALUES (?,?,?,?)";
        statement = conn.prepareStatement(insertionQuery);
        statement.setInt(1, id);
        statement.setString(2, ht);
        statement.setString(3, at);
        statement.setString(4, win);
        statement.executeUpdate();
    }

    public static void addSQLToFixtureList() throws SQLException {
        Statement st = conn.createStatement();
        String q = "SELECT * from fixture";


        ResultSet rs_home = st.executeQuery(q);
        String[] homeTeam = new String[380];
        int home_i = 0;
        while(rs_home.next()){
            homeTeam[home_i] = rs_home.getString("hometeam");
            //System.out.println(rs_home.getString("hometeam"));
            home_i++;
        }

        //System.out.println("_______transitioning_______");

        ResultSet rs_away = st.executeQuery(q);
        String[] awayTeam = new String[380];
        int away_i = 0;
        while(rs_away.next()){
            awayTeam[away_i] = rs_away.getString("awayteam");
            //System.out.println(rs_away.getString("awayteam"));
            away_i++;
        }

        //System.out.println("_______transitioning_______");

        ResultSet rs_result = st.executeQuery(q);
        String[] results = new String[380];
        int res_i = 0;
        while(rs_result.next()){
            results[res_i] = rs_result.getString("result");
            //System.out.println(rs_result.getString("result"));
            res_i++;
        }

        String homeString, awayString, resultString;

        fixtureList.clear();

        for(int i = 0; i < 380; i++){
            homeString = homeTeam[i];
            awayString = awayTeam[i];
            resultString = results[i];

            Fixture fix = new Fixture(homeString, awayString, resultString);
            fixtureList.add(fix);

            //System.out.println(homeString);
        }

        //System.out.println("1st home: " + homeTeam[0] + "; 1st away " + awayTeam[0]);
        //System.out.println("last home: " + homeTeam[379] + "; last away: " + awayTeam[379]);

//        String s = selectHomeTeamFromDB("1");
        //System.out.println(s);
        //selectHomeTeamFromDB();
    }

    public static void showFixtureList(){
        for (int i = 0; i < fixtureList.size(); i++){
            System.out.println((i+1) + " " + fixtureList.get(i));
        }
    }

    public static void showFixtureFromSQL() throws SQLException {
        String query = "SELECT * from FIXTURE";
        statement = conn.prepareStatement(query);
        ResultSet set = statement.executeQuery();

        //int i = 1;

        while (set.next()){
            int id = set.getInt("fixid");
            String hTeam = set.getString("hometeam");
            String aTeam = set.getString("awayteam");
            String res = set.getString("result");
            //System.out.print(i);
            System.out.format("%d, %s, %s, %s\n", id, hTeam,aTeam,res);
            //i++;
        }
    }


}
