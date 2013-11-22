


class MPoint {
  //point location
  PVector pos;

  //progress of animation
  float d = 0;

  public MPoint(float xx, float yy, float zz) {
    pos =  new PVector(xx, yy, zz);
  }

  public void go(){
     d=0;
  }

  public void step(boolean sing, float spd) {
    d = d+spd;
    colOffset+=0.02;
    if (!sing) {
      if (d>1) d=0;
      if (d<0) d=1;
    }

  }
}

