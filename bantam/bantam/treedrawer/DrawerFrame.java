/*
 * @(#)DrawerFrame.java                        2.0 1999/08/11
 *
 * Copyright (C) 1999 D.A. Watt and D.F. Brown
 * Dept. of Computing Science, University of Glasgow, Glasgow G12 8QQ Scotland
 * and School of Computer and Math Sciences, The Robert Gordon University,
 * St. Andrew Street, Aberdeen AB25 1HG, Scotland.
 * All rights reserved.
 *
 * This software is provided free for educational use only. It may
 * not be used for commercial purposes without the prior written permission
 * of the authors.
 */

package proj10LoverudeTymkiwCorrell.bantam.treedrawer;

import javax.swing.*;
import java.awt.*;

class DrawerFrame extends JFrame
{
    public DrawerFrame(String sourceName, JPanel panel)
    {
        setSize(300, 200);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        int screenHeight = d.height;
        int screenWidth = d.width;
        setTitle("Triangle Compiler AST for " + sourceName);
        setSize(screenWidth / 2, screenHeight / 2);
        setLocation(screenWidth / 4, screenHeight / 4);
        Container contentPane = getContentPane();
        contentPane.add(new JScrollPane(panel));
        panel.scrollRectToVisible(new Rectangle(2048-screenWidth/4,0,10,10));

        // uncomment the following code if you want the program to quit
        //          when the drawing window is closed.
        //
        // addWindowListener(
        //       new WindowAdapter()
        //       {
        //           public void windowClosing(WindowEvent e)
        //           {
        //               System.exit(0);
        //           }
        //       }
        //  );
    }
}