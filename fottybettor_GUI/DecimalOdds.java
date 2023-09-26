public class DecimalOdds implements BettingStrategy{
    public String[] oddcalc(float[] odds){
        float odd1calc = (float) ((100 / odds[0]) *0.8);
        float odd2calc = (float) ((100 / odds[1]) *0.8);
        float odd3calc = (float) ((100 / odds[2]) *0.8);

        String odd1 = String.valueOf(odd1calc);
        String odd2 = String.valueOf(odd2calc);
        String odd3 = String.valueOf(odd3calc);

        String[] decimalodd = new String[3];

        decimalodd[0] = odd1;
        decimalodd[1] = odd2;
        decimalodd[2] = odd3;
        return decimalodd;
    }
}