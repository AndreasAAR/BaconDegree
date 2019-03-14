
import java.util.*;

/**
 * @author Arpad Szell
 * @author Andreas Ährlund-Richter
 * @version 1

@Todo Kommentarer på både Svenska och Engelska, uppdateras senare!

<p>
Sammanfattning Program:
        1. Läser fil:
        1.a) rad för rad:
        Bygger hashmap, "actorsMovies".
        2. Initiera "actorsColleagues"
        3. Itererar "actorsMovies":
            3.a) Itererar filmer
            3.a.1
        Frågar actors movie-hash om film.
            2.a.2
        Om matchar:
            2.a.2.1. Ta bort skådisens matchande film.
            2.a.2.2. Gör lista med alla skådisar i film
            2.a.2.3. Lägg till alla skådisar i alla skådisars entry i
        "actorsColleagues".
        4. Gör Breadth-first search,
            returnerar shortest path med skådespelarnamn.

        Motivation:
        Mindre minne för "actorsMovies", färre skådisar
        och färre movies hos skådisar att söka igenom!

 </p>
<p>
 * TESTER:
 *
 * <img src= https://oracleofbacon.org/images/Kevin_Bacon.jpg >
 *
 * Testfall 1
 *
 * smallerTest file with faked data.
 * Worked as a prototype.
 *
 *
 * Testfall 2
 *
 * Testfallsexemplets resonemang:
 Använder filen 'actors.list',
 subset på 120 000 skådespelare, i ett intervall
 som innehåller 3 skådespelare
 på B som garanterat är kopplade.

 Bacon Kevin (Bacon nr 0)
 "Apollo 13
 Xander Berkeley (Bacon nr 1)
 "Apollo 13
 "Terminator  2
 Earl Boen (Bacon nr 2)
 "Terminator


 Intervall som används:
 Intervall från Bacons nr
 till Boen Ken(Earl boens granne)
 1015445:Bacon, Kevin (I)
 2050082:Boen, Earl
 2050560:Boen, Ken
 Radnummer osv plockade i terminalen med
 grep, hittade med grep på rader börjar med "B"(^B),
 med -n option för att se nummret på raderna.

 Lägger till första rad för funktionell BaconReader:
 "----			------"


 PREDICTION:
 Bacon Kevin -> Xander Berkely -> Earl Boen.

 TEST 2:
 Time measurements at 40,000 to (ca 88% left)
 10 sek 41700
 10 se 41800
 10 sek 21900
 20 sek 42000
 10 sek 42400-42500
 5 sek 424900.424900
 5 sek 43300-43400
 8 sek 43900-4000
 mean: 9,75 sek

 Time measurements at 80,000 ( ca 33% left)
 2 sek 80500-80600
 4 sek 81000-81100
 8 sek 81500-81600
 10 sek 81900-82000
 4 sek 82400-824100
 2 sek 85600-85700
 mean: 5 sek

 Time measurements at 1002,000 (10% left)
 mean 2 sek

Took approximately 2hours to finish.

Halfing at 33% resp 10%, exponential in "wrong direction".
 Conclusion probably a O(n^2) algorithm.

RESULTS:
  [Bacon, Kevin (I), Benedict, Paul, Boen, Earl]

 Confirmation via google:
 Paul Benedict and Boen Earl -> "The_Man_with_Two_Brains"
 Paul Benedict and Kevin Bacon -> "2009 The 61st Primetime Emmy Awards (TV Special) Himself - Nominee & Presenter"
</p>
*/

/**
 * BaconCounter
 * class that holds a baconReader,
 * HashMap för Actors and set of their movies.
 * Has methods for filling HashMap and
 * searching for Bacon-Count, and shortest-path of an actor(coded),
 * to Kevin-Bacon.
 *
 * @actorsMovies
 * HashMap with Actornames and ID, and a value of a set,
 * with every movie the actor has starred in.
 *
 * @actorsColleagues
 * HashMap with every Actor and their connected Actors
 * via common movie appearances.
 *
 *
 */

