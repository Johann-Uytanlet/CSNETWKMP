# CSNETWKMP — Socket-Based File Sharing in Java

A lightweight client–server application built with Java TCP sockets.  
It demonstrates basic networking, multi-threading, and file transfer between multiple clients and a central server.

---

## Features
- Client–Server communication over TCP using `ServerSocket` and `Socket`
- Multi-client support, each connection handled on its own thread
- User handles: register a username to tag uploads
- File Upload (`/post <path>`) — send files to the server
- File Directory (`/dir`) — list uploaded files with uploader information
- File Download (`/get <filename>`) — retrieve files from the server
- Session Management (`/connect`, `/leave`, `/disconnect`)
