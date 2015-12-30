import socket
import select
from threading import Thread

from main.Game import Game
from network.NetworkUtils import PORT, tab_to_msg, network_format, msg_to_tab

__author__ = 'Baptiste'


class Server(Thread):
    def __init__(self):
        Thread.__init__(self)
        self.server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.server_socket.bind(('', PORT))
        self.server_socket.listen(5)
        print("Server listening on port "+str(PORT))

        self.connected_clients = []
        self.next_player_id = 0
        self.done_client = 0
        self.turn_msg = ""
        self.created_tab = []
        self.start()
        self.game = Game(self,None)
        self.players_start_gold = self.game.players[0].money
        self.game.run()

    def run(self):
        print("in run")
        self.running = True

        while self.running:
            self.get_new_connections()

            self.get_clients_messages()
        for c in self.connected_clients:
            c.send("close\n".encode())
            c.close()
        self.server_socket.close()
        print("clean close")

    def get_new_connections(self):
        connection_asked, wlist, xlist = select.select([self.server_socket], [], [], 0.05)
        for connection in connection_asked:
            client_socket, info = self.server_socket.accept()
            self.connected_clients.append(client_socket)

            self.send_initialisation(client_socket)


    def send_initialisation(self, client_socket):
        buf = network_format(["player_id", self.next_player_id,self.players_start_gold])
        self.next_player_id += 1
        buf += tab_to_msg(self.game.get_initial_state())

        client_socket.send(buf.encode())


    def get_clients_messages(self):
        try:
            to_read, wlist, xlist = select.select(self.connected_clients, [], [], 0)
        except select.error:
            pass
        else:
            for client in to_read:
                msg = client.recv(1024)
                #self.send_to_others(client,msg)
                self.update_game(msg)
                msg = msg.decode()
                print(msg)

    def update_game(self,msg):
        for line in msg_to_tab(msg):
            self.game.update_from_network(line)
            if len(line)>1 and line[0]=="creation":
                self.created_tab.append(line)
        self.done_client += 1
        if self.done_client == self.next_player_id:
            print(self.game.get_all_actions())
            self.game.check_error_move()
            all_actions = self.game.get_all_actions()
            self.send_messages(tab_to_msg(self.created_tab + all_actions))
            self.game.resolve_day()
            self.game.update()
            self.done_client = 0
            self.created_tab = []


    def send_to_others(self,client,msg):
        for c in self.connected_clients:
            if c != client:
                c.send(msg)

    def send_messages(self,msg):
        msg = msg.encode()
        for client in self.connected_clients:
            client.send(msg)

    def close(self):
        self.running=False


if __name__ == "__main__":
    s = Server()
