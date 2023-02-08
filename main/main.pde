/*
  Evolution Simulator
  Author: Beaux Consunji
  Description: 
  Date: 
*/

// import modules
import shiffman.box2d.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.joints.*;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.*;


// set up the global constants
Box2DProcessing engine;
final float GRAVITY      = -50;
final int   NODE         = 0;
final int   BOX          = 1;
final int   FRAME_RATE   = 60;
final float METER        = 100.0;
final int   TIME_LIMIT   = 15;
final int   GEN_SIZE     = 50;
final float MUTATION     = 0.05;
final color DEF_STROKE   = color(84, 75, 59);
final int   CONFETTI     = 100;

// declare global variables;
JSONObject jsonFile;
ArrayList<Creature> creatures;
ArrayList<int[]> generations;
ArrayList<Confetti> confetti;
ArrayList<String> lessons;
Ground ground;
Vec2 STARTING_POINT;
PFont font;
Button startBtn, loadBtn, slowBtn, fastBtn, skipBtn, sortBtn, killBtn, nextBtn, generateBtn, finishBtn, nextLessonBtn, saveBtn;
PImage groundImg, flagImg, slowImg, fastImg, trophyImg, skipImg;
Graph graph;
Test pretest, posttest;

int state;
int currLesson = 0;
int currCreature = 0;
int currGen = 0;
int renderSpeed = 1;
int nxtRenderSpd = 1;
float maxFitness = 0;
float minFitness = 0;
boolean displayMsg = false;
String msgText = "";

void setup() {
  size(1200, 800);
  frameRate(FRAME_RATE);
  STARTING_POINT = new Vec2(100, height - 200);
  
  // load assets
  groundImg = loadImage("assets/ground.jpg");
  flagImg = loadImage("assets/flag.png");
  slowImg = loadImage("assets/fast backward.png");
  fastImg = loadImage("assets/fast forward.png");
  trophyImg = loadImage("assets/trophy.png");
  skipImg = loadImage("assets/skip forward.png");
  
  font = createFont("Nunito Bold", 32);
  textFont(font);
  
  // initialize Box2D Physics Engine
  engine = new Box2DProcessing(this);
  engine.createWorld();
  engine.setGravity(0, GRAVITY);
  
  // initialize ArrayLists
  
  creatures = new ArrayList<Creature>();
  generations = new ArrayList<int[]>();
  
  // initialize global variables
  
  ground = new Ground();
  
  pretest = new Test("Pre-test");
  posttest = new Test("Post-test");
  
  startBtn = new Button("Start New", new Vec2(width/2 - 120, 350));
  startBtn.setFontSize(50);
  startBtn.setPadding(30);
  
  loadBtn = new Button("Load File", new Vec2(width/2 - 100, 470));
  loadBtn.setFontSize(50);
  loadBtn.setPadding(30);
  
  slowBtn = new Button(slowImg, new Vec2(width - 270, 50), new Vec2(50, 50));
  fastBtn = new Button(fastImg, new Vec2(width - 200, 50), new Vec2(50, 50));
  skipBtn = new Button(skipImg, new Vec2(width - 130, 50), new Vec2(50, 50));
  
  sortBtn = new Button("Sort", new Vec2(width - 200, height - 100));
  sortBtn.setFontSize(40);
  sortBtn.setPadding(30);
  
  killBtn = new Button("Kill", new Vec2(width - 200, height - 100));
  killBtn.setVisibility(false);
  killBtn.setFontSize(40);
  killBtn.setPadding(30);
  
  nextBtn = new Button("Next", new Vec2(width - 200, height - 100));
  nextBtn.setVisibility(false);
  nextBtn.setFontSize(40);
  nextBtn.setPadding(30);
  
  generateBtn = new Button("Next generation", new Vec2(width - 500, height - 100));
  generateBtn.setVisibility(false);
  generateBtn.setPadding(30);
  
  finishBtn = new Button("Finish", new Vec2(width - 278, height - 100));
  finishBtn.setVisibility(false);
  finishBtn.setPadding(30);
  
  saveBtn = new Button("Save", new Vec2(width - 170, height - 100));
  saveBtn.setVisibility(false);
  saveBtn.setPadding(30);
  
  nextLessonBtn = new Button("Next", new Vec2(width - 200, height - 120));
  nextLessonBtn.setVisibility(false);
  nextLessonBtn.setFontSize(40);
  nextLessonBtn.setPadding(30);
  
  graph = new Graph(new Vec2(100, 50), new Vec2(1000, 600), GEN_SIZE);
  
  // initialize confetti
  
  confetti = new ArrayList<Confetti>();
  for (int i = 0; i < CONFETTI; i++) {
    confetti.add(new Confetti(new Vec2(20, -50)));
    confetti.add(new Confetti(new Vec2(width-20, -20)));
  }
  
  // initialize lessons
  
  lessons = new ArrayList<String>();
  lessons.add("What is evolution?\nThe basic premise behind evolution is that populations of an organism change over time. Charles Darwin, a 19th-century naturalist, proposed that this occurs due to the process of natural selection. Natural selection states that individuals with traits that are beneficial in the environment are more likely to survive and reproduce, hence resulting in populations that become better adapted over time.");
  lessons.add("About this simulation\nIn this simulation, the environment is one that rewards creatures which walk to the right as far as they can, so the creatures' fitness will be measured as the distance they travel to the right in 15 seconds. Your task is to observe the trend in the population's fitness levels over time and see if this supports Darwin's theory of evolution.");
  lessons.add("Selection\nIn this stage, the creatures that get to survive and reproduce are determined wherein those with a greater fitness value or those that travelled farthest to the right are more likely to pass on their genes.");
  lessons.add("Variation\nIn this stage, the offspring of the creatures are created where mutation, a change in the DNA, leads to a slight deviation from the traits of the parent thus producing variance in the population.");
  
  jsonFile = new JSONObject(); // define JSON save file
  
  changeState(State.MainMenu);
}

