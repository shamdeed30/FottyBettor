public class oddCalc {

    //TeamChoice choice;


    private float findWinPercentage(TeamStat team){

        float gameWon = team.getGameWon();

        float winPercentage = (gameWon/38) * 100;

        return winPercentage;

    }

    private float findDrawPercentage(TeamStat team){

        float gameDraw = team.getGameTied();

        float drawPercentage = (gameDraw/38) * 100;

        return drawPercentage;
    }

    private float findLossPercentage(TeamStat team){

        float gameLoss = team.getGameLost();

        float drawPercentage = (gameLoss/38) * 100;

        return drawPercentage;
    }

    public float[] findOdd(TeamChoice choice){

        //look at junhao code for getting a team
        TeamStat t1 = choice.t1;
        TeamStat t2 = choice.t2;

        float[] odds = new float[3];

        float oddTeamOneWin = (findWinPercentage(t1)+findLossPercentage(t2))/2;
        float oddTeamTwoWin = (findWinPercentage(t2) + findLossPercentage(t1))/2;
        float oddDraw = (findDrawPercentage(t1)+findDrawPercentage(t2))/2;


        odds[0] = oddTeamOneWin;
        odds[1] = oddTeamTwoWin;
        odds[2] = oddDraw;


        return odds;

    }


}