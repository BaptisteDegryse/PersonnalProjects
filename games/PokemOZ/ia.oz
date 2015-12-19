functor
import
   Map at 'map.ozf'
   Trainer at 'trainer.ozf'
export
   IA
   GoodChoice
define
   M=Map.map
   End=Map.'end'
   proc {IA Player}
      fun{Abs X}
	 if X>0 then X else ~X end
      end
      fun{Or X Y}
	 if X then true else Y end
      end
      fun{Size L Acc}
	 case L of _|T then {Size T Acc+1}
	 else Acc end
      end
      fun{GetXY}
	 Tr={Send Player get($)} in
	 Tr.x#Tr.y
      end
      fun{GetPoke}
	 Tr={Send Player get($)} in
	 {Send Tr.poke getPoke($)}
      end
      fun{IsThere X#Y What}
	 X>0 andthen Y>0 andthen X=<{Record.width Map.map.1} andthen Y=<{Record.width Map.map}
	 andthen M.Y.X==What
      end
      fun{CheckHist Hist N L}
	 case Hist of H|T then
	    if N==0 then L
	    else L2={Remove H L} in {CheckHist T N-1 L2} end
	 [] nil then L end
      end
      fun{Remove X L}
	 case L of H|T andthen X==H then T
	 [] H|T then H|{Remove X T}
	 else nil end
      end
      fun{StayOn Road P}
	 fun{Check X#Y I}
	    if {IsThere X#Y Road} then X#Y|{StayOn2 I+1}
	    else {StayOn2 I+1} end
	 end
	 fun{StayOn2 I}
	    if I<5 then {Check P.I I}
	    else nil end
	 end
      in
	 {StayOn2 1}
      end
      fun {Repetition Hist}
	 fun {Add L X}
	    case L of nil then [X#1]
	    [] (H#I)|T andthen H==X then (H#(I+1))|T
	    [] H|T then H|{Add T X} end
	 end
	 fun {Count Hist N Acc}
	    if N==0 then Acc
	    else case Hist of H|T then {Count T N-1 {Add Acc H}}
		 else Acc end
	    end
	 end
	 fun {Repet L N}
	    case L of (_#I)|_ andthen I>N then true
	    [] _|T then {Repet T N}
	    else false end
	 end
	 L={Count Hist 15 nil}
      in
	 {Repet L 5}
      end
      fun{WhereAreTrainers P}
	 fun{Check X#Y I}
	    R in
	    {Send Trainer.trainerList isStarFree(X Y R)}
	    if R==true then {Loop I+1}
	    else X#Y|{Loop I+1} end
	 end
	 fun{Loop I}
	    if I<5 then {Check P.I I}
	    else nil end
	 end
      in
	 {Loop 1}
      end
      
      fun{Priorities X#Y}% for end
	 P1 P2 P3 P4 in
	 % P1
	 if {Abs End.1-X}>={Abs End.2-Y} then
	    if End.1>X then P1=(X+1)#Y else P1=(X-1)#Y end
	 else
	    if End.2>Y then P1=X#(Y+1) else P1=X#(Y-1) end
	 end

	 %P2
	 if {Abs End.1-X}>={Abs End.2-Y} then
	    if End.2>Y then P2=X#(Y+1) else P2=X#(Y-1) end
	 else
	    if End.1>X then P2=(X+1)#Y else P2=(X-1)#Y end
	 end

	 %P3 (opposite of P2)
	 P3=(2*X-P2.1)#(2*Y-P2.2)
	 %P4 (opposite of P1)
	 P4=(2*X-P1.1)#(2*Y-P1.2)

	 p(P1 P2 P3 P4)
      end
      proc{Loop Hist I}
	 X#Y={GetXY}
	 P={Priorities X#Y}
	 Poke={GetPoke}% record (state), not portObject
	 L
	 Road
	 if {Or Poke.lvl<7 (Poke.hp<12 andthen Poke.hp>5)} then  
	    Road=1
	 else
	    Road=0
	 end
	 L={StayOn Road P} Pos
	 L2
	 if Road==1 then
	    L2={CheckHist Hist 5 L}% Try to not come back on one of the five last places
	 else
	    L2={CheckHist Hist 6 L}
	 end
	 Tr={WhereAreTrainers P}
      in
	 {Trainer.trainerSpeed}
	 
	 if {Repetition Hist} then
	    Pos=P.1
	 elseif L2==nil andthen Tr==nil then
	    Pos=P.1
	 elseif {Size Tr 0}>0 then
	    Pos=(2*X-Tr.1.1)#(2*Y-Tr.1.2) %opposite of the trainer
	 else
	    Pos=L2.1
	 end
	 _={Trainer.move Player Pos.1-X Pos.2-Y}
	 if End=={GetXY} then skip
	 else {Loop Pos|Hist I+1} end
      end
   in
      thread {Loop nil 0} end
   end
   fun {GoodChoice Poke1State Poke2State}
      if Poke1State.lvl>7 andthen Poke1State.hp>15 then false
      elseif Poke2State.type == Poke1State.type andthen Poke2State.lvl > Poke1State.lvl-2 andthen Poke1State.hp<14 then false
      elseif Poke2State.type == Poke1State.type andthen Poke2State.lvl > Poke1State.lvl then false
      elseif (Poke2State.type == 'grass' andthen Poke1State.type == 'water') then false
      elseif (Poke2State.type == 'fire' andthen Poke1State.type == 'grass') then false
      elseif (Poke2State.type == 'water' andthen Poke1State.type == 'fire') then false
      else true
      end	
   end
end