void draw() {
  background(113, 197, 207);
  
  switch (state) {
    case State.Pretest:
      pretest.display();
      break;
    case State.MainMenu:
      startBtn.display();
      loadBtn.display();
      pushMatrix();
      textSize(70);
      textAlign(CENTER);
      fill(DEF_STROKE);
      text("Evolution Simulator", width/2, 300);
      translate(300, 0);
      ground.display();
      popMatrix();
      break;
    case State.GenerationOverview:
      fill(DEF_STROKE);
      noStroke();
      textSize(40);
      text("Generation " + str(currGen + 1), 30, 50);
      textSize(24);
      int rows = 10;
      int margin = 10;
      int w = 60;
      int h = 60;
      int xOffset = (width - ((w + margin) * rows)) / 2 - (w / 2);
      int yOffset = 230;
      
      for (int i = 0; i < GEN_SIZE; i++) { // display creature preview
        int x = ((i % rows) + 1) * (w + margin);
        int y = i / rows * (h + margin);
        creatures.get(generations.get(currGen)[i]).displayPreview(new Vec2(xOffset + x, yOffset + y), 10);
        //generations.get(currGen)[i].displayPreview(new Vec2(xOffset + x, yOffset + y), 10);
      }
      for (int i = 0; i < GEN_SIZE; i++) { // display info about creature
        int x = ((i % rows) + 1) * (w + margin);
        int y = i / rows * (h + margin);
        creatures.get(generations.get(currGen)[i]).displayPreviewInfo(new Vec2(xOffset + x, yOffset + y));
        //generations.get(currGen)[i].displayPreviewInfo(new Vec2(xOffset + x, yOffset + y));
      }
      
      sortBtn.display();
      killBtn.display();
      nextBtn.display();
      break;
    case State.SimulationScreen:
      
      slowBtn.display();
      fastBtn.display();
      skipBtn.display();
      
      fill(DEF_STROKE);
      noStroke();
      textAlign(LEFT);
      text("Simulation speed: " + str(renderSpeed) + "x", width - 300, 30);
      
      if (creatures.get(generations.get(currGen)[currCreature]).getTicks() < FRAME_RATE * 15) {
        int i = 0;
        while (i < renderSpeed && creatures.get(generations.get(currGen)[currCreature]).getTicks() < FRAME_RATE * 15) {
          creatures.get(generations.get(currGen)[currCreature]).tick();
          engine.step(); // increment time in the Physics engine
          i++;
        }
        fill(DEF_STROKE);
        textSize(24);
        text("Creature No: " + str(currCreature + 1) + "/" + str(GEN_SIZE), 30, 30);
        text("Fitness: " + str(creatures.get(generations.get(currGen)[currCreature]).getFitness()) + " m", 30, 60);
        text("Time remaining: " + str(TIME_LIMIT - floor(creatures.get(generations.get(currGen)[currCreature]).getTicks() / FRAME_RATE)) + " s", 30, 90);
        
        
        renderSpeed = nxtRenderSpd;
        pushMatrix();
        translate(-creatures.get(generations.get(currGen)[currCreature]).getPos().x + width/2, 0);
        
        ground.display();
        ground.update();
        
        image(flagImg, 45, height - 268);
        creatures.get(generations.get(currGen)[currCreature]).display();
        popMatrix();
      } else {
        creatures.get(generations.get(currGen)[currCreature]).destroyBody();
        println("Creature No. " + (generations.get(currGen)[currCreature] + 1) + ": "  + creatures.get(generations.get(currGen)[currCreature]).fitness);
        
        currCreature++;
        if (currCreature < GEN_SIZE) {
          creatures.get(generations.get(currGen)[currCreature]).initializeRealtime();
        } else {
          changeState(State.GenerationOverview);
        }
      }
      break;
    case State.Dashboard:
      textSize(24);
      fill(DEF_STROKE);
      text("Generation " + str(currGen + 1), 100, 30);
      generateBtn.display();
      finishBtn.display();
      saveBtn.display();
      graph.display();
      break;
    case State.Posttest:
      posttest.display();
      break;
    case State.Congratulations:
      
      strokeWeight(5);
      stroke(DEF_STROKE);
      fill(221, 216, 148);
      rectMode(CORNER);
      rect(20, 20, width - 40, height - 40, 25);
      fill(DEF_STROKE);
      textSize(80);
      textAlign(CENTER);
      text("Congratulations", width/2, 150);
      imageMode(CENTER);
      image(trophyImg, width/2, 400, 440, 386);
      for (int i = 0; i < confetti.size(); i++) {
        confetti.get(i).update();
        confetti.get(i).display();
      }
      break;
    case State.Lesson:
      strokeWeight(5);
      stroke(DEF_STROKE);
      fill(221, 216, 148);
      rectMode(CORNER);
      rect(20, 20, width - 40, height - 40, 25);
      fill(DEF_STROKE);
      textSize(32);
      textAlign(LEFT);
      text(lessons.get(currLesson), 80, 100, width - 160, height - 200);
      
      nextLessonBtn.display();
  }
  
  if (displayMsg) {
    push();
    stroke(0);
    strokeWeight(2);
    fill(235, 171, 167);
    rect(100, 100, width - 200, height - 200);
    fill(0);
    textAlign(CENTER);
    text(msgText, width / 2, 100 + height / 3);
    pop();
  }
}

