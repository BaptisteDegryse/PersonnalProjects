import sys
from threading import Thread
from core.Main import init_server, init_client

__author__ = 'Baptiste'

if len(sys.argv) > 1:
    if sys.argv == "client":
        print("client")
        init_client(12800)
    else:
        print("server")
        init_server()
else:
    print("client")
    init_client(12800)

