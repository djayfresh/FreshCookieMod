"""Tiny RCON client for driving the dev server.

Usage:  python tools/rcon.py "say hello" "list" ...
Env:    RCON_HOST (127.0.0.1), RCON_PORT (25575), RCON_PASSWORD (freshcaa-dev)
"""
from __future__ import annotations

import os
import socket
import struct
import sys


class Rcon:
    def __init__(self, host: str, port: int, password: str):
        self.sock = socket.create_connection((host, port), timeout=30)
        self.req = 0
        rid, kind, _ = self._call(3, password)
        if rid == -1:
            raise SystemExit("rcon: authentication failed")

    def _send(self, kind: int, payload: str) -> int:
        self.req += 1
        body = struct.pack("<ii", self.req, kind) + payload.encode("utf-8") + b"\x00\x00"
        self.sock.sendall(struct.pack("<i", len(body)) + body)
        return self.req

    def _recv(self):
        raw = self._read(4)
        (length,) = struct.unpack("<i", raw)
        body = self._read(length)
        rid, kind = struct.unpack("<ii", body[:8])
        return rid, kind, body[8:-2].decode("utf-8", errors="replace")

    def _read(self, n: int) -> bytes:
        buf = b""
        while len(buf) < n:
            chunk = self.sock.recv(n - len(buf))
            if not chunk:
                raise ConnectionError("rcon: connection closed")
            buf += chunk
        return buf

    def _call(self, kind: int, payload: str):
        self._send(kind, payload)
        return self._recv()

    def command(self, cmd: str) -> str:
        rid, _, text = self._call(2, cmd)
        # Long replies are split across packets; send a no-op and read until its id shows up.
        marker = self._send(2, "")
        out = text
        while True:
            r, _, t = self._recv()
            if r == marker:
                break
            out += t
        return out

    def close(self) -> None:
        self.sock.close()


def main(argv: list[str]) -> None:
    host = os.environ.get("RCON_HOST", "127.0.0.1")
    port = int(os.environ.get("RCON_PORT", "25575"))
    password = os.environ.get("RCON_PASSWORD", "freshcaa-dev")
    rc = Rcon(host, port, password)
    try:
        for cmd in argv:
            print(f"> {cmd}")
            print(rc.command(cmd))
    finally:
        rc.close()


if __name__ == "__main__":
    main(sys.argv[1:])
