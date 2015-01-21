@echo off
:: assume we're in a folder under the root
cd /d %~dp0
cd ..
call lein auto-demo
