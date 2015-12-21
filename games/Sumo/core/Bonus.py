__author__ = 'Baptiste'


class Bonus:
    def __init__(self, x, y, kind):
        self.x = x
        self.y = y
        self.kind = kind
        self.radius = 7
        if kind == 0:
            self.col = "black"
        if kind == 1:
            self.col = "purple"
        self.alive = True
        self.sent = False
        self.img = None

    def is_on(self, p):
        return p.distance_to(self.x, self.y) < self.radius + p.radius

    def check(self, p):
        if self.is_on(p) and self.alive:
            self.effect(p)
            self.alive = False
            self.sent = False

    def effect(self, p):
        if self.kind == 0:
            if p.radius < 30:
                p.radius += 3
        if self.kind == 1:
            if p.radius > 10:
                p.radius -= 3
