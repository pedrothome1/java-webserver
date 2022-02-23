# Java WebServer

## NOTES:

- Given a single client, is it possible to reuse the same connection for its subsequent requests?
- Can I use the same thread for different connections?
- Can I use a JAR file - that has an entrypoint - as a library?
- What is the best way to benchmark this?
- Should I use Maven or something like that in the case that I don't have external libraries?
- How should I detect the type of a file to know its mime type?  (Static content server)
- What is the best to way to implement a proxy with this server? (Proxy server)
- What is the best to way to implement a dynamic content server? (Dynamic content server)
- What about WebSockets?

## TODO:

- Add more features and flexibility
- Add logging
- Define the scope of this server
- Distinct the "types" of servers this server can be
- Refactor the code
- Benchmark