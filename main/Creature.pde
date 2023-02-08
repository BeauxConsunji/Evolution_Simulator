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
    pos = pos.mul(0.3);
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
      creature.nodes.get(i).params.friction = max(0, nodes.get(i).params.friction + random(-0.5, 0.5));
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
