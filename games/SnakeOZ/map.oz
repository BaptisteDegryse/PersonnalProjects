%Author : Baptiste Degryse
functor
import
   QTk at 'x-oz://system/wp/QTk.ozf'
   OS
   System
   Snake at 'snake.ozf'
export
   Draw
   SnakeImg
   MeatImg
   LostImg
   Init
   TileSize
   Map
define
   Window
   CD = {OS.getCWD} %get current working directory
   TileSize=40 % ne plus changer
   Color=color(c(255 255 255) c(0 0 0) c(0 0 255) c(100 100 100))
   
   Ground={QTk.newImage photo(file:CD#'/images/ground.gif')}
   Wall={QTk.newImage photo(file:CD#'/images/wall.gif')}
   SnakeImg={QTk.newImage photo(file:CD#'/images/Circle_Green.gif')}
   MeatImg={QTk.newImage photo(file:CD#'/images/Circle_Blue.gif')}
   LostImg={QTk.newImage photo(file:CD#'/images/lost.gif')}


   Images=im(Ground Wall)

   Map=map( r(1 1 1 1 1 1 1 1 1 2 2 1 1 1 2 2 1 1 1 2 2 1 2 1) 
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
   {Window set(title:'snake')}
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
   proc {BindKeys}
      P = Snake.head 
      Move = Snake.move in
      %moves
      {Window bind(event:"<Up>" action:proc{$} {Move P 0 ~1} end)}
      {Window bind(event:"<Left>" action:proc{$} {Move P ~1 0} end)}
      {Window bind(event:"<Down>" action:proc{$} {Move P 0 1} end)}
      {Window bind(event:"<Right>" action:proc{$} {Move P 1 0} end)}

   end
   fun {Draw Img X Y}
      H in
      {Canvas create(image image:Img (X-1)*TileSize (Y-1)*TileSize anchor:nw handle:H)}
      H
   end

   proc{Init}
      {DrawMap Map 1}
      %Object.gameList={Object.newList nil}
      
      {BindKeys}
      {Snake.init}
      {System.show 3}

   end
end