functor
import
   QTk at 'x-oz://system/wp/QTk.ozf'
   Bender at 'bender.ozf'
   Object at 'object.ozf'
   Enemies at 'enemies.ozf'
   OS
   System
export
   Canvas
   Init
   TileSize
   Map
   Draw
   RockImg
   AirBall
   EarthBall
   FireBall
   WaterBall
define
   Window
   CD = {OS.getCWD} %get current working directory
   TileSize=40 % ne plus changer
   Color=color(c(255 255 255) c(0 0 0) c(0 0 255) c(100 100 100))
   
   Ground={QTk.newImage photo(file:CD#'/images/ground.gif')}
   Wall={QTk.newImage photo(file:CD#'/images/wall.gif')}
  
   AirImg={QTk.newImage photo(file:CD#'/images/air.gif')}
   EarthImg={QTk.newImage photo(file:CD#'/images/earth.gif')}
   FireImg={QTk.newImage photo(file:CD#'/images/fire.gif')}
   WaterImg={QTk.newImage photo(file:CD#'/images/water.gif')}
   RockImg={QTk.newImage photo(file:CD#'/images/rock.gif')}

   AirBall={QTk.newImage photo(file:CD#'/images/airBall.gif')}
   EarthBall={QTk.newImage photo(file:CD#'/images/earthBall.gif')}
   FireBall={QTk.newImage photo(file:CD#'/images/fireBall.gif')}
   WaterBall={QTk.newImage photo(file:CD#'/images/iceBall.gif')}

   Images=im(Ground Wall)

   Map=map(r(1 1 1 1 1 1 1 1 1 2 2 1 1 1 2 2 1 1 1 2 2 1 2 1) 
            r(1 1 1 2 2 1 1 1 1 1 1 1 1 1 2 2 1 1 1 2 2 1 2 1)
            r(1 1 1 2 2 1 1 1 1 2 2 1 1 1 2 2 1 1 1 1 1 1 2 1)
            r(1 1 1 1 1 1 1 1 1 2 2 1 1 1 1 1 1 1 1 1 1 1 1 1)
            r(1 1 1 1 1 1 1 1 1 2 2 1 1 1 1 1 1 1 1 2 2 1 2 1)
            r(1 1 1 2 2 1 1 1 1 1 1 1 1 1 2 2 1 1 1 2 2 1 1 1)
            r(1 1 1 2 2 1 1 1 1 1 1 1 1 1 2 2 1 1 1 1 1 1 2 1)
            r(1 1 1 2 2 1 1 1 1 2 2 1 1 1 2 2 1 1 1 2 2 1 2 1)
            r(1 1 1 1 1 1 1 1 1 2 2 1 1 1 2 2 1 1 1 2 2 1 2 1) 
            r(1 1 1 2 2 1 1 1 1 1 1 1 1 1 2 2 1 1 1 2 2 1 2 1)
            r(1 1 1 2 2 1 1 1 1 2 2 1 1 1 2 2 1 1 1 1 1 1 2 1)
            r(1 1 1 1 1 1 1 1 1 2 2 1 1 1 1 1 1 1 1 1 1 1 1 1)
            r(1 1 1 1 1 1 1 1 1 2 2 1 1 1 1 1 1 1 1 2 2 1 2 1)
            r(1 1 1 2 2 1 1 1 1 1 1 1 1 1 2 2 1 1 1 2 2 1 1 1)
            r(1 1 1 2 2 1 1 1 1 1 1 1 1 1 2 2 1 1 1 1 1 1 2 1)
            r(1 1 1 2 2 1 1 1 1 2 2 1 1 1 2 2 1 1 1 2 2 1 2 1))            
   
   Canvas
   Desc=td(canvas(bg:black
		  width:{Record.width Map.1}*TileSize
		  height:{Record.width Map}*TileSize
		  handle:Canvas))
   Window={QTk.build Desc}
   {Window set(title:'Avatar')}
   {Window show}
   proc {DrawMap Map Row}
      X=TileSize
      proc {DrawR R I}
	 if I=={Record.width R}+1 then skip
	 else
	    {Canvas create(image image:Images.(R.I) (I-1)*X (Row-1)*X anchor:nw)}
	    {DrawR R I+1}
	 end
      end
   in
      if Row=={Record.width Map}+1 then skip %{Browse Row#Map}
      else {DrawR Map.Row 1} {DrawMap Map Row+1} end
   end
   proc {BindKeys Player}
      X in
      {Send Player get(X)}
      %moves
      {Window bind(event:X.keys.up action:proc{$}{Bender.move Player move(0 ~1)}end)}
      {Window bind(event:X.keys.left action:proc{$}{Bender.move Player move(~1 0)}end)}
      {Window bind(event:X.keys.down action:proc{$}{Bender.move Player move(0 1)}end)}
      {Window bind(event:X.keys.right action:proc{$}{Bender.move Player move(1 0)}end)}
      %spells
      {Window bind(event:X.keys.1 action:proc{$}{Send Player attack(1)}end)}
      {Window bind(event:X.keys.2 action:proc{$}{Send Player attack(2)}end)}
      {Window bind(event:X.keys.3 action:proc{$}{Send Player attack(3)}end)}
      {Window bind(event:X.keys.4 action:proc{$}{Send Player attack(4)}end)}

   end
   fun {Draw Img X Y}
      H in
      {Canvas create(image image:Img (X-1)*TileSize (Y-1)*TileSize anchor:nw handle:H)}
      H
   end
   proc {EnemyCreator I Timer}
      X={OS.rand} mod {Record.width Map.1} +1
      Y={Record.width Map}
      H={Draw FireImg X Y}
   in
      {Enemies.newEnemy X#Y H Timer}
      {Delay 10*Timer}
      if I>20 andthen Timer> 100 then
         {EnemyCreator 0 Timer-40}
      else
         {EnemyCreator I+1 Timer}
      end
   end


   proc{Init}
      H1 H2 H3 in
      {DrawMap Map 1}
      %Object.gameList={Object.newList nil}
      {System.show Object.gameList}
      {Canvas create(image image:EarthImg 0 3 anchor:nw handle:H1)}
      {Canvas create(image image:AirImg 0 TileSize+3 anchor:nw handle:H2)}
      Bender.playerEarth={Bender.newBender 1#1 earth H1}
      Bender.playerAir={Bender.newBender 1#2 air H2}
      {System.show 2}
      {BindKeys Bender.playerAir}
      {BindKeys Bender.playerEarth}
      {System.show 3}

      thread {Delay 5000} {EnemyCreator 0 500} end


   end
end