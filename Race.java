import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.lang.Math;

/**
 * A three-horse race, each horse running in its own lane
 * for a given distance
 * 
 * @author Melanie Teoh Jia Xin
 * @version 1.0
 */
public class Race
{
    private int raceLength;
    private Horse lane1Horse;
    private Horse lane2Horse;
    private Horse lane3Horse;
    private ArrayList<Horse> horses = new ArrayList<Horse>();
    private HashMap<Horse, Integer> points = new HashMap<>();
    private HashMap<Horse, Boolean> fallenStatus = new HashMap<>();
    private HashMap<Horse, Integer> tempPoints = new HashMap<>();
    private ArrayList<String> raceWinners = new ArrayList<>();

    /**
     * Constructor for objects of class Race
     * Initially there are no horses in the lanes
     * 
     * @param distance the length of the racetrack (in metres/yards...)
     */
    public Race(int distance)
    {
        raceLength = distance;
        lane1Horse = null;
        lane2Horse = null;
        lane3Horse = null;
    }
    
    private void setupHorses() 
    {
        Scanner scanner = new Scanner(System.in);
        for (int i = 1; i <= 3; i++) 
        {  
            System.out.println("Enter name for Horse in Lane " + i + ":");
            String name = scanner.nextLine();
            System.out.println("Enter confidence level for " + name + " (0.0 to 1.0):");
            double confidence = scanner.nextDouble();
            scanner.nextLine(); 
            addHorse(new Horse((char)('A' + i - 1), name, confidence), i);
        }
    }

    /**
     * Adds a horse to the race in a given lane
     * 
     * @param theHorse the horse to be added to the race
     * @param laneNumber the lane that the horse will be added to
     */
    public void addHorse(Horse theHorse, int laneNumber)
    {
        if (laneNumber == 1)
        {
            lane1Horse = theHorse;
        }
        else if (laneNumber == 2)
        {
            lane2Horse = theHorse;
        }
        else if (laneNumber == 3)
        {
            lane3Horse = theHorse;
        }
        else
        {
            System.out.println("Cannot add horse to lane " + laneNumber + " because there is no such lane");
        }

        horses.add(theHorse);
        points.put(theHorse, 50);
        tempPoints.put(theHorse, 0);
    }
    
    /**
     * Start the race
     * The horse are brought to the start and
     * then repeatedly moved forward until the 
     * race is finished
     */
    public void startRace()
    {
        resetHorses();

        long startTime = System.currentTimeMillis();
        
        //reset all the lanes (all horses not fallen and back to 0). 
        lane1Horse.goBackToStart();
        lane2Horse.goBackToStart();
        lane3Horse.goBackToStart();

        //declare a local variable to tell us when the race is finished
        boolean finished = false;
                       
        while (!finished)
        {
            //move each horse
            moveHorse(lane1Horse);
            moveHorse(lane2Horse);
            moveHorse(lane3Horse);
                        
            //print the race positions
            printRace();

            long currentTime = System.currentTimeMillis();
            System.out.println("Elapsed time: " + (currentTime - startTime) + " milliseconds");

            boolean allHorsesFallen = true;

            for (Horse h : horses)
            {
                if (h.hasFallen())
                {
                    System.out.println(h.getName() + " has fallen");
                }
                else
                {
                    allHorsesFallen = false;
                    break;
                }
            }

            if (allHorsesFallen)
            {
                System.out.println("All horses have fallen. The race is over.");
                finished = true;
            }
            else
            {
                //if any of the three horses has won the race is finished
                if (raceWonBy(lane1Horse) || raceWonBy(lane2Horse) || raceWonBy(lane3Horse) )
                {
                    finished = true;
                }
            }

            //wait for 100 milliseconds
            try{ 
                TimeUnit.MILLISECONDS.sleep(100);
            }catch(Exception e){
                Thread.currentThread().interrupt();
                System.out.println("Race interrupted.");
            }
        }
    }

    private void resetHorses()
    {
        if (lane1Horse != null)
        {
            lane1Horse.goBackToStart();
        }
        if (lane2Horse != null)
        {
            lane2Horse.goBackToStart();
        }
        if (lane3Horse != null)
        {
            lane3Horse.goBackToStart();
        }

        for (Horse h : horses)
        {
            fallenStatus.put(h, false);
        }
    }

    private void winnerAnnounce()
    {
        boolean winner = false;
        String horseWinner = "none";
        for (Horse horse : horses) 
        {
            if (raceWonBy(horse)) 
            {
                points.put(horse, points.get(horse) + 20);
                System.out.println("The winner is " + horse.getName().toUpperCase());
                winner = true;
                horseWinner = horse.getName();
                adjustConfidence(horse, true);
            } 
        }

        for (Horse horse : horses)
        {
            if (!raceWonBy(horse))
            {
                adjustConfidence(horse, false);
                if (horse.hasFallen())
                {
                    points.put(horse, points.get(horse) - 10);
                }
            }
        }

        for (Horse horse : horses)
        {
            int updatedPoints = points.get(horse) + tempPoints.get(horse);
            points.put(horse, updatedPoints);
            tempPoints.put(horse, 0);
            if (updatedPoints <= 0)
            {
                System.out.println("Race is over.");
                System.out.println(horse.getName() + " has run out of points.");
                printOverallResults();
                System.exit(0);
            }
        }

        raceWinners.add(horseWinner);

        if (!winner) {
            System.out.println("No winners in this race as all horses have fallen.");
        }

        System.out.println("Updated Points:");
        printCurrentPoints();
        return;
    }

