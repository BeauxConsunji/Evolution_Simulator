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
