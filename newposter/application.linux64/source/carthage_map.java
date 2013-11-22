import processing.core.*; 
import processing.data.*; 
import processing.opengl.*; 

import java.applet.*; 
import java.awt.Dimension; 
import java.awt.Frame; 
import java.awt.event.MouseEvent; 
import java.awt.event.KeyEvent; 
import java.awt.event.FocusEvent; 
import java.awt.Image; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class carthage_map extends PApplet {

// semipermamapper v2
//
// based on:
//mipmap, a quick mappign system.
// ephemeral mapper
// from version by maxD 20130917
//
// version 2:
// adding saving and loading xml feature
// version 2.1:
// adding ascii char pixelbrush
// rfboyce@gmail.com 20130923

// todo list:
//add solid blocks?
//
//threading? management - render - other?
//
//step mode with9 external inconsistent slow clock

// note: 
//arrow keys and mouse to map.

ArrayList<PVector> points;
XML xml;
float d = 0.1f;
float colOffset = 0.1f;
float speed = 0.01f;
int trailmix = 10;

int col;//?

int mode = 1;
int thingMode = 1;
int colMode = 1;



int sizer = 8;

int current = 0;

// 0 blackout, 1 edit, 2 rainbow kittens.

int px = 0;
int py = 0;

int x1 = 0;
int x2 = 0;
int y1 = 0;
int y2 = 0;

int pcol = color(0, 0, 0);

boolean mouse = true;
boolean trails = true;
boolean doFill = false;
boolean singles = false;
int shift = 0;

PFont font;

//make fills
int index = 0;

public void setup() {
  size(810, 610, P3D);
  points = new ArrayList();
  smooth();
  noCursor();
  colorMode(HSB, 100);
  background(0);
  font = createFont("Georgia", 24);
  placePoint();
  index++;
}



public void draw() {
  step();
  bg();
  if (points.size()<2 || mode == 1) maper();
  else {
    for (int i = points.size()-1; i > 1; i--) {
      if(singles && d>1) break;
      int j = i - 1;
      if (points.get(i).z!=255) {
        x1 = PApplet.parseInt(points.get(j).x);
        y1 = PApplet.parseInt(points.get(j).y);
        x2 = PApplet.parseInt(points.get(i).x);
        y2 = PApplet.parseInt(points.get(i).y);  
        current = i;
        movement();
      }
    }
    if(doFill) filler();
  }
}


///////////////////   movement    \\\\\\\\\\\\\\\\\\\\\\

public void movement() {
  switch(mode) {
  case 0:
    background(0);
    break;
  case 2:
    polka();
    break;
  case 3:
    dotted();
    break;
  case 4:
    linnen();
    break;
  }
}


public void filler() {
  for (int i = points.size()-1; i > 1; i--) {
    int ha = PApplet.parseInt(points.get(i).z);
    noStroke();
    fill(0, 0, 100);
    beginShape();
    while (points.get (i).z==ha) {
      vertex(points.get(i).x, points.get(i).y);
      if (i>0)i--;
      else break;
    }
    endShape(CLOSE);
  }
}

public void polka() {
  thingner(PApplet.parseInt(x1+d*(x2-x1)), PApplet.parseInt(y1+d*(y2-y1)));
}

public void dotted() {

  int n = 24;
  //adjust number of dots:
  int l = PApplet.parseInt(sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2)));
  n=l/(sizer*5+1);
  for (float k = 0; k<n;k++) {  
    colOffset+=0.005f;
    float e = (d+k)/n;    
    thingner(PApplet.parseInt(x1+e*(x2-x1)), PApplet.parseInt(y1+e*(y2-y1)));
  }
}



/////// STUFF THAT MAKES PIXELS CHANGE COLOR  \\\\\\\\\\\\

///////////////////  things
public void thingner(int xx, int yy) {
  switch(thingMode) {
  case 0:
    pnt(xx, yy);
    break;
  case 1:
    arrow(xx, yy);
    break;
  case 2:
    liner(xx, yy);
    break;
  case 3:
    ascii(xx, yy);
    break;
  }
}


public int colorizer() {
  colOffset+=0.006f;
  return color(((current*5)+colOffset)%100, 255, 255);
}


