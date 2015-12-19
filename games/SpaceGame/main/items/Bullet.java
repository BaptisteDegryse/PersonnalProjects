package items;

import java.awt.Color;
import java.awt.Graphics;

import reseau.Reseau;
import menu.Util;

public class Bullet extends ItemParent{

	public float[] destination=new float[2];
	public float speed=10f;
	public ItemParent target;
	public boolean alife=true;

	public Bullet(float[] position, float[] size) {
		super(position, size);
		// TODO Auto-generated constructor stub
	}
	public Bullet(Unit shooter,ItemParent target){
		super(new float[]{shooter.position[0]+shooter.size[0]/2,shooter.position[1]+shooter.size[1]/2},new float[]{5,5});
		if(target==null)
			alife=false;
		else{
			destination[0]=target.getPosition()[0]+target.getSize()[0]/2;
			destination[1]=target.getPosition()[1]+target.getSize()[1]/2;
			this.target=target;
		}
	}
	public void action(){
		destination[0]=target.position[0]+target.size[0]/2;
		destination[1]=target.position[1]+target.size[1]/2;
		if(Util.getDistance(position, destination)<10)
			kill();
		move();
	}
	public void kill(){
		if(target instanceof Unit){
			Unit target2=(Unit)target;
			target2.life[target2.type]--;
			if(target2.life[target2.type]<=0 && !target2.dying){
				target2.shouldDie=true;
				Reseau.write(Reseau.KILL, target.ID, 0+"", 0+"");
			}
		}
		else if(target instanceof Planet){
			target.life--;
			if(target.life<=0 && !target.dying){
				target.shouldDie=true;
				Reseau.write(Reseau.KILL, target.ID, 0+"", 0+"");
			}
		}
		alife=false;
		//System.out.println("bullet mort");
	}
	public void render(Graphics g){
		g.setColor(Color.red);
		g.fillOval((int)position[0], (int)position[1], (int)size[0], (int)size[1]);

	}
	public void move(){
		float[]realVec=new float[]{destination[0]-position[0],destination[1]-position[1]};
		float length=(float)Math.sqrt(realVec[0]*realVec[0]+realVec[1]*realVec[1]);
		realVec[0]/=length;
		realVec[1]/=length;
		position[0]+=realVec[0]*speed;
		position[1]+=realVec[1]*speed;
	}


}
