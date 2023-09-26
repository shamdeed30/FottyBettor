public class BetCalc {

    // takes in the users prediction, their wager, the odds and the
    // actual result and the odds to calculate their return.

    private int prediction ;
    private float wager;
    private int actualResult;
    private float[] odds;

    BetCalc(int prediction,float wager,int actualResult,float[] odds){
        this.prediction = prediction;
        this.wager = wager;
        this.actualResult = actualResult;
        this.odds = odds;
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