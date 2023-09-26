import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CreateFixture {
    public static ArrayList<Fixture> fixtureList = new ArrayList<>();
    //static String fixtureFile;

    public static void addFixtureFromFile(String filename) throws IOException {
        //fixtureFile = filename;
        fixtureList.clear();
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String Line = br.readLine();

        while(Line != null){
            String[] tLine = Line.split(",");
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
    
}
