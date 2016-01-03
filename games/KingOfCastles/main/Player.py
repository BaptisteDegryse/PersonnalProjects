from tkinter import Label

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

    def create_type(self, type, x, y):
        if self.can_pay(type.price):
            self.pay(type.price)
            self.create_free_type(type, x, y)

    def create_free_type(self, type, x, y):
        self.units.append(type(x, y))

    def upgrade_unit(self, unit):
        if self.can_pay(unit.price):
            self.pay(unit.price)
            unit.upgrade()
            return True
        return False

    def upgrade_free(self, x, y):
        for u in self.get_all_units():
            (x2, y2) = u.get_pos()
            if x == x2 and y == y2:
                u.upgrade()
                return

    def can_pay(self, price):
        return self.money >= price

    def pay(self, price):
        self.money -= price

    def earn(self, money):
        self.money += money

    def get_money(self):
        return self.money

    def refresh_dead(self):
        for u in self.get_all_units():
            if u.img_deleted:
                self.units.remove(u)
