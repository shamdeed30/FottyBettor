import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameManager {
    private boolean checknumber(String string){
        //boolean check = true;
        //for(int i = 0; i < string.length(); i ++){
        //  if(!Character.isDigit(string.charAt(i))){
        //    check = false;
        //}
        //}
        Pattern pattern = Pattern.compile("[0-9]*\\.?[0-9]+");
        Matcher matcher = pattern.matcher(string);
        if(!matcher.matches()){
            return false;
        }
        return true;
    }

    public User login(){
        Scanner scanner = new Scanner(System.in);
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter your username:");
        String userName = scanner.next();
        File acc2 = new File("FootyBettor/"+userName+".txt");
        System.out.println("Enter your password:");
        String password = scanner.next();
        if(acc2.exists() == true) {
            try {
                BufferedReader readTxt = new BufferedReader(new FileReader("FootyBettor/" + userName + ".txt"));
                String str = "";
                String text = "";
                while ((text = readTxt.readLine()) != null) {
                    str += text;
                }
                String[] array = str.split(",");
                String last = array[array.length - 1];
                User user = new User(userName, password, Float.parseFloat(last));
                String pwinput = "Password: " + password;
                if (array[1].equals(pwinput)) {
                    return user;
                } else {
                    return new User("", "", -10);
                }
            } catch (IOException ext) {
                if (ext instanceof FileNotFoundException) {
                    System.out.println("Account does not exist.");
                    ext.printStackTrace();
                } else {
                    System.err.println("Exception " + ext);
                }
            }
        }else {
            return new User("", "", -10);
        }
        return new User("", "", -10);
    }

    public void signUp() throws IOException {
        System.out.println("----Enter 'R' or 'r' to return to the log in page----");
        System.out.println("Enter your username:");

        Scanner scanner = new Scanner(System.in);
        String inputUsername = scanner.next();
        if(inputUsername.equals("R") || inputUsername.equals("r")){

        } else {
            String fileName = "FootyBettor/" + inputUsername + ".txt";
            File acc = new File(fileName);
            if(!acc.exists()){
                System.out.println("----Enter 'R' or 'r' to return to the log in page----");
                System.out.println("Enter your password:");
                String inputPassword = scanner.next();
                if(inputPassword.equals("R") || inputPassword.equals("r")){

                } else {
                    User user = new User(inputUsername,inputPassword,0);
                    try {
                        FileWriter writer = new FileWriter("FootyBettor/"+ user.userName+".txt");
                        writer.write("Username:" + user.userName + ",");
                        writer.write("\nPassword: "+ user.password + ",");
                        writer.write("\nBalance" + "," + user.balance.loadBalance() + ",");
                        writer.flush();
                        writer.close();
                    }catch(IOException ex) {
                        System.out.println("Error.signUp");
                    }
                    System.out.println("Account Created Successfully!");
                }

            } else {
                System.out.println("Account already exists.");
            }
        }
    }
    public void displayStat() throws IOException {
        CreateTeamData.addTeamStatFromFile("src/pastSeason.txt");
        for(int i = 0; i< CreateTeamData.teamList.size(); i ++){
            System.out.println(CreateTeamData.teamList.get(i).toString());
        }
    }
    public void startBet(User user) throws IOException {
        int team1 = 0;
        int team2 = 0;
        int num = 0;
        CreateTeamData.addTeamStatFromFile("src/pastSeason.txt");
        CreateFixture.addFixtureFromFile("src/pySoccer.txt");
        System.out.println("Choose a game you want to bet:");
        for(int i = 1; i < CreateFixture.fixtureList.size(); i ++){
            num = i;
            System.out.println( num + ". home team:"+ CreateFixture.fixtureList.get(i).getHomeTeam() + " | away team:" + CreateFixture.fixtureList.get(i).getAwayTeam());
        }
        while (true) {
            System.out.println("Type game number:");
            Scanner teamchoice1 = new Scanner(System.in);
            String choice1 = teamchoice1.nextLine();
            if (checknumber(choice1)) {
                int choice_final = Integer.parseInt(choice1);
                if (choice_final > 0 && choice_final < CreateFixture.fixtureList.size()) {
                    String team1choice = CreateFixture.fixtureList.get(choice_final).getHomeTeam();
                    String team2choice = CreateFixture.fixtureList.get(choice_final).getAwayTeam();
                    for (int j = 0; j < CreateTeamData.teamList.size(); j++) {
                        if (team1choice.equals(CreateTeamData.teamList.get(j).teamName)) {
                            team1 = j;
                        }
                    }
                    for (int j = 0; j < CreateTeamData.teamList.size(); j++) {
                        if (team2choice.equals(CreateTeamData.teamList.get(j).teamName)) {
                            team2 = j;
                        }
                    }
                    TeamChoice userchoice = new TeamChoice(team1, team2);
                    oddCalc odd = new oddCalc();
                    odd.findOdd(userchoice);
                    while(true) {
                        System.out.println("Choose a type of odds:");
                        System.out.println("1.Decimal odds");
                        System.out.println("2.American odds");
                        System.out.println("Type 1 or 2 to choose:");
                        Scanner usertype = new Scanner(System.in);
                        String type = usertype.nextLine();
                        Oddtype typechoice = new Oddtype(new DecimalOdds());
                        if (type.equals("1") || type.equals("2") ) {
                            if(type.equals("1")){
                                typechoice = new Oddtype(new DecimalOdds());
                            }else{
                                typechoice = new Oddtype(new AmericanOdds());
                            }
                            while (true) {
                                System.out.println("Predict the result:");
                                System.out.println("1. " + CreateTeamData.teamList.get(team1).teamName + " win. Odds: " + typechoice.returnodd(odd.findOdd(userchoice))[0]);
                                System.out.println("2. " + CreateTeamData.teamList.get(team2).teamName + " win. Odds: " + typechoice.returnodd(odd.findOdd(userchoice))[1]);
                                System.out.println("3. This is a draw. Odds:" + typechoice.returnodd(odd.findOdd(userchoice))[2]);
                                System.out.println("Type 1, 2, or 3 to choose a result:");
                                Scanner resultchoice = new Scanner(System.in);
                                String result = resultchoice.nextLine();
                                if (result.equals("1") || result.equals("2") || result.equals("3")) {
                                    int userresult = Integer.parseInt(result);
                                    int actual_result = 0;
                                    String actualresult = CreateFixture.fixtureList.get(choice_final).getWinner();
                                    if (actualresult.equals("H")) {
                                        actual_result = 1;
                                    } else if (actualresult.equals("A")) {
                                        actual_result = 2;
                                    } else if (actualresult.equals("D")) {
                                        actual_result = 3;
                                    } else {

                                    }
                                    while (true) {
                                        System.out.println("Enter Wager(the amount of money to bet):");
                                        String wager = resultchoice.nextLine();
                                        if (checknumber(wager)) {
                                            float userwager = Float.parseFloat(wager);
                                            if (user.balance.loadBalance() >= userwager) {
                                                while(true) {
                                                    System.out.println("Do you want to donate or add insurance?");
                                                    System.out.println("1. Add insurance.");
                                                    System.out.println("2. Donate");
                                                    System.out.println("3. Both");
                                                    System.out.println("4. No");
                                                    System.out.println("Type 1, 2, 3, or 4 to choose:");
                                                    String userdecorator = resultchoice.nextLine();
                                                    if(userdecorator.equals("1") || userdecorator.equals("2") || userdecorator.equals("3")|| userdecorator.equals("4")){
                                                        float fee = 0;
                                                        float donate = 0;
                                                        Bet bet = new BasicBet();
                                                        if(userdecorator.equals("1")){
                                                            Bet bet_insurance = new InsuranceDecorator(bet);
                                                            fee = bet_insurance.bet(userwager);
                                                        }else if (userdecorator.equals("2")){
                                                            Bet bet_donate = new DonateDecorator(bet);
                                                            donate = bet_donate.bet(userwager);
                                                        }else if(userdecorator.equals("3")){
                                                            Bet bet_donate = new DonateDecorator(bet);
                                                            Bet bet_insurance = new InsuranceDecorator(bet);
                                                            donate = bet_donate.bet(userwager);
                                                            fee = bet_insurance.bet(userwager);
                                                        }else if(userdecorator.equals("4")){
                                                            fee = bet.bet(userwager);
                                                        }
                                                        BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                        System.out.println("Result:");
                                                        if (betcalc.calcReturn() > 0) {
                                                            System.out.println("You won: $" + betcalc.calcReturn());
                                                            user.addBalance(userwager * (-1));
                                                            user.addBalance(fee*(-1));
                                                            user.addBalance(donate*(-1));
                                                        } else {
                                                            System.out.println("You lost: $" + betcalc.calcReturn() * (-1));
                                                            user.addBalance(fee*(-1));
                                                        }
                                                        user.addBalance(betcalc.calcReturn());
                                                        System.out.println("Current Balance: $" + user.balance.loadBalance());
                                                        break;
                                                    }else{
                                                        System.out.println("Invalid choice");
                                                    }
                                                }
                                                break;
                                            } else {
                                                System.out.println("Not enough money");
                                                System.out.println("Do you want to recharge or return to the previous window?");
                                                System.out.println("1.recharge");
                                                System.out.println("2.return");
                                                System.out.println("Type a option number:");
                                                Scanner optionchoice = new Scanner(System.in);
                                                String option = optionchoice.nextLine();
                                                int useroption = Integer.parseInt(option);
                                                if (useroption == 1) {
                                                    System.out.println("Enter the amount of money to add:");
                                                    Scanner in2 = new Scanner(System.in);
                                                    float money = in2.nextInt();
                                                    user.addBalance(money);
                                                } else if (useroption == 2) {
                                                }
                                            }
                                        } else {
                                            System.out.println("Invalid choice");
                                        }
                                    }
                                    break;
                                } else {
                                    System.out.println("Invalid choice");
                                }
                            }
                            break;
                        }else{
                            System.out.println("Invalid choice");
                        }
                    }
                    break;
                }else{
                    System.out.println("Invalid choice");
                }

            } else {
                System.out.println("Invalid choice");
            }


        }
    }

    public void showBalance(User user2){
        while (true) {
            System.out.println("Current Balance: $" + user2.balance.loadBalance());
            System.out.println("Do you want to add money?");
            System.out.println("Enter Y for Yes or N for No");
            Scanner in = new Scanner(System.in);
            Scanner in2 = new Scanner(System.in);
            String userChoice2 = in.nextLine();
            if (userChoice2.equals("Y") || userChoice2.equals("y")) {
                while(true) {
                    System.out.println("Enter the amount of money to add:");
                    String usermoney = in2.nextLine();
                    if(checknumber(usermoney)) {
                        float money = Float.parseFloat(usermoney);
                        user2.addBalance(money);
                        break;
                    }else{
                        System.out.println("Invalid amount entered.");
                    }
                }
            } else if (userChoice2.equals("N") || userChoice2.equals("n")) {
                break;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }


}

