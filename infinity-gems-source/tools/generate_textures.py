#!/usr/bin/env python3
"""Erzeugt die 16x16-Item-Texturen (nur Python-Standardbibliothek)."""
import colorsys
import math
import os
import struct
import zlib

OUT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..",
                   "src", "main", "resources", "assets", "infinitygems", "textures", "item")
OUT = os.path.normpath(OUT)
SIZE = 16
CLEAR = (0, 0, 0, 0)


def write_png(path, px):
    raw = b"".join(b"\x00" + b"".join(bytes(p) for p in row) for row in px)

    def chunk(tag, data):
        body = tag + data
        return struct.pack(">I", len(data)) + body + struct.pack(">I", zlib.crc32(body) & 0xFFFFFFFF)

    ihdr = struct.pack(">IIBBBBB", SIZE, SIZE, 8, 6, 0, 0, 0)
    png = b"\x89PNG\r\n\x1a\n" + chunk(b"IHDR", ihdr) + chunk(b"IDAT", zlib.compress(raw, 9)) + chunk(b"IEND", b"")
    with open(path, "wb") as f:
        f.write(png)


def blank():
    return [[CLEAR for _ in range(SIZE)] for _ in range(SIZE)]


def shade(color, factor, add=0):
    return tuple(max(0, min(255, int(c * factor + add))) for c in color) + (255,)


def gem_mask(x, y):
    return abs(x - 7.5) / 6.5 + abs(y - 7.5) / 7.0 <= 1.0


def draw_gem(color_fn):
    """color_fn(x, y, kind) -> RGBA, kind in {'edge','light','base','dark'}"""
    px = blank()
    for y in range(SIZE):
        for x in range(SIZE):
            if not gem_mask(x, y):
                continue
            border = not all(gem_mask(x + dx, y + dy) for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)))
            if border:
                kind = "edge"
            elif x < 8 and y < 8:
                kind = "light"
            elif x >= 8 and y >= 8:
                kind = "dark"
            else:
                kind = "base"
            px[y][x] = color_fn(x, y, kind)
    for sx, sy in ((5, 5), (6, 4), (4, 6)):
        if px[sy][sx][3]:
            px[sy][sx] = (255, 255, 255, 255)
    return px


def solid_gem(base):
    def fn(x, y, kind):
        if kind == "edge":
            return shade(base, 0.35)
        if kind == "light":
            return shade(base, 1.3, 35)
        if kind == "dark":
            return shade(base, 0.65)
        return shade(base, 1.0)
    return draw_gem(fn)


def rainbow_gem():
    def fn(x, y, kind):
        hue = (math.atan2(y - 7.5, x - 7.5) / (2 * math.pi)) % 1.0
        r, g, b = colorsys.hsv_to_rgb(hue, 0.85, 1.0)
        base = (int(r * 255), int(g * 255), int(b * 255))
        if kind == "edge":
            return shade(base, 0.3)
        if kind == "light":
            return shade(base, 1.15, 40)
        if kind == "dark":
            return shade(base, 0.7)
        return shade(base, 1.0)
    return draw_gem(fn)


def core():
    px = blank()
    for y in range(SIZE):
        for x in range(SIZE):
            d = math.hypot(x - 7.5, y - 7.5)
            if d > 7.2:
                continue
            if d > 6.2:
                px[y][x] = (30, 0, 50, 255)
                continue
            base = 25 + int((7 - d) * 4)
            px[y][x] = (base, 0, base + 30, 255)
            # leuchtender Ring (Unendlichkeit)
            if 3.0 < d < 4.4:
                hue = (math.atan2(y - 7.5, x - 7.5) / (2 * math.pi)) % 1.0
                r, g, b = colorsys.hsv_to_rgb(hue, 0.7, 1.0)
                px[y][x] = (int(r * 255), int(g * 255), int(b * 255), 255)
            if d < 1.8:
                px[y][x] = (255, 255, 255, 255)
    return px


TEXTURES = {
    "ruby": lambda: solid_gem((220, 20, 40)),
    "sapphire": lambda: solid_gem((30, 100, 235)),
    "emerald": lambda: solid_gem((20, 200, 80)),
    "amethyst": lambda: solid_gem((170, 70, 235)),
    "topaz": lambda: solid_gem((250, 195, 30)),
    "void_gem": lambda: solid_gem((70, 20, 110)),
    "infinity_core": core,
    "infinity_gem": rainbow_gem,
}

if __name__ == "__main__":
    os.makedirs(OUT, exist_ok=True)
    for name, make in TEXTURES.items():
        write_png(os.path.join(OUT, name + ".png"), make())
        print("wrote", name + ".png")
