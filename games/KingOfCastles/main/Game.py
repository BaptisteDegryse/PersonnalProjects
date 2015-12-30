import copy
import random
from threading import Thread
import time
from editor.bad_loader import load_map

from graphics.Window import Window
from main import Unit
from main.Constants import TILE_SIZE
from main.Player import Player
from network.NetworkUtils import encode_creation, encode_action

__author__ = 'Baptiste'


class Game:
    def __init__(self, server, client):
        w = Window()
        w.canvas.bind("<Button-1>", self.click)
        w.canvas.bind("<B1-Motion>", self.motion)
        w.canvas.bind("<ButtonRelease-1>", self.release)
        w.canvas.bind("<Key>", self.key_pressed)
        w.canvas.bind("<KeyRelease>", self.key_released)
        w.canvas.focus_set()

        self.window = w
        self.click_x = 0
        self.click_y = 0
        self.turn = "planning"
        self.motion_img = None
        self.show_plan = True

        if server is None:
            self.players = [Player(0), Player(1)]
        else:
            self.players = load_map(1)

        self.player_id = 0
        self.server = server
        self.client = client
        self.unit_creation_tab = []

        self.keys = []
        w.focus()

    def run(self):
        self.window.fill_player_menu(
            self.players[self.player_id].create_action_buttons(self.create_soldier, self.create_archer,
                                                               self.create_miner, self.next_step,
                                                               self.change_show_plan))
        self.update()
        self.window.mainloop()
        if self.server is not None:
            self.server.close()

    def click(self, event):
        self.click_x = event.x
        self.click_y = event.y

    def motion(self, event):
        if not self.turn=="planning":
            return

        x = min(self.click_x, event.x) // TILE_SIZE
        y = min(self.click_y, event.y) // TILE_SIZE
        x2 = max(self.click_x, event.x) // TILE_SIZE
        y2 = max(self.click_y, event.y) // TILE_SIZE

        l = []
        for i in range(x, x2 + 1):
            for j in range(y, y2 + 1):
                l.append((i, j))

        self.window.select(l)
        return l

    def release(self, event):
        if not self.turn=="planning":
            return

        if 'a' in self.keys:
            for u in self.get_selected_units():
                u.plan_attack(self.get_unit_on(event.x // TILE_SIZE, event.y // TILE_SIZE))
        elif 'z' in self.keys:
            self.move_selected_units(event.x // TILE_SIZE, event.y // TILE_SIZE)
        elif 'e' in self.keys:
            self.change_creation_tile(event.x // TILE_SIZE, event.y // TILE_SIZE)
        else:
            l = self.motion(event)
            for u in self.players[self.player_id].get_all_units():
                if u.get_pos() in l:
                    u.select()
                else:
                    u.unselect()

        self.update()
        self.window.delete_zone()

    def key_pressed(self, event):
        key = event.keysym

        if key == "space" and not self.turn=="planning":
            self.next_step()
        if not self.turn=="planning":
            return

        self.update_zone(key)
        self.create_unit(key)
        self.update_key(key, True)

    def key_released(self, event):
        if not self.turn=="planning":
            return
        key = event.keysym
        self.window.delete_zone()
        self.update_key(key, False)

    def update_key(self, name, pressed):
        if pressed:
            self.keys.append(name)
        else:
            self.keys.remove(name)

    def move_selected_units(self, x, y):
        closest_unit = self.find_closest_selected_unit(x, y)
        if closest_unit is None:  # no possible moves
            return
        (xu, yu) = closest_unit.get_pos()
        move_vector = (x - xu, y - yu)
        for u in self.players[self.player_id].get_movable_units():
            (xu, yu) = u.get_pos()
            new_pos = (xu + move_vector[0], yu + move_vector[1])
            if u.is_selected():
                u.plan_move(new_pos)
        self.update()

    def find_closest_selected_unit(self, x, y):
        min_distance = 1000  # inf
        closest_unit = None
        for u in self.get_selected_units():
            dis = u.distance_to((x, y))
            min_distance = min(min_distance, dis)
            if min_distance == dis and u.can_move((x, y)):
                closest_unit = u
        return closest_unit

    def get_selected_units(self):
        for u in self.players[self.player_id].get_all_units():
            if u.is_selected():
                yield u

    def get_selected_castles(self):
        for u in self.get_selected_units():
            if isinstance(u, Unit.Castle):
                yield u

    def is_empty_pos(self, pos):
        for u in self.get_all_units():
            if u.pos == pos:
                return False
        return True

    def find_unit_on(self, pos):
        for u in self.get_all_units():
            if u.pos == pos:
                return u
        return None

    def get_all_units(self):
        for p in self.players:
            for u in p.get_all_units():
                yield u

    def get_unit_on(self, x, y):
        for u in self.get_all_units():
            (ux, uy) = u.pos
            if ux == x and uy == y:
                return u
        return None

    def update(self):
        self.window.select([])
        self.window.delete_plan()
        for u in self.get_all_units():
            self.window.draw_unit(u)

        for p in self.players:
            p.refresh_dead()

        if self.show_plan:
            for u in self.get_all_units():
                self.window.draw_plan(u)

        self.players[self.player_id].update_action_buttons(self.get_selected_units())

    def update_zone(self, key):
        if key == 'a':
            for u in self.get_selected_units():
                self.window.draw_zone_around(u.pos, u.attack_range, 'red')
        if key == 'z':
            for u in self.get_selected_units():
                self.window.draw_zone_around(u.pos, u.max_move, 'blue')

    def change_creation_tile(self, x, y):
        for c in self.get_selected_castles():
            c.set_unit_creation_tile((x, y))

    def create_soldier(self):
        for p in self.find_creation_pos():
            self.players[self.player_id].create_soldier(p[0], p[1])
            u = self.get_unit_on(p[0],p[1])
            self.unit_creation_tab.append(encode_creation(u, self.players[self.player_id]))
        self.update()

    def create_archer(self):
        for p in self.find_creation_pos():
            self.players[self.player_id].create_archer(p[0], p[1])
            u = self.get_unit_on(p[0],p[1])
            self.unit_creation_tab.append(encode_creation(u, self.players[self.player_id]))
        self.update()

    def create_miner(self):
        for p in self.find_creation_pos():
            self.players[self.player_id].create_miner(p[0], p[1])
            u = self.get_unit_on(p[0],p[1])
            self.unit_creation_tab.append(encode_creation(u, self.players[self.player_id]))
        self.update()

    def find_creation_pos(self):
        for c in self.get_selected_castles():
            pos = c.get_unit_creation_tile()
            if self.is_empty_pos(pos):
                yield pos

    def create_unit(self, key):
        if key == 's':
            self.create_soldier()
        elif key == 'd':
            self.create_archer()
        elif key == 'f':
            self.create_miner()

    def next_step(self):
        if self.turn == "planning":
            self.turn = "show_other_plan"
            self.window.wait_for_player()
            self.next_turn()
        elif self.turn == "show_other_plan":
            self.turn = "resolve_attack"
            self.resolve_day_if_plan('attack')
        elif self.turn == "resolve_attack":
            self.turn = "planning"
            self.resolve_day_if_plan('move')
            self.window.done_waiting_for_player()
        self.update()


    def next_turn(self):
        self.send_my_actions()
        self.get_others_actions()

    def resolve_day(self):
        self.resolve_day_if_plan('attack')
        self.resolve_day_if_plan('move')

    def resolve_day_if_plan(self, action):
        for u in self.get_all_units():
            (a, arg) = u.plan
            if a == action:
                self.finish_unit_turn(u)

    def check_error_move(self):
        moves = []
        stays = []
        for u in self.get_all_units():
            (a, arg) = u.plan
            if a == 'stay' or a == 'attack':
                stays.append((u, u.get_pos))

        for u in self.get_all_units():
            (a, arg) = u.plan
            if a == 'move':
                moving_u = self.get_in_move_list(arg, moves)
                staying_u = self.get_in_move_list(arg, stays)
                if moving_u is None and staying_u is None:
                    moves.append((u, arg))
                elif staying_u is None:
                    (unit, pos) = moving_u
                    if random.randint(0, 1) == 0:
                        self.change_move_plan(u, moves, stays)
                    else:
                        moves.remove(moving_u)
                        moves.append((u, arg))
                        self.change_move_plan(moving_u[0], moves, stays)

    def get_in_move_list(self, move, move_list):
        for u, m in move_list:
            if move == m:
                return (u, m)
        return None

    def change_move_plan(self, unit, moves, stays):
        (a, pos) = unit.plan
        if a == "stay":
            stays.append((unit, unit.get_pos()))
            there = self.get_in_move_list(pos, moves)
            if there is not None:
                self.change_move_plan(there[0], moves, stays)
            return
        (x, y) = pos
        (xu, yu) = unit.get_pos()

        if self.get_in_move_list(pos, moves) is None and self.get_in_move_list(pos, stays) is None:
            moves.append((unit, pos))
        else:

            if x > xu:
                unit.plan_move((x - 1, y))
            elif y > yu:
                unit.plan_move((x, y - 1))
            elif x < xu:
                unit.plan_move((x + 1, y))
            elif y < yu:
                unit.plan_move((x, y + 1))

            self.change_move_plan(unit, moves, stays)

    def finish_unit_turn(self, u):
        money = u.resolve_day()
        if money != 0:
            self.get_player_for_unit(u).earn(money)

    def get_player_for_unit(self, u):
        for p in self.players:
            if u in list(p.get_all_units()):
                return p

    def send_my_actions(self):
        all_infos = copy.deepcopy(self.unit_creation_tab)
        self.unit_creation_tab = []
        for u in self.players[self.player_id].get_all_units():
            if self.client is not None:
                all_infos.append(encode_action(u, u.plan))
        self.client.send(all_infos)

    def get_all_actions(self):
        all_infos = []
        for u in self.get_all_units():
            all_infos.append(encode_action(u, u.plan))
        return all_infos

    def get_others_actions(self):
        while not self.client.get_others_actions():
            time.sleep(0.5)

    def change_show_plan(self):
        self.show_plan = not self.show_plan
        self.update()

    def update_from_network(self, tab):
        if len(tab) > 1 and tab[0] == "action":
            if len(tab) > 3 and tab[1] == "stay":
                u = self.find_unit_on((int(tab[2]), int(tab[3])))
                if u is None:
                    print("erreur (move), no unit on " + str(tab))
                u.plan_stay()

            elif len(tab) > 4 and tab[1] == "move":
                u = self.find_unit_on((int(tab[2]), int(tab[3])))
                if u is None:
                    print("erreur (move), no unit on " + str(tab))
                u.plan_move((int(tab[4]), int(tab[5])))

            elif len(tab) > 4 and tab[1] == "attack":
                u = self.find_unit_on((int(tab[2]), int(tab[3])))
                if u is None:
                    print("erreur (attack), no unit on " + str(tab))
                u.plan_attack(self.find_unit_on((int(tab[4]), int(tab[5]))))

        elif len(tab) > 4 and tab[0] == "creation":
            if tab[1] == "<class 'main.Unit.Soldier'>":
                self.players[int(tab[4])].create_free_soldier(int(tab[2]), int(tab[3]))
            elif tab[1] == "<class 'main.Unit.Archer'>":
                self.players[int(tab[4])].create_free_archer(int(tab[2]), int(tab[3]))
            elif tab[1] == "<class 'main.Unit.Miner'>":
                self.players[int(tab[4])].create_free_miner(int(tab[2]), int(tab[3]))
            elif tab[1] == "<class 'main.Unit.GoldMine'>":
                self.players[int(tab[4])].create_free_gold_mine(int(tab[2]), int(tab[3]))
            elif tab[1] == "<class 'main.Unit.Castle'>":
                self.players[int(tab[4])].create_free_castle(int(tab[2]), int(tab[3]))
            else:
                print("creation error : " + str(tab))

        elif len(tab) > 2 and tab[0] == "player_id":
            self.player_id = int(tab[1])
            self.players[self.player_id].earn(int(tab[2]))

    def get_initial_state(self):
        all_units = []
        for p in self.players:
            for u in p.get_all_units():
                all_units.append(encode_creation(u, p))
        return all_units

