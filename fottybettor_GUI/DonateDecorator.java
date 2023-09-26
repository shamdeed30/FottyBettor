public class DonateDecorator extends BetDecorator{
    public DonateDecorator(Bet decoratedbet) {
        super(decoratedbet);
    }
    @Override
    public float bet(float wager) {
        decoratedbet.bet(wager);
        return donate(wager);
    }
    private float donate(float wager){
        float donate = (float) (0.05*wager);
        //System.out.println("You will donate 5% wager if you win");
        //System.out.println("Donation:" + donate);
        return donate;
    }

}