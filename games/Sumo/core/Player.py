import math
from core.Block import Block
from core.Bonus import Bonus
from core.Constant import WIDTH, HEIGHT, COLLISION_LOST, FRICTION

__author__ = 'Baptiste'

id = 0


class Player:
    def __init__(self, x, y):
        self.x = x  # center position
        self.y = y
        self.vx = 0.0
        self.vy = 0.0
        self.dir = 0.0
        self.radius = 15
        self.score = 0
        self.last_touch=None

        self.img = None
        global id
        if id == 0:
            self.color = "red"
        elif id == 1:
            self.color = "green"
        self.id = id
        self.keys = [(str(id)+'Left', False), (str(id)+'Right', False), (str(id)+'Up', False)]
        id += 1


    def rotate_left(self):
        self.dir -= 0.3

    def rotate_right(self):
        self.dir += 0.3

    def move(self, players, objects):
        if self.keys[0][1]:
            self.rotate_left()
        if self.keys[1][1]:
            self.rotate_right()
        if self.keys[2][1]:
            self.accelerate()

        self.friction()
        self.collision(players,objects)

        self.x += self.vx
        self.y += self.vy

    def accelerate(self):
        self.vx += math.cos(self.dir)
        self.vy += math.sin(self.dir)

    def collision(self, players, objects):
        touch=False
        # walls
        if (self.x + self.vx) - self.radius <= 0:
            self.vx = math.fabs(self.vx) / COLLISION_LOST
            self.x = 0 + self.radius
            touch=True
        if (self.x + self.vx) + self.radius >= WIDTH:
            self.vx = -math.fabs(self.vx) / COLLISION_LOST
            self.x = WIDTH - self.radius
            touch=True
        if self.y + self.vy - self.radius <= 0:
            self.vy = math.fabs(self.vy) / COLLISION_LOST
            self.y = 0 + self.radius
            touch=True
        if self.y + self.vy + self.radius >= HEIGHT:
            self.vy = -math.fabs(self.vy) / COLLISION_LOST
            self.y = HEIGHT - self.radius
            touch=True

        if touch and self.last_touch is not None:
            self.last_touch.score+=1

        for p in players:
            if p.id != self.id:
                if self.distance_to(p.x,p.y) < self.radius + p.radius:
                    self.assign_new_speed(p)

        for o in objects:
            if isinstance(o,Block):
                o.bounce(self)
            elif isinstance(o,Bonus):
                o.check(self)

    def distance_to(self, x, y):
        return math.sqrt((self.x - x) * (self.x - x) + (self.y - y) * (self.y - y))

    def assign_new_speed(self, p):
        self.last_touch=p
        p.last_touch=self
        #print("assign vx:" + str(self.vx) +"  vy:"+ str(self.vy))
        svx = (self.vx * (self.radius - p.radius) + (2 * p.radius * p.vx)) / (self.radius + p.radius)
        pvx = (p.vx * (p.radius - self.radius) + (2 * self.radius * self.vx)) / (self.radius + p.radius)
        svy = (self.vy * (self.radius - p.radius) + (2 * p.radius * p.vy)) / (self.radius + p.radius)
        pvy = (p.vy * (p.radius - self.radius) + (2 * self.radius * self.vy)) / (self.radius + p.radius)

        self.vx=svx
        self.vy=svy
        p.vx=pvx
        p.vy=pvy


        #print("vx:" + str(self.vx) +"  vy:"+ str(self.vy))
        self.x += self.vx
        self.y += self.vy
        p.x += p.vx
        p.y += p.vy

    def friction(self):
        if self.vx > 0:
            self.vx -= FRICTION
        if self.vx < 0:
            self.vx += FRICTION
        if self.vy > 0:
            self.vy -= FRICTION
        if self.vy < 0:
            self.vy += FRICTION
