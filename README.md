# Puzzle_Solver

< How to Build >
1. Clone this repository.
1. Move to folder "puzzlename_OStype".
2. Put this commands.
 - Mac OS X - 
" gradle clean; gradle distZip; cd build; cd distributions; unzip PuzzleSolver.zip; cd PuzzleSolver; cd bin; cp ../../../../input.txt input.txt; cp ../../../../z3 z3; ./PuzzleSolver -i input.txt"
 - Windows -
" gradle clean; gradle distZip; cd build; cd distributions; unzip PuzzleSolver.zip; cd PuzzleSolver; cd bin; cp ../../../../input.txt input.txt; cp ../../../../z3.exe z3.exe; ./PuzzleSolver -i input.txt"

