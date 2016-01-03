import socket
import select
from main.Game import Game
from network.NetworkUtils import PORT, tab_to_msg

__author__ = 'Baptiste'

class Client:
    def __init__(self, host):
        self.client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.client_socket.connect((host, PORT))
        self.game = Game(None,self)

        try:
            to_read, wlist, xlist = select.select([self.client_socket], [], [], 0.05)
        except select.error:
            pass
        else:
            for client in to_read:
                msg = client.recv(1024)
                msg = msg.decode()
                print("<- "+msg)
                initialisation = [line.split(',') for line in msg.split('\n')]
                for line in initialisation:
                    self.create_world(line)

        self.game.run()
        self.close()

    def create_world(self,line):
        self.game.update_from_network(line)

    def get_others_actions(self):
        try:
            to_read, wlist, xlist = select.select([self.client_socket], [], [], 0.5)
        except select.error:
            pass
        else:
            for client in to_read:
                msg = client.recv(1024)
                msg = msg.decode()
                print("<-"+msg)
                actions = [line.split(',') for line in msg.split('\n')]
                for line in actions:
                    if not (len(line)>4 and line[0]=='creation' and int(line[4])==self.game.player_id)\
                            and not (len(line)>3 and line[0]=='upgrade' and int(line[3])==self.game.player_id):
                        self.update_game(line)
                return True

        return False

    def update_game(self,line):
        self.game.update_from_network(line)

    def send(self,tab_msg):
        msg = tab_to_msg(tab_msg)
        print(msg)
        msg = msg.encode()
        self.client_socket.send(msg)
        # all_keys[name]=boo

    def close(self):
        self.client_socket.close()

if __name__=="__main__":
    c = Client('localhost')