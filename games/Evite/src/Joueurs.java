
/**
 * Write a description of class Joueurs here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Joueurs implements Comparable
{
    public int sec,min;
    public String name,place;
    public Joueurs(String name, int min, int sec)
    {
        this.name=name;
        this.sec=sec;
        this.min=min;
    }

    public Joueurs(String ligne)
    {
        String [] tab= new String[4];
        try
        {
            tab= ligne.split(" ",4);
        }
        catch(Exception e){}
        this.place=tab[0];
        this.name=tab[1];
        try
        {
            this.min=Integer.parseInt(tab[2]);
            this.sec=Integer.parseInt(tab[3]);
        }
        catch(Exception e){System.err.println("erreur format min, sec");}

    }

    public String toString()
    {
        String nom=this.name;
        boolean add=false;
        for(int i=0; i<(15-nom.length()); i++)
        {
            if(place.equals("10")&& !add)
            {
                i++;
                add=true;
            }

            nom+=" ";

        }
        
        String s = String.format
        ("%10s", this.name);
        String format = "|%1$-5s|%2$-12s|%3$-5s|";
        
        String s1= String.format(format,place,this.name,this.min + " : " + (this.sec<10? "0"+this.sec : this.sec ));
        
        return s1;
    }

    public int compareTo(Object n)
    {
        Joueurs j=(Joueurs)n;
        if(n==null || this == null)
            return 10;
        if(this.min>j.min)
            return -1;
        if(this.min<j.min)
            return 1;
        else
        {
            if(this.sec>j.sec)
                return -1;
            if(this.sec<j.sec)
                return 1;
            else
                return this.name.compareTo(j.name);
        }
    }
}
