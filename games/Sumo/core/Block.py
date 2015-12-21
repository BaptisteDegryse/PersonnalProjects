import math

__author__ = 'Baptiste'


class Block:
    def __init__(self, x, y, sx, sy):
        self.x = x
        self.y = y
        self.col = "blue"
        self.sx = sx
        self.sy = sy
        self.img = None

    def bounce(self, p):

        # right of the block
        if self.x <= p.x - p.radius <= self.x + self.sx and self.y <= p.y <= self.y + self.sy:
            p.vx = math.fabs(p.vx)
            p.x = self.x + self.sx + p.radius
        # left
        if self.x <= p.x + p.radius <= self.x + self.sx and self.y <= p.y <= self.y + self.sy:
            p.vx = -math.fabs(p.vx)
            p.x = self.x - p.radius
        # top
        if self.x <= p.x <= self.x + self.sx and self.y <= p.y - p.radius <= self.y + self.sy:
            p.vy = math.fabs(p.vy)
            p.y = self.y + self.sy + p.radius
        # bottom
        if self.x <= p.x <= self.x + self.sx and self.y <= p.y + p.radius <= self.y + self.sy:
            p.vy = -math.fabs(p.vy)
            p.y = self.y - p.radius

        # corners
        # top left
        if p.distance_to(self.x, self.y) <= p.radius:
            if math.fabs(p.x - self.x) < math.fabs(p.y - self.y):
                p.vx = -math.fabs(p.vx)
                p.x += p.vx
            else:
                p.vy = math.fabs(p.vy)
                p.y +=p.vy

        #top right
        elif p.distance_to(self.x+self.sx, self.y) <= p.radius:
            if math.fabs(p.x - (self.x+self.sx)) < math.fabs(p.y - self.y):
                p.vx = math.fabs(p.vx)
                p.x += p.vx
            else:
                p.vy = math.fabs(p.vy)
                p.y +=p.vy

        #bottom right
        elif p.distance_to(self.x+self.sx, self.y+self.sy) <= p.radius:
            if math.fabs(p.x - (self.x+self.sx)) < math.fabs(p.y - (self.y+self.sy)):
                p.vx = math.fabs(p.vx)
                p.x += p.vx
            else:
                p.vy = -math.fabs(p.vy)
                p.y +=p.vy

        #bottom left
        elif p.distance_to(self.x, self.y+self.sy) <= p.radius:
            if math.fabs(p.x - self.x) < math.fabs(p.y - (self.y+self.sy)):
                p.vx = -math.fabs(p.vx)
                p.x += p.vx
            else:
                p.vy = -math.fabs(p.vy)
                p.y +=p.vy
