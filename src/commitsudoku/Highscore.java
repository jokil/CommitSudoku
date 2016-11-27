/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commitsudoku;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 *
 * @author Joona
 */
public class Highscore {
    String[] names = new String[12];    //LIST OF NAMES
    String[] times = new String[12];    //LIST OF TIMES
    ArrayList<String> tmp = new ArrayList<String>();    //LISTS FOR VALUE COMPARISON
    ArrayList<Long> tmp2 = new ArrayList<Long>();       
    /**
     * Constructor for the Highscore class, uses load() to try and load current highscores from a text file,
     * if it catches a FileNotFoundException, does printStackTrace() and restores defaults with defaults().
     */
    public Highscore(){
        try{
            load();
        }catch (FileNotFoundException e){
            e.printStackTrace();
            defaults();
        }
    }
    /**
     * Loads the current highscores from a text file into String[] names
     * and String[] times using BufferedReader.
     * @throws FileNotFoundException 	If BufferedReader can't find the text file specified, restores defaults with defaults().
     * @throws IOException 				printStackTrace() if IOException occurs.
     */
    private void load() throws FileNotFoundException{
        try{
            BufferedReader br = new BufferedReader(new FileReader("high.txt"));
            try{
                for (int i=0; i<12; i++){           
                    String input = br.readLine();   //MAKES A STRING FROM A LINE READ FROM "high.txt"
                    String[] in = input.split("§"); //SPLITS THE STRING INTO AN ARRAY OF STRINGS
                    names[i] = in[0];               //SETS names[i] TO THE FIRST STRING
                    times[i] = in[1];               //SETS times[i] TO THE SECOND STRING
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }catch (FileNotFoundException e){
            defaults();
        }
    }
    /**
     * Ends the current game. If name is null, restores default, if name is over 10 characters long,
     * forms a substring of the first 10 characters to be used as a name.
     * Compares the current game to the highscores according to difficulty.
     * If the current game's time was good enough to make the highscores, the method then sorts the highscores into order.
     * Once the method has completed sorting the highscores, it saves the highscores using save().
     * @param name 			String value the player gives as input
     * @param difficulty 	int value that represents the difficulty chosen by the player
     * @param time			long value of how long the game took
     */
    public void endgame(String name, int difficulty, long time){
        if (name == null){
            name = "**********";
        }
        if (name.length() > 10){
            name = name.substring(0, 9);
        }
        tmp.add(name);        //ADDS GIVEN NAME TO ArrayList tmp FOR COMPARISON
        tmp2.add(time);       //ADDS GIVEN TIME TO ArrayList tmp2 FOR COMPARISON
        int start=0;
        int end=2;
        if (difficulty >= 1){  
            start=3;          //SETS STARTING LINE ACCORDING TO DIFFICULTY
            end=5;            //SETS ENDING LINE ACCORDING TO DIFFICULTY
        }
        if (difficulty >= 2){
            start=6;
            end=8;
        }
        if (difficulty >= 3){
            start=9;
            end=11;
        }
        for (int i=start; i<=end; i++){  
            tmp.add(names[i]);                  //ADDS NAMES BETWEEN START & END TO tmp
            tmp2.add(Long.parseLong(times[i])); //ADDS TIMES BETWEEN START & END TO tmp2
        }
        boolean sort;
        do{                                        //SORTS BOTH ARRAYLISTS BY TIME
            sort = false;
            for (int i=0; i<tmp.size()-1; i++){
                if (tmp2.get(i) > tmp2.get(i+1)){
                    String ntmp = tmp.get(i);
                    Long ttmp = tmp2.get(i);
                    tmp.set(i, tmp.get(i+1));
                    tmp2.set(i, tmp2.get(i+1));
                    tmp.set(i+1, ntmp);
                    tmp2.set(i+1, ttmp);
                    sort = true;
                }
            }
        }while (sort);
        int j=0;
        for (int i=start; i<=end; i++){
            names[i] = tmp.get(j);                         //ADDS 3 FASTEST NAMES TO SCOREBOARD
            times[i] = String.format("%06d", tmp2.get(j)); //ADDS 3 FASTEST TIMES TO SCOREBOARD
            j++;
        }
        save();
}
    /**
     * Restores the text file containing highscores to a default state using PrintStream to overwrite all current
     * entries to have the name "**********" and time "999999".
     */
    private void defaults(){
        try{
            PrintStream ps = new PrintStream(new File("high.txt"));
            for (int i=0; i<12; i++){
                ps.println("**********§999999");
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }
    /**
     * Records the names and times of the players with highscores using PrintStream to print the name and time
     * from String[] names and String[] times into a text file.
     */
    private void save(){
        try{
            PrintStream ps = new PrintStream(new File("high.txt"));
            for (int i=0; i<12; i++){
                ps.println(names[i] + "§" + times[i]);
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }
    /**
     * Returns the name of the player at the index of the highscore list that is given as a parameter,
     * if the given int is below 0 or above 11, or anything but an integer value, returns null.     *
     * @param index
     * @return name
     */
    public String getName(int i){
        if (i>=0 && i<=12){
            return names[i];
        }else{
            return null;
        }
    }
    /**
     * Returns the time of the player at the index of the highscore list that is given as a parameter,
     * if the given int is below 0 or above 11, or anything but an integer value, returns null.
     * @param int i, index of String[] times
     * @return time, String value at index of String[] times
     */
    public String getTime(int i){
        if (i>=0 && i<=12){
            return times[i];
        }else{
            return null;
        }
    } 
}
