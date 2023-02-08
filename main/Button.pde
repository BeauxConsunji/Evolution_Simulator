public class Button {
  private String type;
  private String text; // text inside the button
  private PImage img;
  private int fontSize = 24;
  private int padding = 20; // distance from content to border
  private float radius = 10; // how round the corners are
  private Vec2 pos, dim; // position and dimension vectors
  private boolean visible = true;
  private color colour = color(164, 234, 85);
  
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
  
  public void setColour(color c) {
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
