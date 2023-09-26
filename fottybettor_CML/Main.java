import java.io.*;
import java.util.Scanner;

public class Main {
    private static String foldername;
    private static String filename;

    public static boolean createfolder(String path) {
        File file = null;
        try {
            file = new File(path);
            if (!file.exists()) {
                return file.mkdirs();
            }else {
                return false;
            }
        }catch(Exception e) {
        }return false;
    }

    public static void main(String[] args) throws IOException {
        foldername = "FootyBettor";
        GameManager game = new GameManager();
        if(createfolder(foldername)){
            System.out.println("Folder created!");
        } else {
            System.out.println("Folder exists.");
        }
        while(true){
            System.out.println("\n--------Welcome the Sport Betting Platform---------\n");
            System.out.println("1) Sign up");
            System.out.println("2) Log in");
            System.out.println("3) Quit");
            System.out.println("Please type 1, 2, or 3 to enter your choice:");
            Scanner scanner = new Scanner(System.in);
            String choice = scanner.nextLine();
            if (choice.equals("1")){
                game.signUp();
            } else if (choice.equals("2")){
                User user = game.login();
                if(user.balance.loadBalance() != -10) {
                    System.out.println("Successfully Log in!");
                    Boolean quit = true;
                    while (quit) {
                        System.out.println("----------- Menu -----------");
                        System.out.println("1. Check Team Stats");
                        System.out.println("2. Start to bet");
                        System.out.println("3. Check balance/Deposit");
                        System.out.println("4. Back to Log in Page");
                        System.out.println("Type 1, 2, 3, or 4 to enter your choice:");
                        Scanner in = new Scanner(System.in);
                        String userChoice = in.nextLine();
                        if (userChoice.equals("1")) {
                            game.displayStat();
                        } else if (userChoice.equals("2")) {
                            game.startBet(user);
                        } else if (userChoice.equals("3")) {
                            game.showBalance(user);
                        } else if (userChoice.equals("4")) {
                            quit = false;
                            break;
                        } else {
                            System.out.println("Invalid input.");
                        }

                    }
                } else {
                    System.out.println("Invalid username or password. Failed to log in.");
                }
            } else if (choice.equals("3")) {
                System.out.println("Thank you for using our services.");
                break;
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }

    }
}