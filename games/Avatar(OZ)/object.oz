functor
import
   PO at 'portObject.ozf'
   Map at 'map.ozf'
   System
export
   GameList
   NewPortList
   IsFree
   GiveLineObject
define
   fun {IsInTheMap X Y}
      X=<{Record.width Map.map.1} andthen Y=<{Record.width Map.map} andthen X>0 andthen Y>0
   end
   fun {Remove X L}
      case L of H|T andthen H==X then T
      [] H|T then H|{Remove X T}
      else nil end
   end
   fun {Or X Y}
      if X then true
      else Y end
   end  
   fun {ListBehaviour Msg State}
      case Msg of get(X) then X=State State
      [] add(X) then X|State
      [] remove(X) then {Remove X State}
      end
   end
   fun{NewPortList L}
      {PO.newPortObject ListBehaviour L}
   end
   fun {IsFree X Y}
      fun {F X Y L}
	 case L of H|T then
	    O={Send H get($)} in
	    if {Not O==nil} andthen O.x==X andthen O.y==Y then H
	    else {F X Y T} end
	 else true end
      end
      O2
   in
      O2={F X Y {Send GameList get($)}}
      if O2==true then
	     {IsInTheMap X Y} andthen Map.map.Y.X==1
      else
	 O2
      end
   end
   fun {GiveLineObject X Y DirX DirY}
      L={Send GameList get($)}
      fun {F L}
         case L of H|T then
            S={Send H get($)} in
            {Wait S}
            if S==nil then {F T}
            elseif {Or (DirX==0 andthen S.x==X andthen {Or (DirY>0 andthen S.y >= Y) (DirY<0 andthen S.y=<Y)})
                  (DirY==0 andthen S.y==Y andthen {Or (DirX>0 andthen S.x >= X) (DirX<0 andthen S.x=<X)})} then
                  H|{F T}
            else {F T} end
         else nil end
      end
   in
      {F L}
   end   
   GameList={NewPortList nil}
end