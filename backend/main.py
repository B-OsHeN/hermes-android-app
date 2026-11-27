"""HTTP server for Hermes Agent (stdlib only, runs on Termux)."""
import json
import http.server
import socketserver
import subprocess
import os
import time

PORT = 8080
HERMES_HOME = os.path.expanduser("~/.hermes")

class Handler(http.server.BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path == "/health":
            self._json({"status": "ok", "hermes_home": HERMES_HOME})
        elif self.path == "/skills":
            skills_dir = os.path.join(HERMES_HOME, "skills")
            skills = []
            if os.path.isdir(skills_dir):
                skills = [d for d in os.listdir(skills_dir) if os.path.isdir(os.path.join(skills_dir, d))]
            self._json({"skills": skills})
        elif self.path == "/memory":
            mem_path = os.path.join(HERMES_HOME, "memories", "MEMORY.md")
            content = ""
            if os.path.isfile(mem_path):
                with open(mem_path) as f:
                    content = f.read()
            self._json({"memory": content})
        else:
            self.send_error(404)

    def do_POST(self):
        if self.path == "/chat":
            length = int(self.headers.get("Content-Length", 0))
            body = json.loads(self.rfile.read(length)) if length else {}
            msg = body.get("message", "")
            self.send_response(200)
            self.send_header("Content-Type", "text/event-stream")
            self.send_header("Cache-Control", "no-cache")
            self.end_headers()

            # Try calling Hermes CLI; fall back to echo
            try:
                result = subprocess.run(
                    ["hermes", "chat", "-q", msg, "--quiet"],
                    capture_output=True, text=True, timeout=120
                )
                reply = result.stdout.strip() or result.stderr.strip() or "No response from Hermes."
            except FileNotFoundError:
                reply = f"Hermes not installed. Received: {msg}"
            except subprocess.TimeoutExpired:
                reply = "Hermes timed out."
            except Exception as e:
                reply = f"Error: {e}"

            # Stream reply in chunks for SSE feel
            for i in range(0, len(reply), 80):
                chunk = reply[i:i+80]
                self.wfile.write(f"data: {json.dumps({'content': chunk})}\n\n".encode())
                self.wfile.flush()
                time.sleep(0.05)
            self.wfile.write(f"data: {json.dumps({'content': '', 'done': True})}\n\n".encode())
            self.wfile.flush()
        else:
            self.send_error(404)

    def _json(self, data):
        body = json.dumps(data).encode()
        self.send_response(200)
        self.send_header("Content-Type", "application/json")
        self.send_header("Content-Length", str(len(body)))
        self.end_headers()
        self.wfile.write(body)

    def log_message(self, *args):
        pass

if __name__ == "__main__":
    with socketserver.TCPServer(("", PORT), Handler) as httpd:
        print(f"Hermes Agent API running on http://127.0.0.1:{PORT}")
        httpd.serve_forever()