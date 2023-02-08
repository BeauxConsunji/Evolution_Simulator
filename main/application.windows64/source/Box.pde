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