void mousePressed() {
  switch (state) {
    case State.Pretest:
      if (pretest.isSubmitted()) {
        pretest.submit();
      } else if (pretest.isNext()) {
        pretest.next();
        if (pretest.isFinished()) {
          changeState(State.Lesson);
        }
      }
      break;
    case State.MainMenu:
      if (startBtn.isClicked()) {
        changeState(State.Pretest);
      }
      if (loadBtn.isClicked()) {
        selectInput("Select a JSON load file: ", "loadFile");
      }
      break;
    case State.GenerationOverview:
      if (sortBtn.isClicked()) {
        sort(generations.get(currGen), 0, GEN_SIZE-1);
        sortBtn.setVisibility(false);
        killBtn.setVisibility(true);
      } else if (killBtn.isClicked()) {
        for (int i = 0; i < GEN_SIZE; i++) {
          if (isKilled(i, GEN_SIZE)) {
            creatures.get(generations.get(currGen)[i]).alive = false;
          }
        }
        currLesson = 2;
        changeState(State.Lesson);
        killBtn.setVisibility(false);
      } else if (nextBtn.isClicked()) {
        nextBtn.setVisibility(false);
        changeState(State.Dashboard);
      }
      break;
    case State.Dashboard:
      if (generateBtn.isClicked()) {
        generateBtn.setVisibility(false);
        currLesson = 3;
        changeState(State.Lesson);
        //changeState(State.SimulationScreen);
      }
      if (finishBtn.isClicked()) {
        finishBtn.setVisibility(false);
        changeState(State.Posttest);
      }
      if (saveBtn.isClicked()) {
        saveBtn.setVisibility(false);
        saveFile();
      }
      break;
    case State.SimulationScreen:
      if (slowBtn.isClicked())
        nxtRenderSpd = max(1, nxtRenderSpd/2);
      if (fastBtn.isClicked()) {
        nxtRenderSpd *= 2;
        nxtRenderSpd = min(512, nxtRenderSpd);
      }
      if (skipBtn.isClicked()) {
        while (currCreature < GEN_SIZE) {
          while (creatures.get(generations.get(currGen)[currCreature]).getTicks() < FRAME_RATE * 15) {
            creatures.get(generations.get(currGen)[currCreature]).tick();
            engine.step(); // increment time in the Physics engine
          }
          creatures.get(generations.get(currGen)[currCreature]).destroyBody();
          currCreature++;
          if (currCreature < GEN_SIZE) {
            creatures.get(generations.get(currGen)[currCreature]).initializeRealtime();
          }
        }
        changeState(State.GenerationOverview);
      }
      break;
    case State.Posttest:
      if (posttest.isSubmitted()) {
        posttest.submit();
      } else if (posttest.isNext()) {
        posttest.next();
        if (posttest.isFinished()) {
          changeState(State.Congratulations);
        }
      }
    case State.Lesson:
      if (nextLessonBtn.isClicked()) {
        nextLessonBtn.setVisibility(false);
        
        switch (currLesson) {
          case 0: // what is evolution?
            changeState(State.Lesson);
            break;
          case 1: // about this simulation
            changeState(State.SimulationScreen);
            break;
          case 2:
            nextBtn.setVisibility(true);
            changeState(State.GenerationOverview);
            sortBtn.setVisibility(false);
            break;
          case 3:
            changeState(State.SimulationScreen);
            break;
        }
        currLesson++;
      }
      break;
  }
}

