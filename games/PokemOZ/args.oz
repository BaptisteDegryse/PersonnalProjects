functor
import
   Application
export
   Args
   Manual
define
   Manual % true si jeu en manuel, false si jeu en automatique
   DefMap = 'Map_test.txt'
   DefProb = 3
   DefSpeed = 9
   DefAutoF = 2
   Args={Application.getArgs
	 record( map(single char:&m type:atom default:DefMap)
		 probability(single char:&p type:int default:DefProb)
		 speed(single char:&s type:int default:DefSpeed) 
		 autofight(single char:&a type:int default:DefAutoF)
		 help(single char:&h default:false))}

end
