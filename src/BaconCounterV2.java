

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.io.LineNumberReader;

/**
 * @author Arpad Szell
 * @author Andreas Ährlund-Richter
 * @version 1
 * @Todo//1. Kommentarer på både Svenska och Engelska, uppdateras senare!
 * Todo// 2. är också att pröva att implementera både Bi-Directional search,
 * Todo// 3. samt att experimentera med preferentiell expansion av noder a la djikstra(heuristic num movies),
 * Todo//
 * todo// 4. utför huffman-komprimering av datat till minimala storlekar av strängar.
 * Todo// 5. https://www.researchgate.net/publication/220812751_A_Divide_and_Conquer_Bidirectional_Search_First_Results
 * Todo// Sparar detta minne? Kanske värt ett försök.
 * Todo// 6. Byt till LinkedList med HashSet ist för HashMap! (funkar inte med hashmap på svag dator)
 * <p>
 * <p>
 * Motivation:
 * Mindre minne för "actorsMovies", färre skådisar
 * och färre movies hos skådisar att söka igenom!
 * <p>
 * <p>
 * <p>
 * Lägger till första rad för funktionell BaconReader:
 * "----			------"
 * <p>
 * <p>
 * <p>
 * /**
 * BaconCounter
 * class that holds a baconReader,
 * HashMap för Actors and set of their movies.
 * Has methods for filling HashMap and
 * searching for Bacon-Count, and shortest-path of an actor(coded),
 * to Kevin-Bacon.
 * @actorsMovies HashMap with Actornames and ID, and a value of a set,
 * with every movie the actor has starred in.
 * @actorsColleagues HashMap with every Actor and their connected Actors
 * via common movie appearances.
 */

/*
Mapping actors to movies
9%  of lines done
19%  of lines done
29%  of lines done
39%  of lines done
49%  of lines done
59%  of lines done
69%  of lines done
79%  of lines done
89%  of lines done
99%  of lines done
# of Actors 2663063
Breadth-First Search!
Expanding actor connections
[via Balto1995, Cummings, Jim (I), Bacon, Kevin (I)]
[via The Further Adventures of SuperTed1989Texas Is Mine (#1.5), Boen, Earl, via Balto1995, Cummings, Jim (I), Bacon, Kevin (I)]

Elapsed time
 Minutes: 3 Seconds: 15
 */

public class BaconCounterV2 {

    BaconReader baconReader;
    //Actor, Movies
    //HashMap<String, HashSet<String>> moviesActors = new HashMap<>();
    HashMap<String, HashSet<String>> actorsMovies = new HashMap<>();
    LinkedList<ActorMovieNode> actorsMoviesList = new LinkedList<>();

    //"Earl, Boen" in testdata
    //"Wynorski, Jim" is god for big dataset, low down in dataset.
    final String START = "Boen, Earl";
    final String GOAL = "Bacon, Kevin (I)";

    final String FILENAME = "smallTestData.txt";
    int numLines = 1;
    long elapsedTimeMillis;

    public static void main(String[] args) {
        BaconCounterV2 bc = new BaconCounterV2();
        bc.run();
    }

    public void run() {
        elapsedTimeMillis = System.currentTimeMillis();
        setFilelines();
        /*
        buildActorMovieList();
        */

        buildTables();

        if (!actorsMovies.keySet().contains(START)) {
            System.err.println("Given actor not in dataset! " + START);
            return;
        }if (!actorsMovies.keySet().contains(GOAL)) {
            System.err.println("Given actor not in dataset! " + GOAL);
            return;
        } else {
            System.out.println("Time elapsed mapping: ");
            elapsedTime();
            System.out.println("Breadth-First Search!");
        }

        System.out.println(shortestPathBidirectional(START, GOAL)); //24 sec for 1 deg bacon (4 deg)
        //System.out.println(shortestPathV2(START,GOAL));  // ca 2 min!   (4 deg)

        elapsedTime();

    }

    private static class ActorMovieNode{
        HashSet<String> movies;
        String name;
    }

