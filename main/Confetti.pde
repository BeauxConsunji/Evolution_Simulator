public class Confetti {
  Vec2 pos;
  Vec2 vel;
  Vec2 acc;
  float lifespan;
  int hue;
  
  public Confetti(Vec2 pos) {
    this.acc = new Vec2(0, 0.05);
    this.vel = new Vec2(random(-4, 4), random(-2, 0));
    this.pos = pos;
    this.lifespan = 255.0;
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
    lifespan -= 1.0;
  }
}
