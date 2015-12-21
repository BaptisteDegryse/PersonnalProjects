import random
import sys
from core.Block import Block
from core.Bonus import Bonus
from core.Constant import WIDTH, HEIGHT

__author__ = 'Baptiste'

import socket
import select

id = 0


def launch_server(players, objects, all_keys):
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.bind(('', 12800))
    server_socket.listen(5)
    print("Server listening on port 12800")

    connected_clients = []
    running = True
    rest = ""

    while running:
        connection_asked, wlist, xlist = select.select([server_socket], [], [], 0.05)
        for connection in connection_asked:
            client_socket, info = server_socket.accept()
            connected_clients.append(client_socket)

            msg = ""
            for o in objects:
                if isinstance(o, Block):
                    msg += "block" + "," + str(o.x) + "," + str(o.y) + "," + str(o.sx) + "," + str(o.sy) + "\n"
                elif isinstance(o, Bonus):
                    msg += "bonus" + "," + str(o.x) + "," + str(o.y) + ',' + str(o.kind) + "," + str(o.alive) + "\n"
                    o.sent = True
            global id
            msg += "id" + "," + str(id) + "\n"
            id += 1
            msg = msg.encode()
            client_socket.send(msg)

        to_read = []
        try:
            to_read, wlist, xlist = select.select(connected_clients, [], [], 0)
        except select.error:
            pass
        else:
            for client in to_read:
                msg = client.recv(1024)
                msg = msg.decode()
                print("<-" + msg)
                if rest != "":
                    msg = rest + msg
                keys = [line.split(',') for line in msg.split('\n')]
                for k in keys:
                    if len(k) < 2:
                        rest = k[0]
                    else:
                        all_keys[k[0]] = k[1] == "True"
                        rest = ""
                        print(str(all_keys) + " k[0]=" + str(k[0]) + " k[1]=" + str(k[1]))
                    print("rs " + rest)

        compute(players, objects, all_keys)

        for client in connected_clients:
            msg = ""
            for p in players:
                msg += "player," + str(p.id) + "," + str(p.x) + "," + str(p.y) + "," + str(p.dir) + ',' + str(
                    p.score) + "," + str(p.radius) + "\n"
            for o in objects:
                if isinstance(o, Bonus) and o.sent == False:
                    msg += "bonus" + "," + str(o.x) + "," + str(o.y) + ',' + str(o.kind) + "," + str(o.alive) + "\n"
                    o.sent = True

            msg = msg.encode()
            client.send(msg)

    print("disconnection")
    for client in connected_clients:
        client.close()
    server_socket.close()


def compute(players, objects, all_keys):
    for player in players:
        for i in range(len(player.keys)):
            (k, v) = player.keys[i]
            player.keys[i] = (k, all_keys[k])
        player.move(players, objects)

    rand = random.randint(0, 200)
    if rand == 0:
        x = random.randint(10, WIDTH - 10)
        y = random.randint(10, HEIGHT - 10)

        objects.append(Bonus(x, y, random.randint(0, 1)))
