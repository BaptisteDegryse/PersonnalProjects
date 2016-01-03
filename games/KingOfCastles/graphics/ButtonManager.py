from tkinter import Button, Frame, Label

from graphics.ImageManager import get_image
from main.Unit import Castle, Unit

__author__ = 'Baptiste'


class ButtonManager(Frame):
    def __init__(self, game, player, master=None):
        Frame.__init__(self, master, height=150)
        self.game = game
        self.player = player
        self.pack(fill="x")

        self.current_widget = []

        self.next_turn_button = Button(master=self, image=get_image("next_turn"), command=self.next_turn)
        price = Label(master=self, text="Price :")
        short_cut = Label(master=self, text="Short cut :")

        self.next_turn_button.grid(column=1, row=0)
        price.grid(column=1, row=1)
        short_cut.grid(column=1, row=2)

        self.basic_short_cut = Frame(master=self)
        attack = Label(master=self.basic_short_cut, text="Attack : a")
        move = Label(master=self.basic_short_cut, text="Move : z")
        create = Label(master=self.basic_short_cut, text="Creation tile : e")
        self.money = Label(master=self, text="")

        attack.grid(column=0, row=0)  # in basic_short_cut grid
        move.grid(column=0, row=1)
        create.grid(column=0, row=2)

        self.money.grid(column=0, row=1)  # in normal grid

        self.basic_short_cut.grid(column=0, row=0)

        self.update_menu([])

    def update_menu(self, selected_units):
        su = list(selected_units)
        types = [type(u) for u in su]

        if self.game.current_phase == "planning":
            i = 0
            self.clear_all()
            single_creator = None
            interrupted = False

            for unit_class in list(Unit.__subclasses__()) + list(Castle.__subclasses__()):
                if unit_class.creator in types:

                    if single_creator is None:
                        single_creator = unit_class.creator
                    elif single_creator != unit_class.creator:
                        self.clear_all()
                        interrupted = True

                    if not interrupted:
                        self.create_button(unit_class, i)
                        i += 1
                if unit_class in types and unit_class.max_lvl > 1:
                    u = su[list(types).index(unit_class)]
                    self.upgrade_button(u,i)
                    i+=1

        self.update_money()

    def clear_all(self):
        for b in self.current_widget:
            b.destroy()

    def create_button(self, unit_class, i):
        img = get_image(unit_class.name)

        b = Button(master=self, image=img, command=lambda u_class=unit_class: self.create_button_click(u_class))
        b.grid(column=i + unit_class.price, row=0)
        if self.player.get_money() < unit_class.price:
            b.config(state="disable")

        l = Label(master=self, text=str(unit_class.price) + "$")
        l.grid(column=i + unit_class.price, row=1)
        s = Label(master=self, text="(" + str(unit_class.short_cut) + ")")
        s.grid(column=i + unit_class.price, row=2)

        self.current_widget.append(b)
        self.current_widget.append(l)
        self.current_widget.append(s)

    def upgrade_button(self, unit,i):
        if unit.lvl == unit.max_lvl:
            return
        unit_class = type(unit)
        img = get_image("upgrade")
        b = Button(master=self, image=img, command=lambda unit=unit: self.upgrade_button_click(unit))
        b.grid(column=i+50 + unit_class.price, row=0)
        if self.player.get_money() < unit_class.price:
            b.config(state="disable")

        l = Label(master=self, text=str(unit_class.price) + "$")
        l.grid(column=i+50 + unit_class.price, row=1)

        self.current_widget.append(b)
        self.current_widget.append(l)

    def update_money(self):
        self.money.config(text="Money : " + str(self.player.get_money()) + "$")

    def create_button_click(self, u_class):
        self.game.create_unit_type(u_class)
        self.update_money()

    def upgrade_button_click(self, unit):
        self.game.upgrade_unit(unit)
        self.update_money()
        self.game.update()

    def next_turn(self):
        self.game.next_step()
        self.clear_all()
        if self.game.current_phase == "show_other_plan":
            self.next_turn_button.config(image=get_image("attack_phase"), state="normal")
        elif self.game.current_phase == "resolve_attack":
            self.next_turn_button.config(image=get_image("moves_phase"))
        else:
            self.next_turn_button.config(image=get_image("next_turn"))

    def wait_for_others(self):
        self.next_turn_button.config(image=get_image("wait_for_others"), state="disable")
        self.update()
