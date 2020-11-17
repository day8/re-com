###Using Devtools/Dirac

[Dirac](https://github.com/binaryage/dirac) is a fork of Chrome DevTools which provides integrated REPL in DevTools JS console
where you can evaluate ClojureScript in context of your breakpoint and other goodies.
It is installed as a Chrome extensions *(see details in above link)*
The project.clj already has the necessary dependencies namely:
```binaryage/dirac & binaryage/devtools``` including repl options and middleware.


#####First time setup for Canary/Unstable Chrome with Dirac support:

1. Create a hidden Canary/Unstable profile directory in your home drive e.g.
     - Linux/Mac: `~/.dirac-chrome-profile`
     - Windows: `C:\Users\{username}\.dirac-chrome-profile`
2. From terminal:
     - Linux/Mac: `google-chrome-unstable --remote-debugging-port=9222 --no-first-run --user-data-dir=.dirac-chrome-profile`
     - Windows: `"C:\Users\{username}\AppData\Local\Google\Chrome SxS\Application\chrome.exe" --remote-debugging-port=9222 --no-first-run --user-data-dir="C:\Users\{username}\.dirac-chrome-profile"`
3. From that Canary/Unstable session install the dirac devtools [extension](https://chrome.google.com/webstore/detail/dirac-devtools/kbkdngfljkchidcjpnfcgcokkbhlkogi).


#####Running after setup

1. Build re-demo as normal via ```lein watch```
2. From a project terminal, ```lein repl```  this opens nrepl on port 8230 and gateway waiting for port 8231 (which is dirac/devtools console repl will connect to).
3. Launch Canary/Unstable Chrome as per 2. in first time setup in prior section. You might want to setup a shortcut *(or Intellij external tool command)* for convenience.
4. With re-demo running, click on the Dirac extension icon (not normal devtools).
5. At a breakpoint, go to console and press Ctrl+, and Ctrl+. to switch between js and cljs repl. See the formatting glory and context based repl evaluation.
