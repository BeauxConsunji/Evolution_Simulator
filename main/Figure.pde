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
