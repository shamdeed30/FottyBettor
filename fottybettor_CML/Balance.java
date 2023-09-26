public class Balance {
    private float balanceAmount;
    public Balance(float balance){
        this.balanceAmount = balance;
    }
    public float loadBalance(){
        return balanceAmount;
    }
    public void changeBalance(float add){
        balanceAmount += add;

    }
}