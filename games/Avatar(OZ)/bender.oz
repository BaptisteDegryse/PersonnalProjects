functor
import
	PO at 'portObject.ozf'
	Map at 'map.ozf'
	Object at 'object.ozf'
	System
export
	NewBender
	Move
	Attack
	PlayerEarth
	PlayerAir
	DrawMove
define
Speed=1
PlayerEarth
PlayerAir
	% State= bender(x:X y:Y energy:E
	% keys:key(j k l m up:"<Up>" left:"<Left>" down:"<Down>" right:"<Right>")
	% handler:H)
fun {Or X Y}
	if X then true
	else Y end
end
fun {Abs X}
	if X>0 then X
	else ~X end
end	
proc {DrawMove H X Y N F}
	proc{P I}
		{Delay 5}
		if I==0 then F=unit
		else
			{H move(X*Map.tileSize div N Y*Map.tileSize div N)}
			{P I-1}
		end
	end
in
	thread {P N} end
end	
fun{BenderBehaviour Msg S}
	if {Record.label Msg}==get andthen S==nil then Msg.1=nil nil
	elseif S==nil then nil
	else
		case Msg of move(X Y) then
			case S.attack of 0 then
				{DrawMove S.handler X Y 10 _}
				{Change [x#X+S.x y#Y+S.y attack#0] S}
			else 
				thread {Attack S X Y} end
				{Change [energy#S.energy-S.attackCost.(S.attack) attack#0] S}
			end
		[] get(X) then X=S S
		[] attack(X) then {Change [attack#X] S}
		[] nil then  {S.handler delete} nil 
		end
	end
end
proc{Move Bender move(X Y)}
	%{System.show 'move'#X#Y}
	B={Send Bender get($)} in
	case {Record.label B} of
		bender then if {Or {Object.isFree B.x+X B.y+Y}==true B.attack>0} then
							{Send Bender move(X Y)}
						end
	[] enemy then O={Object.isFree B.x+X B.y+Y} in
						if O==true then
							{Send Bender move(X Y)}
						elseif O==false then skip
						else {Delay 500} 
							O2={Object.isFree B.x+X B.y+Y} in
							if O2==true then {Send Bender move(X Y)}
							elseif {Record.label {Send O2 get($)}}==enemy then skip
							else {Died O2} {Send Bender move(X Y)} end
						end
	[] rock then O={Object.isFree B.x+X B.y+Y} in
						if O==true then
							{Send Bender move(X Y)}
						elseif O==false then skip
						elseif {Or {Record.label {Send O get($)}}==rock 
										{Record.label {Send O get($)}}==enemy} then
							thread {Died O} {Died Bender} end
						end	
	else skip end
end

proc{Attack S X Y}
	case S.type of earth then {EarthAttack S X Y}
	[] air then {AirAttack S X Y}
	else skip end
end


fun{NewBender X#Y Type Handler}
	H Bender in
	case Type of earth then
		Bender={PO.newPortObject BenderBehaviour 
		bender(x:X y:Y energy:100 
			keys:key(j k l m up:"<Up>" left:"<Left>" down:"<Down>" right:"<Right>") 
			handler:Handler type:Type attack:0 
			attackCost:cost(0 10 10 30))}
	[] air then 
		Bender={PO.newPortObject BenderBehaviour 
		bender(x:X y:Y energy:100 
			keys:key(a z e r up:g left:c down:v right:b) 
			handler:Handler type:Type attack:0 
			attackCost:cost(0 10 10 30))}
	else Bender=nil end
	{Send Object.gameList add(Bender)}
	Bender
end

fun{Change L Rec}
	{AdjoinList Rec L $}
end

%%%%% EARTH %%%%%

proc{EarthAttack S X Y}
	case S.attack of 1 then {NewRock S.x+X S.y+Y}
	[] 2 then {MoveRocks S.x S.y X Y}
	[] 3 then {MakeRockLine S.x S.y X Y}
	[] 4 then {BigHouse S.x S.y X Y} end
end
	fun {BasicBehaviour Msg State}
		if {Record.label Msg}==get andthen State==nil then Msg.1=nil nil
		elseif State==nil then nil
		else
			case Msg of move(X Y) then
				{DrawMove State.handler X Y 5 _}
				{Change [x#X+State.x y#Y+State.y] State}
			[] get(X) then X=State State
			[] nil then  {State.handler delete} nil
			end
		end
	end
	proc {NewRock X Y}
		if {Object.isFree X Y}==true then 
			H={Map.draw Map.rockImg X Y}
			O={PO.newPortObject BasicBehaviour rock(x:X y:Y handler:H)}
		in
			{Send Object.gameList add(O)}
		end
	end
	proc{MoveRocks X Y DirX DirY}
		H={Map.draw Map.earthBall X+DirX Y+DirY}
		proc{F I}
			A={Object.isFree X+I*DirX Y+I*DirY} in
			if A==true then Fin in {DrawMove H DirX DirY 10 Fin}
				{Wait Fin} {F I+1}
			elseif A==false then skip
			elseif {Record.label {Send A get($)}}==enemy then
				{Send A loseLife(4)}
			elseif {Record.label {Send A get($)}}==rock then 
			 	for I in 1..5 do {Move A move(DirX DirY)} end 
			end
			{H delete}
		end
	in
		thread {F 1} end
	end
	proc {MakeRockLine X Y DirX DirY}
		proc{F I XY}
			if I==0 then {NewRock X+DirX Y+DirY}
			elseif XY==x then {NewRock X+DirX+I Y+DirY} {NewRock X+DirX-I Y+DirY} {F I-1 XY}
			else {NewRock X+DirX Y+DirY+I} {NewRock X+DirX Y+DirY-I} {F I-1 XY} end
		end
	in
		if DirX==0 then {F 2 x}
		else {F 2 y} end
	end
	proc{BigHouse X Y DirX DirY}
		for I in 1..2 do
			{NewRock X-DirX*I Y-DirY*I} 
			{NewRock X+DirY*I Y+DirX*I}
			{NewRock X-DirY*I Y-DirX*I}
			for J in 1..2 do
				{NewRock X+I Y+J} {NewRock X-I Y-J} {NewRock X-I Y+J} {NewRock X+I Y-J}
			end
		end

	end
	proc{Died Bender}
		thread {Delay 100} {Send Object.gameList remove(Bender)} {Send Bender nil} end
	end

%%%%% AIR %%%%
	proc{AirAttack S X Y}
		case S.attack of 1 then {Move PlayerAir move(3*X 3*Y)}
		[] 2 then {WindAttack S.x S.y X Y 3}
		[] 3 then {TripleWindAttack S.x S.y X Y 2}
		[] 4 then {Tornado S.x S.y 3} end
	end
	proc{WindAttack X Y DirX DirY Power}
		H={Map.draw Map.airBall X+DirX Y+DirY}
		proc{F I}
			A={Object.isFree X+I*DirX Y+I*DirY} in
			if A==true then Fin in {DrawMove H DirX DirY 5 Fin}
				{Wait Fin} {F I+1}
			elseif A==false then skip
			elseif {Record.label {Send A get($)}}==enemy then
				{Send A loseLife(4)}
				for I in 1..Power do {Move A move(DirX DirY)} end
			else
			 	for I in 1..Power do {Move A move(DirX DirY)} end 
			end
			{H delete}
		end
	in
		thread {F 1} end
	end
	proc {TripleWindAttack X Y DirX DirY Power}
		{WindAttack X Y DirX DirY Power}
		if DirX==0 then
		{WindAttack X+1 Y+DirY DirX DirY Power}
		{WindAttack X-1 Y+DirY DirX DirY Power}
		else
		{WindAttack X+DirX Y+1 DirX DirY Power}
		{WindAttack X+DirX Y-1 DirX DirY Power}
		end
	end
	proc {Tornado X Y Power}
		{TripleWindAttack X Y 0 1 Power}
		{TripleWindAttack X Y 1 0 Power}
		{TripleWindAttack X Y 0 ~1 Power}
		{TripleWindAttack X Y ~1 0 Power}
	end
end









