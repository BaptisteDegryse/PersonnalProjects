import java.awt.*;
/**
 * Write a description of class ChainedList here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class BlList
{
    private class Node
    {
        public Node next;
        public Block block;
    }
    private Node top=null;
    private Node runner=top;
    public Block turn()
    {

        if(top==null)
        {   return new Block(0,0,0,0,0);}
        else if(top.next==top)
        {
            return top.block;
        }
        else
        {
            runner=runner.next;
            return runner.block;
        }
    }

    public void add(Block b)
    {
        Node n= new Node();
        n.block=b;
        if(top==null)
        {
            top=n;
            n.next=top;
        }
        else if(top.next==top)
        {
            top.next=n;
            n.next=top;
        }
        else
        {
            n.next=top.next;
            top.next=n;
        }
    }

    public int check()
    {
        int i=0;
        if(top==null){}
        else if(top.next==top)
        {
            if(top.block.alife==false && top.block.mourant==false)
                top=null;
        }
        else
        {
            Node n=top.next;
            do{
                i++;
                if(n.next.block.alife==false && n.next.block.mourant==false)
                {
                    n.next=n.next.next;
                }
                else
                {
                    n=n.next;
                }
            }
            while(n!=top);
        }
        return i;
    }

    public Block creeRandom(int lvl)
    {
        int rx=(int)(Math.random()*Fenetre.width-20);
        int ry=(int)(Math.random()*Fenetre.height-20);
        int rl=(int)(Math.random()*100+10);
        int rL=(int)(Math.random()*100+10);
        int rs=(int)(Math.random()*2*(lvl/2+1)+1);

        int choix=(int)(Math.random()*4);
        if(choix==0)
            return new Block(0,ry,rl,rL,rs);
        else if(choix==1)
            return new Block(rx,0,rl,rL,rs);
        else if(choix==2)
            return new Block(rx,Fenetre.height,rl,rL,rs);
        else 
            return new Block(Fenetre.width,ry,rl,rL,rs);
    }

    public void draw(Graphics g)
    {
        runner=top;
        if(top==null){}
        else if(runner.next!=null)
        {
            while(runner.next!=top)
            {   Block b=this.turn();
                for(int j=0; j<Evite.nbj;j++)
                {
                    if(b.Contains(Evite.guys[j].pos[0], Evite.guys[j].pos[1]))
                    {
                        Evite.guys[j].alife=false;
                        Evite.guys[j].mourant=true;
                    }
                }
                b.draw(g);
            }
        }

    }
}