public void changeState(int s) {
  state = s;
  switch (state) {
    case State.MainMenu:
      
      break;
    case State.GenerationOverview:
      sortBtn.setVisibility(true);
      break;
    case State.SimulationScreen:
      nxtRenderSpd = 1;
      generations.add(new int[GEN_SIZE]);
      if (generations.size() > 1) {
        int parentIndex = 0;
        currGen++;
        currCreature = generations.get(currGen)[0];
        for (int i = 0; i < GEN_SIZE; i++) {
          if (!creatures.get(generations.get(currGen - 1)[i]).alive) {
            generations.get(currGen)[i] = creatures.size();
            creatures.add(creatures.get(parentIndex).createOffspring(creatures.size()));
            creatures.get(creatures.size()-1).setAncestorId(creatures.get(parentIndex).getAncestorId());
            
            parentIndex++;
          } else {
            generations.get(currGen)[i] = generations.get(currGen - 1)[i];
          }
        }
      } else {
        //generations.add(new Creature[GEN_SIZE]);
        for (int i = 0; i < GEN_SIZE; i++) {
          creatures.add(new Creature(6, creatures.size())); // add a new creature
          generations.get(currGen)[i] = creatures.size() - 1; // add creature index to current generation
          //generations.get(currGen)[i] = new Creature(5, i);
        }
        //generations.get(currGen)[currCreature].initializeRealtime();
      }
      creatures.get(generations.get(currGen)[currCreature]).initializeRealtime();
      break;
    case State.Dashboard:
      float[] data = new float[GEN_SIZE];
      for (int i = 0; i < GEN_SIZE; i++) {
        data[i] = creatures.get(generations.get(currGen)[i]).getFitness();
      }
      graph.addData(data);
      generateBtn.setVisibility(true);
      saveBtn.setVisibility(true);
      finishBtn.setVisibility(true);
      break;
    case State.Lesson:
      nextLessonBtn.setVisibility(true);
  }
}

