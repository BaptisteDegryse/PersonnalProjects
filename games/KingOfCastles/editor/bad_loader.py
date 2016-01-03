from main.Constants import WIDTH, HEIGHT
from main.Constants import TILE_SIZE
from main.Player import Player
from main.Unit import Soldier, Archer, Castle, GoldMine

__author__ = 'Baptiste'


def load_map(n):
    players = []
    max_x = (WIDTH - 1) // TILE_SIZE
    max_y = (HEIGHT - 1) // TILE_SIZE
    for i in range(0, 2):
        p = Player(i)
        players.append(p)
        if n == 0:
            p.earn(500)
            p.create_free_type(Soldier, 3 + 5 * i, 4)
            p.create_free_type(Archer, 3 + 5 * i, 5)
            p.create_free_type(Castle,3 + 5 * i, 6)
            p.create_free_type(GoldMine,3 + 5 * i, 8)
        if n == 1:
            p.earn(1000)
            j = -2 * i + 1
            p.create_free_type(Castle,max_x * i + j, max_y * i+j)
            p.create_free_type(GoldMine,i*max_x + max_x // 3 * j, max_y * i)

    if n == 1:
        players[0].create_free_type(GoldMine,max_x // 2, max_y // 2)

    return players