public class BaconCounter {

    BaconReader baconReader;
    //Actor, Movies
    HashMap<String, HashSet<String>> actorsMovies = new HashMap<>();
    HashMap<String, HashSet<String>> actorsColleagues = new HashMap<>();


    public static void main(String[] args) {
        new BaconCounter().buildActorsMovies();
    }


/**
 *
 * How it works:
 * Iterates file via baconReader.
 * When name part is discovered, if a previous actor exists,
 * and previous movies exist in movieSet, we load those into
 * actorsMovies Hashmap in previousActor name-key position.
 * If discovers a movie title, adds to movieSet.
 *
 * @return nothing, creates actorsMovies.
 */
    public void buildActorsMovies() {

        try {
            baconReader = new BaconReader("actors.list");
            BaconReader.Part current = null;
            StringBuilder movie = new StringBuilder();
            HashSet<String> movieSet = new HashSet<>();
            StringBuilder previousActor = null;
            long counter = 0;

            //Todo: fix this or whatever
            while (           counter < 1                  ) {//Not EOF
                current = baconReader.getNextPart();

                if(current == null){ //When EOF
                    counter++;
                    if(counter % 1000 == 0 ){
                        System.out.println(counter);
                    }
                    if (movie.length() != 0) {
                        movieSet.add(movie.toString());
                        movie = new StringBuilder();
                    }
                    if (previousActor != null) {
                        actorsMovies.put(previousActor.toString(), movieSet);
                        movieSet = new HashSet<>();
                    }
                 }

                if(current != null){
                if (current.type == BaconReader.PartType.INFO) {
                    continue;
                }
                if (current.type == BaconReader.PartType.TITLE
                        || current.type == BaconReader.PartType.NAME) {
                    if (movie.length() != 0) {
                        movieSet.add(movie.toString());
                        movie = new StringBuilder();
                    }
                    if(current.type == BaconReader.PartType.TITLE)
                       movie = new StringBuilder().append(current.text);
                }
                if (current.type == BaconReader.PartType.YEAR) {
                    movie.append(current.text);
                }
                if (current.type == BaconReader.PartType.ID) {
                    movie.append(current.text);
                }
                if (current.type == BaconReader.PartType.NAME) {
                    if (previousActor != null) {
                        actorsMovies.put(previousActor.toString(), movieSet);
                        movieSet = new HashSet<>();
                    }
                    previousActor = new StringBuilder(current.text);
                }
             }

            }
            System.out.println(counter);
        System.out.println(actorsMovies.size());
            baconReader.close();
        } catch (java.io.IOException jO) {
            System.err.print("OUFF");
        }
        System.out.println("Survived actorsMovies");
          //buildActorsColleagues();
         //  System.out.println(shortestPath("Bacon, Kevin (I)", "Boen, Earl"));

    }


    /**
     * How it works
     *
     *
     * @return Returns nothing, but loads actorsColleagues
      */
    public void buildActorsColleagues(){

        int counter = 0;
        HashSet<String> cast = new HashSet<>();
        Iterator <Map.Entry<String,HashSet<String>>> actorIterator = actorsMovies.entrySet().iterator();
        Iterator <Map.Entry<String,HashSet<String>>> otherActorsIterator;
        String actorName;
        HashSet actorFilmography = new HashSet();
        String movieName;
        String otherActorName;
        while(actorIterator.hasNext()) {
            counter++;
            if(counter % 10 == 0)
                System.out.println(counter);
            actorName = actorIterator.next().getKey();
            actorFilmography = actorsMovies.get(actorName);
            if(actorFilmography != null && !actorFilmography.isEmpty()){
              Iterator <String> movieIterator = actorFilmography.iterator();
              while(movieIterator.hasNext() ){
                  movieName = movieIterator.next();
                  otherActorsIterator = actorsMovies.entrySet().iterator();
                  while(otherActorsIterator.hasNext() ){
                      otherActorName = otherActorsIterator.next().getKey();
                      if(actorsMovies.get(otherActorName).contains(movieName)){
                          cast.add(otherActorName);
                      }
                  }
                  //todo remove "movieName" movie from actors in cast!
                  for(String castMember : cast){
                      addNotSelf(castMember,cast);
                  }
                  cast = new HashSet<>();
                  movieIterator.remove();
              }
            }
           actorIterator.remove();
      }
    }

