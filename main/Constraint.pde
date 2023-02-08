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