public void pnt(int xx, int yy) {
  strokeWeight(sizer);
  stroke(colorizer());
  point(xx, yy);
}

public void liner(int xx, int yy) {
  stroke(colorizer());
  strokeWeight(3);
  pushMatrix();
  translate(xx, yy);
  rotate(atan2(y1-y2, x1-x2));
  line(0, sizer, 0, -sizer);
  popMatrix();
}

public void arrow(int xx, int yy) {
  stroke(colorizer());
  strokeWeight(3);
  pushMatrix();
  translate(xx, yy);
  rotate(atan2(y1-y2, x1-x2));
  line(0, 0, sizer, sizer);
  line(0, 0, sizer, -sizer);
  popMatrix();
}

public void ascii(int xx, int yy) {
  textFont(font);
  textAlign(CENTER, CENTER);
  //char letter = char(123);
  char letter = PApplet.parseChar(79);
  char[] letters = {
    'x', 'X'
  };
  letter = letters[PApplet.parseInt(random(0, letters.length))];
  fill(colorizer());
  pushMatrix();
  translate(xx, yy);
  rotate(atan2(y1-y2, x1-x2));
  text(letter, 0, 0);
  popMatrix();
}


public void linnen() {
  stroke(colorizer());
  strokeWeight(sizer);
  line(x1, y1, x2, y2);
  if (points.size()<100) delay(10);
}


/////////  mapper things
public void maper() {
  trails = false;
  placePoint();
  drawLines();
  removePoint();
  if (mouse) {
    px=mouseX;
    py=mouseY;
  }
  aimer(px, py);
}


public void drawLines() {
  background(0);
  if (points.size()>2) {
    for (int i = points.size()-1; i > 1; i--) {
      int j = i - 1;
      int x1 = PApplet.parseInt(points.get(j).x);
      int y1 = PApplet.parseInt(points.get(j).y);
      int x2 = PApplet.parseInt(points.get(i).x);
      int y2 = PApplet.parseInt(points.get(i).y);
      col = i-(floor(i/100))*100;
      if (points.get(i).z!=255) stroke(0, 0, 255);
      else stroke(0);
      strokeWeight(2);
      line(x1, y1, x2, y2);
      if (d > 1) d=0;
      ellipse(x1+d*(x2-x1), y1+d*(y2-y1), 3, 3);
    }
  }
}



public void aimer(int xx, int yy) {
  strokeWeight(3);
  stroke(0, 0, 100);
  int out = 20;
  int in = 5;
  if (mouse) {
    line(xx-out, yy-out, xx-in, yy-in);
    line(xx+out, yy+out, xx+in, yy+in);
    line(xx+out, yy-out, xx+in, yy-in);
    line(xx-out, yy+out, xx-in, yy+in);
  }
  else {
    line(xx-out, yy, xx-in, yy);
    line(xx+out, yy, xx+in, yy);
    line(xx, yy-out, xx, yy-in);
    line(xx, yy+out, xx, yy+in);
  }
}

//////SMALL FUNCTIONS\\\\\\\
public void step() {
  d = d+speed;
  if(mode!=3)colOffset+=0.02f;
  if(!singles){
    if (d>1) d=0;
    if (d<0) d=1;
  }
}

public void queue(){
  d = 0;
}

public void bg() {
  if (trails) {
    fill(0, 0, 0, trailmix);
    stroke(0, 0, 0, trailmix);
    rect(0, 0, width, height);
  }
  else {
    background(0);
  }
}

public void placePoint() {
  points.add(new PVector(px, py, index));
}

public void blackPoint() {
  //removePoint();
  points.add(new PVector(px, py, 255));
  index++;
}

public void removePoint() {
  if (points.size()>0) points.remove(points.size()-1);
}



/* save points as XML */
public void savePoints() {
  if (points.size()>2) {
    // create empty XML
    String data = "<points></points>";
    // xml = parseXML(data);
    // populate XML with ArrayList contents
    for (int i = 1; i < points.size(); i++) {
      // save each point attribute to an xml item 
      XML newChild = xml.addChild("point");
      newChild.setFloat("x", points.get(i).x);
      newChild.setFloat("y", points.get(i).y);
      newChild.setFloat("z", points.get(i).z);
      newChild.setContent("point" + i);
      println(newChild);
    }
    //saveXML(xml, "points.xml");
    println("Points saved as points.xml");
  }
  else println("Not enough points to save.");
}

