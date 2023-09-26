public class InsuranceDecorator extends BetDecorator{

    public InsuranceDecorator(Bet decoratedbet) {
        super(decoratedbet);
    }

    @Override
    public float bet(float wager) {
        decoratedbet.bet(wager);
        return addInsurance(wager);
    }

    private float addInsurance(float wager){
        float insurance = (float) (0.05*wager);
        //System.out.println("You pay 5% wager for insurance.");
        //System.out.println("Insurance fee:" + insurance);
        return insurance;
    }
}