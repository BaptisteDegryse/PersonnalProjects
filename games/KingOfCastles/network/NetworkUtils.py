__author__ = 'Baptiste'

PORT = 12800


def network_format(line_to_send):
    buf = str(line_to_send[0])
    for elem in line_to_send[1:]:
        buf += ',' + str(elem)
    buf += "\n"
    return buf


def tab_to_msg(tab_to_send):
    buf = ""
    for l in tab_to_send:
        buf += network_format(l)
    return buf


def msg_to_tab(msg):
    msg_str = msg.decode()
    return [line.split(',') for line in msg_str.split('\n')]


def encode_creation(unit, player):
    (x, y) = unit.get_pos()
    return ["creation", type(unit), x, y, player.id]

def encode_upgrade(unit,player):
    (x, y) = unit.get_pos()
    return ["upgrade", x, y, player.id]


def encode_action(unit, action):
    (x, y) = unit.get_pos()
    (a, arg) = action

    if a == 'stay':
        return ["action", "stay", x, y]
    elif a == 'move':
        return ["action", "move", x, y, arg[0], arg[1]]
    elif a == 'attack':
        return ["action", "attack", x, y, arg.get_pos()[0], arg.get_pos()[1]]
