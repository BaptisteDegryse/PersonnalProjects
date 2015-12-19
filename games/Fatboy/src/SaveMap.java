import java.io.*;
import java.io.*;
import java.util.*;
/**
 * Write a description of class SaveMap here.
 * 
 * @author Baptiste Degryse 
 * @version 03/01/2013
 */
public class SaveMap
{
    public static transient ArrayList<Map> maps= new ArrayList<Map>();
    public static transient ArrayList<Map> maps1= new ArrayList<Map>();
    public static transient ArrayList<Map> mapsBad= new ArrayList<Map>();
    public static void save()
    {
        if(maps.size()==0 )
            loadMaps();
        maps.add(new Map(Editor.name,Editor.listeObj,Editor.c,Editor.text)); 
        saveM();
    } 

    public static void loadMaps()
    {
        maps.clear();
        ObjectInputStream ois; 

        try { 

            ois = new ObjectInputStream( 
                new BufferedInputStream( 
                    new FileInputStream( 
                        new File("map V3 .txt")))); 

            try { 

                maps=((ArrayList<Map>)ois.readObject());

            } catch (ClassNotFoundException e) { 
                e.printStackTrace(); 
            } 
            ois.close(); 
        }
        catch (FileNotFoundException e) { 
            e.printStackTrace(); 
        } catch (IOException e) { 
            e.printStackTrace(); 
        }      
        

        try { 

            ois = new ObjectInputStream( 
                new BufferedInputStream( 
                    new FileInputStream( 
                        new File("map1.txt")))); 

            try { 

                maps1=((ArrayList<Map>)ois.readObject());

            } catch (ClassNotFoundException e) { 
                e.printStackTrace(); 
            } 
            ois.close(); 
        }catch (IOException e) {}      
        for(int i=0; i<maps1.size();i++)
        {
            maps.add(maps1.get(i));
        }
        maps1.clear();
        
        try { 

            ois = new ObjectInputStream( 
                new BufferedInputStream( 
                    new FileInputStream( 
                        new File("map2.txt")))); 

            try { 

                maps1=((ArrayList<Map>)ois.readObject());

            } catch (ClassNotFoundException e) { 
                e.printStackTrace(); 
            } 
            ois.close(); 
        }catch (IOException e) {}      
        for(int i=0; i<maps1.size();i++)
        {
            maps.add(maps1.get(i));
        }
        maps1.clear();
        
        try { 

            ois = new ObjectInputStream( 
                new BufferedInputStream( 
                    new FileInputStream( 
                        new File("map3.txt")))); 

            try { 

                maps1=((ArrayList<Map>)ois.readObject());

            } catch (ClassNotFoundException e) { 
                e.printStackTrace(); 
            } 
            ois.close(); 
        } catch (IOException e) {}      
        for(int i=0; i<maps1.size();i++)
        {
            maps.add(maps1.get(i));
        }
        maps1.clear();
    }
    
    public static void loadMapsBad()
    {
        mapsBad.clear();
        ObjectInputStream ois; 

        try { 

            ois = new ObjectInputStream( 
                new BufferedInputStream( 
                    new FileInputStream( 
                        new File("map V3 .txt")))); 

            try { 

                mapsBad=((ArrayList<Map>)ois.readObject());

            } catch (ClassNotFoundException e) { 
                e.printStackTrace(); 
            } 
            ois.close(); 
        }
        catch (FileNotFoundException e) { 
            e.printStackTrace(); 
        } catch (IOException e) { 
            e.printStackTrace(); 
        }      

    }

    public static void saveM()
    {
        ObjectOutputStream oos; 
        try { 
            oos = new ObjectOutputStream( 
                new BufferedOutputStream( 
                    new FileOutputStream( 
                        new File("map V3 .txt")))); 
            if(maps.size()!=0)
            oos.writeObject(maps);
            oos.close(); 
            
        }
        catch (FileNotFoundException e) { 
            e.printStackTrace(); 
        } catch (IOException e) { 
            e.printStackTrace(); 
        }      
    }
} 

