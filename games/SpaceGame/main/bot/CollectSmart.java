package bot;

import items.Planet;
import items.UnitR;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CollectSmart {
	public ArrayList<UnitR> unitsR=new ArrayList<UnitR>();
	public static CollectSmart current=new CollectSmart();
	public Lock mutex=new ReentrantLock();
	
	public void action(){
		mutex.lock();
		Iterator<UnitR> iter=unitsR.iterator();
		while(iter.hasNext()){
			UnitR u= iter.next();
			if(!u.alife)
				iter.remove();
			else{
				boolean ableToTake=false;
				for(int i=0;i<3;i++)
					if(u.mother.resources[i].quantite>0 && u.resource[i]<u.full)
						ableToTake=true;
				
				if((u.mining==3 || u.mother.resources[u.mining].quantite==0 || u.resource[u.mining]>=u.full) && 
						ableToTake ){
					int mine;
					if(u.mining>=2)
						mine=0;
					else
						mine=u.mining+1;
					u.changeMining(mine,true);
					//System.out.println(mine);
				}
				else if(!ableToTake){
					u.changeMining(3, true);
					Planet p=u.whereCanIGo();
					//System.out.println("stop...");
					if(p!=null){
						//System.out.println(p.ID);
						u.mother.units.remove(u);
						u.mother.sendUnit(u, p, true);
					}
				}
				
			}
		}
		mutex.unlock();
	}
	public void add(UnitR u){
		mutex.lock();
		if(!unitsR.contains(u))
			unitsR.add(u);
		mutex.unlock();
	}
	public void remove(UnitR u){
		mutex.lock();
		unitsR.remove(u);
		mutex.unlock();
	}
}
