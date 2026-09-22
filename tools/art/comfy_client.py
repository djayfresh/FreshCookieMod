"""Tiny ComfyUI HTTP client: upload an image, queue an API-format workflow, wait, download outputs."""
import json
import time
import urllib.request
import uuid
from pathlib import Path

DEFAULT_URL = "http://192.168.60.55:8188"


class ComfyClient:
    def __init__(self, url: str = DEFAULT_URL):
        self.url = url.rstrip("/")
        self.client_id = uuid.uuid4().hex

    def upload_image(self, path: Path, name: str | None = None) -> str:
        name = name or path.name
        boundary = uuid.uuid4().hex
        body = (
            f"--{boundary}\r\nContent-Disposition: form-data; name=\"image\"; filename=\"{name}\"\r\n"
            f"Content-Type: image/png\r\n\r\n").encode() + path.read_bytes() + (
            f"\r\n--{boundary}\r\nContent-Disposition: form-data; name=\"overwrite\"\r\n\r\ntrue\r\n--{boundary}--\r\n").encode()
        req = urllib.request.Request(f"{self.url}/upload/image", data=body,
                                     headers={"Content-Type": f"multipart/form-data; boundary={boundary}"})
        with urllib.request.urlopen(req, timeout=60) as r:
            return json.load(r)["name"]

    def queue(self, workflow: dict) -> str:
        data = json.dumps({"prompt": workflow, "client_id": self.client_id}).encode()
        req = urllib.request.Request(f"{self.url}/prompt", data=data, headers={"Content-Type": "application/json"})
        with urllib.request.urlopen(req, timeout=60) as r:
            res = json.load(r)
        if res.get("node_errors"):
            raise RuntimeError(json.dumps(res["node_errors"], indent=1))
        return res["prompt_id"]

    def wait(self, prompt_id: str, timeout: float = 600) -> dict:
        deadline = time.time() + timeout
        while time.time() < deadline:
            with urllib.request.urlopen(f"{self.url}/history/{prompt_id}", timeout=30) as r:
                hist = json.load(r)
            if prompt_id in hist:
                entry = hist[prompt_id]
                status = entry.get("status", {})
                if status.get("status_str") == "error":
                    raise RuntimeError(json.dumps(status.get("messages", []), indent=1)[:2000])
                if entry.get("outputs"):
                    return entry["outputs"]
            time.sleep(1.0)
        raise TimeoutError(prompt_id)

    def download(self, output: dict, dest: Path) -> Path:
        q = urllib.parse.urlencode({"filename": output["filename"], "subfolder": output.get("subfolder", ""), "type": output.get("type", "output")})
        with urllib.request.urlopen(f"{self.url}/view?{q}", timeout=120) as r:
            dest.parent.mkdir(parents=True, exist_ok=True)
            dest.write_bytes(r.read())
        return dest

    def run(self, workflow: dict, dest: Path, timeout: float = 600) -> list[Path]:
        outputs = self.wait(self.queue(workflow), timeout)
        saved = []
        for node_out in outputs.values():
            for i, img in enumerate(node_out.get("images", [])):
                target = dest if i == 0 and len(node_out["images"]) == 1 else dest.with_name(f"{dest.stem}_{i}{dest.suffix}")
                saved.append(self.download(img, target))
        return saved


import urllib.parse  # noqa: E402  (kept at bottom to keep the top tidy)
