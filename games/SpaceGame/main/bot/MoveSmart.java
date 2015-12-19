package bot;

import items.Planet;
import items.Portail;
import items.Unit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MoveSmart {
	public ArrayList<Unit> units=new ArrayList<Unit>();
	public static MoveSmart current=new MoveSmart();
	public Lock mutex=new ReentrantLock();
	
	public void action(){
		mutex.lock();
		Iterator<Unit> iter=units.iterator();
		while(iter.hasNext()){
			Unit u=iter.next();
			Planet p=u.fightNearby();
			if(p!=null && !(p instanceof Portail)){
				u.mother.units.remove(u);
				u.mother.sendUnit(u, p, true);
			}
		}
		mutex.unlock();
	}
	
	
	public void add(Unit u){
		mutex.lock();
		if(!units.contains(u))
			units.add(u);
		mutex.unlock();
		u.moveSmart=true;
	}
	public void remove(Unit u){
		mutex.lock();
		units.remove(u);
		mutex.unlock();
		u.moveSmart=false;
	}
}
