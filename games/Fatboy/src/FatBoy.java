import java.awt.*;
/**
 * Write a description of class FatBoy here.
 * 
 * @author Baptiste Degryse 
 * @version 03/01/2013
 */
public class FatBoy
{   public static Boy b= new Boy();
    public static Fenetre fen;
    public static double delta=0;
    public static int i=0;
    public static boolean again=true;
    public static void main(String [] args)
    {
        SaveMap.loadMaps();
        SaveMap.saveM();
        while(again)
        {
            fen= new Fenetre();
            SaveMap.loadMapsBad();
            SaveMap.saveM();
            if(again)
            Editor.main(new String [0]);
            //SaveMap.maps.clear();
            i=SaveMap.maps.size()-1;
        }
    }

    public static void wait(int sec)
    {
        long t= System.currentTimeMillis();
        while(System.currentTimeMillis()-t<sec){}
    }
}
