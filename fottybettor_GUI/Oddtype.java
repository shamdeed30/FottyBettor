public class Oddtype {
    private BettingStrategy strategy;

    public Oddtype(BettingStrategy strategy){
        this.strategy = strategy;
    }

    public String[] returnodd(float[] odds){
        return strategy.oddcalc(odds);
    }
}