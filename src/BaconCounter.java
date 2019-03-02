import java.util.*;

/**
 * Arpad Szell
 * Andreas Ährlund-Richter
 */

/**
 *
 *
 * <img src= https://oracleofbacon.org/images/Kevin_Bacon.jpg >
 *
 * Testfallsexemplets resonemang:
 Använder filen 'actors.list'
 Extraherar dessa skådespelare:

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

 PREDICTION:
 Bacon Kevin -> Xander Berkely -> Earl Boen.

 Lägger till:
 "----			------"

 TEST 1:
 Time measurements at 40,000 to
 10 sek 41700
 10 se 41800
 10 sek 21900
 20 sek 42000
 10 sek 42400-42500
 5 sek 424900.424900
 5 sek 43300-43400
 8 sek 43900-4000
 mean: 9,75 sek

 Time measurements at 80,000
 2 sek 80500-80600
 4 sek 81000-81100
 8 sek 81500-81600
 10 sek 81900-82000
 4 sek 82400-824100
 2 sek 85600-85700
 mean: 5 sek

 Time measurements at 1002,000
 1002,000


RESULTS:
  [Bacon, Kevin (I), Benedict, Paul, Boen, Earl]

 Confirmation via google:
 Paul Benedict and Boen Earl -> "The_Man_with_Two_Brains"
 Paul Benedict and Kevin Bacon -> "2009 The 61st Primetime Emmy Awards (TV Special) Himself - Nominee & Presenter"

 */

/*
Idee Program:
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
         1. Ta bort skådisens matchande film.
         2. Gör lista med alla skådisar i film
         3. Lägg till alla skådisar i alla skådisars entry i
            "actorsColleagues".

 Motivation:
    Mindre minne för "actorsMovies", färre skådisar
    och färre movies hos skådisar att söka igenom!
 */
/**
 * BaconCounter
 * class that holds a baconReader,
 *
 *
 */
public class BaconCounter {


    BaconReader baconReader;
    //Actor, Movies
    HashMap<String, HashSet<String>> actorsMovies = new HashMap<>();
    HashMap<String, HashSet<String>> actorsColleagues = new HashMap<>();

    public static void main(String[] args) {
        new BaconCounter().run();
    }

    public void run() {

        try {
            baconReader = new BaconReader("largerTestData.txt");
            BaconReader.Part current = null;
            String movie = null;
            HashSet<String> movieSet = new HashSet<>();
            String previousActor = null;
            long counter = 0;

            //Todo: fix this or whatever
            while (           counter < 1                  ) {//Not EOF
                current = baconReader.getNextPart();

                if(current == null){ //When EOF
                    counter++;
                    if (movie != null) {
                        movieSet.add(movie);
                        movie = null;
                    }
                    if (previousActor != null) {
                        actorsMovies.put(previousActor, movieSet);
                        movieSet = new HashSet<>();
                    }
                 }

                if(current != null){
                if (current.type == BaconReader.PartType.INFO) {
                    continue;
                }
                if (current.type == BaconReader.PartType.TITLE
                        || current.type == BaconReader.PartType.NAME) {
                    if (movie != null) {
                        movieSet.add(movie);
                        movie = null;
                    }
                    if(current.type == BaconReader.PartType.TITLE)
                       movie = current.text;
                }
                if (current.type == BaconReader.PartType.YEAR) {
                    movie += current.text;
                }
                if (current.type == BaconReader.PartType.ID) {
                    movie += current.text;
                }
                if (current.type == BaconReader.PartType.NAME) {
                    if (previousActor != null) {
                        actorsMovies.put(previousActor, movieSet);
                        movieSet = new HashSet<>();
                    }
                    previousActor = current.text;
                }
             }

            }
            System.out.println(counter);
        System.out.println(actorsMovies.size());
            baconReader.close();
        } catch (java.io.IOException jO) {
            System.err.print("OUFF");
        }

       buildActorsColleagues();
       System.out.println(breadthFirstBacon("Bacon, Kevin (I)", "Boen, Earl"));

    }

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
            if(counter % 100 == 0)
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
         /*
    1015445:Bacon, Kevin (I)	12th Annual Screen Actors Guild Awards (2006) (TV)  [Himself]
1015965:Bacon, Kevin (II)	Behind the Scene (2011)  [Grip #1]  <8>
     */
        /*
        Num lines:
         22667478 actors.list
         Command to create a reasonable smaller list
         eduroam-10-200-28-219:BaconNumber AAR$ head -n 50000 actors.list | cat >> smallActor.txt
         */
        //'The Welder' Phinney, Frank'

    }

    private void addNotSelf(String castMember, HashSet cast){
        HashSet<String> cleanCast = new HashSet<>();
        Iterator<String> castIterator = cast.iterator();

        //todo should remove myself with
        //hash-function,
        //THEN just addAll to list!
        //The loop is unecessary!
        while(castIterator.hasNext()){
             String nextActor = castIterator.next();
            if(!nextActor.equals(castMember)) {
                cleanCast.add(nextActor);
            }
        }
        if(actorsColleagues.containsKey(castMember)){
            actorsColleagues.get(castMember).addAll(cleanCast);
        }else{
            actorsColleagues.put(castMember,cleanCast);
        }
    }

    public List<String> breadthFirstBacon(String start, String end) {

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
                    return shortestPath(parentIds, start, end);
                }
            }
        }

        return shortestPath(parentIds, start, end);
    }

    private List<String> shortestPath(HashMap<String, String> parents, String start, String end) {
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

