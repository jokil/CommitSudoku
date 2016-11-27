package commitsudoku;

import java.util.Collections;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Random;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Joona & Saku
 */
public class Board{
  private long start;					  //AJANOTON ALOITUS
  private long loadtime=0;                                //LATAUSTIEDOSTON AIKA
  private long seed;                      //RANDOMIA VARTEN
  private int[][] board = new int[9][9];  //PELILAUTA
  private boolean[][] checkBoard = new boolean[9][9];  //TARKISTUSLAUTA, SALLITUT ALKIOT
  private final int[] num = {1, 2, 3, 4, 5, 6, 7, 8, 9}; //SALLITUT NUMEROT
  private ArrayList<Integer> numb = new ArrayList<Integer>();  //NUMEROIDEN SEKOITUSTA VARTEN
  private String player;   //PELAAJAN NIMI
  private int difficulty;  //VAIKEUSASTE
  /**
   * Constructor for the Board class, utilizes create() to initialize a complete board,
   * then uses prepare(difficulty) to remove numbers from the board according to difficulty,
   * sets the difficulty and player and starts the timer.
   * @param difficulty 		int value that determines the difficulty of the game being initialized
   * @param player			String value that represents the player's name
   */
  public Board(int difficulty, String player){
    create();
    prepare(difficulty);
    this.difficulty = difficulty;
    this.player = player;
    start = System.currentTimeMillis();
  }
  
