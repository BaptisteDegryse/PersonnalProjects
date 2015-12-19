functor
import
   PO at 'portObject.ozf'
   Map at 'map.ozf'
   Args at 'args.ozf'
   OS
export
   NewPokemoz
   WildPoke
   RandomPoke
define

   %gives the type of the pokemoz
   fun {Type Name}
      case Name of 'bulbasoz' then 'grass'
      [] 'chikozrita' then 'grass'
      [] 'charmandoz' then 'fire'
      [] 'rapidoz' then 'fire'
      [] 'oztirtle' then 'water'
      [] 'pozliwhirl' then 'water'
      end
   end

   %fun of the port object
   fun{PokemozBehavior S State}
      XPNeeded=xp(0 0 0 0 5 12 20 30 50)
      HPAvailable=hp(0 0 0 0 20 22 24 26 28 30)
   in
      case S of getPoke(X) then X=State State
      [] lvlup then poke(name:State.name type:State.type xp:State.xp hp:HPAvailable.(State.lvl+1) lvl:State.lvl+1)
      [] xpup(X) then if State.xp+X >= XPNeeded.(State.lvl) then
			 poke(name:State.name type:State.type xp:State.xp+X hp:HPAvailable.(State.lvl+1) lvl:State.lvl+1)
		      else
			 poke(name:State.name type:State.type xp:State.xp+X hp:State.hp lvl:State.lvl)
		      end
      [] hpdown(X) then poke(name:State.name type:State.type xp:State.xp hp:State.hp-X lvl:State.lvl)
      end
   end

   
      
   %create a new pokemoz
   fun{NewPokemoz Name Level}
      Init=poke(name:Name type:{Type Name} xp:0 hp:20+(Level-5)*2 lvl:Level) in
      {PO.newPortObject PokemozBehavior Init}
   end

   %return the Nth element of a list
   fun {GiveNth L N}
      case L of H|nil then H
      [] H|T then if N==0 then H
		  else {GiveNth T N-1} end
      end
   end
   List='bulbasoz'|'oztirtle'|'charmandoz'|'pozliwhirl'|'rapidoz'|'chikozrita'|nil
   fun {RandomPoke}
      Poke = {GiveNth List (1+{OS.rand} mod 6)}
      Level = 5+({OS.rand} mod 3)
   in
      {NewPokemoz Poke Level}
   end
   fun{WildPoke X Y}
      if ({OS.rand} mod 10) < Args.args.probability andthen Map.map.Y.X==1 then
	 {RandomPoke} 
      else nil end
   end

end
