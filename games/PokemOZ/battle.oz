functor
export
   AutoBattle
   ManualBattle
import
   QTk at 'x-oz://system/wp/QTk.ozf'
   Args at 'args.ozf'
   OS
   Application

define

   CD = {OS.getCWD} %get current working directory
   BulbasozImg={QTk.newImage photo(file:CD#'/images/bulbasoz.gif')}
   OztirtleImg={QTk.newImage photo(file:CD#'/images/oztirtle.gif')}
   CharmandozImg ={QTk.newImage photo(file:CD#'/images/charmandoz.gif')}
   PozliwhirlImg ={QTk.newImage photo(file:CD#'/images/pozliwhirl.gif')}
   RapidozImg ={QTk.newImage photo(file:CD#'/images/rapidoz.gif')}
   ChikozritaImg ={QTk.newImage photo(file:CD#'/images/chikozrita.gif')}

   %find right image
   fun {FindImage Name}
      case Name of
	 'bulbasoz' then  BulbasozImg
      [] 'oztirtle' then  OztirtleImg
      [] 'pozliwhirl' then PozliwhirlImg
      [] 'rapidoz' then RapidozImg
      [] 'chikozrita' then ChikozritaImg
      else CharmandozImg
      end
   end
   
   %fonts
   FontAA={QTk.newFont font(family:'Courier' size:60)}
   FontA={QTk.newFont font(family:'Courier' size:20)}
   FontB={QTk.newFont font(family:'Courier' size:15)}
   FontC={QTk.newFont font(family:'Courier' size:12)}
   FontD={QTk.newFont font(family:'Courier' size:30)}


   %Manual battle
   proc{ManualBattle CurFight Poke1 Poke2 Wild ReturnP AF}
      fun{SeeManualBattle}

	 proc{LoopFight Bind}
	    State0 State1 State2 Attacks='Acid'|'Quash'|'Snarl'|nil
	     %return the Nth element of a list
	    fun {GiveNth L N}
	       case L of H|nil then H
	       [] H|T then if N==1 then H
			   else {GiveNth T N-1} end
	       end
	    end
	 in
	    {Send CurFight getState(State0)}
	    case State0 of lifes(max1:_ rest1:_ max2:_ rest2:_ turn:_) then
	       {Send CurFight attack1(Poke1 Poke2)}
	       {Send CurFight getState(State1)}
	       case State1 of lifes(max1:_ rest1:_ max2:_ rest2:_ turn:_) then
		  {Infos2 set(text:Poke2State.name#' lost '#State0.rest2-State1.rest2#' HP')}
		  {Infos3 set(text:Poke2State.name#' is going to attack you !')}

		  if State1.rest2 < 1 then {Send CurFight won(Poke1 Poke2)} Bind=unit
		  else
		     {Bat create(rectangle 720 270-10*State1.max2 750 270-10*State1.rest2 fill:red)}
		     {HP2 set(text:'HP : '#State1.rest2#'/'#State1.max2)}
		     {Delay 2000-200*Args.args.speed}
		     {Infos set(text:Poke2State.name#' sent '#{GiveNth Attacks ({OS.rand} mod 3 + 1)}#' !')}
		     {Send CurFight attack2(Poke1 Poke2)}
		     {Send CurFight getState(State2)}
		     case State2 of lifes(max1:_ rest1:_ max2:_ rest2:_ turn:_) then

			{Infos2 set(text:'You lost '#State0.rest1-State2.rest1#' HP')}
			{Infos3 set(text:'It\'s your turn to attack !')}

			if State2.rest1 < 1 then {Send CurFight lost(Poke2)} Bind=unit
			else
			   {Bat create(rectangle 50 270-10*State2.max1 80 270-10*State2.rest1 fill:red)}
			   {HP1 set(text:'HP : '#State2.rest1#'/'#State2.max1)}
			end
		     else skip end
		  end
	       else skip end
	    else skip end
		       
	    
	 end

	 fun{EndFight}
	    State P2State in
	    {Send CurFight getState(State)}
	    {Send Poke2 getPoke(P2State)}
	    if State == Poke1 then
	       {Bat create(rectangle 0 0 800 400 fill:green)}
	       {Bat create(image image:Image1 400 150)}
	       {Bat create(text text:'You won the battle !' 400 50 font:FontD)}
	       {Bat create(text text:'You won '#P2State.lvl#' XP' 400 280 font:FontA)}
	       
	       {Bat create(text text:'Your actual level : '#{Send State getPoke($)}.lvl 400 310 font:FontA)}
	       {Delay 2500}
	       {Window close}


	    elseif State == Poke2 then
	       {Bat create(rectangle 0 0 800 400 fill:red)}
	       {Bat create(image image:Image1 400 200)}
	       {Bat create(text text:'You have lost the game !' 400 50 font:FontD)}
	       {Delay 4000}
	       {Window close}

	       {Application.exit 0}

	    else skip end
	    State
	 end
	 
	 
	 HP1 HP2
	 Bind
	 Atta Atta1 Atta2 Run
	 Infos Infos2 Infos3
      in
	 
	 
	 {Bat create(rectangle 0 0 800 400 fill:grey)}
	 {Bat create(image image:Image1 220 150)}
	 {Bat create(image image:Image2 580 150)}
	 {Bat create(text text:Poke1State.name font:FontD 200 30)}
	 {Bat create(text text:Poke2State.name font:FontD 600 30)}
	 {Bat create(text text:'VS' font:FontAA 400 160)}

	 {Bat create(rectangle 50 270 80 270-10*FightState.max1 fill:green)}
	 {Bat create(rectangle 750 270 720 270-10*FightState.max2 fill:green)}
	 {Bat create(text text:'HP : '#FightState.rest1#'/'#FightState.max1 font:FontA 65 300 handle:HP1)}
	 {Bat create(text text:'HP : '#FightState.rest2#'/'#FightState.max2 font:FontA 735 300 handle:HP2)}

	 {Bat create(text text:'Ready ? The battle can begin !' font: FontB handle:Infos 220 330)}
	 {Bat create(text text:' ' font: FontB handle:Infos2 220 360)}
	 {Bat create(text text:' ' font: FontB handle:Infos3 220 390)}

	  
	 {Bat create(rectangle 600 325 750 355 fill:white)}
	 {Bat create(text text:'Attack : Acid' font:FontB 675 340 handle:Atta)}
	 {Atta bind(event:'<Button-1>' action:proc{$} {Infos set(text:'You sent Acid !')} {LoopFight Bind} end)}
	 {Bat create(rectangle 445 325 595 355 fill:white)}
	 {Bat create(text text:'Attack : Quash' font:FontB 520 340 handle:Atta1)}
	 {Atta1 bind(event:'<Button-1>' action:proc{$} {Infos set(text:'You sent Quash !')}{LoopFight Bind} end)}
	 {Bat create(rectangle 445 360 595 390 fill:white)}
	 {Bat create(text text:'Attack : Snarl' font:FontB 520 375 handle:Atta2)}
	 {Atta2 bind(event:'<Button-1>' action:proc{$} {Infos set(text:'You sent Snarl !')} {LoopFight Bind} end)}
	 if Wild then
	    {Bat create(rectangle 600 360 750 390 fill:white)}
	    {Bat create(text text:'Run out' font:FontA 675 375 handle:Run)}
	    {Run bind(event:'<Button-1>' action:proc{$} {Send CurFight ran} {Send ReturnP ran} Bind=unit end)}
	 end
	 
	 thread {Wait Bind} {EndFight} end
      end

  %interface commune
      Poke1State
      {Send Poke1 getPoke(Poke1State)}
      Poke2State
      {Send Poke2 getPoke(Poke2State)}
      FightState
      {Send CurFight getState(FightState)}

      Image1={FindImage Poke1State.name}
      Image2={FindImage Poke2State.name}

      Bat
      Desc=td(canvas(handle:Bat width:800 height:400))
      Window={QTk.build Desc}
      BackToMap Begin Begin2 BackToMap2 Begin3
   in
      
      {Bat create(image image:Image1 200 200)}
      {Bat create(image image:Image2 600 200)}
      {Bat create(text text:Poke1State.name font:FontA 200 330)}
      {Bat create(text text:Poke2State.name font:FontA 600 330)}
      {Bat create(text text:'Type : '#Poke1State.type font:FontC 200 350)}
      {Bat create(text text:'Type : '#Poke2State.type font:FontC 600 350)}
      {Bat create(text text:'Level : '#Poke1State.lvl font:FontC 200 370)}
      {Bat create(text text:'Level : '#Poke2State.lvl font:FontC 600 370)}
      {Bat create(text text:'VS' font:FontAA 400 200)}
      {Window show}
      %Si wildPoke
      if Wild then
	 {Window set(title:'Wild PokemOZ')}

	 %Si AF==0 alors run out
	 if AF==0 then {Bat create(text text:'You just met a wild PokemOZ but you decided to run out !' font:FontA 400 40)}
	    {Bat create(rectangle 285 75 515 105 fill:white)}
	    {Bat create(text text:'Back to the map' handle:BackToMap 400 90 font:FontA)}
	    {BackToMap bind(event:'<Button-1>' action:proc{$} {Window close} {Send ReturnP ran} end)}

	 %Si AF==1 alors fight
	 elseif AF==1 then {Bat create(text text:'You just met a wild PokemOZ and you decided to fight him !\n' font:FontA 400 40)}
	    {Bat create(rectangle 285 75 515 105 fill:white)}

	    {Bat create(text text:'Begin the battle' handle:Begin 400 90 font:FontA)}
	    {Begin bind(event:'<Button-1>' action:proc{$} {Send ReturnP {SeeManualBattle}} end)}

	    %Si AF==2 alors choice
	 elseif AF==2 then {Bat create(text text:'You just met a wild PokemOZ !\n  Do you want to fight him ?' font:FontA 400 40)}
	    {Bat create(rectangle 130 75 370 105 fill:white)}
	    {Bat create(text text:'Begin the battle' handle:Begin2 250 90 font:FontA)}
	    {Begin2 bind(event:'<Button-1>' action:proc{$} {Send ReturnP {SeeManualBattle}} end)}

	    {Bat create(rectangle 430 75 670 105 fill:white)}
	    {Bat create(text text:'Back to the map' handle:BackToMap2 550 90 font:FontA)}
	    {BackToMap2 bind(event:'<Button-1>' action:proc{$} {Window close} {Send ReturnP ran} end)}
	    

	 end
	 

      %Si trainer	 
      else
	 {Window set(title:'Trainer PokemOZ')}
	 {Bat create(text text:'You just met another trainer !' font:FontA 400 40)}
	 {Bat create(rectangle 285 75 515 105 fill:white)}
	 {Bat create(text text:'Begin the battle' handle:Begin3 400 90 font:FontA)}
	 {Begin3 bind(event:'<Button-1>' action:proc{$} {Send ReturnP {SeeManualBattle}} end)}
      end
	
       
   end
   
%-----------------------------------------------------------------------------------
   %Automatic battle
   fun{AutoBattle CurFight Poke1 Poke2 Wild AF}
      fun{SeeAutoBattle}

	 proc{LoopFight Bind}
	    State0 State1 State2 Attacks='Acid'|'Quash'|'Snarl'|nil
	     %return the Nth element of a list
	    fun {GiveNth L N}
	       case L of H|nil then H
	       [] H|T then if N==1 then H
			   else {GiveNth T N-1} end
	       end
	    end
	 in
	    
	    {Delay 2000-200*Args.args.speed}
	    {Send CurFight getState(State0)}
	    {Infos set(text:Poke1State.name#' sent '#{GiveNth Attacks ({OS.rand} mod 3 + 1)}#' !')}

	    {Send CurFight attack1(Poke1 Poke2)}
	    {Send CurFight getState(State1)}
	    {Infos2 set(text:Poke2State.name#' lost '#State0.rest2-State1.rest2#' HP')}
	    {Infos3 set(text:Poke2State.name#' is going to attack you !')}

	    if State1.rest2 < 1 then {Send CurFight won(Poke1 Poke2)} Bind=unit
	    else
	       {Bat create(rectangle 720 270-10*State1.max2 750 270-10*State1.rest2 fill:red)}
	       {HP2 set(text:'HP : '#State1.rest2#'/'#State1.max2)}
	       {Delay 2000-200*Args.args.speed}
	       {Infos set(text:Poke2State.name#' sent '#{GiveNth Attacks ({OS.rand} mod 3 + 1)}#' !')}
	       {Send CurFight attack2(Poke1 Poke2)}
	       {Send CurFight getState(State2)}
	       {Infos2 set(text:'You lost '#State0.rest1-State2.rest1#' HP')}
	       {Infos3 set(text:'It\'s your turn to attack !')}

	       if State2.rest1 < 1 then {Send CurFight lost(Poke2)} Bind=unit
	       else
		  {Bat create(rectangle 50 270-10*State2.max1 80 270-10*State2.rest1 fill:red)}
		  {HP1 set(text:'HP : '#State2.rest1#'/'#State2.max1)}
		  {LoopFight Bind}
	       end
	    end
	 end

	 fun{EndFight}
	    State in
	    {Send CurFight getState(State)}
	    
	    if State == Poke1 then P1State P2State in

	       {Bat create(rectangle 0 0 800 400 fill:green)}
	       {Bat create(image image:Image1 400 150)}
	       {Bat create(text text:'You just won the battle !' 400 50 font:FontD)}
	       {Send Poke2 getPoke(P2State)}
	       {Bat create(text text:'You won '#P2State.lvl#' XP' 400 280 font:FontA)}
	       
	       {Send Poke1 getPoke(P1State)}
	       {Bat create(text text:'Your actual level : '#P1State.lvl 400 310 font:FontA)}

	    else
	       {Bat create(rectangle 0 0 800 400 fill:red)}
	       {Bat create(image image:Image1 400 200)}
	       {Bat create(text text:'You lost the game !' 400 50 font:FontD)}
	       {Delay 4000}
	       {Application.exit 0}

	    end
	    {Delay 4000}
	    {Window close}
	    State
	 end
	 
	 
	 HP1 HP2
	 Bind
	 Infos Infos2 Infos3
      in
	 
	 
	 {Bat create(rectangle 0 0 800 400 fill:grey)}
	 {Bat create(image image:Image1 220 150)}
	 {Bat create(image image:Image2 580 150)}
	 {Bat create(text text:Poke1State.name font:FontD 200 30)}
	 {Bat create(text text:Poke2State.name font:FontD 600 30)}
	 {Bat create(text text:'VS' font:FontAA 400 160)}

	 {Bat create(rectangle 50 270 80 270-10*FightState.max1 fill:green)}
	 {Bat create(rectangle 750 270 720 270-10*FightState.max2 fill:green)}
	 {Bat create(text text:'HP : '#FightState.rest1#'/'#FightState.max1 font:FontA 65 300 handle:HP1)}
	 {Bat create(text text:'HP : '#FightState.rest2#'/'#FightState.max2 font:FontA 735 300 handle:HP2)}
	 {Bat create(text text:'Ready ? The battle can begin !' font: FontB handle:Infos 400 330)}
	 {Bat create(text text:' ' font: FontB handle:Infos2 400 360)}
	 {Bat create(text text:' ' font: FontB handle:Infos3 400 390)}


	 thread {LoopFight Bind} end
	 {Wait Bind}
	 {EndFight}
      end


   
      %interface commune
      Poke1State
      {Send Poke1 getPoke(Poke1State)}
      Poke2State
      {Send Poke2 getPoke(Poke2State)}
      FightState
      {Send CurFight getState(FightState)}

      Image1={FindImage Poke1State.name}
      Image2={FindImage Poke2State.name}

      Bat
      Desc=td(canvas(handle:Bat width:800 height:400))
      Window={QTk.build Desc}
   in
      
      {Bat create(image image:Image1 200 200)}
      {Bat create(image image:Image2 600 200)}
      {Bat create(text text:Poke1State.name font:FontA 200 330)}
      {Bat create(text text:Poke2State.name font:FontA 600 330)}
      {Bat create(text text:'Type : '#Poke1State.type font:FontC 200 350)}
      {Bat create(text text:'Type : '#Poke2State.type font:FontC 600 350)}
      {Bat create(text text:'Level : '#Poke1State.lvl font:FontC 200 370)}
      {Bat create(text text:'Level : '#Poke2State.lvl font:FontC 600 370)}
      {Bat create(text text:'VS' font:FontAA 400 200)}
      {Window show}
      %Si wildPoke
      if Wild then
	 {Window set(title:'Wild PokemOZ')}

	 %Si AF==0 alors run out
	 if AF==0 then {Bat create(text text:'You just met a wild PokemOZ but you decided to run out !' font:FontA 400 40)}
	    {Delay 3000}
	    {Window close}
	    'ran'

	 %Si AF==1 alors fight
	 elseif AF==1 then {Bat create(text text:'You just met a wild PokemOZ and you decided to fight him !\n                 The fight will begin' font:FontA 400 40)}
	    {Delay 3000}
	    {SeeAutoBattle}
	 end

      %Si trainer	 
      else
	 {Window set(title:'Trainer PokemOZ')}
	 {Bat create(text text:'You just met another trainer !\n The fight between your PokemOZ will begin' font:FontA 400 40)}
	 {Delay 3000}
	 {SeeAutoBattle}
      end
	
   end
end

   
