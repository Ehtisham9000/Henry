package com.ranjha.aurelis;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.os.*;
import android.view.*;
import java.util.*;

public class MainActivity extends Activity {
  @Override public void onCreate(Bundle b){ super.onCreate(b); getWindow().setStatusBarColor(Color.TRANSPARENT); getWindow().setNavigationBarColor(Color.TRANSPARENT); setContentView(new Game(this)); }

  static class Game extends View {
    Paint p=new Paint(1), t=new Paint(1); int[][][] board=new int[3][3][3]; int turn=1; boolean over=false; String message="YOUR TURN"; float downX,downY;
    int cyan=Color.rgb(100,210,255), purple=Color.rgb(150,120,255), white=Color.WHITE;
    Game(Context c){ super(c); t.setTypeface(Typeface.create("sans-serif",0)); setBackgroundColor(Color.rgb(5,5,9)); }
    void txt(Canvas c,String s,float x,float y,float size,int color){t.setTextSize(size);t.setColor(color);c.drawText(s,x,y,t);}
    void card(Canvas c,float l,float top,float r,float bot){p.setColor(0x241FFFFFF);c.drawRoundRect(l,top,r,bot,26,26,p);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(1);p.setColor(0x38FFFFFF);c.drawRoundRect(l,top,r,bot,26,26,p);p.setStyle(Paint.Style.FILL);}
    protected void onDraw(Canvas c){super.onDraw(c); int w=getWidth(),h=getHeight();
      txt(c,"STACK TAC TOE",28,48,15,cyan); txt(c,"3 x 3 • STACK UP TO 3",28,73,11,0x88FFFFFF);
      txt(c,over?message:"P1  •  YOUR MOVE",28,112,14,white);
      float size=Math.min(w-56,h-300)/3f, left=(w-size*3)/2f, top=145;
      for(int r=0;r<3;r++) for(int col=0;col<3;col++) {float x=left+col*size,y=top+r*size; card(c,x+5,y+5,x+size-5,y+size-5); for(int k=0;k<3;k++){int owner=board[r][col][k]; if(owner!=0){float cy=y+size-26-k*28; p.setColor(owner==1?cyan:purple); c.drawCircle(x+size/2,cy,18,p); txt(c,owner==1?"1":"2",x+size/2-5,cy+6,12,Color.BLACK);}} txt(c,""+(board[r][col].length==3?count(r,col):0),x+size-28,y+25,10,0x66FFFFFF); }
      card(c,28,h-118,w-28,h-52); txt(c,"Tap an empty space OR stack on your color.",48,h-84,12,0xBBFFFFFF); txt(c,"RESET",w-88,h-84,12,cyan);
      if(over){p.setColor(0x90000000);c.drawRect(0,0,w,h,p);card(c,45,h/2f-75,w-45,h/2f+75);txt(c,message,w/2f-95,h/2f-10,24,white);txt(c,"TAP RESET TO PLAY AGAIN",w/2f-100,h/2f+28,11,cyan);}
    }
    int count(int r,int c){int n=0;for(int k=0;k<3;k++)if(board[r][c][k]!=0)n++;return n;}
    boolean win(int player){for(int r=0;r<3;r++)for(int c=0;c<3;c++){if(board[r][c][2]==player){if((c==0&&board[r][1][2]==player&&board[r][2][2]==player)||(r==0&&board[1][c][2]==player&&board[2][c][2]==player))return true;}}
      if(board[0][0][2]==player&&board[1][1][2]==player&&board[2][2][2]==player)return true;if(board[0][2][2]==player&&board[1][1][2]==player&&board[2][0][2]==player)return true;return false;}
    boolean canPlay(int r,int c,int player){int n=count(r,c); if(n==0)return true; if(n>=3)return false; return board[r][c][n-1]==player;}
    void ai(){ArrayList<int[]> moves=new ArrayList<>();for(int r=0;r<3;r++)for(int c=0;c<3;c++)if(canPlay(r,c,2))moves.add(new int[]{r,c});if(moves.isEmpty()){message="DRAW";over=true;return;} int[] best=moves.get(0);for(int[] m:moves){int r=m[0],c=m[1]; if(count(r,c)==2)best=m; if(board[r][c][count(r,c)]==0){}} board[best[0]][best[1]][count(best[0],best[1])]=2;if(win(2)){message="AI WINS";over=true;}else{turn=1;message="YOUR TURN";}}
    public boolean onTouchEvent(MotionEvent e){if(e.getAction()!=MotionEvent.ACTION_UP){if(e.getAction()==MotionEvent.ACTION_DOWN){downX=e.getX();downY=e.getY();return true;}return true;} if(over){if(e.getY()>getHeight()-150)reset();return true;} float size=Math.min(getWidth()-56,getHeight()-300)/3f,left=(getWidth()-size*3)/2f,top=145; if(e.getY()>=top&&e.getY()<top+size*3){int c=(int)((e.getX()-left)/size),r=(int)((e.getY()-top)/size);if(r>=0&&r<3&&c>=0&&c<3&&canPlay(r,c,1)){board[r][c][count(r,c)]=1;if(win(1)){message="YOU WIN";over=true;}else ai();invalidate();return true;}} if(e.getY()>getHeight()-140&&e.getX()>getWidth()-130)reset();return true; }
    void reset(){board=new int[3][3][3];turn=1;over=false;message="YOUR TURN";invalidate();}
  }
}