    public void addtoList(LinkedList<ActorMovieNode> actorMovieList, String actorName,HashSet<String> movieSet){
            //todo add compression here, huffman!
            ActorMovieNode newNode = new ActorMovieNode();
            newNode.movies = movieSet;
            newNode.name = actorName;
            actorMovieList.add(newNode);
    }

public void buildActorMovieList(){

        try {
            baconReader = new BaconReader(FILENAME);
            BaconReader.Part current = null;
            BaconReader.Part previous = null;
            StringBuilder currentActor = new StringBuilder();
            StringBuilder currentMovie = new StringBuilder();
            HashSet<String> currentMovies = new HashSet<>();

            Integer linesRead = 0;
            boolean loop = true;
            System.out.println("Filesize: " + numLines);
            System.out.println("Mapping actors to movies");
            int percentDone;
            while (loop) {//Not EOF

                previous = current;
                current = baconReader.getNextPart();



                if(current != null){


                if(  (current.type == BaconReader.PartType.TITLE ||
                        current.type == BaconReader.PartType.NAME)){

                    percentDone = (int) Math.floor((100.0 * ((linesRead + 0.0) / (numLines + 0.0))));
                    if ( percentDone != 0 && (linesRead % (numLines / 10) == 0)) {
                        System.out.println(percentDone + "%  of lines done");
                    }

                    if(previous == null && current.type  == BaconReader.PartType.NAME ){
                        linesRead++;
                    }
                    if(previous != null){
                       if(current.type == BaconReader.PartType.TITLE && previous.type != BaconReader.PartType.NAME)
                           linesRead++;
                        if(current.type == BaconReader.PartType.NAME)
                            linesRead++;
                    }
                }

                if ( current.type == BaconReader.PartType.TITLE) {
                    if(currentMovie.length() != 0)
                        currentMovies.add(currentMovie.toString());
                    currentMovie.delete(0,currentMovie.length());
                    currentMovie.append(current.text);
                }

                if ( (current.type == BaconReader.PartType.YEAR || current.type == BaconReader.PartType.ID )) {
                    if(currentMovie.length() != 0)
                        currentMovie.append(current.text);
                }

                if ( current.type == BaconReader.PartType.NAME) {
                    if(currentActor.length() != 0) {
                        addtoList(actorsMoviesList, currentActor.toString(), currentMovies);
                        currentMovies = new HashSet<>();
                    }
                    currentActor.delete(0,currentActor.length());
                    currentActor.append(current.text);
                }

                }else {
                    //lastparts
                    loop = false;
                }
                if(actorsMoviesList.size() > 2114020){
                    break;
                }

            }
            addtoList(actorsMoviesList, currentActor.toString(), currentMovies);
            //addtoMap(moviesActors,currentMovie.toString(),currentActor.toString());
            System.out.println("# of Actors " + actorsMoviesList.size());
            // System.out.println("# of Movies: "+ moviesActors.size());
            baconReader.close();

        } catch (IOException jO) {
            System.err.print("OUFF");
        }
    }

    private void addtoMap(HashMap<String, HashSet<String>> hmap, String key, String val) {
        if (hmap.containsKey(key)) {
            hmap.get(key).add(val);
        } else {
            HashSet<String> temp = new HashSet<>();
            temp.add(val);
            hmap.put(key, temp);
        }
    }
    private void elapsedTime() {
        float elapsedTimeMin = (System.currentTimeMillis() - elapsedTimeMillis) / (60 * 1000F);
        int elapsedMin = (int) elapsedTimeMin;
        int elapsedSec = (int) Math.floor(((elapsedTimeMin) - (elapsedMin + 0.0)) * 60);

        System.out.println("Elapsed time \n " + "Minutes: " + elapsedMin + " Seconds: " + elapsedSec);
    }

