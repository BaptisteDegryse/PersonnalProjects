functor
import
	PO at 'portObject.ozf'
	Bender at 'bender.ozf'
	Object at 'object.ozf'
	System
export
	NewEnemy
define
fun{Change L Rec}
	{AdjoinList Rec L $}
end
fun{Abs X}
	if X>0 then X
	else ~X end
end

fun{EnemyBehaviour Msg S}
	if {Record.label Msg}==get andthen S==nil then Msg.1=nil nil
	elseif S==nil then nil
	else
		case Msg of move(X Y) then
			{Bender.drawMove S.handler X Y 10 _}
			{Change [x#X+S.x y#Y+S.y] S}
		[] get(X) then X=S S
		[] loseLife(X) then if S.life-X=<0 then {S.handler delete} nil
							else {Change [life#S.life-X] S} end
		[] nil then {S.handler delete} nil 
		end 
	end
end
proc{NewEnemy X#Y Handler Timer}
	Bender={PO.newPortObject EnemyBehaviour enemy(x:X y:Y life:10 handler:Handler)} in
	{Send Object.gameList add(Bender)}
	{SimpleIA X#Y Bender Timer}
end
proc {SimpleIA X#Y E Timer}
	fun {Repetition Hist Range Times}
		fun {Add L X}
			case L of nil then [X#1]
			[] (H#I)|T andthen H==X then (H#(I+1))|T
			[] H|T then H|{Add T X} 
			end
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
		L={Count Hist Range nil}
	in
		{Repet L Times}
	end
	fun{CheckHist Hist N L}
		case Hist of H|T then
			if N==0 then L
			else L2={Remove H L} in {CheckHist T N-1 L2} end
		[] nil then L 
		end
	end
	fun{Remove X L}
		case L of H|T andthen X==H then T
		[] H|T then H|{Remove X T}
		else nil end
	end

	fun {CheckObstacles P}
		fun{F I}
			if I<5 then {Check P.I I}
			else nil end
		end
		fun {Check X#Y I}
			O={Object.isFree X Y} in
			if O==true then (X#Y)|{F I+1}
			elseif O==false then {F I+1}
			elseif {Or {Record.label {Send O get($)}}==bender 
				{Record.label {Send O get($)}}==rock} then
				(X#Y)|{F I+1}
			else {F I+1} end
		end
	in
		{F 1}
	end
    fun{Priorities X#Y End}% for end
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
	fun{WhoIsCloser X#Y S1 S2}
		if S1==nil then S2
		elseif S2==nil then S1 
		else
			D1={Abs (S1.x-X)}+{Abs (S1.y-Y)}
			D2={Abs (S2.x-X)}+{Abs (S2.y-Y)}
		in
			if D1>D2 then S2 else S1 end
		end
	end
	proc{Loop Hist}
		%{System.show 'in Loop'}
		{Delay Timer}
		Rec={Send E get($)}
		S={WhoIsCloser X#Y {Send Bender.playerEarth get($)} {Send Bender.playerAir get($)}}
	in
		if {Or S==nil Rec==nil} then skip %{System.show S#Rec}
		else
			X=Rec.x
			Y=Rec.y
			P={Priorities X#Y S.x#S.y}
			L={CheckObstacles P}
			L2={CheckHist Hist 4 L}
			%{System.show 'Ls'#L#L2#Hist}
			Pos Move
		in
			
		 	if L2==nil andthen L==nil then
		   		Pos=X#Y
		 	elseif L2==nil then
		    	Pos=L.1
		    else 
		    	Pos=L2.1
		 	end	

		 	Move=(Pos.1-X)#(Pos.2-Y)
		 	{Bender.move E move(Move.1 Move.2)}
			{Loop Pos|Hist}
		end
	end
in
	thread {Loop nil} end
end
end