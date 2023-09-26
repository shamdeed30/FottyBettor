public class Fixture {
    private String homeTeam;
    private String awayTeam;
    private String winner;
    //private int fixID;

    public Fixture(String home, String away, String result){
        //this.fixID = id;
        this.homeTeam = home;
        this.awayTeam = away;
        this.winner = result;
    }

    //public int getFixID(){ return  this.fixID; }
    public String getHomeTeam(){
        return this.homeTeam;
    }
    public String getAwayTeam(){
        return this.awayTeam;
    }
    public String getWinner(){
        return this.winner;
//        String winningTeam;
//        if(this.winner == "H"){
//            winningTeam = this.homeTeam;
//        } else if (this.winner == "A") {
//            winningTeam = this.awayTeam;
//        }
//        else {
//            winningTeam = "Draw";
//        }
//        return winningTeam;
    }

    @Override
    public String toString(){
        return ("Home Team: " + this.getHomeTeam() + ", Away Team: " + this.getAwayTeam() + ", Winner: " + this.getWinner());
    }

}