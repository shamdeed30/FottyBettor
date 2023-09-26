import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.awt.Component.LEFT_ALIGNMENT;
import static java.lang.Boolean.FALSE;


public class GameManager {
    int actual_result = 0;

    private boolean checknumber(String string) {

        Pattern pattern = Pattern.compile("[0-9]*\\.?[0-9]+");
        Matcher matcher = pattern.matcher(string);
        if (!matcher.matches()) {
            return false;
        }
        float value = Float.valueOf(string);
        if(value > 1000000){
            return false;
        }
        return true;
    }

    public void displayStat() throws IOException, SQLException, ClassNotFoundException {
        JFrame Team = new JFrame();
        Team.setTitle("Team Stats");
        Team.setSize(500, 500);
        Team.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Team.setLayout(new FlowLayout());
        //Team.getContentPane().setBackground(Color.LIGHT_GRAY);
        Team.setLocationRelativeTo(null);

        //todo create team data arraylist testing
        CreateTeamData.makeTeamStatToDBConn();
        CreateTeamData.addSQLToTeamList();
        //CreateTeamData.addTeamStatFromFile("pastSeason.txt");
        String[] columnNames = {"Team Name","Game won","Game lost","Game tied"};
        String[][] data = new String[CreateTeamData.teamList.size()][4];

        for (int i = 0; i < CreateTeamData.teamList.size(); i++) {
            //JLabel label = new JLabel(CreateTeamData.teamList.get(i).toString());
            //Team.add(label);
            data[i][0] = CreateTeamData.teamList.get(i).getTeamName();
            data[i][1] = String.valueOf(CreateTeamData.teamList.get(i).getGameWon());
            data[i][2] = String.valueOf(CreateTeamData.teamList.get(i).getGameLost());
            data[i][3] = String.valueOf(CreateTeamData.teamList.get(i).getGameTied());
        }
        JTable table = new JTable();
        //instance table model
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };
        table.setModel(tableModel);
        JScrollPane sp = new JScrollPane(table);
        Team.add(sp);

