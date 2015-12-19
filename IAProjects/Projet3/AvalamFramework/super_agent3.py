#!/usr/bin/env python3
"""
Avalam agent.
Copyright (C) 2015, <<<<<<<<<<< YOUR NAMES HERE >>>>>>>>>>>

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; version 2 of the License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, see <http://www.gnu.org/licenses/>.

"""

import avalam
import minimax
import random
import time
import sys


class Agent:
    """This is the skeleton of an agent to play the Avalam game."""

    def __init__(self, name="Basic_Agent"):
        self.name = name
        self.best_for_step = [0] * 40

    def key(self, e):
        (a, h) = e
        if h < -1:
            return -2
        elif h < 0:
            return -1
        elif h > 1:
            return 1
        else:
            return 2

    def successors(self, state):
        """The successors function must return (or yield) a list of
        pairs (a, s) in which a is the action played to reach the
        state s; s is the new state, i.e. a triplet (b, p, st) where
        b is the new board after the action a has been played,
        p is the player to play the next move and st is the next
        step number.
        """
        (b, p, st) = state
        if st == 1:
            a = (3, 7, 3, 8)
            b2 = b.clone()
            b2.play_action(a)
            yield (a, (b2, -p, st + 1))
            return

        big_towers = self.get_big_towers(state)
        towers = self.get_towers_neighbors(state, big_towers, 2)
        # print(towers)
        self.number_tower = len(towers[0]) + len(towers[1]) + len(towers[2])

        for towers_i in towers:
            actions = []
            for t in towers_i:
                (i, j) = t
                ta = b.get_tower_actions(i, j)
                for a in ta:
                    if b.is_action_valid(a):
                        (i, j, k, l) = a
                        actions.append((a, b.get_tower_height(k, l)))
            sorted(actions, key=self.key)
            for a, h in actions:
                b2 = b.clone()
                b2.play_action(a)
                yield (a, (b2, -p, st + 1))

    def cutoff(self, state, depth):
        """The cutoff function returns true if the alpha-beta/minimax
        search has to stop; false otherwise.
        """
        (b, p, st) = state
        time_left = float('inf')
        if self.time_left is not None:
            time_left = self.time_left - (time.time() - self.time)

        max_depth = 3
        if st < 10:
            max_depth = 1
        elif time_left < (self.number_tower * 2):
            max_depth = 1
        elif time_left < (self.number_tower * 15):
            max_depth = 2
        # print("j'ai une depth de " + max_depth)

        if max_depth != self.max_depth:
            self.max_depth = max_depth
            print(max_depth)

        return depth > max_depth or b.is_finished()

    def evaluate(self, state):
        """The evaluate function must return an integer value
        representing the utility function of the board.
        """

        (b, p, st) = state
        score = 0
        score2 = 0
        score3 = 0.0
        score4 = 0
        for t in b.get_towers():
            (i, j, h) = t
            if not b.is_tower_movable(i, j):
                if h > 0:
                    score2 += 1
                else:
                    score2 -= 1
            else:
                if h > 0:
                    score += 1
                else:
                    score -= 1
            ta = b.get_tower_actions(i, j)
            for a in ta:
                if b.is_action_valid(a):
                    (i, j, k, l) = a
                    h = b.get_tower_height(k, l)
                    friend = 0.0
                    ennemies = 0.0
                    if h > 0:
                        friend += 1.0
                    else:
                        ennemies += 1.0
                    score3 += friend / (friend + ennemies)  # ratio
                    if ennemies == 0:
                        score4 += 1
                    if friend == 0:
                        score4 -= 1

        tot_score = score + 10 * score2 + int(score3 / 3) + 3 * score4
        return tot_score

    def play(self, board, player, step, time_left):
        """This function is used to play a move according
        to the board, player and time left provided as input.
        It must return an action representing the move the player
        will perform.
        """
        self.player = player
        self.time_left = time_left
        self.time = time.time()
        self.max_depth = 0
        print(time_left)
        newBoard = avalam.Board(board.get_percepts(player == avalam.PLAYER2))
        state = (newBoard, player, step)
        return minimax.search(state, self)

    def get_big_towers(self, state):
        (b, p, st) = state
        towers = b.get_towers()
        if st == 1:
            j = 0
            for t in towers:
                j += 1
                if j > 3:
                    break
                yield t
        else:
            for t in towers:
                (i, j, h) = t
                if abs(h) > 1:
                    yield t

    def get_towers_neighbors(self, state, towers, max_dist):
        (board, p, st) = state
        consider_tower = []
        towers0 = []
        towers1 = []
        towers2 = []
        dir = [0]
        for i in range(1, max_dist + 1):
            dir.append(i)
            dir.append(-i)
        for t in towers:  # check around each tower
            (i, j, h) = t
            for a in range(len(dir)):
                for b in range(len(dir)):
                    # print (str((i+a,j+b))+" in_towers:"+str(self.is_in_towers((i + a, j + b), board))+" not_in_consid:" + str((i + a, j + b) not in consider_tower))
                    if is_in_towers((i + dir[a], j + dir[b]), board) and (
                                i + dir[a], j + dir[b]) not in consider_tower:
                        tow = (i + dir[a], j + dir[b])
                        consider_tower.append(tow)
                        if abs(h) > 1:
                            towers0.append(tow)
                        elif abs(dir[a]) <= 1 and abs(dir[b]) <= 1:
                            towers1.append(tow)
                        else:
                            towers2.append(tow)

        # print("consider: " + str(consider_tower))
        random.shuffle(towers0)
        random.shuffle(towers1)
        random.shuffle(towers2)
        towersf = []
        towersf.append(towers1)
        towersf.append(towers0)
        towersf.append(towers2)
        return towersf


def is_in_towers(tower, board):
    """
    :param tower: (i,j) tower
    :param towers: [ (i,j,h) , (i,j,h) , ...]
    :return: True if (tower, ?) in towers
    """
    towers = board.get_towers()
    (i2, j2) = tower
    # print("tower : "+str(tower)+" all_towers:"+str(towers))
    for t in towers:
        (i, j, h) = t
        if i == i2 and j == j2:
            return True
    return False

# if __name__ == "__main__":
# avalam.agent_main(Agent())
