import com.sun.deploy.util.StringUtils;
import sun.awt.WindowClosingListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainFrame extends JFrame {

    public static boolean isFinished = false;
    String resultFile = "rezultatai.txt";
    JProgressBar progressBar;
    JLabel resultFileLabel;
    JLabel currentNumberLabel;
    Thread thread;


    public MainFrame(String title) {
        super(title);
        setContentPane(createContent());
        thread = new Thread();
    }


    // Kuriama grafinė sąsaja
    private Container createContent()
    {
        JPanel result = new JPanel();
        result.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );


        GroupLayout layout = new GroupLayout( result );
        result.setLayout( layout );
        layout.setAutoCreateGaps( true );

        JLabel firstNumberLabel = new JLabel( "Pradinis skaičius" );
        JTextField firstNumberTextField = new JTextField( 10 );
        JLabel secondNumberLabel = new JLabel( "Galutinis skaičius" );
        JTextField secondNumberTextField = new JTextField( 10 );
        JLabel additionalNumberLabel = new JLabel( "Kas kiek didinti" );
        JTextField additionalNumberTextField = new JTextField( 10 );

        resultFileLabel = new JLabel("");

        currentNumberLabel = new JLabel("");

        JButton startButton = new JButton("Pradėti");
        JButton finishButton = new JButton("Baigti");

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);


        PrintWriter writer = null;
        try {
            writer = new PrintWriter(resultFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        writer.print("");
        writer.close();


        // Funkcija, paspaudus mygtuką pradėti
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int firstNumber = Integer.parseInt(firstNumberTextField.getText().toString());
                int secondNumber = Integer.parseInt(secondNumberTextField.getText().toString());
                int additionalNumber = Integer.parseInt(additionalNumberTextField.getText().toString());

                if (firstNumber > secondNumber) {
                    JOptionPane.showMessageDialog(result, "Pradinis skaičius turi būti mažesnis už galutinį");
                    return;
                }

                if(thread.isAlive()) {
                    isFinished = true;
                    try {
                        TimeUnit.MILLISECONDS.sleep(500);

                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }

                }

                isFinished = false;

                PrintWriter writer = null;
                try {
                    writer = new PrintWriter(new BufferedWriter(new FileWriter(resultFile, true)));
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }


                resultFileLabel.setText("");


                double addPercent = (secondNumber - firstNumber) / additionalNumber + 1;

                progressBar.setValue(0);

                final Date[] date = {new Date()};
                final Timestamp[] data = {new Timestamp(date[0].getTime())};


                writer.println(data[0] + " Skaičiavimo pradžia. Naudojami skaičiai: " + firstNumberTextField.getText().toString()
                + " " + secondNumberTextField.getText().toString() + " " + additionalNumberTextField.getText().toString() );


                PrintWriter finalWriter = writer;
                Runnable r = new Runnable() {
                    @Override
                    public void run() {

                        int currentnumber = firstNumber;
                        while (currentnumber <= secondNumber) {
                            currentNumberLabel.setText("Skaidomas skaičius: " + currentnumber);
                            date[0] = new Date();
                            data[0] = new Timestamp(date[0].getTime());
                            long currentMili = System.currentTimeMillis();
                            finalWriter.print(data[0] + " ");
                            divide(currentnumber, finalWriter);
                            finalWriter.println("\n");


                            progressBar.setValue(progressBar.getValue()+(int)Math.round(100/addPercent));

                            if(isFinished){

                                finalWriter.println(data[0] + " Skaičiavimas nutrauktas \n");
                                finalWriter.println("\n");
                                finalWriter.close();
                                return;
                            }

                            currentnumber += additionalNumber;
                            if(currentMili+500 > System.currentTimeMillis()) {
                                try {
                                    TimeUnit.MILLISECONDS.sleep(System.currentTimeMillis()-currentMili+500);
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        date[0] = new Date();
                        data[0] = new Timestamp(date[0].getTime());
                        finalWriter.println(data[0] + " Skaičiavimo pabaiga \n");
                        finalWriter.println("\n");
                        finalWriter.close();
                        progressBar.setValue(100);
                        resultFileLabel.setText("Skaidymas baigtas. Rezultatai faile " + resultFile);

                    }
                };



                thread = new Thread(r);
                thread.start();

            }
        });


        // Funkcija, paspaudus mygtuką baigti
        finishButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isFinished = true;
            }
        });


        // Lygiavimas horizantaliai pagal kairę kraštinę

        layout.setHorizontalGroup( layout.createSequentialGroup()
                .addGroup( layout.createParallelGroup( GroupLayout.Alignment.LEADING )
                        .addComponent( firstNumberLabel, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE  )
                        .addComponent( secondNumberLabel, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE  )
                        .addComponent( additionalNumberLabel, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE  )
                        .addComponent( startButton )
                        .addComponent( progressBar, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE )
                        .addComponent( currentNumberLabel, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE  ))
                .addGroup( layout.createParallelGroup( GroupLayout.Alignment.LEADING )
                        .addComponent( firstNumberTextField )
                        .addComponent( secondNumberTextField )
                        .addComponent( additionalNumberTextField )
                        .addComponent( resultFileLabel )
                        .addComponent(finishButton))

        );

        // Lygiavimas vertikaliai
        layout.setVerticalGroup( layout.createSequentialGroup()
                .addGroup( layout.createParallelGroup( GroupLayout.Alignment.BASELINE )
                        .addComponent( firstNumberLabel )
                        .addComponent( firstNumberTextField ) )
                .addGroup( layout.createParallelGroup( GroupLayout.Alignment.BASELINE )
                        .addComponent( secondNumberLabel )
                        .addComponent( secondNumberTextField ) )
                .addGroup( layout.createParallelGroup( GroupLayout.Alignment.BASELINE )
                        .addComponent( additionalNumberLabel )
                        .addComponent( additionalNumberTextField ) )
                        .addGap(20)
                .addGroup( layout.createParallelGroup( GroupLayout.Alignment.BASELINE)
                        .addComponent(progressBar)
                        .addComponent( resultFileLabel ) )
                        .addGap(20)
                .addGroup( layout.createParallelGroup( GroupLayout.Alignment.BASELINE )
                        .addComponent( currentNumberLabel ))
                        .addGap(20)
                .addGroup( layout.createParallelGroup( GroupLayout.Alignment.BASELINE )
                        .addComponent( startButton )
                        .addComponent( finishButton ) )
        );

        return result;
    }


    /*
    divide - funkcija skaičių skaidymui
    @param number - Skaidomas skaičius
    @param writer - PrintWriter'is rašymui į failą
     */
     void divide(int number, PrintWriter writer) {

        boolean first = true;
        writer.print(number + " =");
        int divide = 2;
        while (number != 1) {

            if(number % divide == 0) {
                number = number/divide;
                if (first) {
                    writer.print(" " + divide);
                    first = false;
                }

                else {
                    writer.print(" * " + divide);
                }
                divide = 2;
            }
            else {
                divide++;
            }
        }
    }

     boolean isInteger( String input )
    {
        try
        {
            Integer.parseInt( input );
            return true;
        }
        catch( Exception e)
        {
            return false;
        }
    }

}
