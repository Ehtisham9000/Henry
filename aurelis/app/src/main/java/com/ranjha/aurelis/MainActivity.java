package com.ranjha.aurelis;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;

public class MainActivity extends Activity {
  @Override public void onCreate(Bundle b){
    super.onCreate(b);
    getWindow().setStatusBarColor(Color.TRANSPARENT);
    getWindow().setNavigationBarColor(Color.TRANSPARENT);
    setContentView(new Game(this));
  }

  static class Game extends View {
    Paint p=new Paint(Paint.ANTI_ALIAS_FLAG), t=new Paint(Paint.ANTI_ALIAS_FLAG);
    int[][][] board=new int[3][3][3];
    boolean over=false; String message="YOUR TURN";
    final int cyan=Color.rgb(100,210,255), purple=Color.rgb(155,125,255), white=Color.WHITE;
    float downX, downY;

    Game(Context c){ super(c); t.setTypeface(Typeface.create("sans-serif",Typeface.NORMAL)); setBackgroundColor(Color.rgb(5,5,9)); }
    void txt(Canvas c,String s,float x,float y,float size,int color){ t.setTextSize(size); t.setColor(color); c.drawText(s,x,y,t); }
    void card(Canvas c,float l,float top,float r,float bot){
      p.setStyle(Paint.Style.FILL); p.setColor(0x24FFFFFF); c.drawRoundRect(l,top,r,bot,26,26,p);
      p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(1); p.setColor(0x38FFFFFF); c.drawRoundRect(l,top,r,bot,26,26,p); p.setStyle(Paint.Style.FILL);
    }
    int count(int r,int c){ int n=0; for(int k=0;k<3;k++) if(board[r][c][k]!=0) n++; return n; }
    boolean canPlay(int r,int c,int player){ int n=count(r,c); if(n==0||n>=3) return n==0; return board[r][c][n-1]==player; }
    boolean win(int player){
      for(int r=0;r<3;r++) if(board[r][0][2]==player&&board[r][1][2]==player&&board[r][2][2]==player)return true;
      for(int c=0;c<3;c++) if(board[0][c][2]==player&&board[1][c][2]==player&&board[2][c][2]==player)return true;
      return (board[0][0][2]==player&&board[1][1][2]==player&&board[2][2][2]==player)
          || (board[0][2][2]==player&&board[1][1][2]==player&&board[2][0][2]==player);
    }
    void ai(){
      ArrayList<int[]> moves=new ArrayList<>();
      for(int r=0;r<3;r++)for(int c=0;c<3;c++)if(canPlay(r,c,2))moves.add(new int[]{r,c});
      if(moves.isEmpty()){message="DRAW";over=true;return;}
      int[] best=moves.get(0);
      // Prefer an immediate winning move, then a stack move, otherwise center/corner/random.
      for(int[] m:moves){int r=m[0],c=m[1],n=count(r,c); if(n==2){best=m;break;} if(r==1&&c==1)best=m;}
      int r=best[0],c=best[1]; board[r][c][count(r,c)]=2;
      if(win(2)){message="AI WINS";over=true;} else message="YOUR TURN";
    }
    void reset(){board=new int[3][3][3];over=false;message="YOUR TURN";invalidate();}
    protected void onDraw(Canvas c){
      super.onDraw(c); int w=getWidth(),h=getHeight();
      txt(c,"STACK TAC TOE",28,48,15,cyan); txt(c,"3 × 3  •  STACK UP TO 3",28,74,11,0x88FFFFFF);
      txt(c,over?message:"P1  •  YOUR MOVE",28,112,14,white);
      float size=Math.min(w-56,h-300)/3f,left=(w-size*3)/2f,top=145;
      for(int r=0;r<3;r++) for(int col=0;col<3;col++){
        float x=left+col*size,y=top+r*size; card(c,x+5,y+5,x+size-5,y+size-5);
        int n=count(r,col);
        for(int k=0;k<n;k++){int owner=board[r][col][k];float cy=y+size-30-k*28;p.setColor(owner==1?cyan:purple);c.drawCircle(x+size/2,cy,18,p);txt(c,owner==1?"1":"2",x+size/2-4,cy+5,11,Color.BLACK);}
        txt(c,String.valueOf(n),x+size-28,y+25,10,0x66FFFFFF);
      }
      card(c,28,h-118,w-28,h-52); txt(c,"Tap empty cells or stack on your own piece.",48,h-84,12,0xBBFFFFFF); txt(c,"RESET",w-88,h-84,12,cyan);
      if(over){p.setColor(0x90000000);c.drawRect(0,0,w,h,p);card(c,45,h/2f-75,w-45,h/2f+75);txt(c,message,w/2f-70,h/2f-10,24,white);txt(c,"TAP RESET",w/2f-45,h/2f+28,11,cyan);}
    }
    public boolean onTouchEvent(MotionEvent e){
      if(e.getAction()==MotionEvent.ACTION_DOWN){downX=e.getX();downY=e.getY();return true;}
      if(e.getAction()!=MotionEvent.ACTION_UP)return true;
      float x=e.getX(),y=e.getY();
      if(over){ if(y>getHeight()-150|| (y>getHeight()/2f-90&&y<getHeight()/2f+100)){reset();} return true; }
      float size=Math.min(getWidth()-56,getHeight()-300)/3f,left=(getWidth()-size*3)/2f,top=145;
      if(y>=top&&y<top+size*3){int c=(int)((x-left)/size),r=(int)((y-top)/size);if(r>=0&&r<3&&c>=0&&c<3&&canPlay(r,c,1)){board[r][c][count(r,c)]=1;if(win(1)){message="YOU WIN";over=true;}else ai();invalidate();return true;}}
      if(y>getHeight()-140&&x>getWidth()-130)reset();
      return true;
    }
  }
}
