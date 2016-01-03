from graphics import ImageManager
import math

__author__ = 'Baptiste'


class Unit:
    creator = None
    name = ''
    short_cut = ""
    max_lvl = 1

    def __init__(self, x, y):
        self.pos = (x, y)

        self.canvas_img = None
        self.selected_canvas_img = None
        self.health_bar_green = None
        self.health_bar_red = None

        self.max_move = 2
        self.attack_dmg = 2
        self.attack_range = 1
        self.attack_range_min = 1
        self.attack_in_line = False
        self.hp = 5
        self.max_hp = 5
        self.kill_reward = 20
        self.lvl=1

        self.selected = False
        self.alive = True
        self.img_deleted = False

        self.plan = ('stay', None)

    def plan_move(self, next_pos):
        if next_pos == self.pos:
            self.plan_stay()
        elif self.can_move(next_pos):
            self.plan = ('move', next_pos)

    def plan_attack(self, enemy):
        if not self.can_attack(enemy):
            self.plan_stay()
        else:
            self.plan = ('attack', enemy)

    def plan_stay(self):
        self.plan = ('stay', None)

    def move(self, next_pos):
        if self.can_move(next_pos):
            self.pos = next_pos

    def can_move(self, next_pos):
        return self.distance_to(next_pos) <= self.max_move

    def distance_to(self, pos):
        (x, y) = self.pos
        (x2, y2) = pos
        return math.fabs(x - x2) + math.fabs(y - y2)

    def get_pos(self):
        return self.pos

    def select(self):
        self.selected = True

    def unselect(self):
        self.selected = False

    def is_selected(self):
        return self.selected

    def attack(self, enemy):
        if self.can_attack(enemy):
            enemy.is_attacked(self)

    def can_attack(self, enemy):
        if enemy is None or enemy == self:
            return False
        return self.can_attack_pos(enemy.pos)

    def can_attack_pos(self, pos):
        (x, y) = pos
        (x2, y2) = self.get_pos()
        in_line = x == x2 or y == y2 or not self.attack_in_line
        return self.attack_range_min <= self.distance_to(pos) <= self.attack_range and in_line

    def is_attacked(self, enemy):
        self.hp -= enemy.attack_dmg
        self.alive = self.hp > 0
        if not self.alive:
            enemy.plan_stay()

    def get_attack_tile_list(self):
        (x, y) = self.get_pos()
        n = self.attack_range
        tile_list = []
        for i in range(-n, n + 1):
            for j in range(-n, n + 1):
                if self.can_attack_pos((x + i, y + j)):
                    tile_list.append((x + i, y + j))
        return tile_list

    def is_building(self):
        return self.max_move == 0

    def resolve_day(self):
        (action, arg) = self.plan
        if action == 'stay':
            pass
        elif action == 'move':
            self.move(arg)
        elif action == 'attack':
            if isinstance(self, Miner) and isinstance(arg, GoldMine) and arg.alive:
                return self.mine(arg)
            if isinstance(arg, Unit) and arg.alive:
                self.attack(arg)
                if not arg.alive:
                    return arg.kill_reward
        return 0


class Castle(Unit):
    price = 150
    creator = None
    name = "castle"
    short_cut = "c"
    max_lvl = 3

    def __init__(self, x, y):
        Unit.__init__(self, x, y)
        self.img = ImageManager.get_image("castle")
        self.lvl = 1


        self.max_move = 0
        self.attack_dmg = 0
        self.attack_range = 0
        self.hp = 15
        self.max_hp = 15
        self.unit_creation_tile = (x + 1, y)
        self.unit_creation_tile_canvas_img = None

    def set_unit_creation_tile(self, new_pos):
        if self.distance_to(new_pos) == 1:
            self.unit_creation_tile = new_pos

    def get_unit_creation_tile(self):
        return self.unit_creation_tile

    def upgrade(self):
        if self.lvl < self.max_lvl:
            self.hp += self.max_hp
            self.max_hp += self.max_hp
            self.attack_dmg += 2
            self.attack_range += 1
            self.lvl += 1
            self.img = ImageManager.get_image("castle"+str(self.lvl))


class Soldier(Unit):
    creator = Castle
    price = 20
    name = "soldier"
    short_cut = "q"

    def __init__(self, x, y):
        Unit.__init__(self, x, y)
        self.img = ImageManager.get_image("soldier")


class Archer(Unit):
    creator = Castle
    price = 30
    name = "archer"
    short_cut = "s"

    def __init__(self, x, y):
        Unit.__init__(self, x, y)
        self.img = ImageManager.get_image("archer")

        self.max_move = 1
        self.attack_dmg = 1
        self.attack_range = 3
        self.attack_range_min = 2
        self.hp = 2
        self.max_hp = 2


class Miner(Unit):
    creator = Castle
    price = 30
    name = "miner"
    short_cut = "d"

    def __init__(self, x, y):
        Unit.__init__(self, x, y)
        self.img = ImageManager.get_image("miner")

        self.max_move = 1
        self.attack_dmg = 1
        self.attack_range = 1
        self.hp = 2
        self.max_hp = 2

        self.gold_amount = 5

    def mine(self, gold_mine):
        gold = gold_mine.get_gold(self.gold_amount)
        if not gold_mine.alive:
            self.plan_stay()
        return gold


class Canon(Unit):
    creator = Castle
    price = 50
    name = "canon"
    short_cut = "f"

    def __init__(self, x, y):
        Unit.__init__(self, x, y)
        self.img = ImageManager.get_image(self.name)

        self.max_move = 1
        self.attack_dmg = 5
        self.attack_range = 5
        self.attack_range_min = 3
        self.attack_in_line = True
        self.hp = 2
        self.max_hp = 2


class Builder(Castle):
    creator = Castle
    price = 20
    name = "builder"
    short_cut = "g"
    max_lvl = 1

    def __init__(self, x, y):
        Castle.__init__(self, x, y)
        self.img = ImageManager.get_image(self.name)

        self.max_move = 1
        self.attack_dmg = 1
        self.attack_range = 1
        self.hp = 2
        self.max_hp = 2


class Turret(Unit):
    creator = Builder
    price = 20
    name = "turret"
    short_cut = "x"

    def __init__(self, x, y):
        Unit.__init__(self, x, y)
        self.img = ImageManager.get_image(self.name)

        self.max_move = 0
        self.attack_dmg = 3
        self.attack_range = 3
        self.hp = 10
        self.max_hp = 10


class Wall(Unit):
    creator = Builder
    price = 5
    name = "wall"
    short_cut = "w"

    def __init__(self, x, y):
        Unit.__init__(self, x, y)
        self.img = ImageManager.get_image(self.name)

        self.max_move = 0
        self.attack_dmg = 0
        self.attack_range = 0
        self.hp = 15
        self.max_hp = 15

    def select(self):
        pass


class GoldMine(Unit):
    creator = None
    name = "gold_mine"

    def __init__(self, x, y):
        Unit.__init__(self, x, y)
        self.img = ImageManager.get_image("gold_mine")

        self.max_move = 0
        self.attack_dmg = 0
        self.attack_range = 0
        self.hp = 300
        self.max_hp = 300

    def get_gold(self, amount):
        if amount <= self.hp:
            self.hp -= amount
            return amount
        else:
            a = self.hp
            self.hp = 0
            self.alive = False
            return a

    def select(self):
        pass
