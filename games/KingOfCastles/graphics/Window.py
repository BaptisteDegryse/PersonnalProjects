from tkinter import Frame, Canvas, Tk

import math
from main import Unit

from main.Constants import WIDTH, HEIGHT, TILE_SIZE

__author__ = 'Baptiste'


class Window(Frame):
    def __init__(self):
        self.root = Tk()
        Frame.__init__(self, self.root, width=WIDTH, height=HEIGHT)
        self.focus_set()
        self.pack()
        self.canvas = Canvas(self, width=WIDTH, height=HEIGHT, bg="Gray")
        self.canvas.pack()

        self.font = ("Helvetica", 16)
        self.img_grid = []

        for i in range(0, WIDTH // TILE_SIZE):
            self.img_grid.append([])
            for j in range(0, HEIGHT // TILE_SIZE):
                self.img_grid[i].append(
                    self.canvas.create_rectangle(i * TILE_SIZE, j * TILE_SIZE, (i + 1) * TILE_SIZE, (j + 1) * TILE_SIZE,
                                                 fill="white"))


    def draw_unit(self, u):
        if not u.alive:
            self.delete_unit(u)
            return
        (x, y) = u.get_pos()
        u.canvas_img = self.draw_img(x, y, u.img, u.canvas_img)

        self.delete_img(u.selected_canvas_img)
        if isinstance(u, Unit.Castle):
            self.delete_img(u.unit_creation_tile_canvas_img)

        if u.is_selected():
            if isinstance(u, Unit.Castle):
                (x2, y2) = u.get_unit_creation_tile()
                u.unit_creation_tile_canvas_img = self.canvas.create_rectangle(x2 * TILE_SIZE + 1, y2 * TILE_SIZE + 1,
                                                                               (x2 + 1) * TILE_SIZE,
                                                                               (y2 + 1) * TILE_SIZE,
                                                                               fill="", width=3, outline="yellow")

            u.selected_canvas_img = self.canvas.create_rectangle(x * TILE_SIZE + 1, y * TILE_SIZE + 1,
                                                                 (x + 1) * TILE_SIZE, (y + 1) * TILE_SIZE,
                                                                 fill="", width=3, outline="purple")
        self.draw_heath_bar(u)

    def draw_plan(self, u):
        (action, arg) = u.plan
        if action == 'move':
            (x, y) = u.get_pos()
            (x2, y2) = arg
            self.canvas.create_line((x + 0.5) * TILE_SIZE, (y + 0.5) * TILE_SIZE, (x2 + 0.5) * TILE_SIZE,
                                    (y2 + 0.5) * TILE_SIZE, tags=('plan'), fill="blue", width=2, arrow='last')
        if action == "attack" and arg is not None and arg.alive:
            (x, y) = u.get_pos()
            (x2, y2) = arg.get_pos()
            self.canvas.create_line((x + 0.5) * TILE_SIZE, (y + 0.5) * TILE_SIZE, (x2 + 0.5) * TILE_SIZE,
                                    (y2 + 0.5) * TILE_SIZE, tags=('plan'), fill="red", width=3, arrow='last')
        if action == "mine":
            (x, y) = u.get_pos()
            (x2, y2) = arg.get_pos()
            self.canvas.create_line((x + 0.5) * TILE_SIZE, (y + 0.5) * TILE_SIZE, (x2 + 0.5) * TILE_SIZE,
                                    (y2 + 0.5) * TILE_SIZE, tags=('plan'), fill="Yellow", width=3, arrow='last')


    def delete_plan(self):
        self.canvas.delete('plan')

    def select(self, list):
        for img_tab in self.img_grid:
            for img in img_tab:
                self.canvas.itemconfig(img, fill="white")
        for pos in list:
            (i, j) = pos
            if 0 <= i < len(self.img_grid) and 0 <= j < len(self.img_grid[i]):
                self.canvas.itemconfig(self.img_grid[i][j], fill="purple")

    def clear(self, objects):
        for o in objects:
            self.canvas.delete(o.img)

    def draw_img(self, x, y, img, canvas_img):
        self.delete_img(canvas_img)
        return self.canvas.create_image(x * TILE_SIZE + 1, y * TILE_SIZE + 1, anchor="nw", image=img)

    def delete_img(self, img):
        if img is not None:
            self.canvas.delete(img)

    def draw_heath_bar(self, unit):
        self.delete_img(unit.health_bar_green)
        self.delete_img(unit.health_bar_red)
        (x, y) = unit.get_pos()
        x_bar = x * TILE_SIZE + 1
        y_bar = (y + 1) * TILE_SIZE - 6
        x2_bar = x_bar + max(unit.hp * TILE_SIZE // unit.max_hp, 0)
        unit.health_bar_red = self.canvas.create_rectangle(x_bar, y_bar, (x + 1) * TILE_SIZE - 1,
                                                           (y + 1) * TILE_SIZE - 1, fill="red")
        unit.health_bar_green = self.canvas.create_rectangle(x_bar, y_bar, x2_bar, (y + 1) * TILE_SIZE - 1,
                                                             fill="green")

    def delete_unit(self, u):
        self.delete_img(u.canvas_img)
        self.delete_img(u.health_bar_green)
        self.delete_img(u.health_bar_red)
        self.delete_img(u.selected_canvas_img)
        u.img_deleted = True

    def draw_zone_around(self, pos, n, color):
        (x, y) = pos
        tile_list = []
        for i in range(-n, n + 1):
            for j in range(-n, n + 1):
                if math.fabs(i) + math.fabs(j) <= n:
                    tile_list.append((x + i, y + j))

        self.draw_zone(tile_list, color)

    def draw_zone(self, tiles, color):
        lines = []
        duplicated_lines = []
        for t in tiles:
            (x, y) = t
            for line in [(x, y, x + 1, y), (x, y, x, y + 1), (x + 1, y, x + 1, y + 1), (x, y + 1, x + 1, y + 1)]:
                if line in duplicated_lines:
                    pass
                elif line in lines:
                    lines.remove(line)
                    duplicated_lines.append(line)
                else:
                    lines.append(line)

        for line in lines:
            (x, y, x2, y2) = line
            l = self.canvas.create_line(x * TILE_SIZE, y * TILE_SIZE, x2 * TILE_SIZE, y2 * TILE_SIZE, width=3,
                                        fill=color, tags=('zone'))

    def delete_zone(self):
        self.canvas.delete('zone')



