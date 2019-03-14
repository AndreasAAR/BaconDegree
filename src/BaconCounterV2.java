import javafx.collections.transformation.SortedList;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.io.LineNumberReader;

/**
 * @author Arpad Szell
 * @author Andreas Ährlund-Richter
 * @version 1

 @Todo Kommentarer på både Svenska och Engelska, uppdateras senare!

 <p>


 Motivation:
 Mindre minne för "actorsMovies", färre skådisar
 och färre movies hos skådisar att söka igenom!



 Lägger till första rad för funktionell BaconReader:
 "----			------"



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

public class BaconCounterV2 {

    BaconReader baconReader;
    //Actor, Movies
    //HashMap<String, HashSet<String>> moviesActors = new HashMap<>();
    HashMap<String, HashSet<String>> actorsMovies = new HashMap<>();
    //"Earl, Boen" in testdata
    //"Wynorski, Jim" is god for big dataset, low down in dataset.
    final String  START = "Wynorski, Jim";
    final String  GOAL = "Bacon, Kevin (I)";
    final String FILENAME = "actors.list";
    int numLines = 1;
    long elapsedTimeMillis;

    public static void main(String[] args) {

        BaconCounterV2 bc = new BaconCounterV2();
        bc.run();
    }

    public void run(){
        elapsedTimeMillis = System.currentTimeMillis();
        setFilelines();
        buildTables();
        if(!actorsMovies.keySet().contains(GOAL) || !actorsMovies.keySet().contains(START)){
            System.err.println("Given actor not in dataset!");
            return;
        }else{
            System.out.println("Breadth-First Search!");
        }
        System.out.println(shortestPathV2( START,GOAL));
        elapsedTime();

    }

    private void elapsedTime(){
        float elapsedTimeMin = (System.currentTimeMillis() - elapsedTimeMillis)/(60*1000F);
        int elapsedMin = (int)elapsedTimeMin;
        int elapsedSec = (int)Math.floor(((elapsedTimeMin)-(elapsedMin+0.0))*60);

        System.out.println("Elapsed time \n " +  "Minutes: " + elapsedMin + " Seconds: " + elapsedSec );
    }

    private void setFilelines(){
        int linenumber = 0;
        try{

            File file = new File(FILENAME);

            if(file.exists()){

                FileReader fr = new FileReader(file);
                LineNumberReader lnr = new LineNumberReader(fr);
                linenumber = 0;
                String line = "";
                while (line != null){
                    if(line.matches(".*[a-zA-Z]+.*"))
                    linenumber++;
                    line = null;
                    line = lnr.readLine();
                }

                lnr.close();

            }else{
                System.out.println("File does not exists!");
            }

        }catch(IOException e){
            e.printStackTrace();
        }
        numLines = linenumber;
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

    private BaconReader.Part readFullMovie(BaconReader reader, StringBuilder movie, BaconReader.Part current){
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
        }catch(java.io.IOException jo){
            System.err.print("Movie-Reading OUFF");
        }
        return current;
    }

    private void addtoMap(HashMap<String, HashSet<String>> hmap,String key, String val){
        if(hmap.containsKey(key)){
            hmap.get(key).add(val);
        }else{
            HashSet<String> temp = new HashSet<>();
            temp.add(val);
            hmap.put(key,temp);
        }
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
            while ( loop ) {//Not EOF
                linesRead++;
                percentDone = (int)Math.floor((100.0*( (linesRead+0.0) / (numLines+0.0))));
                if( percentDone != 0 && (linesRead % (numLines/10) == 0 )){
                    System.out.println(percentDone + "%  of lines done");
                }

                if(current == null || current.type != BaconReader.PartType.TITLE)
               current = baconReader.getNextPart();
                    if ( current != null && current.type == BaconReader.PartType.TITLE) {
                        currentMovie.append(current.text);
                        current = readFullMovie(baconReader,currentMovie,current);
                        addtoMap(actorsMovies,currentActor.toString(),currentMovie.toString());
                        //addtoMap(moviesActors,currentMovie.toString(),currentActor.toString());
                        currentMovie = new StringBuilder();
                    }
                    if (current != null && current.type == BaconReader.PartType.NAME) {
                        currentActor = new StringBuilder();
                        currentActor.append(current.text);
                    }

                if(current == null){
                    //lastparts
                        loop = false;
                    }
                }
            addtoMap(actorsMovies,currentActor.toString(),currentMovie.toString());
            //addtoMap(moviesActors,currentMovie.toString(),currentActor.toString());
            System.out.println("# of Actors " + actorsMovies.size());
           // System.out.println("# of Movies: "+ moviesActors.size());
            baconReader.close();

        } catch (IOException jO) {
            System.err.print("OUFF");
        }
        }


    public String shortestPathV2(String start, String goal){

        LinkedList<String> workingQueue = new LinkedList<>( );
        HashMap<String,String> childParent =  new HashMap<>();

        if(start.equals(goal)){
            return start;
        }
        workingQueue.add(start);
        childParent.put(start,null);
        int actorsChecked = 1;
        int numActors = actorsMovies.size();
        System.out.println("Expanding actor connections");
        int actorsRead = 0;
        int percentDone;
        while(!workingQueue.isEmpty()){
            String currentActor = workingQueue.remove();

            actorsRead++;
            percentDone = (int)Math.floor((100.0*( (actorsRead+0.0) / (numActors+0.0))));

            if( (actorsRead % (1+numActors/10) == 0 )){
                System.out.println(percentDone + "%  of actors checked, minutes past: "  );
                elapsedTime();
            }

            addToactorsColleagues(currentActor,goal,childParent,workingQueue);
            if(childParent.containsKey(goal)){
                workingQueue.clear();
            }
        }

        StringBuilder actorsConnections = new StringBuilder();
        LinkedList<String> path = (createPath(childParent,start,goal));

/*
        String prevActor = goal;
        for(String actor: path){
            if(actor != goal || actor != null){
                String commonMovie = commonMovie(actor,prevActor);
                System.out.println(prevActor + " has common movie with" + actor + "= " + commonMovie);
            }
            prevActor = actor;
        }
*/

        return actorsConnections.toString();
    }

    private String commonMovie(String child, String parent){
        HashSet<String> childFilmography = new HashSet<>();
        HashSet<String> parentFilmography = new HashSet<>();
        childFilmography = actorsMovies.get(child);
        parentFilmography = actorsMovies.get(parent);
        System.out.println(childFilmography.retainAll(parentFilmography));
        System.out.println(childFilmography);

        String returnString = "";
        if(childFilmography.toArray().length > 0)
            returnString = ""+childFilmography.toArray()[0];
        return ""+ returnString;

    }

    public void addToactorsColleagues(String currentActor,String goal,
                                      HashMap<String,String>childParent,
                                      LinkedList<String> workingQueue){

        HashSet<String> otherActors =  new HashSet<>();
        HashSet<String> filmography = actorsMovies.get(currentActor);
        otherActors.addAll(actorsMovies.keySet());
        otherActors.remove(currentActor);
        for(String otherActor : otherActors){
                HashSet otherFilmography = actorsMovies.get(otherActor);
                if(otherFilmography != null && !Collections.disjoint(otherFilmography,filmography)
                        && !childParent.containsKey(otherActor)){
                    childParent.put(otherActor,currentActor);
                    workingQueue.addLast(otherActor);
                    if(otherActor.equals(goal)){
                        return;
                    }
                }
            }
    }

    //Använd
    public LinkedList<String> createPath(HashMap<String,String> childParent,String start,String goal){

        LinkedList<String> path = new LinkedList<>();
        int actorsChecked = 0;
        int actorNum = childParent.size();
        path.add(goal);
        String currentChild = goal;
        String currentParent = childParent.get(currentChild);
        while(actorsChecked <= actorNum && currentParent != null){
              currentChild = currentParent;
             currentParent = childParent.get(currentChild);
            path.addFirst(currentChild);
            System.out.println(path);
              actorsChecked++;
        }
        return path;
    }

}

