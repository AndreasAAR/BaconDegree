# About BaconDegree2
The parser and reader-class is from my teacher at uni, Henrik Bergström.
Uses files and formats from:
ftp://ftp.fu-berlin.de/pub/misc/movies/database

### Purpouse
Optimize reading large text files (1gig plus),
and create a graph search connection between two nodes.
The dataset used is IMDB actors, connections are made for common movies
and colleagues. 
https://oracleofbacon.org/ provides more info.

### What can you use it for?
Its mainly a fun example of algorithms and reading larger files in Java.
Most likely Python, Perl, R and C++ is more relevant for this type of work,
but this was a fun school-exercise that just expanded more than I thought when I started writing it.

## Progress

### V1
Used a very innefficient 
"Build-Graph first"-technique.
It took it about 3 hours to connect a second-degree Bacon Connection.

### V2
Builds the graph as it searches for connected actors.
Maps about 30%-50% of actors each run. 

### V3
Uses a Bi-Directional search that expands nodes both from goal, 
and from 
The presentation of connected actors is a little messy, but its a lot quicker, about 75% earlier running time.
From ca 2 min to find a 4-degree bacon connection, to 24 seconds. 

## Future Improvements

### 1. Memory Efficiency
Implement writing graph directly from file, making runnable on weaker processors.
Both "wave-fronts" of Bi-Directional search expanded while reading.
At the moment my Macbook but not my Windows stationary can manage reading my 1.2gig "actors.list" file.
Implement "Bi-Directional Divide and Conquer by Korf (1999)
https://www.semanticscholar.org/paper/A-Divide-and-Conquer-Bidirectional-Search%3A-First-Korf/c250bd477f966b15b0296ab7c2e01a4a8928c279
if results are correct increase in time only slight, great decrease in temporary memory use.

### 2. Internet access
Not having to access the file on secondary storage will make it easier for other users
to use, and to run the program on any platform.

### 3. UI
Making the program runnable through a user-interface would be great.
The data-set used is typical for fun endeavours, but it could be interesting to create a 
app measuring algorithmics efficiencies too "run algo 1, memory used X, time passed Y". 
We will see, maybe it can be an educational tool(run in advanced mode),
as well as a fun game for guessing the connection-level of different actors.
