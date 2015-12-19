
/**
 * Write a description of class Evite here.
 * 
 * @author Baptiste Degryse
 * @version Version 1.0 
 */
public class Evite
{   public static Guy [] guys;
    public static int nbj=2;
    public static BlList blocks;

    public static void main(String[] args)
    {
        while(true)
        {
            guys= new Guy [nbj];
            guys[0]=new Guy(400,400,1);
            if(nbj==2)
            guys[1]=new Guy(430,400,2);
            blocks=new BlList();
            
            Fenetre fen= new Fenetre();
        }
    }

    public static void wait(int sec)
    {
        long t= System.currentTimeMillis();
        while(System.currentTimeMillis()-t<sec){}
    }
    
    
}
