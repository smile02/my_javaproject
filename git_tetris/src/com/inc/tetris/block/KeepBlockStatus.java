package com.inc.tetris.block;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.inc.tetris.cell.Cell;

public class KeepBlockStatus {
	public Block keepBlock;
	public static final int KEEPROWS = 4;
	public static final int KEEPCOLS = 4;
	public Cell[][] keepcells = new Cell[NextBlockStatus.NEXTROWS][NextBlockStatus.NEXTCOLS];
	private Image keepBack;
	public JPanel keepPanel;
	
	public KeepBlockStatus() {
		
		init();
	}
	
	void init() {
		initPanel();
	}
	
	void initPanel() {
		keepPanel = new JPanel(new BorderLayout());
	}
	
	
	public void keepCells() {
		for(int row = 0; row< KEEPROWS; row++) {
			for(int col = 0; col < KEEPCOLS; col++) {
				keepcells[row][col] = new Cell(new Point(col * Cell.CELL_SZIE, row * Cell.CELL_SZIE),
						Color.GREEN);
			}
		}
	}
	

	public void keepPanel() {
		ImageIcon keepIcon = new ImageIcon("src/images/하늘.png");//
		keepBack = keepIcon.getImage();//	
		keepPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D)g;
				g2.clearRect(0, 0, keepPanel.getWidth(), keepPanel.getHeight());
				g2.drawImage(keepBack, 0, 0, this); //
				for(int row = 0; row < KEEPROWS; row ++) {
					for(int col =0; col < KEEPCOLS; col++) {
						Cell kcell = keepcells[row][col];
						if(kcell.isVisible()) { //true라면 해당 포인트만 색칠 //게임이 시작되기 전에는 false
							g2.setColor(kcell.getC());  // 도형의 색(Shape 클래스에서 가져옴)
							g2.fillRect(kcell.p.x, kcell.p.y, Cell.CELL_SZIE, Cell.CELL_SZIE);
						}
						g2.setColor(Color.GRAY);
						g2.drawRect(kcell.p.x, kcell.p.y, Cell.CELL_SZIE, Cell.CELL_SZIE);
					}
				}
			}
		};
		
		keepPanel.setBounds(340,240,140,140);
	}
	
	
	
}
