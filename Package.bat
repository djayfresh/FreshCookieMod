@echo off
set modid=freshcaa
set modName=FreshCAA
set mcpPath=%1
set sourcePath=%2
set zipPath=%sourcePath%\..

set mcpBin=%mcpPath%\bin\minecraft\%modid%
set mcpReob=%mcpPath%\reobf\minecraft\%modid%
set mcpSrc=%mcpPath%\src\minecraft\%modid%
set mcpAss=%mcpPath%\src\minecraft\assets\%modid%

set destBin=%sourcePath%\bin\minecraft\%modid%
set destReob=%sourcePath%\reobf\minecraft\%modid%
set destSrc=%sourcePath%\src\minecraft\%modid%
set destAss=%sourcePath%\src\minecraft\assets\%modid%
set zipAss=%sourcePath%\src\minecraft\assets\

:ask
echo Skip Packing?(Y/N)
set skipPacking=y
set /P skipPacking=Type Input %=%
if /I "%skipPacking%"=="y" goto yes
if /I "%skipPacking%"=="n" goto no
echo incorrect input & goto ask

:no
cd /D %mcpPath%
runtime\bin\python\python_mcp runtime\recompile.py
runtime\bin\python\python_mcp runtime\reobfuscate.py %*

:yes
cd /D %sourcePath%
xcopy %mcpBin%\* %destBin% /s /i /y /q
xcopy %mcpReob%\* %destReob% /s /i /y /q
xcopy %mcpSrc%\* %destSrc% /s /i /y /q
xcopy %mcpAss%\* %destAss% /s /i /y /q

7z u %zipPath%\%modName%.zip %destReob%\
7z u %zipPath%\%modName%.zip %zipAss%
7z u %zipPath%\%modName%.zip %sourcePath%\mcmod.info

:end
pause