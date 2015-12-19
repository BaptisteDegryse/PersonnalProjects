functor
export
   NewPortObject
   NewPortCell
   Access
   Assign
define
   fun {NewPortObject Fun Init}
      S
      P = {NewPort S}
      proc {MsgLoop Str State}
	 case Str of Msg|S2 then {MsgLoop S2 {Fun Msg State}}
	 [] nil then skip
	 end
      end
   in
      thread {MsgLoop S Init} end
      P
   end


   fun {NewPortCell C} %C est le state
      fun {Fun S State}
	 case S of access(R) then R=State
	 [] assign(E) then E
	 end
      end
   in
      {NewPortObject Fun C}
   end

   proc {Access C R}
      {Send C access(R)}  
   end

   proc {Assign C E}
      {Send C assign(E)}
   end

   
end
