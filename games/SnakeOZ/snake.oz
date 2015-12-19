% Author : Baptiste Degryse
functor
import
   QTk at 'x-oz://system/wp/QTk.ozf'
   OS
   System
   PO at 'portObject.ozf'
   Map at 'map.ozf'
export
   Head
   Move
   Init
define
   Head = _
   LastDir = {PO.newPortCell move(0 1)}
   Meat = {PO.newPortCell pos(x:5 y:5 h:{Map.draw Map.meatImg 5 5})}
   fun{Change L Rec}
      {AdjoinList Rec L $}
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
   proc {Move Port X Y}
      {PO.assign LastDir move(X Y)}
   end
   fun{IsDead}
      S = {Send Head get($)} in
      if  S.x < 1 orelse S.x > {Record.width Map.map.1} orelse
         S.y < 1 orelse S.y > {Record.width Map.map} then
         true
      elseif Map.map.(S.y).(S.x)==2 then
         true
      elseif {TouchYourself S.x S.y S.next} then true
      else false end
   end
   fun{TouchYourself X Y Next}
      if Next==nil then false
      else S={Send Next get($)} in 
         if S.x==X andthen S.y==Y then true
         else {TouchYourself X Y S.next} end
      end
   end
   proc {MoveLoop}
      %{Delay Timer}
      if {IsDead} then _={Map.draw Map.lostImg 10 10 $}         
      else
         T = {PO.access LastDir $} F in
         {Send Head m(T F)}
         {Wait F}
         {MoveLoop}
      end
   end
   proc {MeatLoop}
      {Delay 50}
      S={Send Head get($)} 
      M={PO.access Meat $} in
      if S.x==M.x andthen S.y==M.y then
         {Send Head eat()}
         FX = ({OS.rand} mod {Record.width Map.map.1})+1
         Y = ({OS.rand} mod {Record.width Map.map})+1
         X = {FindXY FX Y} in
         {DrawMove M.h X-S.x Y-S.y 1 _}
         {PO.assign Meat pos(x:X y:Y h:M.h)}
      end
      {MeatLoop}
   end
   fun{FindXY X Y}
      if Map.map.Y.X == 2 then
         {FindXY X+1 Y}
      else
         X
      end
   end

   fun{SnakeBehaviour Msg S}
      case Msg of m(move(X Y) F) then
         {DrawMove S.h X Y 20 F}
         if S.next \= nil then
            {Send S.next m(S.lastMove _)}
         end
         {Change [x#X+S.x y#Y+S.y lastMove#move(X Y)] S}
      [] eat() andthen S.next==nil then {Change [next#{NewSnake S.x-S.lastMove.1 S.y-S.lastMove.2}] S}
      [] eat() then {Send S.next eat()} S
      [] get(R) then R=S S
      end
   end

   fun{NewSnake X Y}
      H = {Map.draw Map.snakeImg X Y} in
      {PO.newPortObject SnakeBehaviour snake(x:X y:Y h:H next:nil)}
   end
   proc {Init}
      Head = {NewSnake 1 1}   
      thread {MoveLoop} end
      thread {MeatLoop} end
   end

end