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
  float friction = 0.3;
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
