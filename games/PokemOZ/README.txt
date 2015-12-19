Welcome to the PokemOZ world !


This game was coded by Baptiste Degryse and Anne-Sophie Branders.
We gave all our imagination and time to create this universe, so we hope you will enjoy it !!

The written report of the project can be found in « report.pdf »

To compile the game, just open you terminal and enter « make »

To launch the game, you have to call « ozengine launcher.ozf » in your terminal. But there are some additional parameters you can choose :

Usage: launcher.ozf [option]
Options:
  --m, --map FILE		Pickle containing the map in .txt
  --p, --probability INT	Probability to find a wild pokemoz in the grass
  --s, --speed INT		Speed of your trainer
  --a, --autofight INT		When your trainer meets a wild pokemoz : 0 if you always run away, 1 if you always fight, 2 if you want to choose
  --h, --?, --help		This help

When you launch the game, you can choose to play manually or to start the automatic game with the IA playing for you. There is something to know if you want to launch the automatic mode. If you don’t have a lot of time, you can increase the parameter speed. It will increase the speed of your trainer but ALSO the speed of the battles. If you want to take the time to see all the game and the battles in details, you have to choose a small speed parameter.

NB: Small tip for you, it’s not a good idea to play in automatic mode and with the parameter autofight set to 0… Indeed, the artificial intelligence first want to increase the level of its trainer by fighting wild pokemOZ before trying to fight other trainers (you see, it’s a very intelligent IA). In the same way, always fighting a wild PokemOZ means more risks to die (autofight set to 1). So, always running away from a wild PokemOZ or always fight a wild PokemOZ won’t let you go to the end of the game —> fix the parameter autofight to 2 when you play in automatic mode ;-)
