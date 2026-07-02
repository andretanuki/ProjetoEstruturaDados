@echo off
rem =======================================================
rem SCRIPT UNIVERSAL DE EXECUÇÃO - O CASO DR. ALMEIDA
rem Funciona em qualquer versão do Java
rem =======================================================

rem 1. Ajusta o console do Windows para aceitar os caracteres Problematicos
chcp 65001 >nul

echo [1/2] Compilando os arquivos do projeto...
rem 2. Cria a pasta bin e compila tudo para lá
if not exist bin mkdir bin
javac -d bin src/Main.java src/engine/*.java src/estruturadados/*.java

echo [2/2] Iniciando a investigacao...
echo.
rem 3. Executa o jogo a partir da pasta bin compilada
rem O "%*" garante funcionamento dos parametros
java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp bin Main %*
