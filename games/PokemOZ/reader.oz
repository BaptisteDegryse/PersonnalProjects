functor
import
   Pickle
   Args at 'args.ozf'
export
   LoadMap
define
   fun {LoadMap}
      {Pickle.load Args.args.map}
   end

   Map = map(r(0 0 0 0 0 0 1 1 1 0 1 1 1 1 1 1 1 0 0 1 1 1 1 1) r(0 0 0 0 0 0 1 1 1 0 1 1 1 1 1 0 1 0 0 1 0 0 0 1) r(0 0 0 0 0 0 1 1 1 0 1 1 0 0 0 0 0 0 0 1 0 1 0 1) r(0 0 0 0 0 0 0 0 0 0 1 1 0 1 0 0 0 0 1 1 0 1 0 1) r(0 1 1 1 0 0 0 0 1 1 1 1 0 1 0 0 0 0 1 1 0 1 0 1) r(0 1 1 1 0 0 0 1 1 1 1 1 0 1 1 1 0 0 1 1 0 1 0 1) r(0 1 1 1 0 0 0 0 0 0 0 0 0 1 1 1 0 0 0 0 0 1 0 0))

   proc {CreateMap Map}
      {Pickle.save Map 'Map_test.txt'}

   end
in
   {CreateMap Map}
end