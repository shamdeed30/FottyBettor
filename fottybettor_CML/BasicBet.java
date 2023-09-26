public class BasicBet implements Bet{
    @Override
    public float bet(float wager) {
        System.out.println("Your wager:" + wager);
        return 0;
    }
}
