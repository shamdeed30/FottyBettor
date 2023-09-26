

public class TeamChoice {
    protected TeamStat t1;
    protected TeamStat t2;

    public TeamChoice(int team1, int team2){
        this.t1 = CreateTeamData.teamList.get(team1);
        this.t2 = CreateTeamData.teamList.get(team2);
    }

}
