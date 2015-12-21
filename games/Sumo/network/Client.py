from core.Block import Block
from core.Bonus import Bonus
from core.Player import Player

__author__ = 'Baptiste'

import socket
import select

class Client:
    def __init__(self,host,port,objects):
        self.client_socket = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
        self.client_socket.connect((host,port))
        try:
            to_read, wlist, xlist = select.select([self.client_socket],[],[],0.05)
        except select.error:
            pass
        else:
            for client in to_read:
                msg = client.recv(1024)
                msg = msg.decode()
                #print("<-"+msg)
                infos = [ line.split(',') for line in msg.split('\n')]
                for i in range(len(infos)):
                    if infos[i][0] == "block":
                        objects.append(Block(int(infos[i][1]),int(infos[i][2]),int(infos[i][3]),int(infos[i][4])))
                    elif infos[i][0] == 'id':
                        self.id= int(infos[i][1])
                    elif infos[i][0] == "bonus":
                        if infos[i][4] == "True":
                            objects.append(Bonus(int(infos[i][1]),int(infos[i][2]),int(infos[i][3])))
                    else: break


    def update_game(self, players,objects):
        try:
            to_read, wlist, xlist = select.select([self.client_socket],[],[],0.05)
        except select.error:
            pass
        else:
            for client in to_read:
                msg = client.recv(1024)
                msg = msg.decode()
                #print("<-"+msg)
                infos = [ line.split(',') for line in msg.split('\n')]
                for i in range(len(infos)):
                    if infos[i][0] == "":
                        break
                    if infos[i][0] == "player":
                        j = int(infos[i][1])
                        if len(players)<j+1:
                            players.append(Player(0,0))
                        players[j].x = float(infos[i][2])
                        players[j].y = float(infos[i][3])
                        players[j].dir = float(infos[i][4])
                        players[j].score = int(infos[i][5])
                        players[j].radius = int(infos[i][6])
                    if infos[i][0] == "bonus" and infos[i][4]=='True':
                        objects.append(Bonus(int(infos[i][1]),int(infos[i][2]),int(infos[i][3])))
                    elif infos[i][0] == "bonus" and infos[i][4]=='False':
                        o=find_bonus(int(infos[i][1]),int(infos[i][2]),objects)
                        o.alive=False



    def update_key(self,name,boo):
        msg =str(self.id)+str(name)+","+str(boo)+"\n"
        msg=msg.encode()
        self.client_socket.send(msg)
        #all_keys[name]=boo

    def key_pressed(self,event):
        key = event.keysym
        self.update_key(key,True)

    def key_released(self,event):
        key = event.keysym
        self.update_key(key,False)


    def close(self):
        self.client_socket.close()

def find_bonus(x,y,objs):
    for o in objs:
        if o.x==x and o.y==y:
            return o