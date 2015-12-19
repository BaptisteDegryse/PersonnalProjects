functor
import
   OS
   QTk at 'x-oz://system/wp/QTk.ozf'
   Reader at 'reader.ozf'
   Application
export
   Run
   DrawMap
   Map
   Leave
   DrawTrainer
   Finish
   End
   Window
define
   CD = {OS.getCWD} %get current working directory

   TrainerImg={QTk.newImage photo(file:CD#'/images/trainer.gif')}
   Trainer2Img={QTk.newImage photo(file:CD#'/images/trainer2.gif')}
   
   TileSize=40
   Color1=green
   Color0=yellow
   ColorEnd=gray

   Map= {Reader.loadMap}


   End={Record.width Map.1}#{Record.width Map}

   Canvas
   Desc=td(canvas(bg:black
		  width:{Record.width Map.1}*TileSize
		  height:{Record.width Map}*TileSize
		  handle:Canvas))
   Window
   proc {DrawMap2 Map Row}
      X=TileSize
      proc {DrawR R I}
	 Color in
	 if I=={Record.width R}+1 then skip
	 else
	    if Row==End.2 andthen I==End.1 then Color=ColorEnd
	    elseif R.I==1 then Color=Color1
	    else Color=Color0 end
	    {Canvas create(rectangle (I-1)*X (Row-1)*X I*X Row*X fill:Color)}
	    {DrawR R I+1}
	 end
      end
   in
      if Row=={Record.width Map}+1 then skip
      else {DrawR Map.Row 1} {DrawMap2 Map Row+1} end
   end
   
    proc {Leave X Y}
      Color in
      if X#Y==End then Color=ColorEnd
      elseif Map.Y.X==1 then Color=Color1
      else Color=Color0 end
      {Canvas create(rectangle (X-1)*TileSize (Y-1)*TileSize
		     X*TileSize Y*TileSize fill:Color)}
    end
    proc {DrawTrainer I X Y}
       if I==0 then
	  {Canvas create(image image:TrainerImg (X-1)*TileSize+7 (Y-1)*TileSize+1 anchor:nw)}
       else
	  {Canvas create(image image:Trainer2Img (X-1)*TileSize+7 (Y-1)*TileSize+1 anchor:nw)}
       end
    end
    proc {Run}
       Window={QTk.build Desc}
       {Window set(title:'PokemOZ !')}
       {DrawMap2 Map 1}
       {Window show}
    end
    proc {DrawMap}
       {DrawMap2 Map 1}
    end
    proc {Finish}
       {Delay 1000}
       {Canvas create(rectangle 0 0 {Record.width Map.1}*TileSize
				     {Record.width Map}*TileSize fill:green)}
       {Canvas create(text 200 50 text:'Congratulations, you finished the game !')}
       {Delay 4000}
       {Application.exit 0}
    end
end