public void merge(int[] arr, int l, int m, int r) {
  int n1 = m - l + 1; // get the number of items in the left half
  int n2 = r - m; // get the number of items in the right half
  
  int L[] = new int[n1]; // create an auxiliary array to store the left half
  int R[] = new int[n2]; // create an auxiliary array to store the right half
  
  // copy the items in the array to the auxiliary arrays
  for (int i = 0; i < n1; i++)
    L[i] = arr[l + i];
  for (int i = 0; i < n2; i++)
    R[i] = arr[m + 1 + i];
  
  int i = 0, j = 0; // initialize the index for the left half (i) and the right half (j)
  
  int k = l; // initialize the current index in the output array
  while (i < n1 && j < n2) { // while the index is not out of bounds for both arrays
  // if the fitness of the left creature is more than or equal to that of the right creature
    if (creatures.get(L[i]).getFitness() >= creatures.get(R[j]).getFitness()) { 
      // setting the item at the current index in the output array
      arr[k] = L[i];
      i++;
    } else {
      arr[k] = R[j];
      j++;
    }
    k++;
  }
  // copy over the remaining items
  while (i < n1) {
    arr[k] = L[i];
    i++;
    k++;
  }
  
  while (j < n2) {
    arr[k] = R[j];
    j++;
    k++;
  }
}

public void sort(int[] arr, int l, int r) {
  if (l < r) { // as long as the left index is lesser than the right index
    int m = (l + r) / 2; // get the midpoint of the array
    
    sort(arr, l, m); // recursively sort the left half
    sort(arr, m + 1, r); // recursively sort the right half
    
    merge(arr, l, m, r); // merge the sorted arrays
  }
}

// method which determines whether a creature at an index is killed where as the index increases, the likelihood increases
public boolean isKilled(int index, int size) {
  // map the index from -8 to 8 so that the sigmoid function is scaled to the generation size
  float x = map(index, 0, size, -8, 8); 
  
  float sigmoid = 1.0 / (1 + exp(-x));
  
  // return true when a random decimal from 0 to 1 is lesser than the sigmoid value at the current index
  return random(1) < sigmoid; 
}

public void loadFile(File selection) {
  if (selection != null) {
    try {
      jsonFile = loadJSONObject(selection.getAbsolutePath());
      try {
        JSONArray jsonCreatures = jsonFile.getJSONArray("creatures");
        for (int i = 0; i < jsonCreatures.size(); i++) {
          // load creature data
          JSONObject creature = jsonCreatures.getJSONObject(i);
          int n = creature.getInt("n");
          creatures.add(new Creature(n, creatures.size()));
          creatures.get(i).setAlive(creature.getBoolean("alive"));
          
          JSONArray nodes = creature.getJSONArray("nodes");
          for (int j = 0; j < n; j++) {
            creatures.get(i).getNodes().get(j).params.friction = nodes.getFloat(j);
          }
          
          JSONArray muscles = creature.getJSONArray("muscles");
          for (int j = 0; j < n; j++) {
            creatures.get(i).muscles.get(j).setLength(muscles.getJSONObject(j).getInt("length"));
            creatures.get(i).muscles.get(j).setExtension(muscles.getJSONObject(j).getInt("extension"));
            creatures.get(i).muscles.get(j).setPeriod(muscles.getJSONObject(j).getInt("period"));
            creatures.get(i).muscles.get(j).setOffset(muscles.getJSONObject(j).getInt("offset"));
          }
        }
        // load generations data
        JSONArray jsonGenerations = jsonFile.getJSONArray("generations");
        for (int i = 0; i < jsonGenerations.size(); i++) {  
          generations.add(new int[GEN_SIZE]);
          for (int j = 0; j < GEN_SIZE; j++) {
            generations.get(i)[j] = jsonGenerations.getJSONArray(i).getInt(j);
          }
        }
        // load graph data
        JSONArray jsonData = jsonFile.getJSONArray("data");
        for (int i = 0; i < jsonData.size(); i++) {
          float[] data = new float[GEN_SIZE];
         
          for (int j = 0; j < GEN_SIZE; j++) {
            float fitness = jsonData.getJSONArray(i).getFloat(j);
            if (fitness > maxFitness) {
                maxFitness = fitness;
            } else if (fitness < minFitness) {
                minFitness = fitness;
            }
            data[j] = fitness;
          }
          graph.addData(data);
        }
        // initialize simulation after finished loading contents of the save file
        currGen = jsonGenerations.size()-1;
        currCreature = generations.get(currGen)[0];
        changeState(State.SimulationScreen);
      } catch (RuntimeException e) {
        displayMsg = true;
        msgText = "ERROR\nPlease select a valid save file.";
        delay(3000);
        displayMsg = false;
      } 
    } catch (RuntimeException e) {
      displayMsg = true;
      msgText = "ERROR\nPlease select a JSON file.";
      delay(3000);
      displayMsg = false;
    }
  }
}

