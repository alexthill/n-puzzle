# n-puzzle
Solving the n-puzzle problem in Java with the A* algorithm.


## Usage
Compile with maven
```
mvn package
```

Execute
```
java -classpath target/classes lu.idk.Main [puzzle] [heuristic]
```

Where `[puzzle]` is either the path to a valid puzzle file (see puzzle folder for examples) or a size for a random generated puzzle.

`[heuristic]` must be one of the implement heuristics.


## Heuristics
Following heuristics are implemented (in order of increasing efficiency)
- const: allways returns 0 (same as doing BFS).
- hamming: number of misplaced tiles.
- manhattan: sum of manhattan distances of misplaced tiles.
- pattern: creates a static pattern database and sums the partial pattern values (only on 3x3 and 4x4 puzzles).


## Pattern Database
On first run with the pattern heuristic the program will create the database and save it to a file in the current folder. This will take some time (about 30 seconds on modern hardware for 4x4 puzzle). On later runs it will reuse this database and be a lot faster.

For 4x4 puzzle it will do 5-5-5 pattern partitioning. This is not the best, but the fastd and smallest 3-way partitioning to calculcate.

## Target Solution
The progam tries to reach the unconventional snake solution. The code could be easily adopted to target a different solution.

3x3 snail
```
1 2 3
8 0 4
7 6 5
```

4x4 snail
```
 1  2  3  4
12 13 14  5
11  0 15  6
10  9  8  7
```
