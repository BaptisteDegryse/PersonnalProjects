from tkinter import Button, Label

from main import Unit

from main.Unit import Soldier, Archer, Castle, Miner, GoldMine

__author__ = 'Baptiste'


class Player:
    def __init__(self, id, gold=0):
        self.id = id
        self.units = []
        self.money = gold
        self.money_lab = Label(None, text=self.money)

    def get_all_units(self):
        for u in self.units:
            yield u

    def get_buildings(self):
        for u in self.units:
            if u.is_building():
                yield u

    def get_movable_units(self):
        for u in self.units:
            if not u.is_building():
                yield u

    def create_free_soldier(self, x, y):
        self.units.append(Soldier(x, y))

    def create_soldier(self, x, y):
        if self.can_pay(Soldier.price):
            self.pay(Soldier.price)
            self.create_free_soldier(x, y)

    def create_archer(self, x, y):
        if self.can_pay(Archer.price):
            self.pay(Archer.price)
            self.create_free_archer(x, y)

    def create_miner(self, x, y):
        if self.can_pay(Miner.price):
            self.pay(Miner.price)
            self.create_free_miner(x, y)

    def create_free_archer(self, x, y):
        self.units.append(Archer(x, y))

    def create_free_miner(self, x, y):
        self.units.append(Miner(x, y))

    def create_free_castle(self, x, y):
        self.units.append(Castle(x, y))

    def create_free_gold_mine(self, x, y):
        self.units.append(GoldMine(x, y, 300))

    def can_pay(self, price):
        return self.money >= price

    def pay(self, price):
        self.money -= price
        self.update_money_lab()

    def earn(self, money):
        self.money += money
        self.update_money_lab()

    def update_money_lab(self):
        self.money_lab.config(text="Money : " + str(self.money))

    def refresh_dead(self):
        for u in self.get_all_units():
            if u.img_deleted:
                self.units.remove(u)

    def create_action_buttons(self, create_soldier_fun, create_archer_fun, create_miner_fun, next_turn_fun,
                              show_plan_fun):
        self.money_lab = Label("")
        self.update_money_lab()
        yield self.money_lab

        # castle_buttons
        self.soldier_button = Button(text="Soldier (s) : " + str(Soldier.price), command=create_soldier_fun)
        yield self.soldier_button
        self.archer_button = Button(text="Archer (d) : " + str(Archer.price), command=create_archer_fun)
        yield self.archer_button
        self.miner_button = Button(text="Gold Miner (f) : " + str(Miner.price), command=create_miner_fun)
        yield self.miner_button
        yield Button(text="Show plan", command=show_plan_fun)
        yield Button(text="Next Day", command=next_turn_fun)

    def update_action_buttons(self, selected_units):
        castle = False
        unit = False
        for u in selected_units:
            if isinstance(u, Unit.Castle):
                castle = True
            else:
                unit = True

        self.soldier_button.config(state="disable")
        self.archer_button.config(state="disable")
        self.miner_button.config(state="disable")

        if castle:
            if self.money >= Soldier.price:
                self.soldier_button.config(state="normal")
            if self.money >= Archer.price:
                self.archer_button.config(state="normal")
            if self.money >= Miner.price:
                self.miner_button.config(state="normal")
