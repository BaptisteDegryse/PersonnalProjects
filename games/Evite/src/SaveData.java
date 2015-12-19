import java.io.*;
import java.util.Scanner;
import java.util.*;

/**
 * Write a description of class SaveData here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class SaveData
{
    public static ArrayList<Joueurs> liste= new ArrayList<Joueurs>();
    public static void save( String name, int min, int sec)
    {
        Joueurs yep= new Joueurs(name, min, sec);
        liste.add(yep);
        //Collections.sort(liste);
        write();
    }
    
    public static Joueurs[] getData()
    {
        Joueurs [] tab= new Joueurs [20];
        Scanner sc= null;
        try
        {
            sc=new Scanner(new FileReader("Data.dat"));
        }
        catch(IOException e)
        {System.err.println("erreur de lecture");}

        String l;
        while(sc.hasNext())
        {
            l=sc.nextLine();
            liste.add(new Joueurs(l));
        }
        sc.close();
        Collections.sort(liste);
        
        for(int i=0; i<20 && i<liste.size(); i++)
        {
            tab[i]=liste.get(i);
        }
        liste.clear();
        return tab;
    }

    public static void write()
    {
        Scanner sc= null;
        try
        {
            sc=new Scanner(new FileReader("Data.dat"));
        }
        catch(IOException e)
        {System.err.println("erreur de lecture");}

        String l;
        while(sc.hasNext())
        {
            l=sc.nextLine();
            liste.add(new Joueurs(l));
        }
        sc.close();
        Collections.sort(liste);
        
        PrintWriter pr;
        Iterator<Joueurs> iter1= liste.iterator();
        Joueurs m,n= null;
        m=iter1.next();
        while(iter1.hasNext())
        {
            m=n;
            n=iter1.next();
            while(n.compareTo(m)==0)
            {
                iter1.remove();
                if(iter1.hasNext())
                n=iter1.next();
                else
                break;
            }
        }
        try{
            File outFile= new File("Data.dat");
            pr= new PrintWriter(outFile);
            Iterator<Joueurs> iter= liste.iterator();
            int i=1;
            while(iter.hasNext() && i<=20)
            {
                Joueurs j= iter.next();
                pr.println(i + " " +j.name + " " + j.min + " " + (j.sec<10? "0"+j.sec : j.sec ));
                i++;
            }
            pr.close();
        }
        catch(IOException e){System.err.println("erreur d'écriture, fichier non trouvé");}
        liste.clear();

    }

}
