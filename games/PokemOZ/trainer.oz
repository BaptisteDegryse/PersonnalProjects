functor
import
   Map at 'map.ozf'
   PO at 'portObject.ozf'
   FightFunctor at 'fight.ozf'
   Pokemoz at 'pokemoz.ozf'
   Args at 'args.ozf'
   IA at 'ia.ozf'
export
   Init
   Fight
   LoopFight
   Move
   TrainerList
   TrainerSpeed
define
   TrainerList
   Fight={PO.newPortCell false}
   DELAY=200 %cst
   Speed=Args.args.speed
   proc{TrainerSpeed}
      {Delay ((10-Speed)*DELAY)}
   end
   % trainer(x: y: poke: i: )
   % i=0 for the player, i>0 for the bots
   
   fun {NewTrainer X Y I Poke}
      fun{F Msg State}
	 case Msg of move(X Y) then
	    if X==0 andthen Y==0 then {Map.drawTrainer State.i State.x State.y} State
	    else
	       {Map.leave State.x State.y}
	       {Map.drawTrainer State.i State.x+X State.y+Y}
	       if State.x+X#State.y+Y==Map.'end' then
		  thread {PO.assign Fight true} {Map.finish} end
	       else skip end
	       trainer(x:X+State.x y:Y+State.y poke:State.poke i:State.i)
	    end
	 [] refresh then {Map.drawTrainer State.i State.x State.y} State
	 [] died then {Map.leave State.x State.y} nil
	 [] get(P) then P=State State
	 end
      end
      P={PO.newPortObject F trainer(x:X y:Y poke:Poke i:I)}
   in
      {Send P refresh}
      P
   end
   proc{LoopFight}
      F in {PO.access Fight F}
      if F then {Delay 200} {LoopFight}
      else skip end
   end
   %true if moves, false if blocked by the map and trainerPort if trainer there (did not move)
   fun {Move T X Y}
      fun {IsInTheMap X Y}
	 X>0 andthen Y>0 andthen X=<{Record.width Map.map.1}
	 andthen Y=<{Record.width Map.map}
      end
      Tr R R2 R3 WildPoke in
      {LoopFight}
      {Send T get(Tr)}
      if Tr==nil then died
      else
	 R={IsInTheMap Tr.x+X Tr.y+Y}
	 if X==0 andthen Y==0 then R2=true
	 else R2={Send TrainerList isFree(Tr.x+X Tr.y+Y $)} end
	 
	 R3= R andthen R2==true

	 if R andthen Tr.i==0 then WildPoke={Pokemoz.wildPoke Tr.x+X Tr.y+Y}
	 else WildPoke=nil end

	 if R3 andthen WildPoke==nil then {Send T move(X Y)} true
	 elseif R3 then {Send T move(X Y)} {StartFight T nil WildPoke} true
	 elseif R==false then false
	 elseif {Send R2 get($)}.i>0 andthen  {Send T get($)}.i>0  then R2
	 elseif Tr.i==0 then {StartFight T R2 nil} false
	 else {StartFight R2 T nil} false end
      end
   end
   proc {StartFight T1 T2 WildPoke}
      {LoopFight}
      {PO.assign Fight true}
      Poke1 Poke2
      Tr1 Tr2
      Winner
      {Send T1 get(Tr1)}
      Poke1=Tr1.poke
   in
      if WildPoke==nil then
	 {Send T2 get(Tr2)}
	 Poke2=Tr2.poke
	 Winner={FightFunctor.start Poke1 Poke2 false}

      else Poke2=WildPoke
	 Winner={FightFunctor.start Poke1 Poke2 true}
      end
      
   
      if Winner=='ran' then skip 
      elseif Winner==Poke1 andthen WildPoke==nil then {Send TrainerList trainerDied(Tr2.i)}
      elseif Winner==Poke1 then skip 
      else skip end

      {PO.assign Fight false}
   end
   proc {LinearMove P Width Horizontal}
      proc{L I X}
	 if I==0 then {L Width ~X}
	 else
	    {TrainerSpeed}
	    R in
	    if Horizontal then R={Move P X 0}
	    else R={Move P 0 X} end
	    if R==true then {L I-1 X}
	    elseif R==false then {L Width-I ~X}
	    elseif R==died then skip
	    else{L I X} end
	 end
      end
   in
      thread {L Width 1} end
   end
   proc {Stay P}
      proc{F}
	 {TrainerSpeed}
	 R={Move P 0 0} in
	 if R==died then skip
	 else {F} end
      end
   in
      thread {F} end
   end
   proc {BindKeys P}
      {Map.window bind(event:"<Up>" action:proc{$}_={Move P 0 ~1} end)}
      {Map.window bind(event:"<Left>" action:proc{$}_={Move P ~1 0} end)}
      {Map.window bind(event:"<Down>" action:proc{$}_={Move P 0 1} end)}
      {Map.window bind(event:"<Right>" action:proc{$}_={Move P 1 0} end)}
   end
   fun {NewTrainerList L}
      proc {SendAll Rec L}
	 case L of H|T then {Send H Rec} {SendAll Rec T}
	 [] nil then skip end
      end
      fun {IsFree X Y L}
	 case L of H|T then Tr={Send H get($)} in
	    if X==Tr.x andthen Y==Tr.y then H
	    else {IsFree X Y T} end
	 [] nil then true end
      end
      fun {RemovePlayer L}
	 case L of H|T then Tr={Send H get($)} in
	    if Tr.i==0 then T
	    else H|{RemovePlayer T} end
	 else nil end
      end
      fun {StarFree X Y L2} % is free up, down, left, right around X Y
	 L={RemovePlayer L2} in
	 {IsFree X+1 Y L}==true andthen {IsFree X-1 Y L}==true andthen
	 {IsFree X Y+1 L}==true andthen {IsFree X Y-1 L}==true andthen
	 {IsFree X Y L}==true
      end
      fun {Remove I L}
	 if I==0 then L
	 else
	    case L of H|T then Tr={Send H get($)} in
	       if Tr.i==I then {Send H died} T
	       else H|{Remove I T} end
	    else nil end
	 end
      end
	    
      fun{F Msg State}
	 case Msg of isFree(X Y R) then
	    R={IsFree X Y State} State
	 [] isStarFree(X Y R) then
	    R={StarFree X Y State} State
	 [] trainerDied(I) then {Remove I State}
	 [] refresh then {SendAll refresh State} State
	 end
      end
   in
      {PO.newPortObject F L}
   end
   
   proc {Init PlayerPoke2}
      PlayerPoke=PlayerPoke2
      {Map.run}
      Player = {NewTrainer 1 1 0 {Pokemoz.newPokemoz PlayerPoke 5}}
      Bot1={NewTrainer 1 3 1 {Pokemoz.randomPoke}}
      Bot2={NewTrainer 5 1 2 {Pokemoz.randomPoke}}
      Bot3={NewTrainer 19 1 3 {Pokemoz.randomPoke}}
      Bot4={NewTrainer 15 6 4 {Pokemoz.randomPoke}}
      Bot5={NewTrainer 13 4 5 {Pokemoz.randomPoke}}
   in
      TrainerList={NewTrainerList [Player Bot1 Bot2 Bot3 Bot4 Bot5]}
      
      if Args.manual then {BindKeys Player}
      else {IA.iA Player} end
      
      {LinearMove Bot1 4 true}
      {LinearMove Bot2 4 false}
      {Stay Bot3}
      {LinearMove Bot4 3 false}
      {LinearMove Bot5 1 false}
   end
end
