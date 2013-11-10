
PFont font;


//IMAGE
PImage front;
PImage back;

void setup() {
  size(730, 550, P3D);
  
  font = createFont("Georgia", 24);
textFont(font);
  back = loadImage("back.png");
  front = loadImage("front.png");
}




void draw() {
 background(0);
//image(back,0,0);

strokeWeight(3);
stroke(255,0,0);
fill(0,0,255);

for(int i = 0; i<width; i+=30){
  line(i,0,i,height);  
  text("ceephax",width/2,i);
  delay(100);
}
 

  //image(front, 0, 0);
}


