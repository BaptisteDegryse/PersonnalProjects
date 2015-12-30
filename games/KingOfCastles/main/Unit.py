from graphics import ImageManager
import math

__author__ = 'Baptiste'


class Unit:
    def __init__(self, x, y):
        self.pos = (x, y)

        # self.img = ImageManager.getImage("selection")
        self.canvas_img = None
        self.selected_canvas_img = None
        self.health_bar_green = None
        self.health_bar_red = None

        self.max_move = 2
        self.attack_dmg = 2
        self.attack_range = 1
        self.hp = 5
        self.max_hp = 5
        self.kill_reward = 20

        self.selected = False
        self.alive = True
        self.img_deleted = False

        self.plan = ('stay', None)

    def plan_move(self,next_pos):
        if next_pos == self.pos:
            self.plan_stay()
        elif self.can_move(next_pos):
            self.plan = ('move',next_pos)

    def plan_attack(self,enemy):
        if enemy is None or enemy.pos == self.pos:
            self.plan_stay()
        elif self.distance_to(enemy.pos) <= self.attack_range:
            self.plan = ('attack',enemy)

    def plan_stay(self):
        self.plan=('stay',None)

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
        if enemy is None:
            return
        if self.distance_to(enemy.pos) <= self.attack_range:
            enemy.is_attacked(self)

    def is_attacked(self, enemy):
        self.hp -= enemy.attack_dmg
        self.alive = self.hp > 0
        if not self.alive:
            enemy.plan_stay()

    def is_building(self):
        return self.max_move == 0

    def resolve_day(self):
        (action, arg) = self.plan
        if action == 'stay':
            pass
        elif action == 'move':
            self.move(arg)
        elif action == 'attack':
            if isinstance(self,Miner) and isinstance(arg,GoldMine) and arg.alive:
                return self.mine(arg)
            if isinstance(arg,Unit) and arg.alive:
                self.attack(arg)
                if not arg.alive:
                    return arg.kill_reward
        return 0


class Soldier(Unit):
    price = 20

    def __init__(self, x, y):
        Unit.__init__(self, x, y)
        self.img = ImageManager.getImage("soldier")


class Archer(Unit):
    price = 30

    def __init__(self, x, y):
        Unit.__init__(self, x, y)
        self.img = ImageManager.getImage("archer")

        self.max_move = 1
        self.attack_dmg = 1
        self.attack_range = 3
        self.hp = 2
        self.max_hp = 2

class Miner(Unit):
    price = 30

    def __init__(self, x, y):
        Unit.__init__(self, x, y)
        self.img = ImageManager.getImage("miner")

        self.max_move = 1
        self.attack_dmg = 1
        self.attack_range = 1
        self.hp = 2
        self.max_hp = 2

        self.gold_amount = 5


    def mine(self,gold_mine):
        gold = gold_mine.get_gold(self.gold_amount)
        if not gold_mine.alive:
            self.plan_stay()
        return gold

class GoldMine(Unit):
    def __init__(self,x,y,gold):
        Unit.__init__(self,x,y)
        self.img = ImageManager.getImage("gold_mine")

        self.max_move = 0
        self.attack_dmg = 0
        self.attack_range = 0
        self.hp = gold
        self.max_hp = gold

    def get_gold(self,amount):
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


class Castle(Unit):
    price = 150

    def __init__(self, x, y):
        Unit.__init__(self, x, y)
        self.img = ImageManager.getImage("castle")

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
