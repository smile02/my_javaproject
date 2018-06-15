package com.inc.tetris.block;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.inc.tetris.cell.Cell;
import com.inc.tetris.frame.TetrisFrame;

public class NextBlockStatus {
	public Block nextBlock;
	BlockTransformer nextformer;
	TetrisFrame tf;
	public static final int NEXTROWS = 4;
	public static final int NEXTCOLS = 4;
	public Cell[][] nextcells = new Cell[NEXTROWS][NEXTCOLS];
	private Image nextBack;
	public JPanel nextPanel; //다음블럭 보여주기
	
	public NextBlockStatus() {
		init();
		nextformer = new BlockTransformer(nextcells);
	}
	
	void init() {
		initComponent();
		initPanel();
	}
	
	void initComponent() {
		
	}
	
	void initPanel() {
		nextPanel = new JPanel(new BorderLayout());
	}

	public void nextPanel() {

		ImageIcon nextIcon = new ImageIcon("src/images/프리지아2.png");//
		nextBack = nextIcon.getImage();//

		nextPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				g2.clearRect(0, 0, nextPanel.getWidth(), nextPanel.getHeight());
				g2.drawImage(nextBack, 0, 0, this); //
				for (int row = 0; row < NextBlockStatus.NEXTROWS; row++) {
					for (int col = 0; col < NextBlockStatus.NEXTCOLS; col++) {
						Cell ncell = nextcells[row][col]; // cell을 새로 생성해서 그려야 할 곳 지정
						/*
						 * cell에 row와 col에 해당하는 좌표를 넣음
						 */
						if (ncell.isVisible()) { // true라면 해당 포인트만 색칠 //게임이 시작되기 전에는 false
							g2.setColor(ncell.getC()); // 도형의 색(Shape 클래스에서 가져옴)
							g2.fillRect(ncell.p.x, ncell.p.y, Cell.CELL_SZIE, Cell.CELL_SZIE);
						}
						g2.setColor(Color.GRAY); // cell의 색상(선의 색)
						g2.drawRect(ncell.p.x, ncell.p.y, Cell.CELL_SZIE, Cell.CELL_SZIE); // 하나의 셀의 크기(모양을 만듬)
					}
				}
			}
		};

		nextPanel.setBounds(340, 50, 120, 120);
	}

	public void nextCells() {
		for (int row = 0; row < NextBlockStatus.NEXTROWS; row++) {
			for (int col = 0; col < NextBlockStatus.NEXTCOLS; col++) {
				nextcells[row][col] = new Cell(new Point(col * Cell.CELL_SZIE, row * Cell.CELL_SZIE),
						Color.BLUE);
			}
		}
	}

	public void nextBlock() {
		int ranNum = (int) (Math.random() * Shape.SHAPE.length); // 0부터 6사이의 난수

		nextBlock = new Block(ranNum, Shape.COLOR[ranNum]);
		nextformer.setBlock(nextBlock);

		for (Point p : nextBlock.getShape()) {
			nextcells[p.x][p.y].setVisible(true);
			nextcells[p.x][p.y].setC(nextBlock.getC());
		}
		nextPanel.repaint();
	}

}