import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import shiffman.box2d.*; 
import org.jbox2d.common.*; 
import org.jbox2d.dynamics.joints.*; 
import org.jbox2d.collision.shapes.*; 
import org.jbox2d.collision.shapes.Shape; 
import org.jbox2d.common.*; 
import org.jbox2d.dynamics.*; 
import org.jbox2d.dynamics.contacts.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class main extends PApplet {

/*
  Evolution Simulator
  Author: Beaux Consunji
  Description: 
  Date: 
*/

// import modules










// set up the global constants
Box2DProcessing engine;
final float GRAVITY      = -50;
final int   NODE         = 0;
final int   BOX          = 1;
final int   FRAME_RATE   = 60;
final float METER        = 100.0f;
final int   TIME_LIMIT   = 15;
final int   GEN_SIZE     = 50;
final float MUTATION     = 0.05f;
final int DEF_STROKE   = color(84, 75, 59);
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

public void setup() {
  
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

public void draw() {
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

public void mousePressed() {
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
  
  float sigmoid = 1.0f / (1 + exp(-x));
  
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
/* Date (2021)
*  Class to construct a Box object
*  (
*/

public class Box extends Figure { // subclass of Figure
  private int w;
  private int h;
  
  public Box(int w, int h) {
    super();
    this.w = w;
    this.h = h;
    
    // create PolygonShape object
    PolygonShape shape = new PolygonShape();
    float tw = engine.scalarPixelsToWorld(w/2);
    float th = engine.scalarPixelsToWorld(h/2);
    shape.setAsBox(tw, th);
    
    this.fixture = new FixtureDef();
    this.fixture.shape = shape;
    // set physics parameters
    this.fixture.density = this.params.density;
    this.fixture.friction = this.params.friction;
    this.fixture.restitution = this.params.restitution;
    
  }
  
  public Box(int w, int h, BodyParameters params) {
    super(params);
    this.w = w;
    this.h = h;
    
    PolygonShape shape = new PolygonShape();
    float tw = engine.scalarPixelsToWorld(w/2);
    float th = engine.scalarPixelsToWorld(h/2);
    shape.setAsBox(tw, th);
    
    this.fixture = new FixtureDef();
    this.fixture.shape = shape;
    // set physics parameters
    this.fixture.density = this.params.density;
    this.fixture.friction = this.params.friction;
    this.fixture.restitution = this.params.restitution;
  }
  
  @Override
  public void display() {
    super.display();
    if (this.inWorld()) {
      Vec2 pos = getPos();
      float a = body.getAngle();
      
      imageMode(CENTER);
      pushMatrix();
      translate(pos.x, pos.y);
      rotate(-a);
      fill(255);
      stroke(DEF_STROKE);
      image(groundImg, 0, 0);
      //rect(0, 0, this.w, this.h);
      popMatrix();
    }
  }
}
public class Button {
  private String type;
  private String text; // text inside the button
  private PImage img;
  private int fontSize = 24;
  private int padding = 20; // distance from content to border
  private float radius = 10; // how round the corners are
  private Vec2 pos, dim; // position and dimension vectors
  private boolean visible = true;
  private int colour = color(164, 234, 85);
  
  public Button(PImage img, Vec2 pos, Vec2 dim) {
    this.type = "image";
    this.img = img;
    this.pos = pos;
    this.dim = dim;
  }
  
  public Button(String text, Vec2 pos) {
    this.type = "text";
    this.text = text;
    this.pos = pos;
    textSize(this.fontSize);
    this.dim = new Vec2(textWidth(this.text) + this.padding, this.fontSize + this.padding);
  }
  
  public void display() {
    if (this.visible) {
      rectMode(CORNER);
      fill(this.colour);
      stroke(DEF_STROKE);
      strokeWeight(2);
      rect(this.pos.x, this.pos.y, this.dim.x, this.dim.y, this.radius);
      
      fill(0);
      noStroke();
      switch (this.type) {
        case "text":
          push();
          textAlign(LEFT);
          textSize(this.fontSize);
          fill(DEF_STROKE);
          text(this.text, this.pos.x + (this.padding / 2), this.pos.y + this.dim.y - (this.padding / 2));
          pop();
          break;
        case "image":
          imageMode(CORNER);
          image(this.img, this.pos.x + (this.padding / 2), this.pos.y + (this.padding / 2), this.dim.x - this.padding, this.dim.y - this.padding);
          break;
      }
    }
  }
  
  public void setColour(int c) {
    this.colour = c;
  }
  
  public void setVisibility(boolean v) {
    this.visible = v;
  }
  
  public void setFontSize(int size) {
    this.fontSize = size;
    textSize(this.fontSize);
    this.dim.x = textWidth(this.text) + this.padding;
  }
  
  public void setPadding(int p) {
    this.padding = p;
    this.dim = new Vec2(textWidth(this.text) + this.padding, this.fontSize + this.padding);
  }
  
  public void setRadius(float r) {
    this.radius = r;
  }
  
  public boolean isHovered() {
    return (this.visible && mouseX > this.pos.x && mouseX < this.pos.x + this.dim.x && mouseY > this.pos.y && mouseY < this.pos.y + this.dim.y);
  }
  
  public boolean isClicked() {
    return this.isHovered() && mousePressed;
  }
}
public class Confetti {
  Vec2 pos;
  Vec2 vel;
  Vec2 acc;
  float lifespan;
  int hue;
  
  public Confetti(Vec2 pos) {
    this.acc = new Vec2(0, 0.05f);
    this.vel = new Vec2(random(-4, 4), random(-2, 0));
    this.pos = pos;
    this.lifespan = 255.0f;
    this.hue = round(random(255));
  }
  public void display() {
    push();
    noStroke();
    colorMode(HSB);
    fill(hue, 200, 200, lifespan);
    circle(pos.x, pos.y, 20);
    pop();
  }
  public void update() {
    this.vel = this.vel.add(this.acc);
    this.pos = this.pos.add(this.vel);
    lifespan -= 1.0f;
  }
}
public class Constraint {
  private Body bodyA;
  private Body bodyB;
  private int baseLen; // default length
  private float area = 1000;
  private float currLen; // current length
  private int extension = 10; // how much longer when extended
  private int period = 20; // number of frames between contractions
  private int offset = 0; // phase shift in the sin function
  private DistanceJoint joint;
  private DistanceJointDef jointDef;
  
  public Constraint(int len, int ext, int period, int offset) {
    //this.bodyA = bodyA;
    //this.bodyB = bodyB;
    this.baseLen = len;
    this.extension = ext;
    this.period = period;
    this.offset = offset;
    this.jointDef = new DistanceJointDef();
    //this.jointDef.bodyA = this.bodyA;
    //this.jointDef.bodyB = this.bodyB;
    this.jointDef.length = engine.scalarPixelsToWorld(this.baseLen);
    
    this.jointDef.frequencyHz = 0;
    this.jointDef.dampingRatio = 0;
  }
  
  public void createJoint(Body bodyA, Body bodyB) {
    this.bodyA = bodyA;
    this.bodyB = bodyB;
    this.jointDef.bodyA = this.bodyA;
    this.jointDef.bodyB = this.bodyB;
    this.joint = (DistanceJoint) engine.createJoint(this.jointDef);
  }
  
  public void createJoint(Body bodyA, Body bodyB, int initialTick, World w) {
    this.bodyA = bodyA;
    this.bodyB = bodyB;
    this.jointDef.bodyA = this.bodyA;
    this.jointDef.bodyB = this.bodyB;
    this.joint = (DistanceJoint) w.createJoint(this.jointDef);
  }
  
  public void destroyJoint() {
    try {
      engine.world.destroyJoint(this.joint);
    } catch (Exception e) {
      print(e);
    }
  }
  
  public void destroyJoint(World w) {
    w.destroyJoint(this.joint);
  }
  
  // method to display the constraint
  public void display() {
    // set the fill to white and the outline to black
    fill(255);
    stroke(0);
    
    // convert the coordinates of the first and second body from the physics engine coordinates to the screen coordinates
    Vec2 pointA = engine.getBodyPixelCoord(this.bodyA);
    Vec2 pointB = engine.getBodyPixelCoord(this.bodyB);

    float w = dist(pointA.x, pointA.y, pointB.x, pointB.y); // set the width of the line to the distance between the two points
    float a = atan(((pointB.y - pointA.y)/ w) / ((pointB.x - pointA.x) / w)); // find the angle from the horizon to go from point A to point B
    
    pushMatrix();
    rectMode(CENTER);
    // translate the matrix so that the center is the midpoint of the two points
    translate((pointA.x + pointB.x) / 2, (pointA.y + pointB.y) / 2); 
    
    // rotate the matrix so that the rectangle connects the two points
    rotate(a);
    // draw a rectangle to represent the constraint
    rect(0, 0, w, this.area / w); // when it is extended, it is thinner and when it is contracted, it is thicker
    popMatrix();
  }
  
  public void update(int ticks) {
    this.currLen = this.baseLen + (this.extension * sin(this.offset + (TWO_PI * ticks / this.period)));
    this.joint.setLength(engine.scalarPixelsToWorld(this.currLen));
  }
  
  public int getLength() {
    return this.baseLen;
  }
  
  public void setLength(int len) {
    this.baseLen = len;
  }
  
  public int getExtension() {
    return this.extension;
  }
  
  public void setExtension(int ext) {
    this.extension = ext;
  }
  
  public int getPeriod() {
    return this.period;
  }
  
  public void setPeriod(int per) {
    this.period = per;
  }
  
  public int getOffset() {
    return this.offset;
  }
  
  public void setOffset(int off) {
    this.offset = off;
  }
}
public class Creature {
  private ArrayList<Node> nodes;
  private ArrayList<Constraint> muscles;
  
  //private int initialFrame = 0; // frame when animation started
  private float fitness = 0; // how far to the right it travelled
  private int n = 5; // number of sides
  private int id = 0;
  private int ancestorId = -1; // by default is the oldest ancestor
  private int muscleLen = 80; // base length of the muscle
  private int muscleExt = 20; // how much longer when extended
  private boolean alive = true; // whether the creature is alive
  private int ticks = 0;
  
  public Creature(int n) {
    this.n = n;
    this.nodes = new ArrayList<Node>();
    this.muscles = new ArrayList<Constraint>();
    
    // dynamically generate a creature with random parameters
    for (int i = 0; i < n; i++) {
      nodes.add(new Node(50));
      nodes.get(i).params.friction = random(1);
      
      muscles.add(new Constraint(this.muscleLen, this.muscleExt, round(random(20, 60)), round(random(60))));
    }
  }
  public Creature(int n, int id) {
    this.n = n;
    this.id = id;
    this.nodes = new ArrayList<Node>();
    this.muscles = new ArrayList<Constraint>();

    for (int i = 0; i < n; i++) {
      nodes.add(new Node(50));
      nodes.get(i).params.friction = random(1);
      
      muscles.add(new Constraint(this.muscleLen, this.muscleExt, round(random(20, 60)), round(random(60))));
    }
  }
  
  public void display() {
    // set the fill to white and the outline to black
    fill(255);
    stroke(DEF_STROKE);
    // display each muscle in the creature
    for (int i = 0; i < muscles.size(); i++) {
      Constraint muscle = muscles.get(i);
      muscle.display();
    }
    // display each node in the creature
    for (int i = 0; i < nodes.size(); i++) {
      Node node = nodes.get(i);
      node.display();
    }
  }
  
  public void displayPreview(Vec2 center, int r) {
    float a = 0;
    int len = 20;
    int x = 30;
    
    stroke(0);
    strokeWeight(1);
    rectMode(CENTER);
    if (this.alive) {
      fill(255);

      rect(center.x, center.y, x * 2, x * 2, 5);
      
      // calculate the position of each node
      Vec2[] pos = new Vec2[nodes.size()];
      for (int i = 0; i < nodes.size(); i++) {
        pos[i] = new Vec2(floor(center.x + len * sin(a)), floor(center.y + len * -cos(a)));
        a += TWO_PI / nodes.size();
      }
      // draw the musles between each node
      for (int i = 0; i < muscles.size(); i++) {
        if (i == muscles.size() - 1) {
          float w = dist(pos[i].x, pos[i].y, pos[0].x, pos[0].y);
          float ta = atan(((pos[i].y - pos[0].y) / w) / ((pos[i].x - pos[0].x) / w));
          pushMatrix();
          translate((pos[i].x + pos[0].x) / 2, (pos[i].y + pos[0].y) / 2);
          rotate(ta);
          rectMode(CENTER);
          rect(0, 0, w, 4);
          popMatrix();
        } else {
          float w = dist(pos[i].x, pos[i].y, pos[i+1].x, pos[i+1].y);
          float ta = atan(((pos[i].y - pos[i+1].y) / w) / ((pos[i].x - pos[i+1].x) / w));
          pushMatrix();
          translate((pos[i].x + pos[i+1].x) / 2, (pos[i].y + pos[i+1].y) / 2);
          rotate(ta);
          rectMode(CENTER);
          rect(0, 0, w, 4);
          popMatrix();
        }
      }
      // draw the nodes
      for (int i = 0; i < nodes.size(); i++) {
        float tint = map(nodes.get(i).params.friction, 0, 1, 0, 255); 
        fill(constrain(120 + tint, 0, 255), constrain(50 + tint, 0, 255), constrain(50 + tint, 0, 255));
        circle(pos[i].x, pos[i].y, r);
      }
    } else {
      fill(0);
      rect(center.x, center.y, x * 2, x * 2, 5);
    }
  }
  
  public void displayPreviewInfo(Vec2 center) {
    int x = 30;
    if (mouseX > center.x - x && mouseX < center.x + x && mouseY > center.y - x && mouseY < center.y + x) {
      String[] text = new String[2];
      text[0] = "Creature #" + (this.id + 1);
      text[1] = "Fitness: " + this.fitness + " m";
      fill(255);
      stroke(0);
      strokeWeight(2);
      rectMode(CORNER);
      rect(center.x + x, mouseY, textWidth(text[1]) + 20, 40 * text.length);
      
      fill(0);
      noStroke();
      for (int i = 0; i < text.length; i++) {
        text(text[i], center.x + x + 10, mouseY + (30 * (i + 1)));
      }
    }
  }
  
  public float getFitness() {
    return this.fitness;
  }
  
  public Vec2 getPos() {
    Vec2 pos = new Vec2();
    for (int i = 0; i < nodes.size(); i++) {
      pos = pos.add(nodes.get(i).getPos());
    }
    pos = pos.mul(0.3f);
    return pos;
  }
  
  public boolean isAlive() {
    return this.alive;
  }
  
  public void setAlive(boolean alive) {
    this.alive = alive;
  }
  
  public int getAncestorId() {
    return this.ancestorId;
  }
  
  public void setAncestorId(int id) {
    this.ancestorId = id;
  }
  
  public void initializeRealtime() {
    this.ticks = 0;
    float a = 0;
    int len = this.muscleLen;
    for (int i = 0; i < nodes.size(); i++) {
      Vec2 pos = STARTING_POINT;
      nodes.get(i).createBody(new Vec2(floor(pos.x + len * sin(a)), floor(pos.y + len * -cos(a))));
      nodes.get(i).setFriction( nodes.get(i).params.friction);
      
      if (i > 0) {
        muscles.get(i-1).createJoint(nodes.get(i).body, nodes.get(i-1).body);
      }
      
      a += TWO_PI / nodes.size();
    }
    
    muscles.get(muscles.size()-1).createJoint(nodes.get(0).body, nodes.get(nodes.size()-1).body);
  }
  
  public void destroyBody() {
    
    for (int i = 0; i < muscles.size(); i++) {
      muscles.get(i).destroyJoint(engine.world);
    }
    for (int i = 0; i < nodes.size(); i++) {
      nodes.get(i).destroyBody();
    }
    
  }
  
  public Creature createOffspring(int id) {
    Creature creature = new Creature(this.n, id);
    for (int i = 0; i < n; i++) { // for each side, there is one node and one muscle
      // use the parent's attributes with a chance for mutation to produce variance
      creature.nodes.get(i).params.friction = max(0, nodes.get(i).params.friction + random(-0.5f, 0.5f));
      creature.muscles.get(i).period = max(0, muscles.get(i).period + (int)random(-10, 10));
      creature.muscles.get(i).offset = max(0, muscles.get(i).offset + (int)random(10));
    }
    return creature;
  }
  
  public int getId() {
    return this.id;
  }
  
  public int getN() {
    return this.n;
  }
  
  public ArrayList<Node> getNodes() {
    return this.nodes;
  }
  
  public ArrayList<Constraint> getMuscles() {
    return this.muscles;
  }
  
  public int getTicks() {
    return this.ticks;
  }
  
  public void tick() {
    for (int i = 0; i < muscles.size(); i++) {
      muscles.get(i).update(this.ticks);
    }
    this.fitness = round((this.getPos().x - STARTING_POINT.x)) / METER;
    if (this.fitness > maxFitness) {
      maxFitness = this.fitness;
    } else if (this.fitness < minFitness) {
      minFitness = this.fitness;
    }
    this.ticks++;
  }
}
public class Figure { 
  public Body body;
  public BodyParameters params; // physical properties of the body
  public FixtureDef fixture;
  private boolean inWorld = false; // whether the body has been destroyed
  
  
  public Figure() {
    this.params = new BodyParameters();
  }
  
  public Figure(BodyParameters params) {
    this.params = params;
  }
  
  public void display() {
    fill(255);
    stroke(0);
    strokeWeight(2);
  }
  
  public boolean inWorld() { 
    return inWorld;
  }
  
  public Vec2 getPos() {
    return engine.getBodyPixelCoord(this.body);
  }
  
  public BodyType getType() {
    return this.params.bodyType;
  }
  
  public void setType(BodyType val) {
    this.params.bodyType = val;
    this.body.setType(val);
  }
  
  public float getDensity() {
    return this.params.density;
  }
  
  public void setDensity(float val) {
    this.params.density = val;
    this.body.getFixtureList().setDensity(val);
  }
  
  public float getFriction() {
    return this.params.friction;
  }
  
  public void setFriction(float val) {
    this.params.friction = val;
    this.body.getFixtureList().setFriction(val);
  }
  
  public float getRestitution() {
    return this.params.restitution;
  }
  
  public void setRestitution(float val) {
    this.params.restitution = val;
    this.body.getFixtureList().setRestitution(val);
  }
  
  public void createBody(Vec2 pos) {
    BodyDef bd = new BodyDef(); 
    bd.type = this.params.bodyType;
    bd.position.set(engine.coordPixelsToWorld(pos));
    
    this.body = engine.createBody(bd);
    body.createFixture(this.fixture);
    this.inWorld = true;
  }
  
  public void createBody(Vec2 pos, World w) {
    BodyDef bd = new BodyDef(); 
    bd.type = this.params.bodyType;
    bd.position.set(engine.coordPixelsToWorld(pos));
    
    this.body = w.createBody(bd);
    body.createFixture(this.fixture);
    this.inWorld = true;
  }
  
  public void destroyBody() {
    engine.destroyBody(this.body);
    this.inWorld = false;
  }
  
  public void destroyBody(World w) {
    w.destroyBody(this.body);
    this.inWorld = false;
  }
}
public class Graph {
  private Vec2 pos;
  private Vec2 dim;
  private int rows;
  private ArrayList<float[]> series;
  private ArrayList<Float> averages;
  
  public Graph(Vec2 pos, Vec2 dim, int rows) {
    this.series = new ArrayList<float[]>();
    this.averages = new ArrayList<Float>();
    this.pos = pos;
    this.dim = dim;
    this.rows = rows;
  }
  
  public void addData(float[] data) {
    series.add(data);
    float avg = 0;
    for (int i = 0; i < GEN_SIZE; i++) {
      avg += data[i];
    }
    avg /= GEN_SIZE;
    averages.add(new Float(avg));
  }
  
  public void display() {
    push();
    rectMode(CORNER);
    fill(255);
    stroke(0);
    
    rect(pos.x, pos.y, dim.x, dim.y);
    
    colorMode(HSB);
    int yLines = 20;
    for (int i = 0; i < yLines; i++) {
      stroke(200);
      strokeWeight(1);
      line(this.pos.x, this.pos.y + map(i, 0, yLines, 0, this.dim.y), this.pos.x + this.dim.x, this.pos.y + map(i, 0, yLines, 0, this.dim.y));
    }
    
    for (int i = 0; i < series.size(); i++) {
      stroke(200);
      strokeWeight(1);
      line(this.pos.x + (this.dim.x * (i + 1) / this.series.size()), this.pos.y, this.pos.x + (this.dim.x * (i + 1) / this.series.size()), this.pos.y + this.dim.y);
     
      
      for (int j = 0; j < rows; j++) {
        
        strokeWeight(5);
        stroke(map(j, 0, rows, 0, 255), 120, 235);
        Vec2 pointA;
        Vec2 pointB = new Vec2(this.pos.x + (this.dim.x * (i + 1) / this.series.size()), this.pos.y + map(this.series.get(i)[j], minFitness, maxFitness, this.dim.y, 0));
        if (i > 0) {
          pointA = new Vec2(this.pos.x + (this.dim.x * i / this.series.size()), this.pos.y + map(this.series.get(i-1)[j], minFitness, maxFitness, this.dim.y, 0));
          line(pointA.x, pointA.y, pointB.x, pointB.y);  
        } else {
          pointA = new Vec2(this.pos.x + (this.dim.x * i / this.series.size()), this.pos.y + map(0, minFitness, maxFitness, this.dim.y, 0));
        }
        line(pointA.x, pointA.y, pointB.x, pointB.y);
      }
      
      stroke(DEF_STROKE);
      strokeWeight(1);
      Vec2 pointA;
      Vec2 pointB = new Vec2(this.pos.x + (this.dim.x * (i + 1) / this.series.size()), this.pos.y + map(this.averages.get(i), minFitness, maxFitness, this.dim.y, 0));
      
      if (i > 0) {
        pointA = new Vec2(this.pos.x + (this.dim.x * i / this.series.size()), this.pos.y + map(this.averages.get(i-1), minFitness, maxFitness, this.dim.y, 0));
      } else {
        pointA = new Vec2(this.pos.x + (this.dim.x * i / this.series.size()), this.pos.y + map(0, minFitness, maxFitness, this.dim.y, 0));
      }
      line(pointA.x, pointA.y, pointB.x, pointB.y);
        
    }
    noFill();
    stroke(DEF_STROKE);
    strokeWeight(5);
    rect(pos.x, pos.y, dim.x, dim.y);
    pop();
    
  }
  
  public void displayStackedLineGraph() {
    
  }
}
public class Ground {
  private ArrayList<Box> boxes;
  private int min = -900;
  private int max = 900;
  
  public Ground() {
    boxes = new ArrayList<Box>();
    for (int i = 0; i < 3; i++) {
      boxes.add(new Box(600, 170));
      boxes.get(i).createBody(new Vec2(-600 + (600 * i), height - 85));
      boxes.get(i).setType(BodyType.STATIC);
      boxes.get(i).setFriction(1.0f);
    }
  }
  
  public void addLeft() {
    boxes.add(new Box(600, 170));
    boxes.get(boxes.size() - 1).createBody(new Vec2(min - 300, height - 85));
    boxes.get(boxes.size() - 1).setType(BodyType.STATIC);
    boxes.get(boxes.size() - 1).setFriction(1.0f);
    min -= 600;
  }
  
  public void addRight() {
    boxes.add(new Box(600, 170));
    boxes.get(boxes.size() - 1).createBody(new Vec2(max + 300, height - 85));
    boxes.get(boxes.size() - 1).setType(BodyType.STATIC);
    boxes.get(boxes.size() - 1).setFriction(1.0f);
    max += 600;
  }
  
  public void display() {
    for (int i = 0; i < boxes.size(); i++) {
      boxes.get(i).display();
      //
      
      //if (boxes.get(i).getPos().x - 300 <= creature.getPos().x + 600 || boxes.get(i).getPos().x + 300 >= creature.getPos().x - 600) {
      //  boxes.get(i).display();
      //}
      
    }
  }
  
  public void update() {
    Creature creature = creatures.get(generations.get(currGen)[currCreature]);
    if (creature.getPos().x + 600 >= max) {
      addRight();
    } else if (creature.getPos().x - 600 <= min) {
      addLeft();
    }
  }
}
public class Node extends Figure { // subclass of Figure
  private int r;
  
  public Node(int r) {
    super();
    this.r = r;
   
    // create CircleShape object
    CircleShape circle = new CircleShape();
    circle.m_radius = engine.scalarPixelsToWorld(this.r/2);
    
    this.fixture = new FixtureDef();
    this.fixture.shape = circle;
    // set physics parameters
    this.fixture.density = this.params.density;
    this.fixture.friction = this.params.friction;
    this.fixture.restitution = this.params.restitution;
  }
  
  public Node(int r, BodyParameters params) {
    super(params);
    this.r = r;
   
    CircleShape circle = new CircleShape();
    circle.m_radius = engine.scalarPixelsToWorld(this.r/2);
    
    this.fixture = new FixtureDef();
    this.fixture.shape = circle;
    // set physics parameters
    this.fixture.density = this.params.density;
    this.fixture.friction = this.params.friction;
    this.fixture.restitution = this.params.restitution;
  }
  
  @Override
  public void display() {
    super.display();
    if (this.inWorld()) { // if the node hasn't been destroyed
      Vec2 pos = getPos();
      float a = body.getAngle();
      
      rectMode(CENTER);
      pushMatrix();
      // translate and rotate the matrix to the position and angle of the node respectively 
      translate(pos.x, pos.y);
      rotate(-a);
      // the more friction, the darker the color
      float tint = map(this.params.friction, 0, 1, 0, 255); 
      fill(constrain(120 + tint, 0, 255), constrain(50 + tint, 0, 255), constrain(50 + tint, 0, 255));
      
      stroke(DEF_STROKE);
      circle(0, 0, this.r);
      popMatrix();
    }
  }
}

public class Test {
  private String title;
  private ArrayList<String> questions;
  private ArrayList<Integer> answers;
  private ArrayList<String[]> choices;
  private Button submitBtn, nxtBtn;
  private int currQuestion = 0;
  private int selectedChoice = -1;
  private int score = 0;
  private boolean isAnswered = false;
  
  public Test(String title) {
    this.title = title;
    this.questions = new ArrayList<String>();
    this.answers = new ArrayList<Integer>();
    this.choices = new ArrayList<String[]>();
    this.submitBtn = new Button("Submit", new Vec2(width - 220, height - 120));
    this.submitBtn.setFontSize(40);
    this.submitBtn.setPadding(20);
    this.nxtBtn = new Button("Next", new Vec2(width - 220, height - 120));
    this.nxtBtn.setFontSize(40);
    this.nxtBtn.setPadding(20);
    this.nxtBtn.setVisibility(false);
    addQuestion("What is a feature that allows an organism to survive better in its environment?", "adaptation", "variation", "homologous structure", 0);
    addQuestion("What are all the individuals of a species that live in a particular area called?", "variation", "group", "population", 2);
    addQuestion("What did Charles Darwin call the ability of an organism to survive and reproduce in its environment", "evolution", "diversity", "fitness", 2);
    addQuestion("What is mutation?", "combination of parents' DNA", "a change in the DNA", "exposure to radiation", 1);
    addQuestion("What is the process of organisms becoming better adapted to their environment?", "natural selection", "adaptation", "transmission", 0);
  }
  
  public void display() {
    strokeWeight(5);
    stroke(DEF_STROKE);
    fill(221, 216, 148);
    rectMode(CORNER);
    rect(20, 20, width - 40, height - 40, 25);
    
    if (this.isAnswered) {
      fill(DEF_STROKE);
      textAlign(CENTER);
      textSize(50);
      text("Score: " + score + "/" + this.questions.size(), width/2, 150);
      textSize(32);
      text("Question " + (currQuestion + 1) + ":\n" + this.questions.get(currQuestion), 80, 220, width - 160, 400);
      if (this.selectedChoice == this.answers.get(currQuestion)) {
        fill(194, 217, 168);
        rect(80, 400, width - 160, 200);
        fill(DEF_STROKE);
        text("Answer:\n" + this.choices.get(currQuestion)[this.answers.get(currQuestion)], width/2, 450);
      } else {
        fill(217, 168, 168);
        rect(80, 400, width - 160, 250);
        fill(DEF_STROKE);
        text("Your answer:\n" + this.choices.get(currQuestion)[this.selectedChoice], width/2, 450);
        text("Correct answer:\n" + this.choices.get(currQuestion)[this.answers.get(currQuestion)], width/2, 550);
      }
      this.nxtBtn.display();
  } else {
      fill(255);
      rect(500, 50, 650, 50, 25);
      textAlign(LEFT);
      fill(164, 234, 85);
      if (currQuestion > 0) {
        rect(500, 50, 650 * currQuestion / this.questions.size(), 50, 25);
      }
      this.submitBtn.display();
      
      for (int i = 0; i < 3; i++) {
        strokeWeight(8);
        if (mousePressed && pow(mouseX - width/2 - 100, 2) + pow(mouseY - 290 - (100 * i), 2) <= 900) {
          selectedChoice = i;
        }
        if (i == selectedChoice) {
          stroke(DEF_STROKE);
          fill(164, 234, 85);
        } else {
          stroke(DEF_STROKE);
          fill(255);
        }
        circle(width/2 + 90, 290 + (100 * i), 30);
        fill(DEF_STROKE);
        text(this.choices.get(currQuestion)[i], width/2 + 120, 300 + (100 * i));
      }
      fill(DEF_STROKE);
      textSize(60);
      text(this.title, 60, 100);
      textSize(32);
      text("Q" + (currQuestion + 1) + ":\n" + this.questions.get(currQuestion), 60, 150, width/2 - 40, height - 190);
    }
    
    
  }
  
  public void addQuestion(String q, String c1, String c2, String c3, int a) {
    questions.add(q);
    choices.add(new String[]{c1, c2, c3});
    answers.add(a);
  }
  
  public int getScore() {
    return this.score;
  }
  
  public boolean isSubmitted() {
     return this.submitBtn.isClicked() && this.selectedChoice != -1;
  }
  public boolean isNext() {
    return this.nxtBtn.isClicked();
  }
  
  public boolean isFinished() {
    return this.currQuestion == this.questions.size();
  }
  
  public void submit() {
    if (this.selectedChoice == this.answers.get(this.currQuestion)) {
      this.score++;
      print(this.score);
    }
    this.isAnswered = true;
    this.submitBtn.setVisibility(false);
    this.nxtBtn.setVisibility(true);
  }
  public void next() {
    this.currQuestion++;
    this.selectedChoice = -1;
    this.isAnswered = false;
    this.nxtBtn.setVisibility(false);
    this.submitBtn.setVisibility(true);
  }
}
public abstract class State {
  static final int Pretest = 0;
  static final int MainMenu = 1;
  static final int GenerationOverview = 2;
  static final int SimulationScreen = 3;
  static final int Dashboard = 4;
  static final int Posttest = 5;
  static final int Congratulations = 6;
  static final int Lesson = 7;
}

public class BodyParameters {
  BodyType bodyType = BodyType.DYNAMIC;
  float density = 1;
  float friction = 0.3f;
  float restitution = 0;
  boolean isVisible = true;
  
  public BodyParameters() {}
  
  public BodyParameters(BodyType bodyType, float density, float friction, float restitution, boolean isVisible) {
    this.bodyType = bodyType;
    this.density = density;
    this.friction = friction;
    this.restitution = restitution;
    this.isVisible = isVisible;
  }
}
  public void settings() {  size(1200, 800); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "main" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
