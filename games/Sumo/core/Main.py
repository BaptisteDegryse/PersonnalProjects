from core.Bonus import Bonus
from core.Block import Block
from core.Player import Player
from graphics.Game import Game
from network.Client import Client
from network.Server import launch_server

__author__ = 'Baptiste'

players = []
objects = []
game = None
all_keys = {}
client = None

def init_server():

    players.append(Player(300, 300))
    players.append(Player(200, 200))
    objects.append(Block(30,30,100,100))
    objects.append(Block(500,30,100,100))
    #objects.append(Bonus(250,250))

    for p in players:
        for k,v in p.keys:
            all_keys[k]=False

    launch_server(players,objects,all_keys)

def init_client(port):
    players.append(Player(300, 300))
    players.append(Player(200, 200))
    for p in players:
        for k,v in p.keys:
            all_keys[k]=False

    global client
    client=Client('localhost',port,objects)
    global game
    game = Game()
    game.bind("<Key>", client.key_pressed)
    game.bind("<KeyRelease>", client.key_released)
    for o in objects:
        if isinstance(o,Block):
            game.draw_block(o)
        elif isinstance(o,Bonus):
            game.draw_bonus(o)

    game.after(50,move)
    game.mainloop()


def move():
    client.update_game(players,objects)
    for o in objects:
        if isinstance(o,Block):
            pass
        elif isinstance(o,Bonus):
            if not o.alive:
                print("delete bonus")
                game.delete_bonus(o)
                objects.remove(o)
            elif o.img is None:
                game.draw_bonus(o)


    for player in players:
        game.draw_player(player)

    game.after(50,move)

def update_key(name,boo):
    all_keys[name]=boo

def key_pressed(event):
    key = event.keysym
    update_key(key,True)

def key_released(event):
    key = event.keysym
    update_key(key,False)
