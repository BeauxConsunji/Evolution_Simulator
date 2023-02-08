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
      boxes.get(i).setFriction(1.0);
    }
  }
  
  public void addLeft() {
    boxes.add(new Box(600, 170));
    boxes.get(boxes.size() - 1).createBody(new Vec2(min - 300, height - 85));
    boxes.get(boxes.size() - 1).setType(BodyType.STATIC);
    boxes.get(boxes.size() - 1).setFriction(1.0);
    min -= 600;
  }
  
  public void addRight() {
    boxes.add(new Box(600, 170));
    boxes.get(boxes.size() - 1).createBody(new Vec2(max + 300, height - 85));
    boxes.get(boxes.size() - 1).setType(BodyType.STATIC);
    boxes.get(boxes.size() - 1).setFriction(1.0);
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