        JButton back = new JButton("Return");
        Team.add(back);
        //Team.add(panel,BorderLayout.SOUTH);
        back.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String command2 = e.getActionCommand();
                if ("Return".equals(command2)) {
                    Team.dispose();
                }
            }
        });
        Team.setVisible(true);
    }

    public void startBet1(User user) throws IOException, SQLException, ClassNotFoundException {
        final int[] team1 = {0};
        final int[] team2 = {0};

        //todo create team data array list test
        CreateTeamData.makeTeamStatToDBConn();
        CreateTeamData.addSQLToTeamList();
        //CreateTeamData.addTeamStatFromFile("pastSeason.txt");

        //todo create fixtureList from SQL
        CreateFixture.makeFixToDBConn();
        CreateFixture.addSQLToFixtureList();
        //CreateFixture.addFixtureFromFile("pySoccer.txt");

        JFrame testFrame = new JFrame("Bet");
        //testFrame.setSize(450, 150);
        JTextArea jta = new JTextArea();
        JScrollPane jsp = new JScrollPane(jta);
        JPanel panel = new JPanel();

        testFrame.add(jsp, BorderLayout.CENTER);
        jta.setEditable(false);
        testFrame.setBounds(200,100,500,500);
        testFrame.setResizable(false);
        testFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        testFrame.setLocationRelativeTo(null);
        testFrame.setVisible(true);
        //list all games in scroll panel
        for(int i = 0; i < CreateFixture.fixtureList.size(); i ++){
            int num = i +1;
            jta.append( num + ". Home team: "+ CreateFixture.fixtureList.get(i).getHomeTeam() + ". Away team: "
                    +CreateFixture.fixtureList.get(i).getAwayTeam() + "." + System.lineSeparator());
        }

        JButton enter = new JButton("Enter");
        JLabel text = new JLabel("Enter game number:");
        JTextField textBook = new JTextField(20);
        panel.add(text);
        panel.add(textBook);
        //testFrame.add(enter);
        panel.add(enter);
        testFrame.add(panel,BorderLayout.SOUTH);
        enter.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String command = e.getActionCommand();
                testFrame.dispose();
                if ("Enter".equals(command)){
                    String userChoice = textBook.getText();
                    testFrame.dispose();
                    if (checknumber(userChoice)) {
                        int choice_final = Integer.parseInt(userChoice);

                        //todo testing if user has bet already or not
                        boolean bool;
                        try {
                            BetCalc.updateGameTrackDBConn();
                            bool = BetCalc.checkGameTrackExists(choice_final, user.userName);
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        } catch (ClassNotFoundException ex) {
                            throw new RuntimeException(ex);
                        }
                        if (bool == false && choice_final > 0 && choice_final <= CreateFixture.fixtureList.size()) {
                            String team1choice = CreateFixture.fixtureList.get(choice_final-1).getHomeTeam();
                            String team2choice = CreateFixture.fixtureList.get(choice_final-1).getAwayTeam();
                            for (int j = 0; j < CreateTeamData.teamList.size(); j++) {
                                if (team1choice.equals(CreateTeamData.teamList.get(j).getTeamName())) {
                                    team1[0] = j;
                                }
                            }
                            for (int j = 0; j < CreateTeamData.teamList.size(); j++) {
                                if (team2choice.equals(CreateTeamData.teamList.get(j).getTeamName())) {
                                    team2[0] = j;
                                }
                            }
                            TeamChoice userchoice = new TeamChoice(team1[0], team2[0]);
                            oddCalc odd = new oddCalc();
                            odd.findOdd(userchoice);

                            final Oddtype[] typechoice = {new Oddtype(new DecimalOdds())};

                            JFrame f = new JFrame();
                            f.setSize(400, 150);
                            f.setTitle("Pick one to bet!");
                            f.setResizable(false);
                            f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                            //f.setLayout(new FlowLayout());
                            f.setLocationRelativeTo(null);
                            f.setVisible(true);

                            JPanel panel = new JPanel();
                            //panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
                            //panel.setLayout(new GridBagLayout());
                            JLabel label = new JLabel("<html>Choose a type of odds: <br/> 1. Decimal odds <br/> 2. American odds <html>");
                            panel.add(label);
                            //f.add(label);

                            JPanel panel2 = new JPanel();
                            panel2.setLayout(new BoxLayout(panel2, BoxLayout.LINE_AXIS));
                            panel.add(panel2);
                            f.add(panel);

                            JButton d_odd = new JButton("1");
                            panel2.add(d_odd);
                            d_odd.addActionListener(new AbstractAction() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    String command2 = e.getActionCommand();
                                    if ("1".equals(command2)) {
                                        typechoice[0] = new Oddtype(new DecimalOdds());
                                        f.dispose();

                                        //show a new window with odds and allow the user to click which team wins or draw
                                        JFrame newFrame = new JFrame();
                                        newFrame.setSize(400, 150);
                                        newFrame.setTitle("Pick one to bet!");
                                        newFrame.setResizable(false);
                                        newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                        //newFrame.setLayout(new FlowLayout());
                                        newFrame.setLocationRelativeTo(null);
                                        newFrame.setVisible(true);
                                        JPanel panel = new JPanel();
                                        panel.setLayout(null);



                                        JLabel label = new JLabel("<html> 1. Home Team " + team1choice + " wins. Odds: " + typechoice[0].returnodd(odd.findOdd(userchoice))[0]
                                                + "<br/> 2. Away Team " + team2choice + " wins. Odds: " + typechoice[0].returnodd(odd.findOdd(userchoice))[1]
                                                + "<br/> 3. This is a draw. Odds: " + typechoice[0].returnodd(odd.findOdd(userchoice))[2] + "<html>");
                                        //newFrame.add(label);
                                        label.setBounds(30,5,350,70);
                                        panel.add(label);
                                        newFrame.add(panel);


                                        String actualresult = CreateFixture.fixtureList.get(choice_final-1).getWinner();
                                        if (actualresult.equals("H")) {
                                            actual_result = 1;
                                        } else if (actualresult.equals("A")) {
                                            actual_result = 2;
                                        } else if (actualresult.equals("D")) {
                                            actual_result = 3;
                                        } else {

                                        }

                                        JButton t1win = new JButton("1");
                                        t1win.setBounds(110,90,40,20);
                                        panel.add(t1win);
                                        //newFrame.add(t1win);
                                        int finalActual_result = actual_result;
                                        t1win.addActionListener(new AbstractAction() {
                                            @Override
                                            public void actionPerformed(ActionEvent e) {
                                                String command2 = e.getActionCommand();
                                                if ("1".equals(command2)) {
                                                    newFrame.dispose();
                                                    int userresult = 1;

                                                    final boolean[] bool = {true};
                                                    while (bool[0]) {
                                                        String wager = JOptionPane.showInputDialog("Enter wager (the amount of money to bet):");
                                                        if (wager == null) {
                                                            break;
                                                        }
                                                        if (checknumber(wager) && !(wager.equals("0"))) {
                                                            bool[0] = false;
                                                            float userwager = Float.parseFloat(wager);
                                                            if (user.balance.loadBalance() >= userwager) {
                                                                JFrame f2 = new JFrame();
                                                                f2.setSize(400, 150);
                                                                f2.setTitle("Bet");
                                                                f2.setResizable(false);
                                                                f2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                                                f2.setLayout(new FlowLayout());
                                                                f2.setLocationRelativeTo(null);
                                                                f2.setVisible(true);
                                                                JLabel label = new JLabel("<html> Do you want to donate or add insurance to your bet? <br/> 1. Add insurance " +
                                                                        "<br/> 2. Donate <br/> 3. Both <br/> 4. No <html>");
                                                                f2.add(label);

                                                                final float[] fee = {0};
                                                                final float[] donate = {0};
                                                                Bet bet = new BasicBet();

                                                                JButton insurance = new JButton("1");
                                                                f2.add(insurance);
                                                                insurance.addActionListener(new AbstractAction() {
                                                                    @Override
                                                                    public void actionPerformed(ActionEvent e) {
                                                                        String command2 = e.getActionCommand();
                                                                        if ("1".equals(command2)) {
                                                                            f2.dispose();
                                                                            Bet bet_insurance = new InsuranceDecorator(bet);
                                                                            fee[0] = bet_insurance.bet(userwager);

                                                                            if(fee[0] + userwager <= user.balance.loadBalance()){
                                                                                JOptionPane.showMessageDialog(null, "You wager: $" + userwager + "\nYou pay 5% wager for insurance. \nInsurance fee: $" + (float) (0.05) * userwager,
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                                BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                                if (betcalc.calcReturn() > 0) {
                                                                                    try {
                                                                                        user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                    } catch (SQLException ex) {
                                                                                        throw new RuntimeException(ex);
                                                                                    }
                                                                                    JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                            "Result", JOptionPane.PLAIN_MESSAGE);

                                                                                } else {
                                                                                    try {
                                                                                        user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                    } catch (SQLException ex) {
                                                                                        throw new RuntimeException(ex);
                                                                                    }
                                                                                    JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                            "Result", JOptionPane.PLAIN_MESSAGE);

                                                                                }
                                                                                try {
                                                                                    BetCalc.updateGameTrackDBConn();
                                                                                    BetCalc.updateGameTrack(choice_final, user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                } catch (ClassNotFoundException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                            } else {
                                                                                JOptionPane.showMessageDialog(null, "No enough money.",
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);
                                                                            }


                                                                        }
                                                                    }
                                                                });

                                                                JButton donation = new JButton("2");
                                                                f2.add(donation);
                                                                donation.addActionListener(new AbstractAction() {
                                                                    @Override
                                                                    public void actionPerformed(ActionEvent e) {
                                                                        String command2 = e.getActionCommand();
                                                                        if ("2".equals(command2)) {
                                                                            f2.dispose();
                                                                            Bet bet_donate = new DonateDecorator(bet);
                                                                            donate[0] = bet_donate.bet(userwager);

                                                                            JOptionPane.showMessageDialog(null, "You wager: $" + userwager + "\nYou will donate 5% wager for if you win. \nDonation: $" + (float) (0.05) * userwager,
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                            if (betcalc.calcReturn() > 0) {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            } else {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            }
                                                                            try {
                                                                                BetCalc.updateGameTrackDBConn();
                                                                                BetCalc.updateGameTrack(choice_final, user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            } catch (ClassNotFoundException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                        }
                                                                    }
                                                                });

                                                                JButton both = new JButton("3");
                                                                f2.add(both);
                                                                both.addActionListener(new AbstractAction() {
                                                                    @Override
                                                                    public void actionPerformed(ActionEvent e) {
                                                                        String command2 = e.getActionCommand();
                                                                        if ("3".equals(command2)) {
                                                                            f2.dispose();
                                                                            Bet bet_donate = new DonateDecorator(bet);
                                                                            Bet bet_insurance = new InsuranceDecorator(bet);
                                                                            donate[0] = bet_donate.bet(userwager);
                                                                            fee[0] = bet_insurance.bet(userwager);

                                                                            if(fee[0] + userwager <= user.balance.loadBalance()){
                                                                                JOptionPane.showMessageDialog(null, "You wager: $" + userwager + "\nYou pay 5% wager for insurance. \nInsurance fee: $" + (float) (0.05) * userwager +
                                                                                        "\nYou will donate 5% wager if you win. \nDonation: $" + (float) (0.05) * userwager, "Result", JOptionPane.PLAIN_MESSAGE);

                                                                                BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                                if (betcalc.calcReturn() > 0) {
                                                                                    try {
                                                                                        user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                    } catch (SQLException ex) {
                                                                                        throw new RuntimeException(ex);
                                                                                    }
                                                                                    JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                            "Result", JOptionPane.PLAIN_MESSAGE);

                                                                                } else {
                                                                                    try {
                                                                                        user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                    } catch (SQLException ex) {
                                                                                        throw new RuntimeException(ex);
                                                                                    }
                                                                                    JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                            "Result", JOptionPane.PLAIN_MESSAGE);

                                                                                }
                                                                                try {
                                                                                    BetCalc.updateGameTrackDBConn();
                                                                                    BetCalc.updateGameTrack(choice_final, user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                } catch (ClassNotFoundException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                            } else {
                                                                                JOptionPane.showMessageDialog(null, "No enough money.",
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);
                                                                            }


                                                                        }
                                                                    }
                                                                });

                                                                JButton no = new JButton("4");
                                                                f2.add(no);
                                                                no.addActionListener(new AbstractAction() {
                                                                    @Override
                                                                    public void actionPerformed(ActionEvent e) {
                                                                        String command2 = e.getActionCommand();
                                                                        if ("4".equals(command2)) {
                                                                            f2.dispose();
                                                                            fee[0] = bet.bet(userwager);

                                                                            BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                            if (betcalc.calcReturn() > 0) {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            } else {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            }
                                                                            try {
                                                                                BetCalc.updateGameTrackDBConn();
                                                                                BetCalc.updateGameTrack(choice_final, user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            } catch (ClassNotFoundException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }

                                                                        }
                                                                    }
                                                                });

                                                            } else {
                                                                //no enough money, show a new window to ask whether the user want to add money or not
                                                                UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("Verdana", Font.PLAIN, 13)));
                                                                UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("Verdana", Font.PLAIN, 13)));
                                                                Object[] options = {"Yes", "No"};
                                                                int response = JOptionPane.showOptionDialog(null, "You don't have enough money in current balance. \nDo you want to recharge?", "Balance", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                                                                if (response == 0) {
                                                                    //yes
                                                                    String add_s = JOptionPane.showInputDialog("Enter amount of money to add:");
                                                                    if (checknumber(add_s)){
                                                                        float add = Float.parseFloat(add_s);
                                                                        try {
                                                                            user.updateBalanceInUserAccount(add, user.userName);
                                                                        } catch (SQLException ex) {
                                                                            throw new RuntimeException(ex);
                                                                        }
                                                                        JOptionPane.showMessageDialog(null, "$" + add_s + " has been added." + "\nYour current balance: $" + user.balance.loadBalance(),
                                                                                "Result", JOptionPane.PLAIN_MESSAGE);

                                                                    } else {
                                                                        JOptionPane.showMessageDialog(null, "Invalid money input.",
                                                                                "Result", JOptionPane.PLAIN_MESSAGE);
                                                                    }

                                                                }
                                                            }

                                                        } else {
                                                            JOptionPane.showMessageDialog(null, "Invalid wager input.",
                                                                    "Result", JOptionPane.PLAIN_MESSAGE);
                                                        }

                                                    }

                                                }
                                            }
                                        });

                                        JButton t2win = new JButton("2");
                                        t2win.setBounds(190,90,40,20);
                                        panel.add(t2win);
                                        //newFrame.add(t2win);
                                        t2win.addActionListener(new AbstractAction() {
                                            @Override
                                            public void actionPerformed(ActionEvent e) {
                                                String command2 = e.getActionCommand();
                                                if ("2".equals(command2)) {
                                                    newFrame.dispose();
                                                    int userresult = 2;

                                                    final boolean[] bool = {true};
                                                    while (bool[0]) {
                                                        String wager = JOptionPane.showInputDialog("Enter wager (the amount of money to bet):");
                                                        if (wager == null) {
                                                            break;
                                                        }
                                                        if (checknumber(wager)) {
                                                            bool[0] = false;
                                                            float userwager = Float.parseFloat(wager);
                                                            if (user.balance.loadBalance() >= userwager) {
                                                                JFrame f2 = new JFrame();
                                                                f2.setSize(400, 150);
                                                                f2.setTitle("Bet");
                                                                f2.setResizable(false);
                                                                f2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                                                f2.setLayout(new FlowLayout());
                                                                f2.setLocationRelativeTo(null);
                                                                f2.setVisible(true);
                                                                JLabel label = new JLabel("<html> Do you want to donate or add insurance to your bet? <br/> 1. Add insurance " +
                                                                        "<br/> 2. Donate <br/> 3. Both <br/> 4. No <html>");
                                                                f2.add(label);

                                                                final float[] fee = {0};
                                                                final float[] donate = {0};
                                                                Bet bet = new BasicBet();

                                                                JButton insurance = new JButton("1");
                                                                f2.add(insurance);
                                                                insurance.addActionListener(new AbstractAction() {
                                                                    @Override
                                                                    public void actionPerformed(ActionEvent e) {
                                                                        String command2 = e.getActionCommand();
                                                                        if ("1".equals(command2)) {
                                                                            f2.dispose();
                                                                            Bet bet_insurance = new InsuranceDecorator(bet);
                                                                            fee[0] = bet_insurance.bet(userwager);

                                                                            if(fee[0] + userwager <= user.balance.loadBalance()){
                                                                                JOptionPane.showMessageDialog(null, "You wager: $" + userwager + "\nYou pay 5% wager for insurance. \nInsurance fee: $" + (float) (0.05) * userwager,
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                                BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                                if (betcalc.calcReturn() > 0) {
                                                                                    try {
                                                                                        user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                    } catch (SQLException ex) {
                                                                                        throw new RuntimeException(ex);
                                                                                    }
                                                                                    JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                            "Result", JOptionPane.PLAIN_MESSAGE);

                                                                                } else {
                                                                                    try {
                                                                                        user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                    } catch (SQLException ex) {
                                                                                        throw new RuntimeException(ex);
                                                                                    }
                                                                                    JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                            "Result", JOptionPane.PLAIN_MESSAGE);

                                                                                }
                                                                                try {
                                                                                    BetCalc.updateGameTrackDBConn();
                                                                                    BetCalc.updateGameTrack(choice_final, user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                } catch (ClassNotFoundException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                            } else {
                                                                                JOptionPane.showMessageDialog(null, "No enough money.",
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);
                                                                            }


                                                                        }
                                                                    }
                                                                });

                                                                JButton donation = new JButton("2");
                                                                f2.add(donation);
                                                                donation.addActionListener(new AbstractAction() {
                                                                    @Override
                                                                    public void actionPerformed(ActionEvent e) {
                                                                        String command2 = e.getActionCommand();
                                                                        if ("2".equals(command2)) {
                                                                            f2.dispose();
                                                                            Bet bet_donate = new DonateDecorator(bet);
                                                                            donate[0] = bet_donate.bet(userwager);

                                                                            JOptionPane.showMessageDialog(null, "You wager: $" + userwager + "\nYou will donate 5% wager for if you win. \nDonation: $" + (float) (0.05) * userwager,
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                            if (betcalc.calcReturn() > 0) {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            } else {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            }
                                                                            try {
                                                                                BetCalc.updateGameTrackDBConn();
                                                                                BetCalc.updateGameTrack(choice_final, user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            } catch (ClassNotFoundException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                        }
                                                                    }
                                                                });

                                                                JButton both = new JButton("3");
                                                                f2.add(both);
                                                                both.addActionListener(new AbstractAction() {
                                                                    @Override
                                                                    public void actionPerformed(ActionEvent e) {
                                                                        String command2 = e.getActionCommand();
                                                                        if ("3".equals(command2)) {
                                                                            f2.dispose();
                                                                            Bet bet_donate = new DonateDecorator(bet);
                                                                            Bet bet_insurance = new InsuranceDecorator(bet);
                                                                            donate[0] = bet_donate.bet(userwager);
                                                                            fee[0] = bet_insurance.bet(userwager);

                                                                            if(fee[0] + userwager <= user.balance.loadBalance()){
                                                                                JOptionPane.showMessageDialog(null, "You wager: $" + userwager + "\nYou pay 5% wager for insurance. \nInsurance fee: $" + (float) (0.05) * userwager +
                                                                                        "\nYou will donate 5% wager if you win. \nDonation: $" + (float) (0.05) * userwager, "Result", JOptionPane.PLAIN_MESSAGE);

                                                                                BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                                if (betcalc.calcReturn() > 0) {
                                                                                    try {
                                                                                        user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                    } catch (SQLException ex) {
                                                                                        throw new RuntimeException(ex);
                                                                                    }
                                                                                    JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                            "Result", JOptionPane.PLAIN_MESSAGE);

                                                                                } else {
                                                                                    try {
                                                                                        user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                    } catch (SQLException ex) {
                                                                                        throw new RuntimeException(ex);
                                                                                    }
                                                                                    JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                            "Result", JOptionPane.PLAIN_MESSAGE);

                                                                                }
                                                                                try {
                                                                                    BetCalc.updateGameTrackDBConn();
                                                                                    BetCalc.updateGameTrack(choice_final, user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                } catch (ClassNotFoundException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                            } else {
                                                                                JOptionPane.showMessageDialog(null, "No enough money.",
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);
                                                                            }

                                                                        }
                                                                    }
                                                                });

                                                                JButton no = new JButton("4");
                                                                f2.add(no);
                                                                no.addActionListener(new AbstractAction() {
                                                                    @Override
                                                                    public void actionPerformed(ActionEvent e) {
                                                                        String command2 = e.getActionCommand();
                                                                        if ("4".equals(command2)) {
                                                                            f2.dispose();
                                                                            fee[0] = bet.bet(userwager);

                                                                            BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                            if (betcalc.calcReturn() > 0) {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            } else {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            }
                                                                            try {
                                                                                BetCalc.updateGameTrackDBConn();
                                                                                BetCalc.updateGameTrack(choice_final, user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            } catch (ClassNotFoundException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }

                                                                        }
                                                                    }
                                                                });

                                                            } else {
                                                                //no enough money, show a new window to ask whether the user want to add money or not
                                                                UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("Verdana", Font.PLAIN, 13)));
                                                                UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("Verdana", Font.PLAIN, 13)));
                                                                Object[] options = {"Yes", "No"};
                                                                int response = JOptionPane.showOptionDialog(null, "You don't have enough money in current balance. \nDo you want to recharge?", "Balance", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                                                                if (response == 0) {
                                                                    //yes
                                                                    String add_s = JOptionPane.showInputDialog("Enter amount of money to add:");
                                                                    if (checknumber(add_s)){
                                                                        float add = Float.parseFloat(add_s);
                                                                        try {
                                                                            user.updateBalanceInUserAccount(add, user.userName);
                                                                        } catch (SQLException ex) {
                                                                            throw new RuntimeException(ex);
                                                                        }
                                                                        JOptionPane.showMessageDialog(null, "$" + add_s + " has been added." + "\nYour current balance: $" + user.balance.loadBalance(),
                                                                                "Result", JOptionPane.PLAIN_MESSAGE);

                                                                    } else {
                                                                        JOptionPane.showMessageDialog(null, "Invalid money input.",
                                                                                "Result", JOptionPane.PLAIN_MESSAGE);
                                                                    }
                                                                }
                                                            }

                                                        } else {
                                                            JOptionPane.showMessageDialog(null, "Invalid wager input.",
                                                                    "Result", JOptionPane.PLAIN_MESSAGE);
                                                        }
                                                    }
                                                }
                                            }
                                        });

                                        JButton draw = new JButton("3");
                                        draw.setBounds(270,90,40,20);
                                        panel.add(draw);
                                        //newFrame.add(draw);
                                        draw.addActionListener(new AbstractAction() {
                                            @Override
                                            public void actionPerformed(ActionEvent e) {
                                                String command2 = e.getActionCommand();
                                                if ("3".equals(command2)) {
                                                    newFrame.dispose();
                                                    int userresult = 3;

                                                    final boolean[] bool = {true};
                                                    while (bool[0]) {
                                                        String wager = JOptionPane.showInputDialog("Enter wager (the amount of money to bet):");
                                                        if (wager == null) {
                                                            break;
                                                        }
                                                        if (checknumber(wager)) {
                                                            bool[0] = false;
                                                            float userwager = Float.parseFloat(wager);
                                                            if (user.balance.loadBalance() >= userwager) {
                                                                JFrame f2 = new JFrame();
                                                                f2.setSize(400, 150);
                                                                f2.setTitle("Bet");
                                                                f2.setResizable(false);
                                                                f2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                                                f2.setLayout(new FlowLayout());
                                                                f2.setLocationRelativeTo(null);
                                                                f2.setVisible(true);
                                                                JLabel label = new JLabel("<html> Do you want to donate or add insurance to your bet? <br/> 1. Add insurance " +
                                                                        "<br/> 2. Donate <br/> 3. Both <br/> 4. No <html>");
                                                                f2.add(label);

                                                                final float[] fee = {0};
                                                                final float[] donate = {0};
                                                                Bet bet = new BasicBet();

                                                                JButton insurance = new JButton("1");
                                                                f2.add(insurance);
                                                                insurance.addActionListener(new AbstractAction() {
                                                                    @Override
                                                                    public void actionPerformed(ActionEvent e) {
                                                                        String command2 = e.getActionCommand();
                                                                        if ("1".equals(command2)) {
                                                                            f2.dispose();
                                                                            Bet bet_insurance = new InsuranceDecorator(bet);
                                                                            fee[0] = bet_insurance.bet(userwager);

                                                                            if(fee[0] + userwager <= user.balance.loadBalance()){
                                                                                JOptionPane.showMessageDialog(null, "You wager: $" + userwager + "\nYou pay 5% wager for insurance. \nInsurance fee: $" + (float) (0.05) * userwager,
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                                BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                                if (betcalc.calcReturn() > 0) {
                                                                                    try {
                                                                                        user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                    } catch (SQLException ex) {
                                                                                        throw new RuntimeException(ex);
                                                                                    }
                                                                                    JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                            "Result", JOptionPane.PLAIN_MESSAGE);

                                                                                } else {
                                                                                    try {
                                                                                        user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                    } catch (SQLException ex) {
                                                                                        throw new RuntimeException(ex);
                                                                                    }
                                                                                    JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                            "Result", JOptionPane.PLAIN_MESSAGE);

                                                                                }
                                                                                try {
                                                                                    BetCalc.updateGameTrackDBConn();
                                                                                    BetCalc.updateGameTrack(choice_final, user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                } catch (ClassNotFoundException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                            } else {
                                                                                JOptionPane.showMessageDialog(null, "No enough money.",
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);
                                                                            }


                                                                        }
                                                                    }
                                                                });

                                                                JButton donation = new JButton("2");
                                                                f2.add(donation);
                                                                donation.addActionListener(new AbstractAction() {
                                                                    @Override
                                                                    public void actionPerformed(ActionEvent e) {
                                                                        String command2 = e.getActionCommand();
                                                                        if ("2".equals(command2)) {
                                                                            f2.dispose();
                                                                            Bet bet_donate = new DonateDecorator(bet);
                                                                            donate[0] = bet_donate.bet(userwager);

                                                                            JOptionPane.showMessageDialog(null, "You wager: $" + userwager + "\nYou will donate 5% wager for if you win. \nDonation: $" + (float) (0.05) * userwager,
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                            if (betcalc.calcReturn() > 0) {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            } else {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            }
                                                                            try {
                                                                                BetCalc.updateGameTrackDBConn();
                                                                                BetCalc.updateGameTrack(choice_final, user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            } catch (ClassNotFoundException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                        }
                                                                    }
                                                                });

                                                                JButton both = new JButton("3");
                                                                f2.add(both);
                                                                both.addActionListener(new AbstractAction() {
                                                                    @Override
                                                                    public void actionPerformed(ActionEvent e) {
                                                                        String command2 = e.getActionCommand();
                                                                        if ("3".equals(command2)) {
                                                                            f2.dispose();
                                                                            Bet bet_donate = new DonateDecorator(bet);
                                                                            Bet bet_insurance = new InsuranceDecorator(bet);
                                                                            donate[0] = bet_donate.bet(userwager);
                                                                            fee[0] = bet_insurance.bet(userwager);

                                                                            if(fee[0] + userwager <= user.balance.loadBalance()){
                                                                                JOptionPane.showMessageDialog(null, "You wager: $" + userwager + "\nYou pay 5% wager for insurance. \nInsurance fee: $" + (float) (0.05) * userwager +
                                                                                        "\nYou will donate 5% wager if you win. \nDonation: $" + (float) (0.05) * userwager, "Result", JOptionPane.PLAIN_MESSAGE);

                                                                                BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                                if (betcalc.calcReturn() > 0) {
                                                                                    try {
                                                                                        user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                    } catch (SQLException ex) {
                                                                                        throw new RuntimeException(ex);
                                                                                    }
                                                                                    JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                            "Result", JOptionPane.PLAIN_MESSAGE);

                                                                                } else {
                                                                                    try {
                                                                                        user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                    } catch (SQLException ex) {
                                                                                        throw new RuntimeException(ex);
                                                                                    }
                                                                                    JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                            "Result", JOptionPane.PLAIN_MESSAGE);

                                                                                }
                                                                                try {
                                                                                    BetCalc.updateGameTrackDBConn();
                                                                                    BetCalc.updateGameTrack(choice_final, user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                } catch (ClassNotFoundException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                            } else {
                                                                                JOptionPane.showMessageDialog(null, "No enough money.",
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);
                                                                            }

                                                                        }
                                                                    }
                                                                });

                                                                JButton no = new JButton("4");
                                                                f2.add(no);
                                                                no.addActionListener(new AbstractAction() {
                                                                    @Override
                                                                    public void actionPerformed(ActionEvent e) {
                                                                        String command2 = e.getActionCommand();
                                                                        if ("4".equals(command2)) {
                                                                            f2.dispose();
                                                                            fee[0] = bet.bet(userwager);

                                                                            BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                            if (betcalc.calcReturn() > 0) {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            } else {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            }
                                                                            try {
                                                                                BetCalc.updateGameTrackDBConn();
                                                                                BetCalc.updateGameTrack(choice_final, user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            } catch (ClassNotFoundException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }

                                                                        }
                                                                    }
                                                                });

                                                            } else {
                                                                //no enough money, show a new window to ask whether the user want to add money or not
                                                                UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("Verdana", Font.PLAIN, 13)));
                                                                UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("Verdana", Font.PLAIN, 13)));
                                                                Object[] options = {"Yes", "No"};
                                                                int response = JOptionPane.showOptionDialog(null, "You don't have enough money in current balance. \nDo you want to recharge?", "Balance", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                                                                if (response == 0) {
                                                                    //yes
                                                                    String add_s = JOptionPane.showInputDialog("Enter amount of money to add:");
                                                                    if (checknumber(add_s)){
                                                                        float add = Float.parseFloat(add_s);
                                                                        try {
                                                                            user.updateBalanceInUserAccount(add, user.userName);
                                                                        } catch (SQLException ex) {
                                                                            throw new RuntimeException(ex);
                                                                        }
                                                                        JOptionPane.showMessageDialog(null, "$" + add_s + " has been added." + "\nYour current balance: $" + user.balance.loadBalance(),
                                                                                "Result", JOptionPane.PLAIN_MESSAGE);

                                                                    } else {
                                                                        JOptionPane.showMessageDialog(null, "Invalid money input.",
                                                                                "Result", JOptionPane.PLAIN_MESSAGE);
                                                                    }
                                                                }
                                                            }

                                                        } else {
                                                            JOptionPane.showMessageDialog(null, "Invalid wager input.",
                                                                    "Result", JOptionPane.PLAIN_MESSAGE);
                                                        }
                                                    }

                                                }
                                            }
                                        });
                                    }
                                }
                            });

                            JButton a_odd = new JButton("2");
                            panel2.add(a_odd);
                            a_odd.addActionListener(new AbstractAction() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    String command2 = e.getActionCommand();
                                    if ("2".equals(command2)) {
                                        typechoice[0] = new Oddtype(new AmericanOdds());
                                        f.dispose();

                                        //show a new window with odds and allow the user to click which team wins or draw
                                        JFrame newFrame = new JFrame();
                                        newFrame.setSize(400, 150);
                                        newFrame.setTitle("Pick one to bet!");
                                        newFrame.setResizable(false);
                                        newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                        //newFrame.setLayout(new FlowLayout());
                                        newFrame.setLocationRelativeTo(null);
                                        newFrame.setVisible(true);
                                        JPanel panel = new JPanel();
                                        panel.setLayout(null);



                                        JLabel label = new JLabel("<html> 1. Home Team " + team1choice + " wins. Odds: " + typechoice[0].returnodd(odd.findOdd(userchoice))[0]
                                                + "<br/> 2. Away Team " + team2choice + " wins. Odds: " + typechoice[0].returnodd(odd.findOdd(userchoice))[1]
                                                + "<br/> 3. This is a draw. Odds: " + typechoice[0].returnodd(odd.findOdd(userchoice))[2] + "<html>");
                                        //newFrame.add(label);
                                        label.setBounds(30,5,350,70);
                                        panel.add(label);
                                        newFrame.add(panel);


                                        String actualresult = CreateFixture.fixtureList.get(choice_final).getWinner();
                                        if (actualresult.equals("H")) {
                                            actual_result = 1;
                                        } else if (actualresult.equals("A")) {
                                            actual_result = 2;
                                        } else if (actualresult.equals("D")) {
                                            actual_result = 3;
                                        } else {

                                        }

                                        JButton t1win = new JButton("1");
                                        t1win.setBounds(110,90,40,20);
                                        panel.add(t1win);
                                        //newFrame.add(t1win);
                                        int finalActual_result = actual_result;
                                        t1win.addActionListener(new AbstractAction() {
                                            @Override
                                            public void actionPerformed(ActionEvent e) {
                                                String command2 = e.getActionCommand();
                                                if ("1".equals(command2)) {
                                                    newFrame.dispose();
                                                    int userresult = 1;

                                                    final boolean[] bool = {true};
                                                    while (bool[0]) {
                                                        String wager = JOptionPane.showInputDialog("Enter wager (the amount of money to bet):");
                                                        if (wager == null) {
                                                            break;
                                                        }
                                                        if (checknumber(wager)) {
                                                            bool[0] = false;
                                                            float userwager = Float.parseFloat(wager);
                                                            if (user.balance.loadBalance() >= userwager) {
                                                                JFrame f2 = new JFrame();
                                                                f2.setSize(400, 150);
                                                                f2.setTitle("Bet");
                                                                f2.setResizable(false);
                                                                f2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                                                f2.setLayout(new FlowLayout());
                                                                f2.setLocationRelativeTo(null);
                                                                f2.setVisible(true);
                                                                JLabel label = new JLabel("<html> Do you want to donate or add insurance to your bet? <br/> 1. Add insurance " +
                                                                        "<br/> 2. Donate <br/> 3. Both <br/> 4. No <html>");
                                                                f2.add(label);

                                                                final float[] fee = {0};
                                                                final float[] donate = {0};
                                                                Bet bet = new BasicBet();

                                                                JButton insurance = new JButton("1");
                                                                f2.add(insurance);
                                                                insurance.addActionListener(new AbstractAction() {
                                                                    @Override
                                                                    public void actionPerformed(ActionEvent e) {
                                                                        String command2 = e.getActionCommand();
                                                                        if ("1".equals(command2)) {
                                                                            f2.dispose();
                                                                            Bet bet_insurance = new InsuranceDecorator(bet);
                                                                            fee[0] = bet_insurance.bet(userwager);

                                                                            if(fee[0] + userwager <= user.balance.loadBalance()){
                                                                                JOptionPane.showMessageDialog(null, "You wager: $" + userwager + "\nYou pay 5% wager for insurance. \nInsurance fee: $" + (float) (0.05) * userwager,
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                                BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                                if (betcalc.calcReturn() > 0) {
                                                                                    try {
                                                                                        user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                    } catch (SQLException ex) {
                                                                                        throw new RuntimeException(ex);
                                                                                    }
                                                                                    JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                            "Result", JOptionPane.PLAIN_MESSAGE);

                                                                                } else {
                                                                                    try {
                                                                                        user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                    } catch (SQLException ex) {
                                                                                        throw new RuntimeException(ex);
                                                                                    }
                                                                                    JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                            "Result", JOptionPane.PLAIN_MESSAGE);

                                                                                }
                                                                                try {
                                                                                    BetCalc.updateGameTrackDBConn();
                                                                                    BetCalc.updateGameTrack(choice_final, user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                } catch (ClassNotFoundException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                            } else {
                                                                                JOptionPane.showMessageDialog(null, "No enough money.",
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);
                                                                            }


                                                                        }
                                                                    }
                                                                });

                                                                JButton donation = new JButton("2");
                                                                f2.add(donation);
                                                                donation.addActionListener(new AbstractAction() {
                                                                    @Override
                                                                    public void actionPerformed(ActionEvent e) {
                                                                        String command2 = e.getActionCommand();
                                                                        if ("2".equals(command2)) {
                                                                            f2.dispose();
                                                                            Bet bet_donate = new DonateDecorator(bet);
                                                                            donate[0] = bet_donate.bet(userwager);

                                                                            JOptionPane.showMessageDialog(null, "You wager: $" + userwager + "\nYou will donate 5% wager for if you win. \nDonation: $" + (float) (0.05) * userwager,
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                            if (betcalc.calcReturn() > 0) {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            } else {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            }
                                                                            try {
                                                                                BetCalc.updateGameTrackDBConn();
                                                                                BetCalc.updateGameTrack(choice_final, user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            } catch (ClassNotFoundException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                        }
                                                                    }
                                                                });

                                                                JButton both = new JButton("3");
                                                                f2.add(both);
                                                                both.addActionListener(new AbstractAction() {
                                                                    @Override
                                                                    public void actionPerformed(ActionEvent e) {
                                                                        String command2 = e.getActionCommand();
                                                                        if ("3".equals(command2)) {
                                                                            f2.dispose();
                                                                            Bet bet_donate = new DonateDecorator(bet);
                                                                            Bet bet_insurance = new InsuranceDecorator(bet);
                                                                            donate[0] = bet_donate.bet(userwager);
                                                                            fee[0] = bet_insurance.bet(userwager);

                                                                            if(fee[0] + userwager <= user.balance.loadBalance()){
                                                                                JOptionPane.showMessageDialog(null, "You wager: $" + userwager + "\nYou pay 5% wager for insurance. \nInsurance fee: $" + (float) (0.05) * userwager +
                                                                                        "\nYou will donate 5% wager if you win. \nDonation: $" + (float) (0.05) * userwager, "Result", JOptionPane.PLAIN_MESSAGE);

                                                                                BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                                if (betcalc.calcReturn() > 0) {
                                                                                    try {
                                                                                        user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                    } catch (SQLException ex) {
                                                                                        throw new RuntimeException(ex);
                                                                                    }
                                                                                    JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                            "Result", JOptionPane.PLAIN_MESSAGE);

                                                                                } else {
                                                                                    try {
                                                                                        user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                    } catch (SQLException ex) {
                                                                                        throw new RuntimeException(ex);
                                                                                    }
                                                                                    JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                            "Result", JOptionPane.PLAIN_MESSAGE);

                                                                                }
                                                                                try {
                                                                                    BetCalc.updateGameTrackDBConn();
                                                                                    BetCalc.updateGameTrack(choice_final, user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                } catch (ClassNotFoundException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                            } else {
                                                                                JOptionPane.showMessageDialog(null, "No enough money.",
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);
                                                                            }

                                                                        }
                                                                    }
                                                                });

                                                                JButton no = new JButton("4");
                                                                f2.add(no);
                                                                no.addActionListener(new AbstractAction() {
                                                                    @Override
                                                                    public void actionPerformed(ActionEvent e) {
                                                                        String command2 = e.getActionCommand();
                                                                        if ("4".equals(command2)) {
                                                                            f2.dispose();
                                                                            fee[0] = bet.bet(userwager);

                                                                            BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                            if (betcalc.calcReturn() > 0) {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            } else {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            }
                                                                            try {
                                                                                BetCalc.updateGameTrackDBConn();
                                                                                BetCalc.updateGameTrack(choice_final, user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            } catch (ClassNotFoundException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }

                                                                        }
                                                                    }
                                                                });

                                                            } else {
                                                                //no enough money, show a new window to ask whether the user want to add money or not
                                                                UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("Verdana", Font.PLAIN, 13)));
                                                                UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("Verdana", Font.PLAIN, 13)));
                                                                Object[] options = {"Yes", "No"};
                                                                int response = JOptionPane.showOptionDialog(null, "You don't have enough money in current balance. \nDo you want to recharge?", "Balance", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                                                                if (response == 0) {
                                                                    //yes
                                                                    String add_s = JOptionPane.showInputDialog("Enter amount of money to add:");
                                                                    if (checknumber(add_s)){
                                                                        float add = Float.parseFloat(add_s);
                                                                        try {
                                                                            user.updateBalanceInUserAccount(add, user.userName);
                                                                        } catch (SQLException ex) {
                                                                            throw new RuntimeException(ex);
                                                                        }
                                                                        JOptionPane.showMessageDialog(null, "$" + add_s + " has been added." + "\nYour current balance: $" + user.balance.loadBalance(),
                                                                                "Result", JOptionPane.PLAIN_MESSAGE);

                                                                    } else {
                                                                        JOptionPane.showMessageDialog(null, "Invalid money input.",
                                                                                "Result", JOptionPane.PLAIN_MESSAGE);
                                                                    }
                                                                }
                                                            }

                                                        } else {
                                                            JOptionPane.showMessageDialog(null, "Invalid wager input.",
                                                                    "Result", JOptionPane.PLAIN_MESSAGE);
                                                        }

                                                    }

                                                }
                                            }
                                        });

                                        JButton t2win = new JButton("2");
                                        t2win.setBounds(190,90,40,20);
                                        panel.add(t2win);
                                        //newFrame.add(t1win);
                                        //newFrame.add(t2win);
                                        t2win.addActionListener(new AbstractAction() {
                                            @Override
                                            public void actionPerformed(ActionEvent e) {
                                                String command2 = e.getActionCommand();
                                                if ("2".equals(command2)) {
                                                    newFrame.dispose();
                                                    int userresult = 2;

                                                    final boolean[] bool = {true};
                                                    while (bool[0]) {
                                                        String wager = JOptionPane.showInputDialog("Enter wager (the amount of money to bet):");
                                                        if (wager == null) {
                                                            break;
                                                        }
                                                        if (checknumber(wager)) {
                                                            bool[0] = false;
                                                            float userwager = Float.parseFloat(wager);
                                                            if (user.balance.loadBalance() >= userwager) {
                                                                JFrame f2 = new JFrame();
                                                                f2.setSize(400, 150);
                                                                f2.setTitle("Bet");
                                                                f2.setResizable(false);
                                                                f2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                                                f2.setLayout(new FlowLayout());
                                                                f2.setLocationRelativeTo(null);
                                                                f2.setVisible(true);
                                                                JLabel label = new JLabel("<html> Do you want to donate or add insurance to your bet? <br/> 1. Add insurance " +
                                                                        "<br/> 2. Donate <br/> 3. Both <br/> 4. No <html>");
                                                                f2.add(label);

                                                                final float[] fee = {0};
                                                                final float[] donate = {0};
                                                                Bet bet = new BasicBet();

                                                                JButton insurance = new JButton("1");
                                                                f2.add(insurance);
                                                                insurance.addActionListener(new AbstractAction() {
                                                                    @Override
                                                                    public void actionPerformed(ActionEvent e) {
                                                                        String command2 = e.getActionCommand();
                                                                        if ("1".equals(command2)) {
                                                                            f2.dispose();
                                                                            Bet bet_insurance = new InsuranceDecorator(bet);
                                                                            fee[0] = bet_insurance.bet(userwager);

                                                                            if(fee[0] + userwager <= user.balance.loadBalance()){
                                                                                JOptionPane.showMessageDialog(null, "You wager: $" + userwager + "\nYou pay 5% wager for insurance. \nInsurance fee: $" + (float) (0.05) * userwager,
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                                BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                                if (betcalc.calcReturn() > 0) {
                                                                                    try {
                                                                                        user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                    } catch (SQLException ex) {
                                                                                        throw new RuntimeException(ex);
                                                                                    }
                                                                                    JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                            "Result", JOptionPane.PLAIN_MESSAGE);

                                                                                } else {
                                                                                    try {
                                                                                        user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                    } catch (SQLException ex) {
                                                                                        throw new RuntimeException(ex);
                                                                                    }
                                                                                    JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                            "Result", JOptionPane.PLAIN_MESSAGE);

                                                                                }
                                                                                try {
                                                                                    BetCalc.updateGameTrackDBConn();
                                                                                    BetCalc.updateGameTrack(choice_final, user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                } catch (ClassNotFoundException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                            } else {
                                                                                JOptionPane.showMessageDialog(null, "No enough money.",
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);
                                                                            }


                                                                        }
                                                                    }
                                                                });

                                                                JButton donation = new JButton("2");
                                                                f2.add(donation);
                                                                donation.addActionListener(new AbstractAction() {
                                                                    @Override
                                                                    public void actionPerformed(ActionEvent e) {
                                                                        String command2 = e.getActionCommand();
                                                                        if ("2".equals(command2)) {
                                                                            f2.dispose();
                                                                            Bet bet_donate = new DonateDecorator(bet);
                                                                            donate[0] = bet_donate.bet(userwager);

                                                                            JOptionPane.showMessageDialog(null, "You wager: $" + userwager + "\nYou will donate 5% wager for if you win. \nDonation: $" + (float) (0.05) * userwager,
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                            if (betcalc.calcReturn() > 0) {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            } else {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            }
                                                                            try {
                                                                                BetCalc.updateGameTrackDBConn();
                                                                                BetCalc.updateGameTrack(choice_final, user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            } catch (ClassNotFoundException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                        }
                                                                    }
                                                                });

                                                                JButton both = new JButton("3");
                                                                f2.add(both);
                                                                both.addActionListener(new AbstractAction() {
                                                                    @Override
                                                                    public void actionPerformed(ActionEvent e) {
                                                                        String command2 = e.getActionCommand();
                                                                        if ("3".equals(command2)) {
                                                                            f2.dispose();
                                                                            Bet bet_donate = new DonateDecorator(bet);
                                                                            Bet bet_insurance = new InsuranceDecorator(bet);
                                                                            donate[0] = bet_donate.bet(userwager);
                                                                            fee[0] = bet_insurance.bet(userwager);

                                                                            if(fee[0] + userwager <= user.balance.loadBalance()){
                                                                                JOptionPane.showMessageDialog(null, "You wager: $" + userwager + "\nYou pay 5% wager for insurance. \nInsurance fee: $" + (float) (0.05) * userwager +
                                                                                        "\nYou will donate 5% wager if you win. \nDonation: $" + (float) (0.05) * userwager, "Result", JOptionPane.PLAIN_MESSAGE);

                                                                                BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                                if (betcalc.calcReturn() > 0) {
                                                                                    try {
                                                                                        user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                    } catch (SQLException ex) {
                                                                                        throw new RuntimeException(ex);
                                                                                    }
                                                                                    JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                            "Result", JOptionPane.PLAIN_MESSAGE);

                                                                                } else {
                                                                                    try {
                                                                                        user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                    } catch (SQLException ex) {
                                                                                        throw new RuntimeException(ex);
                                                                                    }
                                                                                    JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                            "Result", JOptionPane.PLAIN_MESSAGE);

                                                                                }
                                                                                try {
                                                                                    BetCalc.updateGameTrackDBConn();
                                                                                    BetCalc.updateGameTrack(choice_final, user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                } catch (ClassNotFoundException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                            } else {
                                                                                JOptionPane.showMessageDialog(null, "No enough money.",
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);
                                                                            }

                                                                        }
                                                                    }
                                                                });

                                                                JButton no = new JButton("4");
                                                                f2.add(no);
                                                                no.addActionListener(new AbstractAction() {
                                                                    @Override
                                                                    public void actionPerformed(ActionEvent e) {
                                                                        String command2 = e.getActionCommand();
                                                                        if ("4".equals(command2)) {
                                                                            f2.dispose();
                                                                            fee[0] = bet.bet(userwager);

                                                                            BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                            if (betcalc.calcReturn() > 0) {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            } else {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            }
                                                                            try {
                                                                                BetCalc.updateGameTrackDBConn();
                                                                                BetCalc.updateGameTrack(choice_final, user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            } catch (ClassNotFoundException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }

                                                                        }
                                                                    }
                                                                });

                                                            } else {
                                                                //no enough money, show a new window to ask whether the user want to add money or not
                                                                UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("Verdana", Font.PLAIN, 13)));
                                                                UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("Verdana", Font.PLAIN, 13)));
                                                                Object[] options = {"Yes", "No"};
                                                                int response = JOptionPane.showOptionDialog(null, "You don't have enough money in current balance. \nDo you want to recharge?", "Balance", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                                                                if (response == 0) {
                                                                    //yes
                                                                    String add_s = JOptionPane.showInputDialog("Enter amount of money to add:");
                                                                    if (checknumber(add_s)){
                                                                        float add = Float.parseFloat(add_s);
                                                                        try {
                                                                            user.updateBalanceInUserAccount(add, user.userName);
                                                                        } catch (SQLException ex) {
                                                                            throw new RuntimeException(ex);
                                                                        }
                                                                        JOptionPane.showMessageDialog(null, "$" + add_s + " has been added." + "\nYour current balance: $" + user.balance.loadBalance(),
                                                                                "Result", JOptionPane.PLAIN_MESSAGE);

                                                                    } else {
                                                                        JOptionPane.showMessageDialog(null, "Invalid money input.",
                                                                                "Result", JOptionPane.PLAIN_MESSAGE);
                                                                    }
                                                                }
                                                            }

                                                        } else {
                                                            JOptionPane.showMessageDialog(null, "Invalid wager input.",
                                                                    "Result", JOptionPane.PLAIN_MESSAGE);
                                                        }
                                                    }
                                                }
                                            }
                                        });

                                        JButton draw = new JButton("3");
                                        draw.setBounds(270,90,40,20);
                                        panel.add(draw);
                                        //newFrame.add(draw);
                                        draw.addActionListener(new AbstractAction() {
                                            @Override
                                            public void actionPerformed(ActionEvent e) {
                                                String command2 = e.getActionCommand();
                                                if ("3".equals(command2)) {
                                                    newFrame.dispose();
                                                    int userresult = 3;

                                                    final boolean[] bool = {true};
                                                    while (bool[0]) {
                                                        String wager = JOptionPane.showInputDialog("Enter wager (the amount of money to bet):");
                                                        if (wager == null) {
                                                            break;
                                                        }
                                                        if (checknumber(wager)) {
                                                            bool[0] = false;
                                                            float userwager = Float.parseFloat(wager);
                                                            if (user.balance.loadBalance() >= userwager) {

                                                                JFrame f2 = new JFrame();
                                                                f2.setSize(400, 150);
                                                                f2.setTitle("Bet");
                                                                f2.setResizable(false);
                                                                f2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                                                f2.setLayout(new FlowLayout());
                                                                f2.setLocationRelativeTo(null);
                                                                f2.setVisible(true);
                                                                JLabel label = new JLabel("<html> Do you want to donate or add insurance to your bet? <br/> 1. Add insurance " +
                                                                        "<br/> 2. Donate <br/> 3. Both <br/> 4. No <html>");
                                                                f2.add(label);

                                                                final float[] fee = {0};
                                                                final float[] donate = {0};
                                                                Bet bet = new BasicBet();

                                                                JButton insurance = new JButton("1");
                                                                f2.add(insurance);
                                                                insurance.addActionListener(new AbstractAction() {
                                                                    @Override
                                                                    public void actionPerformed(ActionEvent e) {
                                                                        String command2 = e.getActionCommand();
                                                                        if ("1".equals(command2)) {
                                                                            f2.dispose();
                                                                            Bet bet_insurance = new InsuranceDecorator(bet);
                                                                            fee[0] = bet_insurance.bet(userwager);

                                                                            if(fee[0] + userwager <= user.balance.loadBalance()){
                                                                                JOptionPane.showMessageDialog(null, "You wager: $" + userwager + "\nYou pay 5% wager for insurance. \nInsurance fee: $" + (float) (0.05) * userwager,
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                                BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                                if (betcalc.calcReturn() > 0) {
                                                                                    try {
                                                                                        user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                    } catch (SQLException ex) {
                                                                                        throw new RuntimeException(ex);
                                                                                    }
                                                                                    JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                            "Result", JOptionPane.PLAIN_MESSAGE);

                                                                                } else {
                                                                                    try {
                                                                                        user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                    } catch (SQLException ex) {
                                                                                        throw new RuntimeException(ex);
                                                                                    }
                                                                                    JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                            "Result", JOptionPane.PLAIN_MESSAGE);

                                                                                }
                                                                                try {
                                                                                    BetCalc.updateGameTrackDBConn();
                                                                                    BetCalc.updateGameTrack(choice_final, user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                } catch (ClassNotFoundException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                            } else {
                                                                                JOptionPane.showMessageDialog(null, "No enough money.",
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);
                                                                            }


                                                                        }
                                                                    }
                                                                });

                                                                JButton donation = new JButton("2");
                                                                f2.add(donation);
                                                                donation.addActionListener(new AbstractAction() {
                                                                    @Override
                                                                    public void actionPerformed(ActionEvent e) {
                                                                        String command2 = e.getActionCommand();
                                                                        if ("2".equals(command2)) {
                                                                            f2.dispose();
                                                                            Bet bet_donate = new DonateDecorator(bet);
                                                                            donate[0] = bet_donate.bet(userwager);

                                                                            JOptionPane.showMessageDialog(null, "You wager: $" + userwager + "\nYou will donate 5% wager for if you win. \nDonation: $" + (float) (0.05) * userwager,
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                            if (betcalc.calcReturn() > 0) {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            } else {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            }
                                                                            try {
                                                                                BetCalc.updateGameTrackDBConn();
                                                                                BetCalc.updateGameTrack(choice_final, user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            } catch (ClassNotFoundException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                        }
                                                                    }
                                                                });

                                                                JButton both = new JButton("3");
                                                                f2.add(both);
                                                                both.addActionListener(new AbstractAction() {
                                                                    @Override
                                                                    public void actionPerformed(ActionEvent e) {
                                                                        String command2 = e.getActionCommand();
                                                                        if ("3".equals(command2)) {
                                                                            f2.dispose();
                                                                            Bet bet_donate = new DonateDecorator(bet);
                                                                            Bet bet_insurance = new InsuranceDecorator(bet);
                                                                            donate[0] = bet_donate.bet(userwager);
                                                                            fee[0] = bet_insurance.bet(userwager);

                                                                            if(fee[0] + userwager <= user.balance.loadBalance()){
                                                                                JOptionPane.showMessageDialog(null, "You wager: $" + userwager + "\nYou pay 5% wager for insurance. \nInsurance fee: $" + (float) (0.05) * userwager +
                                                                                        "\nYou will donate 5% wager if you win. \nDonation: $" + (float) (0.05) * userwager, "Result", JOptionPane.PLAIN_MESSAGE);

                                                                                BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                                if (betcalc.calcReturn() > 0) {
                                                                                    try {
                                                                                        user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                    } catch (SQLException ex) {
                                                                                        throw new RuntimeException(ex);
                                                                                    }
                                                                                    JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                            "Result", JOptionPane.PLAIN_MESSAGE);

                                                                                } else {
                                                                                    try {
                                                                                        user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                        user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                    } catch (SQLException ex) {
                                                                                        throw new RuntimeException(ex);
                                                                                    }
                                                                                    JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                            "Result", JOptionPane.PLAIN_MESSAGE);

                                                                                }
                                                                                try {
                                                                                    BetCalc.updateGameTrackDBConn();
                                                                                    BetCalc.updateGameTrack(choice_final, user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                } catch (ClassNotFoundException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                            } else {
                                                                                JOptionPane.showMessageDialog(null, "No enough money.",
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);
                                                                            }

                                                                        }
                                                                    }
                                                                });

                                                                JButton no = new JButton("4");
                                                                f2.add(no);
                                                                no.addActionListener(new AbstractAction() {
                                                                    @Override
                                                                    public void actionPerformed(ActionEvent e) {
                                                                        String command2 = e.getActionCommand();
                                                                        if ("4".equals(command2)) {
                                                                            f2.dispose();
                                                                            fee[0] = bet.bet(userwager);

                                                                            BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                            if (betcalc.calcReturn() > 0) {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            } else {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            }
                                                                            try {
                                                                                BetCalc.updateGameTrackDBConn();
                                                                                BetCalc.updateGameTrack(choice_final, user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            } catch (ClassNotFoundException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }

                                                                        }
                                                                    }
                                                                });

                                                            } else {
                                                                //no enough money, show a new window to ask whether the user want to add money or not
                                                                UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("Verdana", Font.PLAIN, 13)));
                                                                UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("Verdana", Font.PLAIN, 13)));
                                                                Object[] options = {"Yes", "No"};
                                                                int response = JOptionPane.showOptionDialog(null, "You don't have enough money in current balance. \nDo you want to recharge?", "Balance", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                                                                if (response == 0) {
                                                                    //yes
                                                                    String add_s = JOptionPane.showInputDialog("Enter amount of money to add:");
                                                                    if (checknumber(add_s)){
                                                                        float add = Float.parseFloat(add_s);
                                                                        try {
                                                                            user.updateBalanceInUserAccount(add, user.userName);
                                                                        } catch (SQLException ex) {
                                                                            throw new RuntimeException(ex);
                                                                        }
                                                                        JOptionPane.showMessageDialog(null, "$" + add_s + " has been added." + "\nYour current balance: $" + user.balance.loadBalance(),
                                                                                "Result", JOptionPane.PLAIN_MESSAGE);

                                                                    } else {
                                                                        JOptionPane.showMessageDialog(null, "Invalid money input.",
                                                                                "Result", JOptionPane.PLAIN_MESSAGE);
                                                                    }
                                                                }
                                                            }

                                                        } else {
                                                            JOptionPane.showMessageDialog(null, "Invalid wager input.",
                                                                    "Result", JOptionPane.PLAIN_MESSAGE);
                                                        }
                                                    }

                                                }
                                            }
                                        });

                                    }
                                }
                            });


                        } else if (bool == true) {
                            JFrame newFrame = new JFrame();
                            newFrame.setSize(400, 150);
                            newFrame.setTitle("Result");
                            newFrame.setResizable(false);
                            newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                            newFrame.setLayout(new FlowLayout());
                            newFrame.setLocationRelativeTo(null);
                            JLabel label = new JLabel("UNABLE TO PROCEED: You have already bet on this game!");
                            newFrame.add(label);
                            newFrame.setVisible(true);
                            JButton back = new JButton("Return");
                            newFrame.add(back);
                            back.addActionListener(new AbstractAction() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    String command2 = e.getActionCommand();
                                    if ("Return".equals(command2)) {
                                        newFrame.dispose();
                                    }
                                }
                            });

                        } else {
                            JFrame newFrame = new JFrame();
                            newFrame.setSize(400, 150);
                            newFrame.setTitle("Result");
                            newFrame.setResizable(false);
                            newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                            newFrame.setLayout(new FlowLayout());
                            newFrame.setLocationRelativeTo(null);
                            JLabel label = new JLabel("Invalid game number entered.");
                            newFrame.add(label);
                            newFrame.setVisible(true);
                            JButton back = new JButton("Return");
                            newFrame.add(back);
                            back.addActionListener(new AbstractAction() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    String command2 = e.getActionCommand();
                                    if ("Return".equals(command2)) {
                                        newFrame.dispose();
                                    }
                                }
                            });
                        }

                    } else {
                        JFrame newFrame = new JFrame();
                        newFrame.setSize(400, 150);
                        newFrame.setTitle("Result");
                        newFrame.setResizable(false);
                        newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        newFrame.setLayout(new FlowLayout());
                        newFrame.setLocationRelativeTo(null);
                        JLabel label = new JLabel("Invalid game number entered.");
                        newFrame.add(label);
                        newFrame.setVisible(true);
                        JButton back = new JButton("Return");
                        newFrame.add(back);
                        back.addActionListener(new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                String command2 = e.getActionCommand();
                                if ("Return".equals(command2)) {
                                    newFrame.dispose();
                                }
                            }
                        });
                    }
                }
            }
        });
        //testFrame.add(enter);
        testFrame.setVisible(true);

    }

    public void startBet2(User user) throws IOException, SQLException, ClassNotFoundException {
        final int[] index = {0};
        final int[] team1 = {0};
        final int[] team2 = {0};

        //todo add TeamStat to teamList from SQL test
        CreateTeamData.makeTeamStatToDBConn();
        CreateTeamData.addSQLToTeamList();
        //CreateTeamData.addTeamStatFromFile("pastSeason.txt");

        //todo add Fixture to fixtureList from SQL test
        CreateFixture.makeFixToDBConn();
        CreateFixture.addSQLToFixtureList();
        //CreateFixture.addFixtureFromFile("pySoccer.txt");

        //enter team names
        JFrame testFrame = new JFrame("Bet");
        //testFrame.setSize(450, 150);
        JTextArea jta = new JTextArea();
        JScrollPane jsp = new JScrollPane(jta);
        JPanel panel = new JPanel();
        //panel.setSize(500,150);
        //jta.setBounds(200, 100,500,300);
        //jsp.setBounds(200, 100, 480, 350);
        testFrame.add(jsp, BorderLayout.NORTH);
        jta.setEditable(false);
        testFrame.setBounds(200, 100, 480, 480);
        testFrame.setResizable(false);
        testFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        testFrame.setLocationRelativeTo(null);
        testFrame.setVisible(true);

        //list all teams in scroll panel
        for (int i = 0; i < CreateTeamData.teamList.size(); i++) {
            int num = i + 1;
            jta.append(num + ". " + CreateTeamData.teamList.get(i).toString() + System.lineSeparator());
        }

        JButton enter = new JButton("Enter");
        JLabel text = new JLabel("Enter Home Team name:");
        JTextField textBook = new JTextField(20);
        panel.add(text);
        panel.add(textBook);

        JLabel text2 = new JLabel("Enter Away Team name:");
        JTextField textBook2 = new JTextField(20);
        panel.add(text2);
        panel.add(textBook2);

        panel.add(enter);
        testFrame.add(panel);
        enter.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String command = e.getActionCommand();
                testFrame.dispose();
                if ("Enter".equals(command)) {
                    
                    String homeTeam = textBook.getText().toLowerCase();
                    String awayTeam = textBook2.getText().toLowerCase();
                    
                    //String homeTeam = textBook.getText();
                    //String awayTeam = textBook2.getText();
                    
                    boolean flag = false;
                    ///original i = 1
                    for (int i = 0; i < CreateFixture.fixtureList.size(); i++) {
                        if (homeTeam.equals(CreateFixture.fixtureList.get(i).getHomeTeam().toLowerCase()) && awayTeam.equals(CreateFixture.fixtureList.get(i).getAwayTeam().toLowerCase())) {
                            flag = true;
                            index[0] = i;
                        }
                    }
                    //todo testing if user has bet already or not
                    boolean bool;
                    try {
                        BetCalc.updateGameTrackDBConn();
                        bool = BetCalc.checkGameTrackExists(index[0], user.userName);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    } catch (ClassNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                    if (flag == true && bool == false) {
                        //flag = true
                        //show confirm window first
                        JOptionPane.showMessageDialog(null, "The game you picked is:" + "\nHome Team: " + homeTeam + "\nAway Team: " + awayTeam,
                                "Result", JOptionPane.PLAIN_MESSAGE);
                        //after confirm
                        for (int j = 0; j < CreateTeamData.teamList.size(); j++) {
                            if (homeTeam.equals(CreateTeamData.teamList.get(j).getTeamName())) {
                                team1[0] = j;
                            }
                        }
                        for (int j = 0; j < CreateTeamData.teamList.size(); j++) {
                            if (awayTeam.equals(CreateTeamData.teamList.get(j).getTeamName())) {
                                team2[0] = j;
                            }
                        }
                        TeamChoice userchoice = new TeamChoice(team1[0], team2[0]);
                        ////show odds
                        oddCalc odd = new oddCalc();
                        odd.findOdd(userchoice);

                        final Oddtype[] typechoice = {new Oddtype(new DecimalOdds())};

                        JFrame f = new JFrame();
                        f.setSize(400, 150);
                        f.setTitle("Pick one to bet!");
                        f.setResizable(false);
                        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        f.setLayout(new FlowLayout());
                        f.setLocationRelativeTo(null);
                        f.setVisible(true);

                        JLabel label = new JLabel("<html>Choose a type of odds: <br/> 1. Decimal odds <br/> 2. American odds  <html>");
                        f.add(label);

                        JButton d_odd = new JButton("1");
                        f.add(d_odd);
                        d_odd.addActionListener(new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                String command2 = e.getActionCommand();
                                if ("1".equals(command2)) {
                                    typechoice[0] = new Oddtype(new DecimalOdds());
                                    f.dispose();

                                    //show a new window with odds and allow the user to click which team wins or draw
                                    JFrame newFrame = new JFrame();
                                    newFrame.setSize(400, 150);
                                    newFrame.setTitle("Pick one to bet!");
                                    newFrame.setResizable(false);
                                    newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                    //newFrame.setLayout(new FlowLayout());
                                    newFrame.setLocationRelativeTo(null);
                                    newFrame.setVisible(true);
                                    JPanel panel = new JPanel();
                                    panel.setLayout(null);



                                    JLabel label = new JLabel("<html> 1. Home Team " + homeTeam + " wins. Odds: " + typechoice[0].returnodd(odd.findOdd(userchoice))[0]
                                            + "<br/> 2. Away Team " + awayTeam + " wins. Odds: " + typechoice[0].returnodd(odd.findOdd(userchoice))[1]
                                            + "<br/> 3. This is a draw. Odds: " + typechoice[0].returnodd(odd.findOdd(userchoice))[2] + "<html>");
                                    //newFrame.add(label);
                                    label.setBounds(30,5,350,70);
                                    panel.add(label);
                                    newFrame.add(panel);


                                    String actualresult = CreateFixture.fixtureList.get(index[0]).getWinner();
                                    if (actualresult.equals("H")) {
                                        actual_result = 1;
                                    } else if (actualresult.equals("A")) {
                                        actual_result = 2;
                                    } else if (actualresult.equals("D")) {
                                        actual_result = 3;
                                    } else {

                                    }

                                    JButton t1win = new JButton("1");
                                    t1win.setBounds(110,90,40,20);
                                    panel.add(t1win);
                                    //newFrame.add(t1win);
                                    int finalActual_result = actual_result;
                                    t1win.addActionListener(new AbstractAction() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            String command2 = e.getActionCommand();
                                            if ("1".equals(command2)) {
                                                newFrame.dispose();
                                                int userresult = 1;

                                                final boolean[] bool = {true};
                                                while (bool[0]) {
                                                    String wager = JOptionPane.showInputDialog("Enter wager (the amount of money to bet):");
                                                    if(wager == null){
                                                        break;
                                                    }
                                                    if (checknumber(wager)) {
                                                        bool[0] = false;
                                                        float userwager = Float.parseFloat(wager);
                                                        if (user.balance.loadBalance() >= userwager) {
                                                            JFrame f2 = new JFrame();
                                                            f2.setSize(400, 150);
                                                            f2.setTitle("Bet");
                                                            f2.setResizable(false);
                                                            f2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                                            f2.setLayout(new FlowLayout());
                                                            f2.setLocationRelativeTo(null);
                                                            f2.setVisible(true);
                                                            JLabel label = new JLabel("<html> Do you want to donate or add insurance to your bet? <br/> 1. Add insurance " +
                                                                    "<br/> 2. Donate <br/> 3. Both <br/> 4. No <html>");
                                                            f2.add(label);

                                                            final float[] fee = {0};
                                                            final float[] donate = {0};
                                                            Bet bet = new BasicBet();

                                                            JButton insurance = new JButton("1");
                                                            f2.add(insurance);
                                                            insurance.addActionListener(new AbstractAction() {
                                                                @Override
                                                                public void actionPerformed(ActionEvent e) {
                                                                    String command2 = e.getActionCommand();
                                                                    if ("1".equals(command2)) {
                                                                        f2.dispose();
                                                                        Bet bet_insurance = new InsuranceDecorator(bet);
                                                                        fee[0] = bet_insurance.bet(userwager);

                                                                        if(fee[0] + userwager <= user.balance.loadBalance()) {
                                                                            JOptionPane.showMessageDialog(null, "You wager: $" + userwager + "\nYou pay 5% wager for insurance. \nInsurance fee: $" + (float)(0.05) * userwager,
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                            if (betcalc.calcReturn() > 0) {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            } else {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            }
                                                                            try {
                                                                                BetCalc.updateGameTrackDBConn();
                                                                                BetCalc.updateGameTrack(index[0], user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            } catch (ClassNotFoundException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                        } else {
                                                                            JOptionPane.showMessageDialog(null, "No enough money.",
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);
                                                                        }

                                                                    }
                                                                }
                                                            });

                                                            JButton donation = new JButton("2");
                                                            f2.add(donation);
                                                            donation.addActionListener(new AbstractAction() {
                                                                @Override
                                                                public void actionPerformed(ActionEvent e) {
                                                                    String command2 = e.getActionCommand();
                                                                    if ("2".equals(command2)) {
                                                                        f2.dispose();
                                                                        Bet bet_donate = new DonateDecorator(bet);
                                                                        donate[0] = bet_donate.bet(userwager);

                                                                        JOptionPane.showMessageDialog(null, "You wager: $" + userwager + "\nYou will donate 5% wager for if you win. \nDonation: $" + (float)(0.05) * userwager,
                                                                                "Result", JOptionPane.PLAIN_MESSAGE);

                                                                        BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                        if (betcalc.calcReturn() > 0) {
                                                                            try {
                                                                                user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                            JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);

                                                                        } else {
                                                                            try {
                                                                                user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                            JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);

                                                                        }
                                                                        try {
                                                                            BetCalc.updateGameTrackDBConn();
                                                                            BetCalc.updateGameTrack(index[0], user.userName);
                                                                        } catch (SQLException ex) {
                                                                            throw new RuntimeException(ex);
                                                                        } catch (ClassNotFoundException ex) {
                                                                            throw new RuntimeException(ex);
                                                                        }
                                                                    }
                                                                }
                                                            });

                                                            JButton both = new JButton("3");
                                                            f2.add(both);
                                                            both.addActionListener(new AbstractAction() {
                                                                @Override
                                                                public void actionPerformed(ActionEvent e) {
                                                                    String command2 = e.getActionCommand();
                                                                    if ("3".equals(command2)) {
                                                                        f2.dispose();
                                                                        Bet bet_donate = new DonateDecorator(bet);
                                                                        Bet bet_insurance = new InsuranceDecorator(bet);
                                                                        donate[0] = bet_donate.bet(userwager);
                                                                        fee[0] = bet_insurance.bet(userwager);

                                                                        if(fee[0] + userwager <= user.balance.loadBalance()){
                                                                            JOptionPane.showMessageDialog(null, "You wager: $" + userwager + "\nYou pay 5% wager for insurance. \nInsurance fee: $" + (float)(0.05) * userwager +
                                                                                    "\nYou will donate 5% wager if you win. \nDonation: $" +(float)(0.05) * userwager, "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                            if (betcalc.calcReturn() > 0) {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            } else {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            }
                                                                            try {
                                                                                BetCalc.updateGameTrackDBConn();
                                                                                BetCalc.updateGameTrack(index[0], user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            } catch (ClassNotFoundException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                        } else {
                                                                            JOptionPane.showMessageDialog(null, "No enough money.",
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);
                                                                        }



                                                                    }
                                                                }
                                                            });

                                                            JButton no = new JButton("4");
                                                            f2.add(no);
                                                            no.addActionListener(new AbstractAction() {
                                                                @Override
                                                                public void actionPerformed(ActionEvent e) {
                                                                    String command2 = e.getActionCommand();
                                                                    if ("4".equals(command2)) {
                                                                        f2.dispose();
                                                                        fee[0] = bet.bet(userwager);

                                                                        BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                        if (betcalc.calcReturn() > 0) {
                                                                            try {
                                                                                user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                            JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);

                                                                        } else {
                                                                            try {
                                                                                user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                            JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);

                                                                        }
                                                                        try {
                                                                            BetCalc.updateGameTrackDBConn();
                                                                            BetCalc.updateGameTrack(index[0], user.userName);
                                                                        } catch (SQLException ex) {
                                                                            throw new RuntimeException(ex);
                                                                        } catch (ClassNotFoundException ex) {
                                                                            throw new RuntimeException(ex);
                                                                        }

                                                                    }
                                                                }
                                                            });

                                                        } else {
                                                            //no enough money, show a new window to ask whether the user want to add money or not
                                                            UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("Verdana", Font.PLAIN, 13)));
                                                            UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("Verdana", Font.PLAIN, 13)));
                                                            Object[] options = {"Yes", "No"};
                                                            int response = JOptionPane.showOptionDialog(null, "You don't have enough money in current balance. \nDo you want to recharge?", "Balance", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                                                            if (response == 0) {
                                                                //yes
                                                                String add_s = JOptionPane.showInputDialog("Enter amount of money to add:");
                                                                if (checknumber(add_s)){
                                                                    float add = Float.parseFloat(add_s);
                                                                    try {
                                                                        user.updateBalanceInUserAccount(add, user.userName);
                                                                    } catch (SQLException ex) {
                                                                        throw new RuntimeException(ex);
                                                                    }
                                                                    JOptionPane.showMessageDialog(null, "$" + add_s + " has been added." + "\nYour current balance: $" + user.balance.loadBalance(),
                                                                            "Result", JOptionPane.PLAIN_MESSAGE);

                                                                } else {
                                                                    JOptionPane.showMessageDialog(null, "Invalid money input.",
                                                                            "Result", JOptionPane.PLAIN_MESSAGE);
                                                                }
                                                            }
                                                        }

                                                    } else {
                                                        JOptionPane.showMessageDialog(null, "Invalid wager input.",
                                                                "Result", JOptionPane.PLAIN_MESSAGE);
                                                    }

                                                }

                                            }
                                        }
                                    });

                                    JButton t2win = new JButton("2");
                                    t2win.setBounds(190,90,40,20);
                                    panel.add(t2win);
                                    //newFrame.add(t2win);
                                    t2win.addActionListener(new AbstractAction() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            String command2 = e.getActionCommand();
                                            if ("2".equals(command2)) {
                                                newFrame.dispose();
                                                int userresult = 2;

                                                final boolean[] bool = {true};
                                                while (bool[0]) {
                                                    String wager = JOptionPane.showInputDialog("Enter wager (the amount of money to bet):");
                                                    if(wager == null){
                                                        break;
                                                    }
                                                    if (checknumber(wager)) {
                                                        bool[0] = false;
                                                        float userwager = Float.parseFloat(wager);
                                                        if (user.balance.loadBalance() >= userwager) {
                                                            JFrame f2 = new JFrame();
                                                            f2.setSize(400, 150);
                                                            f2.setTitle("Bet");
                                                            f2.setResizable(false);
                                                            f2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                                            f2.setLayout(new FlowLayout());
                                                            f2.setLocationRelativeTo(null);
                                                            f2.setVisible(true);
                                                            JLabel label = new JLabel("<html> Do you want to donate or add insurance to your bet? <br/> 1. Add insurance " +
                                                                    "<br/> 2. Donate <br/> 3. Both <br/> 4. No <html>");
                                                            f2.add(label);

                                                            final float[] fee = {0};
                                                            final float[] donate = {0};
                                                            Bet bet = new BasicBet();

                                                            JButton insurance = new JButton("1");
                                                            f2.add(insurance);
                                                            insurance.addActionListener(new AbstractAction() {
                                                                @Override
                                                                public void actionPerformed(ActionEvent e) {
                                                                    String command2 = e.getActionCommand();
                                                                    if ("1".equals(command2)) {
                                                                        f2.dispose();
                                                                        Bet bet_insurance = new InsuranceDecorator(bet);
                                                                        fee[0] = bet_insurance.bet(userwager);

                                                                        if(fee[0] + userwager <= user.balance.loadBalance()) {
                                                                            JOptionPane.showMessageDialog(null, "You wager: $" + userwager + "\nYou pay 5% wager for insurance. \nInsurance fee: $" + (float)(0.05) * userwager,
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                            if (betcalc.calcReturn() > 0) {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            } else {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            }
                                                                            try {
                                                                                BetCalc.updateGameTrackDBConn();
                                                                                BetCalc.updateGameTrack(index[0], user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            } catch (ClassNotFoundException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                        } else {
                                                                            JOptionPane.showMessageDialog(null, "No enough money.",
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);
                                                                        }

                                                                    }
                                                                }
                                                            });

                                                            JButton donation = new JButton("2");
                                                            f2.add(donation);
                                                            donation.addActionListener(new AbstractAction() {
                                                                @Override
                                                                public void actionPerformed(ActionEvent e) {
                                                                    String command2 = e.getActionCommand();
                                                                    if ("2".equals(command2)) {
                                                                        f2.dispose();
                                                                        Bet bet_donate = new DonateDecorator(bet);
                                                                        donate[0] = bet_donate.bet(userwager);

                                                                        JOptionPane.showMessageDialog(null, "You wager: $" + userwager + "\nYou will donate 5% wager for if you win. \nDonation: $" + (float)(0.05) * userwager,
                                                                                "Result", JOptionPane.PLAIN_MESSAGE);

                                                                        BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                        if (betcalc.calcReturn() > 0) {
                                                                            try {
                                                                                user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                            JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);

                                                                        } else {
                                                                            try {
                                                                                user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                            JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);

                                                                        }
                                                                        try {
                                                                            BetCalc.updateGameTrackDBConn();
                                                                            BetCalc.updateGameTrack(index[0], user.userName);
                                                                        } catch (SQLException ex) {
                                                                            throw new RuntimeException(ex);
                                                                        } catch (ClassNotFoundException ex) {
                                                                            throw new RuntimeException(ex);
                                                                        }
                                                                    }
                                                                }
                                                            });

                                                            JButton both = new JButton("3");
                                                            f2.add(both);
                                                            both.addActionListener(new AbstractAction() {
                                                                @Override
                                                                public void actionPerformed(ActionEvent e) {
                                                                    String command2 = e.getActionCommand();
                                                                    if ("3".equals(command2)) {
                                                                        f2.dispose();
                                                                        Bet bet_donate = new DonateDecorator(bet);
                                                                        Bet bet_insurance = new InsuranceDecorator(bet);
                                                                        donate[0] = bet_donate.bet(userwager);
                                                                        fee[0] = bet_insurance.bet(userwager);

                                                                        if(fee[0] + userwager <= user.balance.loadBalance()){
                                                                            JOptionPane.showMessageDialog(null, "You wager: $" + userwager + "\nYou pay 5% wager for insurance. \nInsurance fee: $" + (float)(0.05) * userwager +
                                                                                    "\nYou will donate 5% wager if you win. \nDonation: $" +(float)(0.05) * userwager, "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                            if (betcalc.calcReturn() > 0) {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            } else {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            }
                                                                            try {
                                                                                BetCalc.updateGameTrackDBConn();
                                                                                BetCalc.updateGameTrack(index[0], user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            } catch (ClassNotFoundException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                        } else {
                                                                            JOptionPane.showMessageDialog(null, "No enough money.",
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);
                                                                        }

                                                                    }
                                                                }
                                                            });

                                                            JButton no = new JButton("4");
                                                            f2.add(no);
                                                            no.addActionListener(new AbstractAction() {
                                                                @Override
                                                                public void actionPerformed(ActionEvent e) {
                                                                    String command2 = e.getActionCommand();
                                                                    if ("4".equals(command2)) {
                                                                        f2.dispose();
                                                                        fee[0] = bet.bet(userwager);

                                                                        BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                        if (betcalc.calcReturn() > 0) {
                                                                            try {
                                                                                user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                            JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);

                                                                        } else {
                                                                            try {
                                                                                user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                            JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);

                                                                        }
                                                                        try {
                                                                            BetCalc.updateGameTrackDBConn();
                                                                            BetCalc.updateGameTrack(index[0], user.userName);
                                                                        } catch (SQLException ex) {
                                                                            throw new RuntimeException(ex);
                                                                        } catch (ClassNotFoundException ex) {
                                                                            throw new RuntimeException(ex);
                                                                        }

                                                                    }
                                                                }
                                                            });

                                                        } else {
                                                            //no enough money, show a new window to ask whether the user want to add money or not
                                                            UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("Verdana", Font.PLAIN, 13)));
                                                            UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("Verdana", Font.PLAIN, 13)));
                                                            Object[] options = {"Yes", "No"};
                                                            int response = JOptionPane.showOptionDialog(null, "You don't have enough money in current balance. \nDo you want to recharge?", "Balance", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                                                            if (response == 0) {
                                                                //yes
                                                                String add_s = JOptionPane.showInputDialog("Enter amount of money to add:");
                                                                if (checknumber(add_s)){
                                                                    float add = Float.parseFloat(add_s);
                                                                    try {
                                                                        user.updateBalanceInUserAccount(add, user.userName);
                                                                    } catch (SQLException ex) {
                                                                        throw new RuntimeException(ex);
                                                                    }
                                                                    JOptionPane.showMessageDialog(null, "$" + add_s + " has been added." + "\nYour current balance: $" + user.balance.loadBalance(),
                                                                            "Result", JOptionPane.PLAIN_MESSAGE);

                                                                } else {
                                                                    JOptionPane.showMessageDialog(null, "Invalid money input.",
                                                                            "Result", JOptionPane.PLAIN_MESSAGE);
                                                                }
                                                            }
                                                        }

                                                    } else {
                                                        JOptionPane.showMessageDialog(null, "Invalid wager input.",
                                                                "Result", JOptionPane.PLAIN_MESSAGE);
                                                    }
                                                }
                                            }
                                        }
                                    });

                                    JButton draw = new JButton("3");
                                    draw.setBounds(270,90,40,20);
                                    panel.add(draw);
                                    //newFrame.add(draw);
                                    draw.addActionListener(new AbstractAction() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            String command2 = e.getActionCommand();
                                            if ("3".equals(command2)) {
                                                newFrame.dispose();
                                                int userresult = 3;

                                                final boolean[] bool = {true};
                                                while (bool[0]) {
                                                    String wager = JOptionPane.showInputDialog("Enter wager (the amount of money to bet):");
                                                    if(wager == null){
                                                        break;
                                                    }
                                                    if (checknumber(wager)) {
                                                        bool[0] = false;
                                                        float userwager = Float.parseFloat(wager);
                                                        if (user.balance.loadBalance() >= userwager) {
                                                            JFrame f2 = new JFrame();
                                                            f2.setSize(400, 150);
                                                            f2.setTitle("Bet");
                                                            f2.setResizable(false);
                                                            f2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                                            f2.setLayout(new FlowLayout());
                                                            f2.setLocationRelativeTo(null);
                                                            f2.setVisible(true);
                                                            JLabel label = new JLabel("<html> Do you want to donate or add insurance to your bet? <br/> 1. Add insurance " +
                                                                    "<br/> 2. Donate <br/> 3. Both <br/> 4. No <html>");
                                                            f2.add(label);

                                                            final float[] fee = {0};
                                                            final float[] donate = {0};
                                                            Bet bet = new BasicBet();

                                                            JButton insurance = new JButton("1");
                                                            f2.add(insurance);
                                                            insurance.addActionListener(new AbstractAction() {
                                                                @Override
                                                                public void actionPerformed(ActionEvent e) {
                                                                    String command2 = e.getActionCommand();
                                                                    if ("1".equals(command2)) {
                                                                        f2.dispose();
                                                                        Bet bet_insurance = new InsuranceDecorator(bet);
                                                                        fee[0] = bet_insurance.bet(userwager);

                                                                        if(fee[0] + userwager <= user.balance.loadBalance()) {
                                                                            JOptionPane.showMessageDialog(null, "You wager: $" + userwager + "\nYou pay 5% wager for insurance. \nInsurance fee: $" + (float)(0.05) * userwager,
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                            if (betcalc.calcReturn() > 0) {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            } else {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            }
                                                                            try {
                                                                                BetCalc.updateGameTrackDBConn();
                                                                                BetCalc.updateGameTrack(index[0], user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            } catch (ClassNotFoundException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                        } else {
                                                                            JOptionPane.showMessageDialog(null, "No enough money.",
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);
                                                                        }

                                                                    }
                                                                }
                                                            });

                                                            JButton donation = new JButton("2");
                                                            f2.add(donation);
                                                            donation.addActionListener(new AbstractAction() {
                                                                @Override
                                                                public void actionPerformed(ActionEvent e) {
                                                                    String command2 = e.getActionCommand();
                                                                    if ("2".equals(command2)) {
                                                                        f2.dispose();
                                                                        Bet bet_donate = new DonateDecorator(bet);
                                                                        donate[0] = bet_donate.bet(userwager);

                                                                        JOptionPane.showMessageDialog(null, "You wager: $" + userwager + "\nYou will donate 5% wager for if you win. \nDonation: $" + (float)(0.05) * userwager,
                                                                                "Result", JOptionPane.PLAIN_MESSAGE);

                                                                        BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                        if (betcalc.calcReturn() > 0) {
                                                                            try {
                                                                                user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                            JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);

                                                                        } else {
                                                                            try {
                                                                                user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                            JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);

                                                                        }
                                                                        try {
                                                                            BetCalc.updateGameTrackDBConn();
                                                                            BetCalc.updateGameTrack(index[0], user.userName);
                                                                        } catch (SQLException ex) {
                                                                            throw new RuntimeException(ex);
                                                                        } catch (ClassNotFoundException ex) {
                                                                            throw new RuntimeException(ex);
                                                                        }
                                                                    }
                                                                }
                                                            });

                                                            JButton both = new JButton("3");
                                                            f2.add(both);
                                                            both.addActionListener(new AbstractAction() {
                                                                @Override
                                                                public void actionPerformed(ActionEvent e) {
                                                                    String command2 = e.getActionCommand();
                                                                    if ("3".equals(command2)) {
                                                                        f2.dispose();
                                                                        Bet bet_donate = new DonateDecorator(bet);
                                                                        Bet bet_insurance = new InsuranceDecorator(bet);
                                                                        donate[0] = bet_donate.bet(userwager);
                                                                        fee[0] = bet_insurance.bet(userwager);

                                                                        if(fee[0] + userwager <= user.balance.loadBalance()){
                                                                            JOptionPane.showMessageDialog(null, "You wager: $" + userwager + "\nYou pay 5% wager for insurance. \nInsurance fee: $" + (float)(0.05) * userwager +
                                                                                    "\nYou will donate 5% wager if you win. \nDonation: $" +(float)(0.05) * userwager, "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                            if (betcalc.calcReturn() > 0) {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            } else {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            }
                                                                            try {
                                                                                BetCalc.updateGameTrackDBConn();
                                                                                BetCalc.updateGameTrack(index[0], user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            } catch (ClassNotFoundException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                        } else {
                                                                            JOptionPane.showMessageDialog(null, "No enough money.",
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);
                                                                        }

                                                                    }
                                                                }
                                                            });

                                                            JButton no = new JButton("4");
                                                            f2.add(no);
                                                            no.addActionListener(new AbstractAction() {
                                                                @Override
                                                                public void actionPerformed(ActionEvent e) {
                                                                    String command2 = e.getActionCommand();
                                                                    if ("4".equals(command2)) {
                                                                        f2.dispose();
                                                                        fee[0] = bet.bet(userwager);

                                                                        BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                        if (betcalc.calcReturn() > 0) {
                                                                            try {
                                                                                user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                            JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);

                                                                        } else {
                                                                            try {
                                                                                user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                            JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);

                                                                        }
                                                                        try {
                                                                            BetCalc.updateGameTrackDBConn();
                                                                            BetCalc.updateGameTrack(index[0], user.userName);
                                                                        } catch (SQLException ex) {
                                                                            throw new RuntimeException(ex);
                                                                        } catch (ClassNotFoundException ex) {
                                                                            throw new RuntimeException(ex);
                                                                        }

                                                                    }
                                                                }
                                                            });

                                                        } else {
                                                            //no enough money, show a new window to ask whether the user want to add money or not
                                                            UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("Verdana", Font.PLAIN, 13)));
                                                            UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("Verdana", Font.PLAIN, 13)));
                                                            Object[] options = {"Yes", "No"};
                                                            int response = JOptionPane.showOptionDialog(null, "You don't have enough money in current balance. \nDo you want to recharge?", "Balance", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                                                            if (response == 0) {
                                                                //yes
                                                                String add_s = JOptionPane.showInputDialog("Enter amount of money to add:");
                                                                if (checknumber(add_s)){
                                                                    float add = Float.parseFloat(add_s);
                                                                    try {
                                                                        user.updateBalanceInUserAccount(add, user.userName);
                                                                    } catch (SQLException ex) {
                                                                        throw new RuntimeException(ex);
                                                                    }
                                                                    JOptionPane.showMessageDialog(null, "$" + add_s + " has been added." + "\nYour current balance: $" + user.balance.loadBalance(),
                                                                            "Result", JOptionPane.PLAIN_MESSAGE);

                                                                } else {
                                                                    JOptionPane.showMessageDialog(null, "Invalid money input.",
                                                                            "Result", JOptionPane.PLAIN_MESSAGE);
                                                                }
                                                            }
                                                        }

                                                    } else {
                                                        JOptionPane.showMessageDialog(null, "Invalid wager input.",
                                                                "Result", JOptionPane.PLAIN_MESSAGE);
                                                    }
                                                }

                                            }
                                        }
                                    });
                                }
                            }
                        });

                        JButton a_odd = new JButton("2");
                        f.add(a_odd);
                        a_odd.addActionListener(new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                String command2 = e.getActionCommand();
                                if ("2".equals(command2)) {
                                    typechoice[0] = new Oddtype(new AmericanOdds());
                                    f.dispose();

                                    //show a new window with odds and allow the user to click which team wins or draw
                                    JFrame newFrame = new JFrame();
                                    newFrame.setSize(400, 150);
                                    newFrame.setTitle("Pick one to bet!");
                                    newFrame.setResizable(false);
                                    newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                    //newFrame.setLayout(new FlowLayout());
                                    newFrame.setLocationRelativeTo(null);
                                    newFrame.setVisible(true);
                                    JPanel panel = new JPanel();
                                    panel.setLayout(null);



                                    JLabel label = new JLabel("<html> 1. Home Team " + homeTeam + " wins. Odds: " + typechoice[0].returnodd(odd.findOdd(userchoice))[0]
                                            + "<br/> 2. Away Team " + awayTeam + " wins. Odds: " + typechoice[0].returnodd(odd.findOdd(userchoice))[1]
                                            + "<br/> 3. This is a draw. Odds: " + typechoice[0].returnodd(odd.findOdd(userchoice))[2] + "<html>");
                                    //newFrame.add(label);
                                    label.setBounds(30,5,350,70);
                                    panel.add(label);
                                    newFrame.add(panel);

                                    String actualresult = CreateFixture.fixtureList.get(index[0]).getWinner();
                                    if (actualresult.equals("H")) {
                                        actual_result = 1;
                                    } else if (actualresult.equals("A")) {
                                        actual_result = 2;
                                    } else if (actualresult.equals("D")) {
                                        actual_result = 3;
                                    } else {

                                    }

                                    JButton t1win = new JButton("1");
                                    t1win.setBounds(110,90,40,20);
                                    panel.add(t1win);
                                    //newFrame.add(t1win);
                                    int finalActual_result = actual_result;
                                    t1win.addActionListener(new AbstractAction() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            String command2 = e.getActionCommand();
                                            if ("1".equals(command2)) {
                                                newFrame.dispose();
                                                int userresult = 1;

                                                final boolean[] bool = {true};
                                                while (bool[0]) {
                                                    String wager = JOptionPane.showInputDialog("Enter wager (the amount of money to bet):");
                                                    if(wager == null){
                                                        break;
                                                    }
                                                    if (checknumber(wager)) {
                                                        bool[0] = false;
                                                        float userwager = Float.parseFloat(wager);
                                                        if (user.balance.loadBalance() >= userwager) {
                                                            JFrame f2 = new JFrame();
                                                            f2.setSize(400, 150);
                                                            f2.setTitle("Bet");
                                                            f2.setResizable(false);
                                                            f2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                                            f2.setLayout(new FlowLayout());
                                                            f2.setLocationRelativeTo(null);
                                                            f2.setVisible(true);
                                                            JLabel label = new JLabel("<html> Do you want to donate or add insurance to your bet? <br/> 1. Add insurance " +
                                                                    "<br/> 2. Donate <br/> 3. Both <br/> 4. No <html>");
                                                            f2.add(label);

                                                            final float[] fee = {0};
                                                            final float[] donate = {0};
                                                            Bet bet = new BasicBet();

                                                            JButton insurance = new JButton("1");
                                                            f2.add(insurance);
                                                            insurance.addActionListener(new AbstractAction() {
                                                                @Override
                                                                public void actionPerformed(ActionEvent e) {
                                                                    String command2 = e.getActionCommand();
                                                                    if ("1".equals(command2)) {
                                                                        f2.dispose();
                                                                        Bet bet_insurance = new InsuranceDecorator(bet);
                                                                        fee[0] = bet_insurance.bet(userwager);

                                                                        if(fee[0] + userwager <= user.balance.loadBalance()) {
                                                                            JOptionPane.showMessageDialog(null, "You wager: $" + userwager + "\nYou pay 5% wager for insurance. \nInsurance fee: $" + (float)(0.05) * userwager,
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                            if (betcalc.calcReturn() > 0) {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            } else {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            }
                                                                            try {
                                                                                BetCalc.updateGameTrackDBConn();
                                                                                BetCalc.updateGameTrack(index[0], user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            } catch (ClassNotFoundException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                        } else {
                                                                            JOptionPane.showMessageDialog(null, "No enough money.",
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);
                                                                        }



                                                                    }
                                                                }
                                                            });

                                                            JButton donation = new JButton("2");
                                                            f2.add(donation);
                                                            donation.addActionListener(new AbstractAction() {
                                                                @Override
                                                                public void actionPerformed(ActionEvent e) {
                                                                    String command2 = e.getActionCommand();
                                                                    if ("2".equals(command2)) {
                                                                        f2.dispose();
                                                                        Bet bet_donate = new DonateDecorator(bet);
                                                                        donate[0] = bet_donate.bet(userwager);

                                                                        JOptionPane.showMessageDialog(null, "You wager: $" + userwager + "\nYou will donate 5% wager for if you win. \nDonation: $" + (float)(0.05) * userwager,
                                                                                "Result", JOptionPane.PLAIN_MESSAGE);

                                                                        BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                        if (betcalc.calcReturn() > 0) {
                                                                            try {
                                                                                user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                            JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);

                                                                        } else {
                                                                            try {
                                                                                user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                            JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);

                                                                        }
                                                                        try {
                                                                            BetCalc.updateGameTrackDBConn();
                                                                            BetCalc.updateGameTrack(index[0], user.userName);
                                                                        } catch (SQLException ex) {
                                                                            throw new RuntimeException(ex);
                                                                        } catch (ClassNotFoundException ex) {
                                                                            throw new RuntimeException(ex);
                                                                        }
                                                                    }
                                                                }
                                                            });

                                                            JButton both = new JButton("3");
                                                            f2.add(both);
                                                            both.addActionListener(new AbstractAction() {
                                                                @Override
                                                                public void actionPerformed(ActionEvent e) {
                                                                    String command2 = e.getActionCommand();
                                                                    if ("3".equals(command2)) {
                                                                        f2.dispose();
                                                                        Bet bet_donate = new DonateDecorator(bet);
                                                                        Bet bet_insurance = new InsuranceDecorator(bet);
                                                                        donate[0] = bet_donate.bet(userwager);
                                                                        fee[0] = bet_insurance.bet(userwager);

                                                                        if(fee[0] + userwager <= user.balance.loadBalance()){
                                                                            JOptionPane.showMessageDialog(null, "You wager: $" + userwager + "\nYou pay 5% wager for insurance. \nInsurance fee: $" + (float)(0.05) * userwager +
                                                                                    "\nYou will donate 5% wager if you win. \nDonation: $" +(float)(0.05) * userwager, "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                            if (betcalc.calcReturn() > 0) {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            } else {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            }
                                                                            try {
                                                                                BetCalc.updateGameTrackDBConn();
                                                                                BetCalc.updateGameTrack(index[0], user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            } catch (ClassNotFoundException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                        } else {
                                                                            JOptionPane.showMessageDialog(null, "No enough money.",
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);
                                                                        }

                                                                    }
                                                                }
                                                            });

                                                            JButton no = new JButton("4");
                                                            f2.add(no);
                                                            no.addActionListener(new AbstractAction() {
                                                                @Override
                                                                public void actionPerformed(ActionEvent e) {
                                                                    String command2 = e.getActionCommand();
                                                                    if ("4".equals(command2)) {
                                                                        f2.dispose();
                                                                        fee[0] = bet.bet(userwager);

                                                                        BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                        if (betcalc.calcReturn() > 0) {
                                                                            try {
                                                                                user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                            JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);

                                                                        } else {
                                                                            try {
                                                                                user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                            JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);

                                                                        }
                                                                        try {
                                                                            BetCalc.updateGameTrackDBConn();
                                                                            BetCalc.updateGameTrack(index[0], user.userName);
                                                                        } catch (SQLException ex) {
                                                                            throw new RuntimeException(ex);
                                                                        } catch (ClassNotFoundException ex) {
                                                                            throw new RuntimeException(ex);
                                                                        }

                                                                    }
                                                                }
                                                            });

                                                        } else {
                                                            //no enough money, show a new window to ask whether the user want to add money or not
                                                            UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("Verdana", Font.PLAIN, 13)));
                                                            UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("Verdana", Font.PLAIN, 13)));
                                                            Object[] options = {"Yes", "No"};
                                                            int response = JOptionPane.showOptionDialog(null, "You don't have enough money in current balance. \nDo you want to recharge?", "Balance", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                                                            if (response == 0) {
                                                                //yes
                                                                String add_s = JOptionPane.showInputDialog("Enter amount of money to add:");
                                                                if (checknumber(add_s)){
                                                                    float add = Float.parseFloat(add_s);
                                                                    try {
                                                                        user.updateBalanceInUserAccount(add, user.userName);
                                                                    } catch (SQLException ex) {
                                                                        throw new RuntimeException(ex);
                                                                    }
                                                                    JOptionPane.showMessageDialog(null, "$" + add_s + " has been added." + "\nYour current balance: $" + user.balance.loadBalance(),
                                                                            "Result", JOptionPane.PLAIN_MESSAGE);

                                                                } else {
                                                                    JOptionPane.showMessageDialog(null, "Invalid money input.",
                                                                            "Result", JOptionPane.PLAIN_MESSAGE);
                                                                }
                                                            }
                                                        }

                                                    } else {
                                                        JOptionPane.showMessageDialog(null, "Invalid wager input.",
                                                                "Result", JOptionPane.PLAIN_MESSAGE);
                                                    }

                                                }

                                            }
                                        }
                                    });

                                    JButton t2win = new JButton("2");
                                    t2win.setBounds(190,90,40,20);
                                    panel.add(t2win);
                                    //newFrame.add(t2win);
                                    t2win.addActionListener(new AbstractAction() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            String command2 = e.getActionCommand();
                                            if ("2".equals(command2)) {
                                                newFrame.dispose();
                                                int userresult = 2;

                                                final boolean[] bool = {true};
                                                while (bool[0]) {
                                                    String wager = JOptionPane.showInputDialog("Enter wager (the amount of money to bet):");
                                                    if(wager == null){
                                                        break;
                                                    }
                                                    if (checknumber(wager)) {
                                                        bool[0] = false;
                                                        float userwager = Float.parseFloat(wager);
                                                        if (user.balance.loadBalance() >= userwager) {
                                                            JFrame f2 = new JFrame();
                                                            f2.setSize(400, 150);
                                                            f2.setTitle("Bet");
                                                            f2.setResizable(false);
                                                            f2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                                            f2.setLayout(new FlowLayout());
                                                            f2.setLocationRelativeTo(null);
                                                            f2.setVisible(true);
                                                            JLabel label = new JLabel("<html> Do you want to donate or add insurance to your bet? <br/> 1. Add insurance " +
                                                                    "<br/> 2. Donate <br/> 3. Both <br/> 4. No <html>");
                                                            f2.add(label);

                                                            final float[] fee = {0};
                                                            final float[] donate = {0};
                                                            Bet bet = new BasicBet();

                                                            JButton insurance = new JButton("1");
                                                            f2.add(insurance);
                                                            insurance.addActionListener(new AbstractAction() {
                                                                @Override
                                                                public void actionPerformed(ActionEvent e) {
                                                                    String command2 = e.getActionCommand();
                                                                    if ("1".equals(command2)) {
                                                                        f2.dispose();
                                                                        Bet bet_insurance = new InsuranceDecorator(bet);
                                                                        fee[0] = bet_insurance.bet(userwager);

                                                                        if(fee[0] + userwager <= user.balance.loadBalance()) {
                                                                            JOptionPane.showMessageDialog(null, "You wager: $" + userwager + "\nYou pay 5% wager for insurance. \nInsurance fee: $" + (float)(0.05) * userwager,
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                            if (betcalc.calcReturn() > 0) {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            } else {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            }
                                                                            try {
                                                                                BetCalc.updateGameTrackDBConn();
                                                                                BetCalc.updateGameTrack(index[0], user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            } catch (ClassNotFoundException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                        } else {
                                                                            JOptionPane.showMessageDialog(null, "No enough money.",
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);
                                                                        }

                                                                    }
                                                                }
                                                            });

                                                            JButton donation = new JButton("2");
                                                            f2.add(donation);
                                                            donation.addActionListener(new AbstractAction() {
                                                                @Override
                                                                public void actionPerformed(ActionEvent e) {
                                                                    String command2 = e.getActionCommand();
                                                                    if ("2".equals(command2)) {
                                                                        f2.dispose();
                                                                        Bet bet_donate = new DonateDecorator(bet);
                                                                        donate[0] = bet_donate.bet(userwager);

                                                                        JOptionPane.showMessageDialog(null, "You wager: $" + userwager + "\nYou will donate 5% wager for if you win. \nDonation: $" + (float)(0.05) * userwager,
                                                                                "Result", JOptionPane.PLAIN_MESSAGE);

                                                                        BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                        if (betcalc.calcReturn() > 0) {
                                                                            try {
                                                                                user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                            JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);

                                                                        } else {
                                                                            try {
                                                                                user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                            JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);

                                                                        }
                                                                        try {
                                                                            BetCalc.updateGameTrackDBConn();
                                                                            BetCalc.updateGameTrack(index[0], user.userName);
                                                                        } catch (SQLException ex) {
                                                                            throw new RuntimeException(ex);
                                                                        } catch (ClassNotFoundException ex) {
                                                                            throw new RuntimeException(ex);
                                                                        }
                                                                    }
                                                                }
                                                            });

                                                            JButton both = new JButton("3");
                                                            f2.add(both);
                                                            both.addActionListener(new AbstractAction() {
                                                                @Override
                                                                public void actionPerformed(ActionEvent e) {
                                                                    String command2 = e.getActionCommand();
                                                                    if ("3".equals(command2)) {
                                                                        f2.dispose();
                                                                        Bet bet_donate = new DonateDecorator(bet);
                                                                        Bet bet_insurance = new InsuranceDecorator(bet);
                                                                        donate[0] = bet_donate.bet(userwager);
                                                                        fee[0] = bet_insurance.bet(userwager);

                                                                        if(fee[0] + userwager <= user.balance.loadBalance()){
                                                                            JOptionPane.showMessageDialog(null, "You wager: $" + userwager + "\nYou pay 5% wager for insurance. \nInsurance fee: $" + (float)(0.05) * userwager +
                                                                                    "\nYou will donate 5% wager if you win. \nDonation: $" +(float)(0.05) * userwager, "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                            if (betcalc.calcReturn() > 0) {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            } else {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            }
                                                                            try {
                                                                                BetCalc.updateGameTrackDBConn();
                                                                                BetCalc.updateGameTrack(index[0], user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            } catch (ClassNotFoundException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                        } else {
                                                                            JOptionPane.showMessageDialog(null, "No enough money.",
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);
                                                                        }
                                                                    }
                                                                }
                                                            });

                                                            JButton no = new JButton("4");
                                                            f2.add(no);
                                                            no.addActionListener(new AbstractAction() {
                                                                @Override
                                                                public void actionPerformed(ActionEvent e) {
                                                                    String command2 = e.getActionCommand();
                                                                    if ("4".equals(command2)) {
                                                                        f2.dispose();
                                                                        fee[0] = bet.bet(userwager);

                                                                        BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                        if (betcalc.calcReturn() > 0) {
                                                                            try {
                                                                                user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                            JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);

                                                                        } else {
                                                                            try {
                                                                                user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                            JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);

                                                                        }
                                                                        try {
                                                                            BetCalc.updateGameTrackDBConn();
                                                                            BetCalc.updateGameTrack(index[0], user.userName);
                                                                        } catch (SQLException ex) {
                                                                            throw new RuntimeException(ex);
                                                                        } catch (ClassNotFoundException ex) {
                                                                            throw new RuntimeException(ex);
                                                                        }

                                                                    }
                                                                }
                                                            });

                                                        } else {
                                                            //no enough money, show a new window to ask whether the user want to add money or not
                                                            UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("Verdana", Font.PLAIN, 13)));
                                                            UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("Verdana", Font.PLAIN, 13)));
                                                            Object[] options = {"Yes", "No"};
                                                            int response = JOptionPane.showOptionDialog(null, "You don't have enough money in current balance. \nDo you want to recharge?", "Balance", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                                                            if (response == 0) {
                                                                //yes
                                                                String add_s = JOptionPane.showInputDialog("Enter amount of money to add:");
                                                                if (checknumber(add_s)){
                                                                    float add = Float.parseFloat(add_s);
                                                                    try {
                                                                        user.updateBalanceInUserAccount(add, user.userName);
                                                                    } catch (SQLException ex) {
                                                                        throw new RuntimeException(ex);
                                                                    }
                                                                    JOptionPane.showMessageDialog(null, "$" + add_s + " has been added." + "\nYour current balance: $" + user.balance.loadBalance(),
                                                                            "Result", JOptionPane.PLAIN_MESSAGE);

                                                                } else {
                                                                    JOptionPane.showMessageDialog(null, "Invalid money input.",
                                                                            "Result", JOptionPane.PLAIN_MESSAGE);
                                                                }
                                                            }
                                                        }

                                                    } else {
                                                        JOptionPane.showMessageDialog(null, "Invalid wager input.",
                                                                "Result", JOptionPane.PLAIN_MESSAGE);
                                                    }
                                                }
                                            }
                                        }
                                    });

                                    JButton draw = new JButton("3");
                                    draw.setBounds(270,90,40,20);
                                    panel.add(draw);
                                    //newFrame.add(draw);
                                    draw.addActionListener(new AbstractAction() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            String command2 = e.getActionCommand();
                                            if ("3".equals(command2)) {
                                                newFrame.dispose();
                                                int userresult = 3;

                                                final boolean[] bool = {true};
                                                while (bool[0]) {
                                                    String wager = JOptionPane.showInputDialog("Enter wager (the amount of money to bet):");
                                                    if(wager == null){
                                                        break;
                                                    }
                                                    if (checknumber(wager)) {
                                                        bool[0] = false;
                                                        float userwager = Float.parseFloat(wager);
                                                        if (user.balance.loadBalance() >= userwager) {
                                                            JFrame f2 = new JFrame();
                                                            f2.setSize(400, 150);
                                                            f2.setTitle("Bet");
                                                            f2.setResizable(false);
                                                            f2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                                            f2.setLayout(new FlowLayout());
                                                            f2.setLocationRelativeTo(null);
                                                            f2.setVisible(true);
                                                            JLabel label = new JLabel("<html> Do you want to donate or add insurance to your bet? <br/> 1. Add insurance " +
                                                                    "<br/> 2. Donate <br/> 3. Both <br/> 4. No <html>");
                                                            f2.add(label);

                                                            final float[] fee = {0};
                                                            final float[] donate = {0};
                                                            Bet bet = new BasicBet();

                                                            JButton insurance = new JButton("1");
                                                            f2.add(insurance);
                                                            insurance.addActionListener(new AbstractAction() {
                                                                @Override
                                                                public void actionPerformed(ActionEvent e) {
                                                                    String command2 = e.getActionCommand();
                                                                    if ("1".equals(command2)) {
                                                                        f2.dispose();
                                                                        Bet bet_insurance = new InsuranceDecorator(bet);
                                                                        fee[0] = bet_insurance.bet(userwager);

                                                                        if(fee[0] + userwager <= user.balance.loadBalance()) {
                                                                            JOptionPane.showMessageDialog(null, "You wager: $" + userwager + "\nYou pay 5% wager for insurance. \nInsurance fee: $" + (float)(0.05) * userwager,
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                            if (betcalc.calcReturn() > 0) {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            } else {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            }
                                                                            try {
                                                                                BetCalc.updateGameTrackDBConn();
                                                                                BetCalc.updateGameTrack(index[0], user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            } catch (ClassNotFoundException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                        } else {
                                                                            JOptionPane.showMessageDialog(null, "No enough money.",
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);
                                                                        }

                                                                    }
                                                                }
                                                            });

                                                            JButton donation = new JButton("2");
                                                            f2.add(donation);
                                                            donation.addActionListener(new AbstractAction() {
                                                                @Override
                                                                public void actionPerformed(ActionEvent e) {
                                                                    String command2 = e.getActionCommand();
                                                                    if ("2".equals(command2)) {
                                                                        f2.dispose();
                                                                        Bet bet_donate = new DonateDecorator(bet);
                                                                        donate[0] = bet_donate.bet(userwager);

                                                                        JOptionPane.showMessageDialog(null, "You wager: $" + userwager + "\nYou will donate 5% wager for if you win. \nDonation: $" + (float)(0.05) * userwager,
                                                                                "Result", JOptionPane.PLAIN_MESSAGE);

                                                                        BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                        if (betcalc.calcReturn() > 0) {
                                                                            try {
                                                                                user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                            JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);

                                                                        } else {
                                                                            try {
                                                                                user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                            JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);

                                                                        }
                                                                        try {
                                                                            BetCalc.updateGameTrackDBConn();
                                                                            BetCalc.updateGameTrack(index[0], user.userName);
                                                                        } catch (SQLException ex) {
                                                                            throw new RuntimeException(ex);
                                                                        } catch (ClassNotFoundException ex) {
                                                                            throw new RuntimeException(ex);
                                                                        }
                                                                    }
                                                                }
                                                            });

                                                            JButton both = new JButton("3");
                                                            f2.add(both);
                                                            both.addActionListener(new AbstractAction() {
                                                                @Override
                                                                public void actionPerformed(ActionEvent e) {
                                                                    String command2 = e.getActionCommand();
                                                                    if ("3".equals(command2)) {
                                                                        f2.dispose();
                                                                        Bet bet_donate = new DonateDecorator(bet);
                                                                        Bet bet_insurance = new InsuranceDecorator(bet);
                                                                        donate[0] = bet_donate.bet(userwager);
                                                                        fee[0] = bet_insurance.bet(userwager);

                                                                        if(fee[0] + userwager <= user.balance.loadBalance()){
                                                                            JOptionPane.showMessageDialog(null, "You wager: $" + userwager + "\nYou pay 5% wager for insurance. \nInsurance fee: $" + (float)(0.05) * userwager +
                                                                                    "\nYou will donate 5% wager if you win. \nDonation: $" +(float)(0.05) * userwager, "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                            if (betcalc.calcReturn() > 0) {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            } else {
                                                                                try {
                                                                                    user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                    user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                                } catch (SQLException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                        "Result", JOptionPane.PLAIN_MESSAGE);

                                                                            }
                                                                            try {
                                                                                BetCalc.updateGameTrackDBConn();
                                                                                BetCalc.updateGameTrack(index[0], user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            } catch (ClassNotFoundException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                        } else {
                                                                            JOptionPane.showMessageDialog(null, "No enough money.",
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);
                                                                        }

                                                                    }
                                                                }
                                                            });

                                                            JButton no = new JButton("4");
                                                            f2.add(no);
                                                            no.addActionListener(new AbstractAction() {
                                                                @Override
                                                                public void actionPerformed(ActionEvent e) {
                                                                    String command2 = e.getActionCommand();
                                                                    if ("4".equals(command2)) {
                                                                        f2.dispose();
                                                                        fee[0] = bet.bet(userwager);

                                                                        BetCalc betcalc = new BetCalc(userresult, userwager, actual_result, odd.findOdd(userchoice));
                                                                        if (betcalc.calcReturn() > 0) {
                                                                            try {
                                                                                user.updateBalanceInUserAccount(userwager * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(donate[0] * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                            JOptionPane.showMessageDialog(null, "You won: $" + betcalc.calcReturn() + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);

                                                                        } else {
                                                                            try {
                                                                                user.updateBalanceInUserAccount(fee[0] * (-1), user.userName);
                                                                                user.updateBalanceInUserAccount(betcalc.calcReturn(), user.userName);
                                                                            } catch (SQLException ex) {
                                                                                throw new RuntimeException(ex);
                                                                            }
                                                                            JOptionPane.showMessageDialog(null, "You lost: $" + betcalc.calcReturn() * (-1) + "\nCurrent Balance: $" + user.balance.loadBalance(),
                                                                                    "Result", JOptionPane.PLAIN_MESSAGE);

                                                                        }
                                                                        try {
                                                                            BetCalc.updateGameTrackDBConn();
                                                                            BetCalc.updateGameTrack(index[0], user.userName);
                                                                        } catch (SQLException ex) {
                                                                            throw new RuntimeException(ex);
                                                                        } catch (ClassNotFoundException ex) {
                                                                            throw new RuntimeException(ex);
                                                                        }

                                                                    }
                                                                }
                                                            });

                                                        } else {
                                                            //no enough money, show a new window to ask whether the user want to add money or not
                                                            UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("Verdana", Font.PLAIN, 13)));
                                                            UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("Verdana", Font.PLAIN, 13)));
                                                            Object[] options = {"Yes", "No"};
                                                            int response = JOptionPane.showOptionDialog(null, "You don't have enough money in current balance. \nDo you want to recharge?", "Balance", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                                                            if (response == 0) {
                                                                //yes
                                                                String add_s = JOptionPane.showInputDialog("Enter amount of money to add:");
                                                                if (checknumber(add_s)){
                                                                    float add = Float.parseFloat(add_s);
                                                                    try {
                                                                        user.updateBalanceInUserAccount(add, user.userName);
                                                                    } catch (SQLException ex) {
                                                                        throw new RuntimeException(ex);
                                                                    }
                                                                    JOptionPane.showMessageDialog(null, "$" + add_s + " has been added." + "\nYour current balance: $" + user.balance.loadBalance(),
                                                                            "Result", JOptionPane.PLAIN_MESSAGE);

                                                                } else {
                                                                    JOptionPane.showMessageDialog(null, "Invalid money input.",
                                                                            "Result", JOptionPane.PLAIN_MESSAGE);
                                                                }
                                                            }
                                                        }

                                                    } else {
                                                        JOptionPane.showMessageDialog(null, "Invalid wager input.",
                                                                "Result", JOptionPane.PLAIN_MESSAGE);
                                                    }
                                                }

                                            }
                                        }
                                    });

                                }
                            }
                        });

                    } else if (bool == true && flag == true){
                        JOptionPane.showMessageDialog(null, "UNABLE TO PROCEED: You have already bet on the game!",
                                "Error", JOptionPane.PLAIN_MESSAGE);

                    } else {
                        //flag = false
                        //check if the input is valid
                        boolean flag2 = false;
                        for (int j = 0; j < CreateTeamData.teamList.size(); j++) {
                            if (homeTeam.equals(CreateTeamData.teamList.get(j).getTeamName())) {
                                team1[0] = j;
                                flag2 = true;
                            }
                        }
                        boolean flag3 = false;
                        for (int j = 0; j < CreateTeamData.teamList.size(); j++) {
                            if (awayTeam.equals(CreateTeamData.teamList.get(j).getTeamName())) {
                                team1[0] = j;
                                flag3 = true;
                            }
                        }
                        if (flag2 == false && flag3 == false) {
                            JOptionPane.showMessageDialog(null, "Invalid Home Team name and Away Team name.",
                                    "Error", JOptionPane.PLAIN_MESSAGE);
                        } else if (flag2 == false && flag3 == true) {
                            JOptionPane.showMessageDialog(null, "Invalid Home Team name.",
                                    "Error", JOptionPane.PLAIN_MESSAGE);

                        } else if (flag2 == true && flag3 == false) {
                            JOptionPane.showMessageDialog(null, "Invalid Away Team name.",
                                    "Error", JOptionPane.PLAIN_MESSAGE);

                        } else {
                            //flag2 == true & flag3 == true
                            JOptionPane.showMessageDialog(null, "The game between the two input teams does not exist.",
                                    "Error", JOptionPane.PLAIN_MESSAGE);
                        }

                    }

                }
            }
        });
    }

    public void showBalance(User user){
        JFrame testFrame = new JFrame("Balance");
        testFrame.setSize(400, 100);
        testFrame.setResizable(false);
        testFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        testFrame.setLocationRelativeTo(null);
        testFrame.setLayout(new FlowLayout());
        //JLabel show_balance = new JLabel("Current Balance: $"+user.balance.loadBalance());
        //testFrame.add(show_balance);

        JButton enter = new JButton("Enter");
        JLabel text = new JLabel("<html> Current Balance: $" + user.balance.loadBalance() + "<br/>Enter amount of money to add: <html>");
        JTextField textBook = new JTextField(20);
        testFrame.add(text);
        testFrame.add(textBook);
        enter.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String command = e.getActionCommand();
                if ("Enter".equals(command)) {
                    String input = textBook.getText();
                    if (checknumber(input)){
                        testFrame.dispose();
                        float money = Float.parseFloat(input);
                        try {
                            user.updateBalanceInUserAccount(money, user.userName);
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                        //user.addBalance(money);
                        ///new window and show money successfully added
                        //testFrame.dispose();
                        JFrame newFrame = new JFrame();
                        newFrame.setSize(400, 150);
                        newFrame.setTitle("Result");
                        newFrame.setResizable(false);
                        newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        newFrame.setLayout(new FlowLayout());
                        newFrame.setLocationRelativeTo(null);
                        JLabel label = new JLabel("<html> Money Added Successfully! <br/> Current Balance: $" + user.balance.loadBalance()+ "<html>");
                        newFrame.add(label);
                        //JLabel label2 = new JLabel("Current Balance: $"+ user.balance.loadBalance());
                        //newFrame.add(label2);
                        newFrame.setVisible(true);
                        JButton back = new JButton("Return");
                        newFrame.add(back);
                        back.addActionListener(new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                String command2 = e.getActionCommand();
                                if ("Return".equals(command2)){
                                    newFrame.dispose();
                                    textBook.setText("");
                                }
                            }
                        });
                    } else {
                        //testFrame.dispose();
                        JFrame newFrame = new JFrame();
                        newFrame.setSize(400, 150);
                        newFrame.setTitle("Result");
                        newFrame.setResizable(false);
                        newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        newFrame.setLayout(new FlowLayout());
                        newFrame.setLocationRelativeTo(null);
                        JLabel label = new JLabel("<html> Invalid amount entered. <br/> Current Balance: $" + user.balance.loadBalance()+ "<html>");
                        newFrame.add(label);
                        //JLabel label2 = new JLabel("Current Balance: $"+ user.balance.loadBalance());
                        //newFrame.add(label2);
                        newFrame.setVisible(true);
                        JButton back = new JButton("Return");
                        newFrame.add(back);
                        back.addActionListener(new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                String command2 = e.getActionCommand();
                                if ("Return".equals(command2)){
                                    newFrame.dispose();
                                    textBook.setText("");
                                }
                            }
                        });
                    }

                }
            }

        });
        testFrame.add(enter);
        testFrame.setVisible(true);
    }


    public void signUp(JTextField textBook, JTextField textBook2, JFrame testFrame) throws SQLException, ClassNotFoundException {
        String inputUsername = textBook.getText();
        String inputPassword = textBook2.getText();

        //String fileName = "FootyBettor/" + inputUsername + ".txt";
        //File acc = new File(fileName);
        //if(!acc.exists()){
        User.madeUserToDBConn();
        if(!User.checkUserExists(inputUsername) && !inputUsername.equals("")){
            User user = new User(inputUsername,inputPassword,0);
            User.addUserToUserAccountTable(user);
//            try {
//                FileWriter writer = new FileWriter("FootyBettor/"+ user.userName+".txt");
//                writer.write("Username:" + user.userName + ",");
//                writer.write("\nPassword: "+ user.password + ",");
//                writer.write("\nBalance" + "," + user.balance.loadBalance() + ",");
//                writer.flush();
//                writer.close();
//            }catch(IOException ex) {
//                System.out.println("Error.signUp");
//            }

            testFrame.dispose();
            JFrame newFrame = new JFrame();
            newFrame.setSize(400, 150);
            newFrame.setTitle("Result");
            newFrame.setResizable(false);
            newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            newFrame.setLayout(new FlowLayout());
            newFrame.setLocationRelativeTo(null);
            JLabel label = new JLabel("Account Created Successfully!");
            newFrame.add(label);
            newFrame.setVisible(true);
            JButton back = new JButton("Return");
            newFrame.add(back);
            back.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String command2 = e.getActionCommand();
                    if ("Return".equals(command2)){
                        newFrame.dispose();
                    }
                }
            });
        } else if (inputUsername.equals("")){
            testFrame.dispose();
            JFrame newFrame = new JFrame();
            newFrame.setSize(400, 150);
            newFrame.setTitle("Result");
            newFrame.setResizable(false);
            newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            newFrame.setLayout(new FlowLayout());
            newFrame.setLocationRelativeTo(null);
            JLabel label = new JLabel("username cannot be empty.");
            newFrame.add(label);
            newFrame.setVisible(true);
            JButton back = new JButton("Return");
            newFrame.add(back);
            back.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String command2 = e.getActionCommand();
                    if ("Return".equals(command2)){
                        newFrame.dispose();
                    }
                }
            });
        } else {
            testFrame.dispose();
            JFrame newFrame = new JFrame();
            newFrame.setSize(400, 150);
            newFrame.setTitle("Result");
            newFrame.setResizable(false);
            newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            newFrame.setLayout(new FlowLayout());
            newFrame.setLocationRelativeTo(null);
            JLabel label = new JLabel("Account already exists.");
            newFrame.add(label);
            newFrame.setVisible(true);
            JButton back = new JButton("Return");
            newFrame.add(back);
            back.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String command2 = e.getActionCommand();
                    if ("Return".equals(command2)){
                        newFrame.dispose();
                    }
                }
            });
        }
    }


    public User login(JTextField textBook, JTextField textBook2) throws SQLException, ClassNotFoundException {
        String inputUsername = textBook.getText();
        String inputPassword = textBook2.getText();
        //String fileName = "FootyBettor/" + inputUsername + ".txt";
        //File acc = new File(fileName);
        //if(acc.exists() == true) {
        User.madeUserToDBConn();
        if(User.checkUserExists(inputUsername)){
            User userObj = User.retrunUserObjectFromSQL(inputUsername);
            if(inputPassword.equals(userObj.password)){
                return userObj;
            } else {
                new User("","",-10);
            }

//            try {
//                BufferedReader readTxt = new BufferedReader(new FileReader("FootyBettor/" + inputUsername + ".txt"));
//                String str = "";
//                String text = "";
//                while ((text = readTxt.readLine()) != null) {
//                    str += text;
//                }
//                String[] array = str.split(",");
//                String last = array[array.length - 1];
//                User user = new User(inputUsername, inputPassword, Float.parseFloat(last));
//                String pwinput = "Password: " + inputPassword;
//                if (array[1].equals(pwinput)) {
//                    return user;
//                } else {
//                    //wrong password
//                    return new User("","",-10);
//                }
//            } catch (IOException ext) {
//                if (ext instanceof FileNotFoundException) {
//                    System.out.println("Account does not exist.");
//                    ext.printStackTrace();
//                } else {
//                    System.err.println("Exception " + ext);
//                }
//            }


        }else {
            return new User("","",-20);
        }
        return new User("","",-20);
    }
}
