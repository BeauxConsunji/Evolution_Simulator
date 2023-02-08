public class Test {
  private String title;
  private ArrayList<String> questions;
  private ArrayList<Integer> answers;
  private ArrayList<String[]> choices;
  private Button submitBtn, nxtBtn;
  private int currQuestion = 0;
  private int selectedChoice = -1;
  private int score = 0;
  private boolean isAnswered = false;
  
  public Test(String title) {
    this.title = title;
    this.questions = new ArrayList<String>();
    this.answers = new ArrayList<Integer>();
    this.choices = new ArrayList<String[]>();
    this.submitBtn = new Button("Submit", new Vec2(width - 220, height - 120));
    this.submitBtn.setFontSize(40);
    this.submitBtn.setPadding(20);
    this.nxtBtn = new Button("Next", new Vec2(width - 220, height - 120));
    this.nxtBtn.setFontSize(40);
    this.nxtBtn.setPadding(20);
    this.nxtBtn.setVisibility(false);
    addQuestion("What is a feature that allows an organism to survive better in its environment?", "adaptation", "variation", "homologous structure", 0);
    addQuestion("What are all the individuals of a species that live in a particular area called?", "variation", "group", "population", 2);
    addQuestion("What did Charles Darwin call the ability of an organism to survive and reproduce in its environment", "evolution", "diversity", "fitness", 2);
    addQuestion("What is mutation?", "combination of parents' DNA", "a change in the DNA", "exposure to radiation", 1);
    addQuestion("What is the process of organisms becoming better adapted to their environment?", "natural selection", "adaptation", "transmission", 0);
  }
  
  public void display() {
    strokeWeight(5);
    stroke(DEF_STROKE);
    fill(221, 216, 148);
    rectMode(CORNER);
    rect(20, 20, width - 40, height - 40, 25);
    
    if (this.isAnswered) {
      fill(DEF_STROKE);
      textAlign(CENTER);
      textSize(50);
      text("Score: " + score + "/" + this.questions.size(), width/2, 150);
      textSize(32);
      text("Question " + (currQuestion + 1) + ":\n" + this.questions.get(currQuestion), 80, 220, width - 160, 400);
      if (this.selectedChoice == this.answers.get(currQuestion)) {
        fill(194, 217, 168);
        rect(80, 400, width - 160, 200);
        fill(DEF_STROKE);
        text("Answer:\n" + this.choices.get(currQuestion)[this.answers.get(currQuestion)], width/2, 450);
      } else {
        fill(217, 168, 168);
        rect(80, 400, width - 160, 250);
        fill(DEF_STROKE);
        text("Your answer:\n" + this.choices.get(currQuestion)[this.selectedChoice], width/2, 450);
        text("Correct answer:\n" + this.choices.get(currQuestion)[this.answers.get(currQuestion)], width/2, 550);
      }
      this.nxtBtn.display();
  } else {
      fill(255);
      rect(500, 50, 650, 50, 25);
      textAlign(LEFT);
      fill(164, 234, 85);
      if (currQuestion > 0) {
        rect(500, 50, 650 * currQuestion / this.questions.size(), 50, 25);
      }
      this.submitBtn.display();
      
      for (int i = 0; i < 3; i++) {
        strokeWeight(8);
        if (mousePressed && pow(mouseX - width/2 - 100, 2) + pow(mouseY - 290 - (100 * i), 2) <= 900) {
          selectedChoice = i;
        }
        if (i == selectedChoice) {
          stroke(DEF_STROKE);
          fill(164, 234, 85);
        } else {
          stroke(DEF_STROKE);
          fill(255);
        }
        circle(width/2 + 90, 290 + (100 * i), 30);
        fill(DEF_STROKE);
        text(this.choices.get(currQuestion)[i], width/2 + 120, 300 + (100 * i));
      }
      fill(DEF_STROKE);
      textSize(60);
      text(this.title, 60, 100);
      textSize(32);
      text("Q" + (currQuestion + 1) + ":\n" + this.questions.get(currQuestion), 60, 150, width/2 - 40, height - 190);
    }
    
    
  }
  
  public void addQuestion(String q, String c1, String c2, String c3, int a) {
    questions.add(q);
    choices.add(new String[]{c1, c2, c3});
    answers.add(a);
  }
  
  public int getScore() {
    return this.score;
  }
  
  public boolean isSubmitted() {
     return this.submitBtn.isClicked() && this.selectedChoice != -1;
  }
  public boolean isNext() {
    return this.nxtBtn.isClicked();
  }
  
  public boolean isFinished() {
    return this.currQuestion == this.questions.size();
  }
  
  public void submit() {
    if (this.selectedChoice == this.answers.get(this.currQuestion)) {
      this.score++;
      print(this.score);
    }
    this.isAnswered = true;
    this.submitBtn.setVisibility(false);
    this.nxtBtn.setVisibility(true);
  }
  public void next() {
    this.currQuestion++;
    this.selectedChoice = -1;
    this.isAnswered = false;
    this.nxtBtn.setVisibility(false);
    this.submitBtn.setVisibility(true);
  }
}
