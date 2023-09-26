public class AmericanOdds implements BettingStrategy{
    @Override
    public String[] oddcalc(float[] odds) {
        float odd1calc = (float) ((100 / odds[0]) *0.8);
        float odd2calc = (float) ((100 / odds[1]) *0.8);
        float odd3calc = (float) ((100 / odds[2]) *0.8);
        float american_odd1 = 0;
        float american_odd2 = 0;
        float american_odd3 = 0;
        if(odd1calc > 2){
            american_odd1 = (odd1calc -1) *100;
        }else {
            american_odd1 = -100/(odd1calc -1);
        }
        if(odd2calc > 2){
            american_odd2 = (odd2calc -1) *100;
        }else {
            american_odd2 = -100/(odd2calc -1);
        }
        if(odd3calc > 2){
            american_odd3 = (odd3calc -1) *100;
        }else {
            american_odd3 = -100/(odd3calc -1);
        }
        String odd1 = String.valueOf(american_odd1);
        String odd2 = String.valueOf(american_odd2);
        String odd3 = String.valueOf(american_odd3);

        String[] decimalodd = new String[3];

        decimalodd[0] = odd1;
        decimalodd[1] = odd2;
        decimalodd[2] = odd3;
        return decimalodd;

    }
}