/* load new points from XML */
public void loadPoints() {
  // clear points ArrayList
  points.clear();
  xml = loadXML("points.xml");
  XML[] children = xml.getChildren("point");
  // check to see if XML is parsing properly
  for (int i = 0; i < children.length; i++) {
    int ptX = PApplet.parseInt(children[i].getFloat("x"));
    int ptY = PApplet.parseInt(children[i].getFloat("y"));
    int ptZ = PApplet.parseInt(children[i].getFloat("z"));
    // set ArrayList items to loaded values
    points.add(new PVector(ptX, ptY, ptZ));
    println("point " + i + ": " + ptX + ", " + ptY + ", " + ptZ);
  }
  println("Loaded points.xml");
}



/////////////////////  INPUT  \\\\\\\\\\\\\\\\\\\\\\\\\

public void mousePressed() {
  if (mode==1&&mouse) {
    if (mouseButton == LEFT) {
      placePoint();
    }
    else if (mouseButton == RIGHT) {
      removePoint();
    }
    else {
      blackPoint();
    }
  }
}


public void keyReleased() {
  if (keyCode == SHIFT) {
    shift = 0;
  }
}


public void keyPressed() {
  if (key == CODED) {
    if (keyCode == SHIFT) {
      shift = 10;
    }
    else if (keyCode == LEFT) {
      px-=(1+shift);
      if (px<0) px=width;
    }
    else if (keyCode == RIGHT) {
      px+=(1+shift);
      px=px%height;
    }
    else if (keyCode == UP) {
      py-=(1+shift);
      if (py<0) py=height;
    }
    else if (keyCode == DOWN) {
      py+=(1+shift);
      py=py%width;
    }
  }
  else if (key == 32) {
    placePoint();
  }
  else if (key == 'b') {
    blackPoint();
  }
  else if (key == 'f') {
    doFill = !doFill;
  }
  else if (key =='l') {
    loadPoints();
  }
  else if (key == 'm') {
    mouse=!mouse;
    println("Mouse = "+mouse);
  }
  else if (key == 'o') {
    sizer-=1;
    println("sizer = "+sizer);
  }
  else if (key == 'p') {
    sizer+=1;
    println("sizer = "+sizer);
  }
  else if (key == 'q') {
    queue();
  }
  else if (key == 'r') {
    speed = speed * -1;
  }
  else if (key == 's') {
    savePoints();
  }
  else if (key == 't') {
    trails=!trails;
    println("Trails = "+trails);
  }
  else if (key == 'w') {
    singles = !singles;
  }
  else if (key == 'z') {
    removePoint();
  }
  else if (key == '-') {
    speed-=0.003f;
    println("Speed : "+speed);
  }
  else if (key == '=') {
    speed+=0.003f;
    println("Speed : "+speed);
  }
  else if (key == '[') {
    trailmix-=1;
    println("Trailmix : "+trailmix);
  }
  else if (key == ']') {
    trailmix+=1;
    println("Trailmix : "+trailmix);
  }


  else if (PApplet.parseFloat(key)>=48&&PApplet.parseFloat(key)<=57) {
    int mde = PApplet.parseInt(key)-48;
    if (mde<6)mode = mde;
    else thingMode = mde-6; 
    println("Mode #"+mode);
  }
  else if (key == 'h') {
    println("SPACE OR left button -> place point");
    println("z OR right button -> undo");
    println("b OR middle button break line");
    println("h help");
    println("l load points");
    println("m mouse");
    println("r reverse direction");
    println("s save points");
    println("t trails");
    println("f fills");
    println("w queue mode");
    println("p/o point size"); 
    println("q queue"); 
    println("0-9 modes");
    println("- speed--");
    println("= speed++");
    println("[ trailmix--");
    println("[ trailmix++");
  }
}

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--hide-stop", "carthage_map" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
