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

xcopy %mcpBin%\* %destBin% /s /i /y
xcopy %mcpReob%\* %desReob% /s /i /y
xcopy %mcpSrc%\* %destSrc% /s /i /y

7z u %zipPath%\%modName%.zip %destReob%\
7z u %zipPath%\%modName%.zip %destAss%\assets\
7z u %zipPath%\%modName%.zip %sourcePath%\mcmod.info
pause