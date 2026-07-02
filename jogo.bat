@echo off
rem Executa o jogo no Windows com acentuação correta.
rem Os dois lados precisam combinar: o console decodificando UTF-8 (chcp 65001)
rem e o Java emitindo UTF-8 (propriedades de encoding abaixo).
chcp 65001 >nul
java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 "%~dp0src\Main.java" %*
