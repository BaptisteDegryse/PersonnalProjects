import math
from core.Constant import WIDTH, HEIGHT

__author__ = 'Baptiste'

from tkinter import *


class Game(Frame):
    def __init__(self):
        self.root = Tk()
        Frame.__init__(self, self.root, width=WIDTH, height=HEIGHT)
        self.focus_set()
        self.pack()
        self.canvas = Canvas(self, width=WIDTH, height=HEIGHT, bg="Gray")
        self.canvas.pack()
        self.font=("Helvetica", 16)
        self.score = [Label(self,text="0",fg="red",font=self.font),Label(self,text="0",fg="green",font=self.font)]
        for lab in self.score:
            lab.pack()

    def draw_player(self, player):
        p = player
        if p.img is not None:
            (b,l)=p.img
            self.canvas.delete(b)
            self.canvas.delete(l)
        p.img = self.draw_p(p)
        self.score[p.id].config(text=p.score)

    def draw_p(self,p):
        return (self.canvas.create_oval(p.x - p.radius, p.y - p.radius, p.x + p.radius, p.y + p.radius, fill=p.color),
            self.canvas.create_line(p.x, p.y, math.cos(p.dir) * p.radius + p.x, math.sin(p.dir) * p.radius + p.y))

    def draw_block(self,b):
        if b.img is not None:
            self.canvas.delete(b.img)
        b.img=self.canvas.create_rectangle(b.x,b.y,b.x+b.sx,b.y+b.sy,fill=b.col)

    def draw_bonus(self,b):
        if b.img is not None:
            self.canvas.delete(b.img)
        b.img=self.canvas.create_oval(b.x-b.radius,b.y-b.radius,b.x+b.radius,b.y+b.radius,fill=b.col)

    def delete_bonus(self,b):
        if b.img is not None:
            self.canvas.delete(b.img)

