functor
import
   QTk at 'x-oz://system/wp/QTk.ozf'
   System
   Application
   OS
   Property
   Args at 'args.ozf'
   Trainer at 'trainer.ozf'
define
   CD = {OS.getCWD} %get current working directory
   Accueil = {QTk.newImage photo(file:CD#'/images/pokemoz.gif')}
   Window
   Launcher
   GoMan
   GoAuto
   Quit
   FontA={QTk.newFont font(family:'Courier' size:30)}
   FontB={QTk.newFont font(family:'Courier' size:20)}
   FontC={QTk.newFont font(family:'Courier' size:15)}

   Desc=td(canvas(handle:Launcher width:600 height:600))
   Window={QTk.build Desc}
   {Launcher create(image 300 350 image:Accueil)}
   {Launcher create(text 300 100 text:'Welcome to PokemOZ !\n    Are you ready ?' font:FontA)}
   {Launcher create(rectangle 70 165 215 195 fill:white)}
   {Launcher create(rectangle 245 165 425 195 fill:white)}
   {Launcher create(rectangle 455 165 530 195 fill:white)}
   {Launcher create(text 142 180 text:'Manual mode' font:FontB handle:GoMan)}
   {Launcher create(text 335 180 text:'Automatic mode' font:FontB handle:GoAuto)}
   {Launcher create(text 492 180 text:'Quit' font:FontB handle:Quit)}

   {GoMan bind(event:'<Button-1>' action:proc {$} Args.manual=true {ChoosePoke} end)}
   {GoAuto bind(event:'<Button-1>' action:proc {$} Args.manual=false {ChoosePoke} end)}
   {Quit bind(event:'<Button-1>' action:proc {$} {Window close} {Application.exit 0} end)}

   proc{ChoosePoke}
      {Launcher create(rectangle 0 0 600 600 fill:grey)} 
      {Launcher create(text 300 20 text:'\t\tWelcome !\n First of all, you have to choose your PokemOZ' font:FontB)}
      {Launcher create(line 25 50 575 50)}
      %load images
      Bulbasoz = {QTk.newImage photo(file:CD#'/images/bulbasoz.gif')}
      Charmandoz = {QTk.newImage photo(file:CD#'/images/charmandoz.gif')}
      Chikozrita = {QTk.newImage photo(file:CD#'/images/chikozrita.gif')}
      Oztirtle = {QTk.newImage photo(file:CD#'/images/oztirtle.gif')}
      Pozliwhirl = {QTk.newImage photo(file:CD#'/images/pozliwhirl.gif')}
      Rapidoz = {QTk.newImage photo(file:CD#'/images/rapidoz.gif')}
      Bul Cha Chi Ozt Poz Rap
   in
      %cases pokemoz
      {Launcher create(image image:Bulbasoz 100 150 handle:Bul)}
      {Launcher create(text text:'Bulbasoz' 100 260 font:FontB)}
      {Launcher create(text text:'Grass' 100 280 font:FontC)}
      {Bul bind(event:'<Button-1>' action:proc {$} {Send StartP bulbasoz} end)}

      {Launcher create(image image:Charmandoz 300 152 handle:Cha)}
      {Launcher create(text text:'Charmandoz' 300 260 font:FontB)}
      {Launcher create(text text:'Fire' 300 280 font:FontC)}
      {Cha bind(event:'<Button-1>' action:proc {$} {Send StartP charmandoz}end)}

      {Launcher create(image image:Chikozrita 500 130 handle:Chi)}
      {Launcher create(text text:'Chikozrita' 500 260 font:FontB)}
      {Launcher create(text text:'Grass' 500 280 font:FontC)}
      {Chi bind(event:'<Button-1>' action:proc {$} {Send StartP chikozrita} end)}

      {Launcher create(image image:Oztirtle 100 410 handle:Ozt)}
      {Launcher create(text text:'Oztirtle' 100 540 font:FontB)}
      {Launcher create(text text:'Water' 100 560 font:FontC)}
      {Ozt bind(event:'<Button-1>' action:proc {$} {Send StartP oztirtle} end)}

      {Launcher create(image image:Pozliwhirl 290 400 handle:Poz)}
      {Launcher create(text text:'Pozliwhirl' 300 540 font:FontB)}
      {Launcher create(text text:'Water' 300 560 font:FontC)}
      {Poz bind(event:'<Button-1>' action:proc {$} {Send StartP pozliwhirl} end)}

      {Launcher create(image image:Rapidoz 500 410 handle:Rap)}
      {Launcher create(text text:'Rapidoz' 500 540 font:FontB)}
      {Launcher create(text text:'Fire' 500 560 font:FontC)}
      {Rap bind(event:'<Button-1>' action:proc {$} {Send StartP rapidoz} end)}

   end
   
   StartS
   StartP={NewPort StartS}
   
in
   %% Help message
   if Args.args.help then
      {System.showInfo "Usage: "#{Property.get 'application.url'}#" [option]"}
      {System.showInfo "Options:"}
      {System.showInfo "  --m, --map FILE\t\tPickle containing the map in .txt"}
      {System.showInfo "  --p, --probability INT\tProbability to find a wild pokemon in the grass"}
      {System.showInfo "  --s, --speed INT\t\tSpeed of your trainer"}
      {System.showInfo "  --a, --autofight INT\t\tWhen our trainer meet a wild pokemon : 0 if you always run away, 1 if you always fight, 2 if you want to choose"}
      {System.showInfo "  --h, --?, --help\t\tThis help"}
      {Application.exit 0}
   end
   {System.showInfo "Launching PokemOZ"}
   {Window set(title:'PokemOZ Launcher')}
   {Window show}
   thread case StartS of H|_ then
	     {Window close}
	     {Trainer.init H}
	  end
   end
   
   
end