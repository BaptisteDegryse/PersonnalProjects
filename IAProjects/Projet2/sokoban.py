from search import *
from copy import copy, deepcopy
import time
import hashlib



class State:
    count_state = 0 # count the number of states !
    def __init__(self, grid, placement, pos):
        self.grid = grid  # on garde que l'etat (pas le goal) dans state
        self.pos = pos
        self.placement = placement  # final position of the boxes
        State.count_state += 1

    def __str__(self):
        buf = ''
        for i in range(len(self.grid)):
            for j in range(len(self.grid[i])):
                if [i,j] in self.placement and self.grid[i][j]==' ':
                    buf += '.'
                else:
                    buf += self.grid[i][j]
            buf += '\n'
        return buf

    def __hash__(self):
        return int(hashlib.sha1(self.__str__().encode('utf-8')).hexdigest(), 16)

    def __eq__(self, other):
        return self.__str__() == other.__str__()

    def copyState(self):
        s2 = State(deepcopy(self.grid), self.placement, copy(self.pos))
        s2.pos = copy(self.pos)
        return s2

    def move(self, nextPos):
        self.grid[self.pos[0]][self.pos[1]] = ' '
        self.grid[nextPos[0]][nextPos[1]] = '@'
        self.pos = copy(nextPos)

    def can_push(self, boxPos):
        i = boxPos[0] - self.pos[0]
        j = boxPos[1] - self.pos[1]
        nextBoxPos = [boxPos[0] + i, boxPos[1] + j]
        cannot_push = not inBounds(self.grid, nextBoxPos) or self.grid[boxPos[0] + i][boxPos[1] + j] != ' ' or abs(
            i + j) != 1
        return not cannot_push

    def push(self, boxPos):
        i = boxPos[0] - self.pos[0]
        j = boxPos[1] - self.pos[1]
        self.grid[self.pos[0]][self.pos[1]] = ' '
        self.grid[boxPos[0]][boxPos[1]] = '@'
        self.grid[boxPos[0] + i][boxPos[1] + j] = '$'
        self.pos = copy(boxPos)

    def closest(self):
        """ this function return a list with the minimum distance between the goal and the closest box and
        coordinate of the the bow with min distance"""
        boxes = [[i, j] for i in range(len(self.grid)) for j in range(len(self.grid[i])) if
                 (self.grid[i][j] == '$')]
        tot_min = 0
        for b in boxes:
            minim = 100
            for p in self.placement:
                minim = min(minim, abs(b[0] - p[0]) + abs(b[1] - p[1]))
            tot_min += minim

        return tot_min

    def heuristic(self):
        """return the sum of the manhattan distance between each box and their closest goal"""
        return 10 * self.closest()


class Sobokan(Problem):
    dir = [[1, 0], [-1, 0], [0, 1], [0, -1]]

    def __init__(self, file):

        Problem.__init__(self, None)

        file_init = file + '.init'
        file_goal = file + '.goal'

        with open(file_goal) as f:
            self.goal = [list(line.strip()) for line in f.readlines()]
        with open(file_init) as f:
            grid = [list(line.strip()) for line in f.readlines()]

        placement = [[i, j] for i in range(len(self.goal)) for j in range(len(self.goal[i])) if
                     (self.goal[i][j] == '.')]
        pos = [[i, j] for i in range(len(grid)) for j in range(len(grid[i])) if
               (grid[i][j] == '@')]

        self.initial = State(grid, placement, pos[0])

    def successor(self, state):
        """Given a state, return a sequence of (action, state) pairs reachable
        from this state. If there are many successors, consider an iterator
        that yields the successors one at a time, rather than building them
        all at once. Iterators will work fine within the framework."""
        for n in self.valid_push_neighbors(state, state.pos):
            s2 = state.copyState()
            s2.push(n)
            # print(s2)
            if not self.dead_end(s2):
                yield ('x', s2)
        for n in self.valid_move_neighbors(state, state.pos):
            s2 = state.copyState()
            s2.move(n)
            # print(s2)
            yield ('x', s2)

    def goal_test(self, state):
        """Return True if the state is a goal. The default method compares the
        state to self.goal, as specified in the constructor. Implement this
        method if checking against a single self.goal is not enough."""
        return state.heuristic() == 0

    def dead_end(self, state):
        """ any blocking situation, the tests should be done in other subfunctions """
        boxes = [[i, j] for i in range(len(state.grid)) for j in range(len(state.grid[i])) if
                 (state.grid[i][j] == '$') and [i, j] not in [p for p in state.placement]]
        for b in boxes:
            if not movable(state, b):
                return True
        return False

    def neighbors(self, pos):
        n = []
        for d in self.dir:
            i = pos[0] + d[0]
            j = pos[1] + d[1]
            n.append([i, j])
        return n

    def valid_move_neighbors(self, state, pos):
        n = []
        for d in self.neighbors(pos):
            if state.grid[d[0]][d[1]] == ' ':
                n.append(d)
        return n

    def valid_push_neighbors(self, state, pos):
        n = []
        for d in self.neighbors(pos):
            if state.grid[d[0]][d[1]] == '$' and state.can_push(d):
                n.append(d)
        return n


def h(node):
    """heuristic of astar"""
    return node.state.heuristic()


def inBounds(grid, pos):
    return 0 <= pos[0] < len(grid) and 0 <= pos[1] < len(grid[0])


def movable(state, b):
    if (state.grid[b[0]+1][b[1]] == '#' and state.grid[b[0]][b[1]+1] == '#') \
            or (state.grid[b[0]-1][b[1]] == '#' and state.grid[b[0]][b[1]-1] == '#')\
            or (state.grid[b[0]-1][b[1]] == '#' and state.grid[b[0]][b[1]+1] == '#')\
            or (state.grid[b[0]+1][b[1]] == '#' and state.grid[b[0]][b[1]-1] == '#'):
        return False
    return True



if len(sys.argv) > 1:
    problem = Sobokan(sys.argv[1])
else:
    problem = Sobokan('benchsGiven/sokoInst15')
start = time.time()
# example of bfs search
node = astar_graph_search(problem, h)
if node == None:
    print("FAIL")
else:
    time_search = time.time() - start

    # example of print
    path = node.path()
    path.reverse()

    for n in path:
        print(n.state)

#print(time_search)
#print(State.count_state)