  // ----------------------- ASETUS ---------------------------------------------
  /**
   * Method used to initialize a complete sudoku board.
   * Uses an int[] for storing numbers 1-9 and ArrayList<Integer> to shuffle them into,
   * then has a do-while structure inside a double for-loop that fills each row of the board and
   * utilizes the validInt() method to determine whether or not numbers fit into specific locations on the board.
   */
  private void create(){
    numb.clear();                    //SEKOITETTUJEN NUMEROIDEN LISTAN TYHJENNYS
    for (int i=0; i<num.length; i++){
      numb.add(num[i]);              //SEKOITETTUJEN NUMEROIDEN LISTAN T�YTT�
    }
    this.seed = System.nanoTime();   //UUSI SEED RANDOMILLE
    Collections.shuffle(numb, new Random(seed)); //NUMEROIDEN SEKOITUS
    int numbindex = 0;                           //INDEKSI NUMEROIDEN LIS�YST� VARTEN
    for (int i=0; i<board.length; i++){  
      for (int j=0; j<board[i].length; j++){
        do{
          board[i][j] = numb.get(numbindex);
          numbindex++;
          if (numbindex >=9){             //KUN INDEKSI ON 9, ALOITETAAN TAAS NOLLASTA
            numbindex = 0;
          }
        }while (!validInt(board[i][j], i, j));    //KOKEILEE LUKUJA KUNNES LUKU ON MAHDOLLINEN PELIN KANNALTA
      }
    }
  }
  /**
   * Modifies the complete board according to the level of difficulty chosen.
   * If the difficulty chosen is easy, replaces 2 numbers from each sub matrix with 0,
   * for medium the amount replaced is 3, for hard it is 5 and for extreme it is 7.
   * The replacement of numbers from each sub matrix is done randomly so it cannot be predicted which numbers will be replaced.
   * After replacing the numbers according to the level of difficulty, the boolean[][] checkBoard is then also modified,
   * this is done by replacing the 'false' values with 'true' values in the same positions as the replaced numbers on the board.
   * @param difficulty 		level of difficulty chosen for the game being initialized
   */
  private void prepare(int difficulty){   //OTTAA VASTAAN VAIKEUSASTEASETUKSEN 0-3
    Random rnd = new Random(seed);        //JA ASETTAA TIETYN M��R�N ALKIOITA NOLLIKSI
    int amount=0;
    if (difficulty == 0){     //EASY
      amount = 2;
    }
    if (difficulty == 1){     //MEDIUM
      amount = 3;
    }
    if (difficulty == 2){     //HARD
      amount = 5;
    }
    if (difficulty == 3){     //EXTREME
      amount = 7;
    }
    int[] xy = {0, 3, 6};   //ALIMATRIISIEN ENSIMM�ISET ALKIOT
    int x=0;
    int y=0;
    for (int i=0; i<xy.length; i++){
      for (int j=0; j<xy.length; j++){
        for (int k=amount; k>0; k--){
          do{
            x = rnd.nextInt(3)+xy[i];     //RANDOM-KOORDINAATIT TIETYN ALAMATRIISIN SIS�LL�
            y = rnd.nextInt(3)+xy[j];
          }while (board[x][y] == 0);      //TARKISTAA ETTEI ALKIO OLE VALMIIKSI 0
          board[x][y] = 0;
        }
      }
    }
    for (int i=0; i<board.length; i++){       //ASETTAA TARKISTUSTAULUSTA KAIKKI 0-ALKIOITA VASTAAVAT
      for (int j=0; j<board[i].length; j++){  //ALKIOT TRUE:KSI
        if (board[i][j] == 0){
          checkBoard[i][j] = true;
        }else{
          checkBoard[i][j] = false;
        }
      }
    }
  }
/**
 * Sets the value given as a parameter into the position indicated by the xy-coordinates.
 * Value of said parameter must be 0 or above and 9 or below, and the boolean at checkBoard[x][y] must be true in order to set the value.
 * If the validInt() method doesn't return true, the value given can't be placed in the given position and is then set as a 0.
 * @param x		int value for x-coordinate on the board
 * @param y		int value for y-coordinate on the board
 * @param arvo	int value for the number to be placed in the position indicated by the xy-coordinates
 */
  public void set(int x, int y, int arvo){      //ASETTAA ANNETTUJEN INDEKSIEN MUKAISEN ALKION ARVON
    if ((arvo >= 0) && (arvo <= 9) && checkBoard[x][y]){
       board[x][y] = arvo;
       if (!validInt(arvo, x, y)){
         board[x][y] = 0;
       }
     }
   }
  


  
  // ---------------------HAVAINNONTI -------------------------
  /**
   * Checks whether or not a number can be placed in a specific position on the board.
   * First checks if there is an identical number on the same horizontal line, then the vertical line,
   * and lastly uses validSubBoard() to determine whether or not an identical number exists in the same sub matrix.
   * @param number		int value given to be placed in a specific position
   * @param index1		int value of the index of the horizontal line of the board
   * @param index2		int value of the index of the vertical line of the board
   */
  private boolean validInt(int number, int index1, int index2){ //TARKISTAA ETTEI SAMAA NUMEROA OLE VAAKA/PYSTYRIVISSÄ TAI ALIMATRIISISSA
    for (int i=0; i<board[index1].length; i++){
      if ((board[index1][i] == number) && (i != index2)){     //TARKISTAA VAAKARIVIN
        return false;
      }
    }
    for (int i=0; i<board.length; i++){
      if ((board[i][index2] == number) && (i != index1)){     //TARKISTAA PYSTYRIVIN
        return false;
      }
    }
    if (validSubBoard(index1, index2)){                     //TARKISTAA 3X3
      return true;
    }else{
      return false;
    }
  }
  /**
   * Checks whether or not a sub matrix contains the same number twice.
   * According to the indices given as parameters, the method determines which sub matrix to check,
   * with a double for-loop, it checks the designated sub matrix, each time increasing the number
   * in int[] numbers matching the number in the sub matrix, finally it checks the numbers-array
   * to see that none of the indices has a value of more than one and then returns the boolean value accordingly.
   * @param index1		int value of the index of the horizontal line of the board
   * @param index2		int value of the index of the vertical line of the board
   */
  private boolean validSubBoard(int index1, int index2){    //TARKISTAA ETTEI SAMAA NUMEROA OLE SAMASSA ALIMATRIISISSA
    boolean palautus = true;
    int[] numbers = new int[10];         //TARKISTUSTA VARTEN
    int startx = 0;
    int endx = 2;
    int starty = 0;
    int endy = 2;
    if ((index1 == 3) || (index1 == 4) || (index1 == 5)){  //VAAKARIVIN ALKU JA LOPPU
      startx = 3;
      endx = 5;
    }else if ((index1 == 6) || (index1 == 7) || (index1 == 8)){
      startx = 6;
      endx = 8;
    }
    if ((index2 == 3) || (index2 == 4) || (index2 == 5)){  //PYSTYRIVIN ALKU JA LOPPU
      starty = 3;
      endy = 5;
    }else if ((index2 == 6) || (index2 == 7) || (index2 == 8)){
      starty = 6;
      endy = 8;
    }
    for (int i=startx; i<=endx; i++){       //K�Y L�PI �SKEN M��RITELLYN OSAN MATRIISISTA
      for (int j=starty; j<=endy; j++){
        numbers[board[i][j]]++;             //KASVATTAA INDEKSISS� OLLEEN LUVUN MUKAISTA INDEKSI� TARKISTUSTAULUSTA
      }
    }
    for (int i=1; i<numbers.length; i++){     //K�Y L�PI TARKISTUSTAULUN
      if (numbers[i] > 1){                    //TARKISTAA ETTEI MINK��N INDEKSIN ARVO OLE YLI 1
        palautus = false;
      }
    }
    return palautus;
  }
  /**
   * Checks the entire board to see if it has been filled correctly.
   * If the validInt() method returns false or the board has a 0 on it, the method returns false, else it returns true.
   */
  public boolean done(){                       //K�Y L�PI LAUDAN KAIKKI ALKIOT JA TARKISTAA ONKO LAUTA T�YTETTY OIKEIN
    for (int i=0; i<board.length; i++){
      for (int j=0; j<board[i].length; j++){
        if (!validInt(board[i][j], i, j) || (board[i][j] == 0)){
          return false;
        }
      }
    }
    return true;
  }

