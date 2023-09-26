
import java.io.*;

public class User {
    public String userName;
    public String password;
    public Balance balance;

    public User(String userName, String password, float balance){
        this.userName = userName;
        this.password = password;
        this.balance = new Balance(balance);
    }

    protected float addBalance(float money){
        balance.changeBalance(money);
        try {
            FileWriter writer = new FileWriter("FootyBettor/"+userName+".txt");
            writer.write("Username:" + userName + ",");
            writer.write("\nPassword: "+ password + ",");
            writer.write("\nBalance" + "," + balance.loadBalance() + ",");
            writer.flush();
            writer.close();

        }catch(IOException ex) {
            System.out.println("Error.addBalance");
            ex.printStackTrace();
        }
        return balance.loadBalance();
    }


}