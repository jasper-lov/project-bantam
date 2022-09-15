/*
 * @(#)DrawerPanel.java                        2.0 1999/08/11
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

class DrawerPanel extends JPanel
{
    private DrawingTree drawingTree;

    public DrawerPanel()
    {
        setPreferredSize(new Dimension(4096, 4096));
    }

    public void setDrawing(DrawingTree drawingTree)
    {
        this.drawingTree = drawingTree;
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.setColor(getBackground());
        Dimension d = getSize();
        g.fillRect(0, 0, d.width, d.height);

        if (drawingTree != null) {
            drawingTree.paint(g);
        }
    }
}