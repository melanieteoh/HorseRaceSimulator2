/**
 * Write a description of class Horse here.
 * 
 * @author Melanie Teoh Jia Xin
 * @version 1 April 2024
 */
public class Horse
{
    private char horseSymbol;
    private String horseName = "";
    private double horseConfidence;
    private int horseDistance = 0;
    private boolean fellOrNot;
      
    public Horse(char horseSymbol, String horseName, double horseConfidence)
    {
       this.horseSymbol = horseSymbol;
       this.horseName = horseName;
       setConfidence(horseConfidence);
       return;
    }
    
    public void fall()
    {
        fellOrNot = true;
        return;
    }
    
    public double getConfidence()
    {
        return horseConfidence;
    }
    
    public int getDistanceTravelled()
    {
        return horseDistance;
    }
    
    public String getName()
    {
        return horseName;
    }
    
    public char getSymbol()
    {
        return horseSymbol;
    }
    
    public void goBackToStart()
    {
        horseDistance = 0;
        this.fellOrNot = false;
        return;
    }
    
    public boolean hasFallen()
    {
        return fellOrNot;
    }

    public void moveForward()
    {
        horseDistance++;
        return;
    }

    public void setConfidence(double newConfidence) 
    {
        if (newConfidence < 0.1) {
            this.horseConfidence = 0.1;
        } else if (newConfidence > 0.9) {
            this.horseConfidence = 0.9;
        } else {
            this.horseConfidence = newConfidence;
        }
    }
    
    public void setSymbol(char newSymbol)
    {
        this.horseSymbol = newSymbol;
    }

    public static void main(String[]args)
    {
        Horse horse1 = new Horse('A', "Apple", -1);
        
        System.out.println(horse1.getSymbol());
        System.out.println(horse1.getName());
        System.out.println(horse1.getConfidence());
        System.out.println(horse1.getDistanceTravelled());
        System.out.println(horse1.hasFallen());

        Horse horse2 = new Horse('B', "Blueberry", 1.2);
        
        System.out.println(horse2.getSymbol());
        System.out.println(horse2.getName());
        System.out.println(horse2.getConfidence());
        System.out.println(horse2.getDistanceTravelled());
        System.out.println(horse2.hasFallen());
    }
}