  public boolean[][] getCheckBoard(){         //PALAUTTAA TAULUN JOSSA TIETO ALKIOISTA JOIDEN ARVOA SAA MUUTTAA
    return checkBoard;
  }
  
  public void printCheckBoard(){             //TULOSTAA TARKISTUSLAUDAN
    for (int i=0; i<checkBoard.length; i++){
      System.out.println(Arrays.toString(checkBoard[i]));
    }
  }

  public void print(){   //TULOSTAA LAUDAN
    for (int i=0; i<board.length; i++){
      System.out.println(Arrays.toString(board[i]));
    }
  }
  
  public int getInt(int a, int b){ //PALAUTTAA NUMERON KOHDASTA BOARD[a][b]
      return board[a][b];
  }
  
  public int getDifficulty(){  //PALAUTTAA VAIKEUSASTEEN NUMERONA 0-3
      return difficulty;
  }
  
  public String getPlayer(){  //PALAUTTAA PELAAJAN NIMEN
      return player;
  }
  /**
   * Saves the current game.
   * First it turns the board into a StringBuilder with a whitespace in between each number,
   * then does the same for the checkBoard with whitespaces in between each boolean,
   * and then removes the trailing whitespaces from each StringBuilder.
   * Then using PrintStream it writes each StringBuilder onto their own lines, followed by difficulty,
   * the time that has elapsed since starting the game and the player's name, each onto their own lines.
   * @throws FileNotFoundException		if the exception is caught, printStackTrace() is performed
   */
  public void saveGame(){  //TALLENTAA PELIN NYKYTILANTEEN TEKSTITIEDOSTOON
    StringBuilder sb1 = new StringBuilder();
    StringBuilder sb2 = new StringBuilder();
    for(int i=0; i<board.length; i++){
     for(int j=0; j<board[i].length; j++){       //KÄY LÄPI LAUDAN KAIKKI ALKIOT JA TEKEE NIISTÄ MERKKIJONON
      sb1.append(board[i][j]);
      sb1.append(" ");
     }
    }
    for(int k=0; k<checkBoard.length; k++){
     for(int l=0; l<checkBoard[k].length; l++){ //KÄY LÄPI TARKISTUSLAUDAN JA TEKEE SIITÄ MERKKIJONON
      sb2.append(checkBoard[k][l]);
      sb2.append(" ");
     }
    }sb1.deleteCharAt(sb1.length()-1);			//POISTAA MERKKIJONOISTA YLIMAARAISET VALILYONNIT
    sb2.deleteCharAt(sb2.length()-1);
    try{
    PrintStream ps = new PrintStream(new File("save.txt"));       
    ps.println(sb1);                              //KIRJOITTAA TIEDOSTOON NUMEROT
    ps.println(sb2);                              //KIRJOITTAA TIEDOSTOON BOOLEAN-ARVOT
    ps.println(difficulty);                       //KIRJOITTAA TIEDOSTOON VAIKEUSASTEEN
    ps.println(System.currentTimeMillis()-start); //KIRJOITTAA TIEDOSTOON KULUNEEN AJAN
    ps.println(player);                           //KIRJOITTAA TIEDOSTOON PELAAJAN NIMEN
    ps.close();
    }catch(FileNotFoundException e){
      e.printStackTrace();
     }
    }
  	/**
  	 * Loads a saved game from a text file.
  	 * First it reads a line from the text file and converts it into a String, then splits the String
  	 * into an array, then, in order, places the values from the array into the board using Integer.parseInt().
  	 * Then it reads the next line of the text file and does the same respectively for the boolean checkBoard
  	 * using Boolean.parseBoolean(), and then reads the next 3 lines and sets the difficulty, time and player name
  	 * using Integer.parseInt() and Long.parseLong() for the difficulty and time.
  	 * @throws FileNotFoundException		the exception is thrown
  	 * @throws IOException					if the exception is caught, printStackTrace() is performed
  	 */
    public void loadGame() throws FileNotFoundException{  //LATAA TIEDOSTOSTA TALLENNETUN PELIN
     BufferedReader br = new BufferedReader(new FileReader("save.txt")); //AVAA TALLENNUSTIEDOSTON
     String[] load1 = new String[81];
     String[] load2 = new String[81];
     int tmp1 = 0;
     boolean tmp2 = true;
     try{
     String input = br.readLine();     //LUKEE RIVIN TIEDOSTOSTA JA TEKEE SIITÄ MERKKIJONON
     load1 = input.split(" ");         //TEKEE MERKKIJONOSTA LISTAN
     int i=0;
     for(int j=0; j<board.length; j++){
       for(int k=0; k<board[j].length; k++){
         board[j][k] = Integer.parseInt(load1[i]);   //ASETTAA LISTAN ARVOT LAUDAN ARVOIKSI
         i++;
       }
     }
     input = br.readLine();            //LUKEE SEURAAVAN RIVIN
     load2 = input.split(" ");
     i=0;
      for(int b=0; b<checkBoard.length; b++){
       for(int c=0; c<checkBoard[b].length; c++){
        checkBoard[b][c] = Boolean.parseBoolean(load2[i]); //JA ASETTAA ARVOT LAUDALLE
        i++;
       }
      }
      this.difficulty = Integer.parseInt(br.readLine());   //LUKEE JA ASETTAA VAIKEUSTASON TALLENNUKSEN MUKAISEKSI
      this.loadtime = Long.parseLong(br.readLine());       //LUKEE KÄYTETYN AJAN JA ASETTAA SEN MUUTTUJAAN loadtime
      this.player = br.readLine();                         //LUKEE JA ASETTAA PELAAJAN NIMEN TALLENNUSTIEDOSTON MUKAISEKSI
     }catch(IOException e){
       e.printStackTrace();
     }
    }
    /**
     * Returns the amount of time that has elapsed since starting the game in HHMMSS-format.
     * Using the start and current times, calculates the time elapsed in hours, minutes and seconds.
     */
    public String timeElapsed(){                           //PALAUTTAA KÄYTETYN AJAN MUODOSSA HHMMSS
    	long current = System.currentTimeMillis();
    	double elapsed = (current + loadtime - start) / 1000.0;
        int hours = (int)(elapsed / 3600);
        int minutes = (int)(elapsed % 3600) / 60;
        int seconds = (int)elapsed % 60;
        return (String.format("%02d", hours) + String.format("%02d", minutes) + String.format("%02d", seconds));
    }
}
       
  