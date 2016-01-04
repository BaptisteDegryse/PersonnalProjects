from io import BytesIO
from main.Constants import TILE_SIZE
from pkg_resources import resource_string

__author__ = 'Baptiste'
from PIL import ImageTk, Image

image_book = {}

def get_image(name):
    img = image_book.get(name)
    if img is not None:
        return img
    else:
        path = resource_string(__name__, 'images/'+name+'.png')
        img = Image.open(BytesIO(path))
        img = img.resize((TILE_SIZE - 1, TILE_SIZE - 1), Image.ANTIALIAS)
        img = ImageTk.PhotoImage(image=img)
        image_book[name] = img
        return img
