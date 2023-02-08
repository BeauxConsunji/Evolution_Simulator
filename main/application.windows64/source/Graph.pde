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
