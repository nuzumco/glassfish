

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * Portions Copyright Apache Software Foundation.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
import java.util.*;
import java.awt.*;
import java.applet.*;
import java.text.*;

/**
 * Time!
 *
 * @author Rachel Gollub
 */

public class Clock2 extends Applet implements Runnable {
    Thread timer;                // The thread that displays clock
    int lastxs, lastys, lastxm,
        lastym, lastxh, lastyh;  // Dimensions used to draw hands 
    SimpleDateFormat formatter;  // Formats the date displayed
    String lastdate;             // String to hold date displayed
    Font clockFaceFont;          // Font for number display on clock
    Date currentDate;            // Used to get date to display
    Color handColor;             // Color of main hands and dial
    Color numberColor;           // Color of second hand and numbers

    public void init() {
        int x,y;
        lastxs = lastys = lastxm = lastym = lastxh = lastyh = 0;
        formatter = new SimpleDateFormat ("EEE MMM dd hh:mm:ss yyyy", Locale.getDefault());
        currentDate = new Date();
        lastdate = formatter.format(currentDate);
        clockFaceFont = new Font("Serif", Font.PLAIN, 14);
        handColor = Color.blue;
        numberColor = Color.darkGray;

        try {
            setBackground(new Color(Integer.parseInt(getParameter("bgcolor"),16)));
        } catch (Exception E) { }
        try {
            handColor = new Color(Integer.parseInt(getParameter("fgcolor1"),16));
        } catch (Exception E) { }
        try {
            numberColor = new Color(Integer.parseInt(getParameter("fgcolor2"),16));
        } catch (Exception E) { }
        resize(300,300);              // Set clock window size
    }

    // Plotpoints allows calculation to only cover 45 degrees of the circle,
    // and then mirror
    public void plotpoints(int x0, int y0, int x, int y, Graphics g) {
        g.drawLine(x0+x,y0+y,x0+x,y0+y);
        g.drawLine(x0+y,y0+x,x0+y,y0+x);
        g.drawLine(x0+y,y0-x,x0+y,y0-x);
        g.drawLine(x0+x,y0-y,x0+x,y0-y);
        g.drawLine(x0-x,y0-y,x0-x,y0-y);
        g.drawLine(x0-y,y0-x,x0-y,y0-x);
        g.drawLine(x0-y,y0+x,x0-y,y0+x);
        g.drawLine(x0-x,y0+y,x0-x,y0+y);
    }

    // Circle is just Bresenham's algorithm for a scan converted circle
    public void circle(int x0, int y0, int r, Graphics g) {
        int x,y;
        float d;
        x=0;
        y=r;
        d=5/4-r;
        plotpoints(x0,y0,x,y,g);

        while (y>x){
            if (d<0) {
                d=d+2*x+3;
                x++;
            }
            else {
                d=d+2*(x-y)+5;
                x++;
                y--;
            }
            plotpoints(x0,y0,x,y,g);
        }
    }

    // Paint is the main part of the program
    public void paint(Graphics g) {
        int xh, yh, xm, ym, xs, ys, s = 0, m = 10, h = 10, xcenter, ycenter;
        String today;

        currentDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("s",Locale.getDefault());
        try {
            s = Integer.parseInt(formatter.format(currentDate));
        } catch (NumberFormatException n) {
            s = 0;
        }
        formatter.applyPattern("m");
        try {
            m = Integer.parseInt(formatter.format(currentDate));
        } catch (NumberFormatException n) {
            m = 10;
        }    
        formatter.applyPattern("h");
        try {
            h = Integer.parseInt(formatter.format(currentDate));
        } catch (NumberFormatException n) {
            h = 10;
        }
        formatter.applyPattern("EEE MMM dd HH:mm:ss yyyy");
        today = formatter.format(currentDate);
        xcenter=80;
        ycenter=55;
    
    // a= s* pi/2 - pi/2 (to switch 0,0 from 3:00 to 12:00)
    // x = r(cos a) + xcenter, y = r(sin a) + ycenter
    
        xs = (int)(Math.cos(s * 3.14f/30 - 3.14f/2) * 45 + xcenter);
        ys = (int)(Math.sin(s * 3.14f/30 - 3.14f/2) * 45 + ycenter);
        xm = (int)(Math.cos(m * 3.14f/30 - 3.14f/2) * 40 + xcenter);
        ym = (int)(Math.sin(m * 3.14f/30 - 3.14f/2) * 40 + ycenter);
        xh = (int)(Math.cos((h*30 + m/2) * 3.14f/180 - 3.14f/2) * 30 + xcenter);
        yh = (int)(Math.sin((h*30 + m/2) * 3.14f/180 - 3.14f/2) * 30 + ycenter);
    
    // Draw the circle and numbers
    
        g.setFont(clockFaceFont);
        g.setColor(handColor);
        circle(xcenter,ycenter,50,g);
        g.setColor(numberColor);
        g.drawString("9",xcenter-45,ycenter+3); 
        g.drawString("3",xcenter+40,ycenter+3);
        g.drawString("12",xcenter-5,ycenter-37);
        g.drawString("6",xcenter-3,ycenter+45);

    // Erase if necessary, and redraw
    
        g.setColor(getBackground());
        if (xs != lastxs || ys != lastys) {
            g.drawLine(xcenter, ycenter, lastxs, lastys);
            g.drawString(lastdate, 5, 125);
        }
        if (xm != lastxm || ym != lastym) {
            g.drawLine(xcenter, ycenter-1, lastxm, lastym);
            g.drawLine(xcenter-1, ycenter, lastxm, lastym); }
        if (xh != lastxh || yh != lastyh) {
            g.drawLine(xcenter, ycenter-1, lastxh, lastyh);
            g.drawLine(xcenter-1, ycenter, lastxh, lastyh); }
        g.setColor(numberColor);
        g.drawString("", 5, 125);
        g.drawString(today, 5, 125);    
        g.drawLine(xcenter, ycenter, xs, ys);
        g.setColor(handColor);
        g.drawLine(xcenter, ycenter-1, xm, ym);
        g.drawLine(xcenter-1, ycenter, xm, ym);
        g.drawLine(xcenter, ycenter-1, xh, yh);
        g.drawLine(xcenter-1, ycenter, xh, yh);
        lastxs=xs; lastys=ys;
        lastxm=xm; lastym=ym;
        lastxh=xh; lastyh=yh;
        lastdate = today;
        currentDate=null;
    }

    public void start() {
        timer = new Thread(this);
        timer.start();
    }

    public void stop() {
        timer = null;
    }

    public void run() {
        Thread me = Thread.currentThread();
        while (timer == me) {
            try {
                Thread.currentThread().sleep(100);
            } catch (InterruptedException e) {
            }
            repaint();
        }
    }

    public void update(Graphics g) {
        paint(g);
    }

    public String getAppletInfo() {
        return "Title: A Clock \nAuthor: Rachel Gollub, 1995 \nAn analog clock.";
    }
  
    public String[][] getParameterInfo() {
        String[][] info = {
            {"bgcolor", "hexadecimal RGB number", "The background color. Default is the color of your browser."},
            {"fgcolor1", "hexadecimal RGB number", "The color of the hands and dial. Default is blue."},
            {"fgcolor2", "hexadecimal RGB number", "The color of the seconds hand and numbers. Default is dark gray."}
        };
        return info;
    }
}