    private void adjustConfidence(Horse horse, boolean won) 
    {
        double currentConfidence = horse.getConfidence();
    
        if (won) 
        {
            if (currentConfidence < 0.9) 
            {
                horse.setConfidence(Math.min(currentConfidence + 0.1, 0.9));
            }
        } 
        else 
        {
            horse.setConfidence(Math.max(currentConfidence - 0.1, 0.1));
        }
    }    

    private void printOverallResults()
    {
        System.out.println("Overall Results:");
        System.out.println("Winners for each race:");
        int raceCount = 1;
        for (String winner : raceWinners)
        {
            System.out.println("Race " + raceCount + ": " + winner);
            raceCount++;
        }
        Horse overallWinner = null;
        int maxPoints = Integer.MIN_VALUE;
        for (Horse horse : horses) {
            int horsePoints = points.get(horse);
            System.out.println(horse.getName() + ": " + horsePoints + " points");
            if (horsePoints > maxPoints) {
                maxPoints = horsePoints;
                overallWinner = horse;
            }
        }

        if (overallWinner != null)
        {
            System.out.println("OVERALL WINNER IS " + overallWinner.getName().toUpperCase());
        }
        else
        {
            System.out.println("No overall winner.");
        }
        return;
    }
    
    /**
     * Randomly make a horse move forward or fall depending
     * on its confidence rating
     * A fallen horse cannot move
     * 
     * @param theHorse the horse to be moved
     */
    private void moveHorse(Horse theHorse)
    {
        //if the horse has fallen it cannot move, 
        //so only run if it has not fallen
        if (!fallenStatus.getOrDefault(theHorse, false) && !theHorse.hasFallen())
        {
            //the probability that the horse will move forward depends on the confidence;
            if (Math.random() < theHorse.getConfidence())
            {
                theHorse.moveForward();
            }
            
            //the probability that the horse will fall is very small (max is 0.1)
            //but will also will depends exponentially on confidence 
            //so if you double the confidence, the probability that it will fall is *2
            if (Math.random() < (0.1*theHorse.getConfidence()*theHorse.getConfidence()))
            {
                if (theHorse.getDistanceTravelled() != raceLength)
                {
                    theHorse.fall();
                    fallenStatus.put(theHorse, true);
                    tempPoints.put(theHorse, tempPoints.get(theHorse) - 20);
                }
            }
        }
    }

    /** 
     * Determines if a horse has won the race
     *
     * @param theHorse The horse we are testing
     * @return true if the horse has won, false otherwise.
     */
    private boolean raceWonBy(Horse theHorse)
    {
        if (theHorse.getDistanceTravelled() == raceLength)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /***
     * Print the race on the terminal
     */
    private void printRace()
    {
        System.out.print("\033[H\033[2J");  
        System.out.flush();
        
        multiplePrint('=',raceLength+3); //top edge of track
        System.out.println();
        
        printLane(lane1Horse);
        System.out.println();
        
        printLane(lane2Horse);
        System.out.println();
        
        printLane(lane3Horse);
        System.out.println();
        
        multiplePrint('=',raceLength+3); //bottom edge of track
        System.out.println(); 
        System.out.println("ANNOUNCEMENT");
        System.out.println("Current Points:");
        printCurrentPoints();
    }

    private void printCurrentPoints()
    {
        for (Horse horse : horses)
        {
            System.out.println(horse.getName() + ": " + points.get(horse) + " points");
        }
        return;
    }
    
    /**
     * print a horse's lane during the race
     * for example
     * |           X                      |
     * to show how far the horse has run
     */
    private void printLane(Horse theHorse)
    {
        //calculate how many spaces are needed before
        //and after the horse
        int spacesBefore = theHorse.getDistanceTravelled();
        int spacesAfter = raceLength - theHorse.getDistanceTravelled();
        
        //print a | for the beginning of the lane
        System.out.print('|');
        
        //print the spaces before the horse
        multiplePrint(' ',spacesBefore);
        
        //if the horse has fallen then print dead
        //else print the horse's symbol
        if(theHorse.hasFallen())
        {
            System.out.print('\u2322');
        }
        else
        {
            System.out.print(theHorse.getSymbol());
        }
        
        //print the spaces after the horse
        multiplePrint(' ',spacesAfter);
        
        //print the | for the end of the track
        System.out.print('|');
        String formattedConfidence = String.format("%.1f", theHorse.getConfidence());
        System.out.print(" " + theHorse.getName() + " (Current confidence " + formattedConfidence + ")");
    }
    
    /***
     * print a character a given number of times.
     * e.g. printmany('x',5) will print: xxxxx
     * 
     * @param aChar the character to Print
     */
    private void multiplePrint(char aChar, int times)
    {
        int i = 0;
        while (i < times)
        {
            System.out.print(aChar);
            i = i + 1;
        }
    }

    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        Race r1 = new Race(15);

        r1.setupHorses();

        boolean continueRace = true;
        while (continueRace) 
        {
            r1.startRace();
            r1.winnerAnnounce();
            
            System.out.println("Do you want to race again? (Y/N)");
            String input = scanner.nextLine();
            if (!input.equalsIgnoreCase("Y")) 
            {
                continueRace = false;
            }
            r1.printOverallResults();
        }
    }
}