public void saveFile() { // save generated creatures to a file which can be imported in another session
  JSONArray jsonCreatures = new JSONArray();
  for (int i = 0; i < creatures.size(); i++) {
    Creature creature = creatures.get(i);
    JSONObject creatureObj = new JSONObject();
    
    // add information about the node objects to the JSON file
    
    ArrayList<Node> nodes = creature.getNodes();
    JSONArray nodesArr = new JSONArray();
    
    for (int j = 0; j < nodes.size(); j++) {
      nodesArr.setFloat(j, nodes.get(j).params.friction); 
    }
    
    // add information about the muscle objects to JSON file
    
    ArrayList<Constraint> muscles = creature.getMuscles();
    JSONArray musclesArr = new JSONArray();
    
    for (int j = 0; j < muscles.size(); j++) {
      JSONObject muscleObj = new JSONObject();
      muscleObj.setInt("length", muscles.get(j).getLength());
      muscleObj.setInt("extension", muscles.get(j).getExtension());
      muscleObj.setInt("period", muscles.get(j).getPeriod());
      muscleObj.setInt("offset", muscles.get(j).getOffset());
      
      musclesArr.setJSONObject(j, muscleObj);
    }
    
    // add information about creature objects to JSON file
    creatureObj.setInt("id", creature.getId());
    creatureObj.setFloat("fitness", creature.getFitness());
    creatureObj.setInt("n", creature.getN());
    creatureObj.setBoolean("alive", creature.isAlive());
    creatureObj.setJSONArray("nodes", nodesArr);
    creatureObj.setJSONArray("muscles", musclesArr);
    
    
    jsonCreatures.setJSONObject(i, creatureObj);
  } 
  
  jsonFile.setJSONArray("creatures", jsonCreatures);
  
  JSONArray jsonGenerations = new JSONArray();
  
  for (int i = 0; i < generations.size(); i++) {
    JSONArray generation = new JSONArray();
    for (int j = 0; j < GEN_SIZE; j++) {
      generation.setInt(j, generations.get(i)[j]);
    }
    
    jsonGenerations.setJSONArray(i, generation);
  }
  
  jsonFile.setJSONArray("generations", jsonGenerations);
  
  JSONArray jsonData = new JSONArray();
  
  for (int i = 0; i < graph.series.size(); i++) {
    JSONArray data = new JSONArray();
    for (int j = 0; j < GEN_SIZE; j++) {
      data.setFloat(j, graph.series.get(i)[j]);
    }
    jsonData.setJSONArray(i, data);
  }
  
  jsonFile.setJSONArray("data", jsonData);
  
  JSONArray jsonAverages = new JSONArray();
  
  for (int i = 0; i < graph.averages.size(); i++) {
    jsonAverages.setFloat(i, graph.averages.get(i));
  }
  
  jsonFile.setJSONArray("averages", jsonAverages);
  
  String filename = year() + "-" + month() + "-" + day() + "-" + hour() + "-" + floor(random(1000, 9999)) + "-SAVE.json"; // save to a JSON file with a unique filename
  
  saveJSONObject(jsonFile, "saves/" + filename); 
}
