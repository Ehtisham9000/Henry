package com.ranjha.aurelis;

import android.app.Activity;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
  @Override public void onCreate(Bundle b){ super.onCreate(b); getWindow().setStatusBarColor(Color.TRANSPARENT); getWindow().setNavigationBarColor(Color.TRANSPARENT); setContentView(new Surface(this)); }
  class Surface extends View {
    Paint p=new Paint(1); Paint t=new Paint(1); Handler h=new Handler(); float downX,downY; long down;
    String[] modes={"CANVAS","SEEK","SIGNALS","CONTROL","SPACES"}; int mode=0;
    Surface(Context c){super(c); t.setTypeface(Typeface.create("sans-serif",0)); setBackgroundColor(Color.rgb(5,5,8)); h.post(new Runnable(){public void run(){invalidate();h.postDelayed(this,1000);}});}
    void txt(Canvas c,String s,float x,float y,float size,int color){t.setTextSize(size);t.setColor(color);c.drawText(s,x,y,t);}
    void glass(Canvas c,float l,float top,float r,float bot){p.setColor(0x24FFFFFF);c.drawRoundRect(l,top,r,bot,28,28,p);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(1);p.setColor(0x35FFFFFF);c.drawRoundRect(l,top,r,bot,28,28,p);p.setStyle(Paint.Style.FILL);}
    protected void onDraw(Canvas c){super.onDraw(c); int w=getWidth(),hh=getHeight();
      long now=System.currentTimeMillis(); String time=new SimpleDateFormat("HH:mm",Locale.getDefault()).format(new Date(now)); String date=new SimpleDateFormat("EEEE, d MMMM",Locale.getDefault()).format(new Date(now));
      if(mode==0){ txt(c,time,w/2f-55,145,72,Color.WHITE); txt(c,date,w/2f-72,178,14,0x99FFFFFF); txt(c,"AURELIS",28,45,13,0xFF64D2FF); txt(c,"Good evening.",28,225,20,Color.WHITE); txt(c,"What do you need?",28,252,13,0x99FFFFFF);
        p.setShader(new RadialGradient(w/2f,hh/2f,80,new int[]{0xFF64D2FF,0xFF7C6CFF,0x00101018},null,Shader.TileMode.CLAMP)); c.drawCircle(w/2f,hh/2f,78,p); p.setShader(null);
        glass(c,28,hh-170,w-28,hh-105); txt(c,"⌕  Seek apps, people, actions…",52,hh-136,15,0xCCFFFFFF);
        String[] dock={"Phone","Messages","Browser","Music","Camera"}; for(int i=0;i<5;i++){float x=28+i*(w-56)/4f; txt(c,dock[i],x,hh-58,11,0xCCFFFFFF);}
      } else { txt(c,"AURELIS",28,48,13,0xFF64D2FF); txt(c,modes[mode],28,92,30,Color.WHITE); txt(c,"Adaptive Android environment",28,115,12,0x88FFFFFF);
        String[][] rows={{"Surface","Your spatial home"},{"Library","Every app, one place"},{"Signals","What needs attention"},{"Flow","Continue where you left off"},{"Spaces","Activities, not folders"}};
        for(int i=0;i<5;i++){float y=150+i*82;glass(c,24,y,w-24,y+64);txt(c,rows[i][0],44,y+28,15,Color.WHITE);txt(c,rows[i][1],44,y+49,11,0x88FFFFFF);}
        txt(c,"Swipe down to return",28,hh-35,11,0x66FFFFFF);
      }
    }
    public boolean onTouchEvent(android.view.MotionEvent e){if(e.getAction()==0){downX=e.getX();downY=e.getY();down=System.currentTimeMillis();return true;} if(e.getAction()!=1)return true; float dx=e.getX()-downX,dy=e.getY()-downY;
      if(Math.abs(dx)>100&&Math.abs(dx)>Math.abs(dy)){mode=dx<0?2:3;invalidate();return true;} if(Math.abs(dy)>100&&Math.abs(dy)>Math.abs(dx)){mode=dy<0?1:0;invalidate();return true;} if(mode!=0&&dy>60){mode=0;invalidate();return true;} if(mode==0&&downY>getHeight()-200){mode=1;invalidate();return true;} return true; }
  }
}
