from main.Constants import WIDTH, HEIGHT
from main.Constants import TILE_SIZE
from main.Player import Player

__author__ = 'Baptiste'


def load_map(n):
    players = []
    for i in range(0, 2):
        p = Player(i)
        players.append(p)
        max_x = (WIDTH-1) // TILE_SIZE
        max_y = (HEIGHT-1) // TILE_SIZE
        if n == 0:
            p.earn(500)
            p.create_free_soldier(3 + 5 * i, 4)
            p.create_free_archer(3 + 5 * i, 5)
            p.create_free_castle(3 + 5 * i, 6)
            p.create_free_gold_mine(3 + 5 * i, 8)
        if n == 1:
            p.earn(100)
            p.create_free_castle(max_x * i, max_y * i)
            p.create_free_gold_mine(max_x//2,max_y * i)

    return players