    private void setFilelines() {
        int linenumber = 0;
        try {

            File file = new File(FILENAME);

            if (file.exists()) {

                FileReader fr = new FileReader(file);
                LineNumberReader lnr = new LineNumberReader(fr);
                linenumber = 0;
                String line = "";
                while (line != null) {
                    if (line.matches(".*[a-zA-Z]+.*"))
                        linenumber++;
                    line = lnr.readLine();
                }

                lnr.close();

            } else {
                System.out.println("File does not exists!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        numLines = linenumber;
    }

    private BaconReader.Part readFullMovie(BaconReader reader, StringBuilder movie, BaconReader.Part current) {
        try {
            current = reader.getNextPart();
            while (current != null && current.type != BaconReader.PartType.TITLE
                    && current.type != BaconReader.PartType.NAME
                    ) {
                if (current.type != BaconReader.PartType.INFO) {
                    movie.append(current.text);
                }
                current = reader.getNextPart();
            }
        } catch (java.io.IOException jo) {
            System.err.print("Movie-Reading OUFF");
        }
        return current;
    }



    public void buildTables() {

        try {
            baconReader = new BaconReader(FILENAME);
            BaconReader.Part current = null;
            StringBuilder currentActor = new StringBuilder();
            StringBuilder currentMovie = new StringBuilder();
            Integer linesRead = 0;
            boolean loop = true;
            System.out.println("Filesize: " + numLines);
            System.out.println("Mapping actors to movies");
            int percentDone;
            while (loop) {//Not EOF
                linesRead++;
                percentDone = (int) Math.floor((100.0 * ((linesRead + 0.0) / (numLines + 0.0))));
                if (percentDone != 0 && (linesRead % (numLines / 10) == 0)) {
                    System.out.println(percentDone + "%  of lines done");
                }

                if (current == null || current.type != BaconReader.PartType.TITLE)
                    current = baconReader.getNextPart();
                if (current != null && current.type == BaconReader.PartType.TITLE) {
                    currentMovie.append(current.text);
                    current = readFullMovie(baconReader, currentMovie, current);
                    addtoMap(actorsMovies, currentActor.toString(), currentMovie.toString());
                    //addtoMap(moviesActors,currentMovie.toString(),currentActor.toString());
                    currentMovie = new StringBuilder();
                }
                if (current != null && current.type == BaconReader.PartType.NAME) {
                    currentActor = new StringBuilder();
                    currentActor.append(current.text);
                }

                if (current == null) {
                    //lastparts
                    loop = false;
                }
            }
            addtoMap(actorsMovies, currentActor.toString(), currentMovie.toString());
            //addtoMap(moviesActors,currentMovie.toString(),currentActor.toString());
            System.out.println("# of Actors " + actorsMovies.size());
            // System.out.println("# of Movies: "+ moviesActors.size());
            baconReader.close();

        } catch (IOException jO) {
            System.err.print("OUFF");
        }
    }


    public String shortestPathBidirectional(String start, String end) {

        LinkedList<String> workingQueueStart = new LinkedList<>();
        workingQueueStart.add(start);
        LinkedList<String> workingQueueEnd = new LinkedList<>();
        workingQueueEnd.add(end);
        //Both add, but one parent-child has to be reversed!
        HashMap<String, String> childParentStart = new HashMap<>();
        childParentStart.put(start, null);
        HashMap<String, String> childParentEnd = new HashMap<>();
        childParentEnd.put(end, null);
        String currentNode;
        StringBuilder matchNode = new StringBuilder();
        Boolean connected = false;
        while (connected == false && (!workingQueueStart.isEmpty() || !workingQueueEnd.isEmpty()) ) {
               if(!workingQueueStart.isEmpty()){
                 currentNode = workingQueueStart.remove();
                 connected = addToChildParentBi(currentNode,end,childParentStart,
                         childParentEnd,workingQueueStart,workingQueueEnd,matchNode);
               }
            if(connected == false) {
                if(!workingQueueEnd.isEmpty()) {
                    currentNode = workingQueueEnd.remove();
                    connected = addToChildParentBi(currentNode, start, childParentEnd,
                            childParentStart, workingQueueEnd,workingQueueStart, matchNode);
                }
            }
        }

        if(connected){
            StringBuilder actorsConnections = new StringBuilder();
            LinkedList<String> pathStart = new LinkedList<>();
            LinkedList<String> pathEnd = new LinkedList<>();
            System.out.println("match:" + matchNode);
            pathStart = createPath(childParentStart,matchNode.toString());
            System.out.println(pathStart.toString());
           // reverseParents(childParentEnd, matchNode.toString());
            pathEnd = createPath(childParentEnd,matchNode.toString());
            System.out.println(pathEnd.toString());
            return actorsConnections.toString();
        }
        return "found no connection";
    }
/*
    private void reverseParents(HashMap<String, String> childParent, String end) {
            HashMap<String, String> reversed =  new HashMap<>();

            int actorsChecked = 0;
            int actorNum = childParent.size();
            String currentChild = end;
            String currentParent = childParent.get(currentChild);
            while (actorsChecked <= actorNum && currentParent != null) {
                currentChild = currentParent;
                currentParent = childParent.get(currentChild);
                path.addFirst(currentChild);
                actorsChecked++;
            }
            return path;
        }
    }
*/


    //need unique connectionPath too, creating two lists combined in matchNode.
    public boolean addToChildParentBi(String currentActor, String thisEnd,
                                 HashMap<String, String> childParentThis,
                                      HashMap<String, String> childParentOther,LinkedList<String> workingQueueThis,
                                      LinkedList<String> workingQueueOther,StringBuilder matchNode) {
        HashSet<String> otherActors = new HashSet<>();
        HashSet<String> filmography = actorsMovies.get(currentActor);
        otherActors.addAll(actorsMovies.keySet());
        otherActors.remove(currentActor);
        otherActors.removeAll(childParentThis.keySet()); //remove already checked
        otherActors.addAll(childParentOther.keySet()); //add other sides nodes

        for (String otherActor : otherActors) {
            HashSet otherFilmography = actorsMovies.get(otherActor);
            boolean commonMovie = !Collections.disjoint(otherFilmography, filmography);
            if (otherFilmography != null && commonMovie) {
                if (otherActor.equals(thisEnd) || workingQueueOther.contains(otherActor) || childParentOther.containsKey(otherActor)) {
                    childParentThis.put(otherActor, currentActor);
                    matchNode.append(otherActor);
                    return true;
                }else{
                 childParentThis.put(otherActor, currentActor);
                 workingQueueThis.addLast(otherActor);
                }
            }
        }
        return false;
    }


    //Needs its own actorsColleagues

    public String shortestPathV2(String start, String end) {

        LinkedList<String> workingQueue = new LinkedList<>();
        HashMap<String, String> childParent = new HashMap<>();

        if (start.equals(end)) {
            return start;
        }
        workingQueue.add(start);
        childParent.put(start, null);
        int actorsChecked = 1;
        int numActors = actorsMovies.size();
        System.out.println("Expanding actor connections");
        int actorsRead = 0;
        int percentDone;
        while (!workingQueue.isEmpty()) {
            String currentActor = workingQueue.remove();

            actorsRead++;
            percentDone = (int) Math.floor((100.0 * ((actorsRead + 0.0) / (numActors + 0.0))));

            if ((actorsRead % (1 + numActors / 10) == 0) ) {
                System.out.println(percentDone + "%  of actors checked, minutes past: ");
                elapsedTime();
            }

            addToChildParent(currentActor, end, childParent, workingQueue);
            if (childParent.containsKey(end)) {
                workingQueue.clear();
            }
        }

        StringBuilder actorsConnections = new StringBuilder();
        LinkedList<String> path = (createPath(childParent, end));

        return path.toString();
    }

    public LinkedList<String> createPath(HashMap<String, String> childParent,  String end) {

        LinkedList<String> path = new LinkedList<>();
        int actorsChecked = 0;
        int actorNum = childParent.size();
        path.add(end);
        String currentChild = end;
        String currentParent = childParent.get(currentChild);
        while (actorsChecked <= actorNum && currentParent != null) {
            String commonMovie =  commonMovie(currentParent,currentChild);
            currentChild = currentParent;
            currentParent = childParent.get(currentChild);
            path.addFirst(currentChild);
            if(!commonMovie.equals(""))
               //path.addFirst("via " + commonMovie);
            actorsChecked++;
        }
        return path;
    }

    private java.lang.String commonMovie(String child, String parent) {
        HashSet<String> childFilmography = new HashSet<>();
        HashSet<String> parentFilmography = new HashSet<>();
        childFilmography = actorsMovies.get(child);
        parentFilmography = actorsMovies.get(parent);
        String returnString = "";

        if(childFilmography != null && parentFilmography != null)
        if( childFilmography.size() <= parentFilmography.size()) {
          returnString = theOneCommon(childFilmography, parentFilmography);
        }else{
            returnString = theOneCommon(parentFilmography,childFilmography);
        }

        return "" + returnString;

    }

    private String theOneCommon(HashSet<String> small, HashSet<String> big){
        for(String movie : small){
            if(big.contains(movie)){
                return movie;
            }
        }
        return "";
    }

    public void addToChildParent(String currentActor, String end,
                                 HashMap<String, String> childParent,
                                 LinkedList<String> workingQueue) {

        HashSet<String> otherActors = new HashSet<>();
        HashSet<String> filmography = actorsMovies.get(currentActor);
        otherActors.addAll(actorsMovies.keySet());
        otherActors.remove(currentActor);
        for (String otherActor : otherActors) {
            HashSet otherFilmography = actorsMovies.get(otherActor);
            if (otherFilmography != null && !Collections.disjoint(otherFilmography, filmography)
                    && !childParent.containsKey(otherActor)) {
                childParent.put(otherActor, currentActor);
                workingQueue.addLast(otherActor);
                if (otherActor.equals(end)) {
                    return;
                }
            }
        }
    }

    //Använd


}

