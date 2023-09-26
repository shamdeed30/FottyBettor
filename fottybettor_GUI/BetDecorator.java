public abstract class BetDecorator implements Bet{
    protected Bet decoratedbet;

    public BetDecorator(Bet decoratedbet){
        this.decoratedbet = decoratedbet;
    }

    @Override
    public float bet(float wager) {
        return decoratedbet.bet(wager);
    }
}