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




//// To make gif:
//   mogrify -resample 72x72  -resize 186x263 ceegif_0000*
//    convert -delay 10 -loop 0 ceegif_0000* ceeethis.gif

ArrayList<PVector> points;
XML xml;
float d = 0.1;
float speed = 0.01;
int col;
int mode = 1;

int pntSz = 8;
int lnSz = 7;
// 0 blackout, 1 edit, 2 rainbow kittens.

int px = 0;
int py = 0;

int x1 = 0;
int x2 = 0;
int y1 = 0;
int y2 = 0;

color pcol = color(0, 0, 0);

boolean mouse = true;
boolean trails = true;

int shift = 0;

PFont font;

boolean forground =false;
//IMAGE
PImage front;
PImage back;
PImage backsolid;
boolean capture = false;
boolean docapture = false;
int saveCount = 0;
float keypoint = 0;
void setup() {
  size(744, 1052, P2D);
  points = new ArrayList();
  smooth();
  noCursor();
  colorMode(HSB, 100);
  background(0);
  placePoint();
  font = createFont("Georgia", 24);

  back = loadImage("posterbg.png");
  front = loadImage("posterfg.png");
  backsolid = loadImage("posterbg_bkp.png");
}

boolean skecthFullScreen() {
  return false;
}


void draw() {

  //bg();
  image(back, 0, 0);

  if (points.size()<2 || mode == 1) maper();
  else {
    for (int i = points.size()-1; i > 1; i--) {
      int j = i - 1;
      if (points.get(i).z!=255) {
        x1 = int(points.get(j).x);
        y1 = int(points.get(j).y);
        x2 = int(points.get(i).x);
        y2 = int(points.get(i).y);     
        switch(mode) {
        case 0:
          background(0);
          break;
        case 2:
          polka(i);
          break;
        case 3:
          dotted(i);
          break;
        case 4:
          ascii(i);
          break;
        case 5:
          linnen(i);
          break;
        }
      }
    }
  }
  step();
  if (capture && d<1 && int(d*100)%2==1) {
    String filename = "ceegif_" + nf(saveCount, 6) + ".jpg";
    saveFrame(filename);
    saveCount++;
    println("frame "+saveCount);
  }

  if (forground) image(front, 0, 0);
}



  void step() {
    if (d>1){
      d=0;
      if(docapture){
        docapture = false;
        capture = true;
      }
      else if(capture) capture = false;
    }
    if (d<0) d=1;
    d = d+speed;
  
  }
  
void cap(){
 docapture = true;
 offset =0;
 saveCount=0;
}


  void maper() {
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


  /////// STUFF THAT MAKES PIXELS CHANGE COLOR  \\\\\\\\\\\\
  float offset = 0;
  void polka(int i) {

    offset+=0.02;
    //stroke(points.get(i).z, 255, 255);
    //stroke(((i*5)+offset)%100, 255, 255);
    stroke((i*5)%100, 255, 255);
    strokeWeight(pntSz);

    point(x1+d*(x2-x1), y1+d*(y2-y1));
  }


  void dotted(int i) {

    int n = 24;
    //adjust number of dots:
    int l = int(sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2)));
    n=l/30;
    //stroke(points.get(i).z, 255, 255);

    strokeWeight(pntSz);

    for (float k = 0; k<n;k++) {  
      offset+=0.01;
      stroke((((i+k)*5)+offset)%100, 255, 255);
      float e = (d+k)/n;  

      point(x1+e*(x2-x1), y1+e*(y2-y1));
    }
  }

  void ascii(int i) {
    textFont(font);
    textAlign(CENTER, CENTER);
    //fill(25, 255, 180);
    fill(points.get(i).z, 255, 255);
    stroke(points.get(i).z, 255, 255);
    //char letter = char(123);
    char letter = char(79);
    char[] letters = {
      's', 'S'
    };
    letter = letters[int(random(0, letters.length))];

    translate(x1+d*(x2-x1), y1+d*(y2-y1));
    rotate(radians(d*360));
  }


  void linnen(int i) {
    stroke(random(100), 100, 100);

    strokeWeight(lnSz);

    line(x1, y1, x2, y2);

    if (points.size()<100) delay(10);
  }

  void drawLines() {
    //background(0);
    if (points.size()>2) {
      for (int i = points.size()-1; i > 1; i--) {
        int j = i - 1;
        int x1 = int(points.get(j).x);
        int y1 = int(points.get(j).y);
        int x2 = int(points.get(i).x);
        int y2 = int(points.get(i).y);
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



  void aimer(int xx, int yy) {
    strokeWeight(3);
    stroke(100, 100, 100);
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

  /* save points as XML */
  void savePoints() {
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
  void loadPoints() {
    // clear points ArrayList
    points.clear();
    xml = loadXML("points.xml");
    XML[] children = xml.getChildren("point");
    // check to see if XML is parsing properly
    for (int i = 0; i < children.length; i++) {
      int ptX = int(children[i].getFloat("x"));
      int ptY = int(children[i].getFloat("y"));
      int ptZ = int(children[i].getFloat("z"));
      // set ArrayList items to loaded values
      points.add(new PVector(ptX, ptY, ptZ));
      println("point " + i + ": " + ptX + ", " + ptY + ", " + ptZ);
    }
    println("Loaded points.xml");
  }

  //////SMALL FUNCTIONS\\\\\\\


  void bg() {
    if (trails) {
      fill(0, 0, 0, 10);
      noStroke();
      rect(0, 0, width, height);
    }
    else {
      background(0);
    }
  }

  void placePoint() {
    points.add(new PVector(px, py, random(100)));
  }

  void blackPoint() {
    //removePoint();
    points.add(new PVector(px, py, 255));
  }

  void removePoint() {
    if (points.size()>0) points.remove(points.size()-1);
  }


  /////////////////////  INPUT  \\\\\\\\\\\\\\\\\\\\\\\\\

  void mousePressed() {
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


  void keyReleased() {
    if (keyCode == SHIFT) {
      shift = 0;
    }
  }


  void keyPressed() {
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
    else if (key =='l') {
      loadPoints();
    }
    else if (key == 'm') {
      mouse=!mouse;
      println("Mouse = "+mouse);
    }
    else if (key == 'o') {
      pntSz-=1;
      println("pntSz = "+pntSz);
    }
    else if (key == 'p') {
      pntSz+=1;
      println("pntSz = "+pntSz);
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
    else if (key == 'z') {
      removePoint();
    }
    else if (key == 'f') {
      forground = !forground;
    }
    
    else if (key == 'c') {
      cap();

    }
    
    else if (key == '-') {
      speed-=0.01;
      println("Speed : "+speed);
    }
    else if (key == '=') {
      speed+=0.01;
      println("Speed : "+speed);
    }
    else if (float(key)>=48&&float(key)<=57) {
      if (mode == 1 && int(key)-48!=1)  image(backsolid, 0, 0);
      mode = int(key)-48;
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
      println("f forground");
      println("p/o point size"); 
      println("0-9 modes");
      println("- speed--");
      println("= speed++");
    }
  }