    /**
     * @param castMember
     * For every castMember, this is the used name,
     * ensures that every actor does not add itself to its
     * colleagues.
     * @param cast
     * All the actors in a movie, added as colleagues,
     * as long as not same person as the actor to add colleagues to!
     * @return nothing, adds to actorsColleagues.
     */

    private void addNotSelf(String castMember, HashSet cast){
        HashSet<String> cleanCast = new HashSet<>();

        cleanCast.addAll(cast);
        cleanCast.remove(castMember);
        if(actorsColleagues.containsKey(castMember)){
            actorsColleagues.get(castMember).addAll(cleanCast);
        }else{
            actorsColleagues.put(castMember,cleanCast);
        }

    }

    /**
     * @param start
     * Actor selected to start from(coded for Kevin Bacon).
     * NullException cast if not in data!
     * @param end
     * Actor to end on(the one to go to, we coded Earl Boen).
     * NullException cast if not in data!
     * @return list with start and end actors, with connected
     * actors inbetween, in order.
     * If end == start, returns "[end]".
     *
     * Creates a list(workingNodeIds) used as a queue,
     * to iterate breadth-first through actorColleagues.
     * Saves a HashMap on every nodes previous node/parent.
     * Also makes sure you only have the closest parent first!
     * (rejects any new connections, or new parents to a node,
     * breadth-first always finds the closest parent-node to start!).
     *
     * Sends parentIds to help method shortestPath for "summary".
     *
     */

    public List<String> shortestPath(String start, String end) {

        if(!actorsColleagues.keySet().contains(start)){
            throw new  NullPointerException("Start-actor not in data!");
        }
        if(!actorsColleagues.keySet().contains(end)){
            throw new  NullPointerException("end-actor not in data!");
        }

        //Kid, Parent
        HashMap<String, String> parentIds = new HashMap<>();
        LinkedList<String> workingNodeIds = new LinkedList<>();

        //start has no parent
        parentIds.put(start, null);
        workingNodeIds.addLast(start);

        String currentNodeIds;
        while (!workingNodeIds.isEmpty()) {
            currentNodeIds = workingNodeIds.removeFirst();
            //Add last place in workingNodes = workingNodeIds.size
            workingNodeIds.addAll(workingNodeIds.size(), actorsColleagues.get(currentNodeIds));
            LinkedList<String> children = new LinkedList<>();
            children.addAll(actorsColleagues.get(currentNodeIds));
            String currentChildId = null;
            while (!children.isEmpty()) {
                currentChildId = children.removeFirst();
                if (!parentIds.containsKey(currentChildId)) {
                    parentIds.put(currentChildId, currentNodeIds);
                }
                if (currentChildId.equals(end)) {
                    //We can end early if end found before
                    //actorsColleagues is iterated fully through!
                    return shortestPath(parentIds, end);
                }
            }
        }

        return shortestPath(parentIds, end);
    }

    /**
     * @param parents
     * The first-found previous nodes of the nodes.
     * @param end
     * Node to start from, we end on start!
     *
     * How it works:
     * Starts at end(because end has a parent, start doesent),
     * works up in connections, until we find start(the only one
     * with a null parent).
     * All nodes are added at the front of the list.
     *
     * @return
     */

    private List<String> shortestPath(HashMap<String, String> parents, String end) {
        List<String> shortestPath = new LinkedList<>();
        shortestPath.add(0, end);
        String next = end;
        if (parents.get(end) != null) {
            while ((next = parents.get(next)) != null) {
                shortestPath.add(0, next);
            }
        }
        return shortestPath;

    }

}

