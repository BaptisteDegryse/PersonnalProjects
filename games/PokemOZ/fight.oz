functor
import
   OS
   PO at 'portObject.ozf'
   Args at 'args.ozf'
   Battle at 'battle.ozf'
   IA at 'ia.ozf'
export
   Start
   NewFight
define

   fun {FightBehavior S State}
      case S of
	 getState(X) then X=State State
      [] attack1(Poke1 Poke2) then HPLost={Attack Poke1 Poke2} in {Send Poke2 hpdown(HPLost)} lifes(max1:State.max1 rest1:State.rest1 max2:State.max2 rest2:State.rest2-HPLost turn:2)
      [] attack2(Poke1 Poke2) then HPLost={Attack Poke2 Poke1} in {Send Poke1 hpdown(HPLost)} lifes(max1:State.max1 rest1:State.rest1-HPLost max2:State.max2 rest2:State.rest2 turn:1)
      [] won(Poke1 Poke2) then Poke2State in
	 {Send Poke2 getPoke(Poke2State)}
	 {Send Poke1 xpup(Poke2State.lvl)}
	 Poke1
      [] lost(Poke2) then Poke2
      [] ran then ran
	 
	 
      end
   end


   fun {NewFight Poke1 Poke2}
      Poke1State
      {Send Poke1 getPoke(Poke1State)}
      Poke2State
      {Send Poke2 getPoke(Poke2State)}
      MaxHP1=20+(Poke1State.lvl - 5)*2
      MaxHP2=20+(Poke2State.lvl - 5)*2
      Init=lifes(max1:MaxHP1 rest1:Poke1State.hp max2:MaxHP2 rest2:Poke2State.hp turn:1)
   in
      {PO.newPortObject FightBehavior Init}

   end
   
   
   fun {Attack Poke1 Poke2}
      Dommage=a( a(2 1 3) a(3 2 1) a(1 3 2) )
      fun{Kind A}
	 case A of grass then 1
	 [] fire then 2
	 [] water then 3
	 end
      end

      Poke1State
      {Send Poke1 getPoke(Poke1State)}
      Poke2State
      {Send Poke2 getPoke(Poke2State)}
      
      I={Kind Poke1State.type}
      J={Kind Poke2State.type}
   in
      if({OS.rand} mod (100-((6+Poke1State.lvl-Poke2State.lvl)*9))) < 10 then Dommage.I.J
      else 0
      end
   end
   
   %return the winner of the fight
   fun{Start Poke1 Poke2 WildPoke} %wildpoke true si wild poke or false if trainer
      
      if Args.manual==true then {ManualFight Poke1 Poke2 WildPoke} 
      else {AutomaticFight Poke1 Poke2 WildPoke}
      end
   end
   

   fun {ManualFight Poke1 Poke2 WildPoke}
      %Appel de 'interface sur battle (attention, adapter en fct de autofight) où on bind les boutons à {Send CurFight}

      CurFight ReturnS
      ReturnP = {NewPort ReturnS} in
      CurFight={NewFight Poke1 Poke2}
      thread {Battle.manualBattle CurFight Poke1 Poke2 WildPoke ReturnP Args.args.autofight} end
      case ReturnS of H|_ then H end
   end
   

   fun {AutomaticFight Poke1 Poke2 Wild}
      %Vérifier si on se bat si wild pokemoz (seulement si autofight laisse le choix)
      Poke1State
      {Send Poke1 getPoke(Poke1State)}
      Poke2State
      {Send Poke2 getPoke(Poke2State)}

      CurFight
   in
      CurFight = {NewFight Poke1 Poke2}
      
      if Args.args.autofight == 2 then
	 if {IA.goodChoice Poke1State Poke2State} then {Battle.autoBattle CurFight Poke1 Poke2 Wild 1}
	 else {Battle.autoBattle CurFight Poke1 Poke2 Wild 0}
	 end
      elseif Args.args.autofight == 1 then {Battle.autoBattle CurFight Poke1 Poke2 Wild 1}
      else {Battle.autoBattle CurFight Poke1 Poke2 Wild 0}
      end

      
   end
   
end

